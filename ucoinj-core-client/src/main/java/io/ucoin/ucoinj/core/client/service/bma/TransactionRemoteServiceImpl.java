package io.ucoin.ucoinj.core.client.service.bma;


import io.ucoin.ucoinj.core.client.model.TxOutput;
import io.ucoin.ucoinj.core.client.model.bma.TxHistory;
import io.ucoin.ucoinj.core.client.model.bma.TxSource;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.model.local.Wallet;
import io.ucoin.ucoinj.core.client.service.ServiceLocator;
import io.ucoin.ucoinj.core.client.service.exception.InsufficientCreditException;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.core.service.CryptoService;
import io.ucoin.ucoinj.core.util.CollectionUtils;
import io.ucoin.ucoinj.core.util.ObjectUtils;
import io.ucoin.ucoinj.core.util.StringUtils;
import io.ucoin.ucoinj.core.util.crypto.DigestUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class TransactionRemoteServiceImpl extends BaseRemoteServiceImpl implements TransactionRemoteService {

	private static final Logger log = LoggerFactory.getLogger(TransactionRemoteServiceImpl.class);

    public static final String URL_TX_BASE = "/tx";

    public static final String URL_TX_PROCESS = URL_TX_BASE + "/process";

    public static final String URL_TX_SOURCES = URL_TX_BASE + "/sources/%s";

    public static final String URL_TX_HISTORY = URL_TX_BASE + "/history/%s/blocks/%s/%s";


	private CryptoService cryptoService;

	public TransactionRemoteServiceImpl() {
		super();
	}

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
        cryptoService = ServiceLocator.instance().getCryptoService();
	}

	public String transfert(Wallet wallet, String destPubKey, long amount,
							String comment) throws InsufficientCreditException {
		
		// http post /tx/process
		HttpPost httpPost = new HttpPost(
				getPath(wallet.getCurrencyId(), URL_TX_PROCESS));

		// compute transaction
		String transaction = getSignedTransaction(wallet, destPubKey, amount,
                comment);

		if (log.isDebugEnabled()) {
			log.debug(String.format(
                "Will send transaction document: \n------\n%s------",
                transaction));
		}

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("transaction", transaction));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
		} catch (UnsupportedEncodingException e) {
			throw new TechnicalException(e);
		}

		String selfResult = executeRequest(httpPost, String.class);
		log.info("received from /tx/process: " + selfResult);


        String fingerprint = DigestUtils.sha1Hex(transaction);
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"Fingerprint: %s",
					fingerprint));
		}
        return fingerprint;
	}

	public TxSource getSources(long currencyId, String pubKey) {
		if (log.isDebugEnabled()) {
			log.debug(String.format("Get sources by pubKey: %s", pubKey));
		}

		// get parameter
		String path = String.format(URL_TX_SOURCES, pubKey);
		TxSource result = executeRequest(currencyId, path, TxSource.class);

		return result;
	}

	public TxSource getSources(Peer peer, String pubKey) {
		if (log.isDebugEnabled()) {
			log.debug(String.format("Get sources by pubKey: %s from ", pubKey, peer.toString()));
		}

		// get parameter
		String path = String.format(URL_TX_SOURCES, pubKey);
		TxSource result = executeRequest(peer, path, TxSource.class);

		return result;
	}

    public long getCreditOrZero(long currencyId, String pubKey) {
        Long credit = getCredit(currencyId, pubKey);

        if (credit == null) {
            return 0;
        }
        return credit.longValue();
    }

    public Long getCredit(long currencyId, String pubKey) {
		if (log.isDebugEnabled()) {
			log.debug(String.format("Get credit by pubKey [%s] for currency [id=%s]", pubKey, currencyId));
		}

        // get parameter
        String path = String.format(URL_TX_SOURCES, pubKey);
        TxSource result = executeRequest(currencyId, path, TxSource.class);

        if (result == null) {
            return null;
        }

        // Compute the credit
        return computeCredit(result.getSources());
    }

    public Long getCredit(Peer peer, String pubKey) {
        if (log.isDebugEnabled()) {
			log.debug(String.format("Get credit by pubKey [%s] from peer [%s]", pubKey, peer.getUrl()));
		}

        // get parameter
        String path = String.format(URL_TX_SOURCES, pubKey);
        TxSource result = executeRequest(peer, path, TxSource.class);

        if (result == null) {
            return null;
        }

        // Compute the credit
        return computeCredit(result.getSources());
    }


    public long computeCredit(TxSource.Source[] sources) {
        if (CollectionUtils.isEmpty(sources)) {
            return 0;
        }

        long credit = 0;
        for (TxSource.Source source : sources) {
            credit += source.getAmount();
        }
        return credit;
    }

    public TxHistory getTxHistory(long currencyId, String pubKey, long fromBlockNumber, long toBlockNumber) {
        ObjectUtils.checkNotNull(pubKey);
        ObjectUtils.checkArgument(fromBlockNumber >= 0);
        ObjectUtils.checkArgument(fromBlockNumber <= toBlockNumber);

        if (log.isDebugEnabled()) {
			log.debug(String.format("Get TX history by pubKey [%s], from block [%s -> %s]", pubKey, fromBlockNumber, toBlockNumber));
		}

        // get parameter
        String path = String.format(URL_TX_HISTORY, pubKey, fromBlockNumber, toBlockNumber);
		TxHistory result = executeRequest(currencyId, path, TxHistory.class);

        return result;
    }

	/* -- internal methods -- */

	public String getSignedTransaction(Wallet wallet, String destPubKey,
			long amount, String comment) throws InsufficientCreditException {
        ObjectUtils.checkNotNull(wallet);
        ObjectUtils.checkArgument(StringUtils.isNotBlank(wallet.getCurrency()));
        ObjectUtils.checkArgument(StringUtils.isNotBlank(wallet.getPubKeyHash()));

		// Retrieve the wallet sources
		TxSource sourceResults = getSources(wallet.getCurrencyId(), wallet.getPubKeyHash());
		if (sourceResults == null) {
			throw new TechnicalException("Unable to load user sources.");
		}

		TxSource.Source[] sources = sourceResults.getSources();
		if (CollectionUtils.isEmpty(sources)) {
			throw new InsufficientCreditException(
					"Insufficient credit : no credit found.");
		}

		List<TxSource.Source> txInputs = new ArrayList<TxSource.Source>();
		List<TxOutput> txOutputs = new ArrayList<TxOutput>();
		computeTransactionInputsAndOuputs(wallet.getPubKeyHash(), destPubKey,
				sources, amount, txInputs, txOutputs);

		String transaction = getTransaction(wallet.getCurrency(),
				wallet.getPubKeyHash(), destPubKey, txInputs, txOutputs,
				comment);

		String signature = cryptoService.sign(transaction, wallet.getSecKey());

		return new StringBuilder().append(transaction).append(signature)
				.append('\n').toString();
	}

	public String getTransaction(String currency, String srcPubKey,
			String destPubKey, List<TxSource.Source> inputs, List<TxOutput> outputs,
			String comments) {

		StringBuilder sb = new StringBuilder();
		sb.append("Version: 1\n").append("Type: Transaction\n")
				.append("Currency: ").append(currency).append('\n')
				.append("Issuers:\n")
				// add issuer pubkey
				.append(srcPubKey).append('\n');

		// Inputs coins
		sb.append("Inputs:\n");
		for (TxSource.Source input : inputs) {
			// INDEX:SOURCE:NUMBER:FINGERPRINT:AMOUNT
			sb.append(0).append(':').append(input.getType()).append(':')
					.append(input.getNumber()).append(':')
					.append(input.getFingerprint()).append(':')
					.append(input.getAmount()).append('\n');
		}

		// Output
		sb.append("Outputs:\n");
		for (TxOutput output : outputs) {
			// ISSUERS:AMOUNT
			sb.append(output.getPubKey()).append(':')
					.append(output.getAmount()).append('\n');
		}

		// Comment
		sb.append("Comment: ").append(comments).append('\n');

		return sb.toString();
	}

	public String getCompactTransaction(String currency, String srcPubKey,
			String destPubKey, List<TxSource.Source> inputs, List<TxOutput> outputs,
			String comments) {

		boolean hasComment = comments != null && comments.length() > 0;
		StringBuilder sb = new StringBuilder();
		sb.append("TX:")
				// VERSION
				.append(PROTOCOL_VERSION).append(':')
				// NB_ISSUERS
				.append("1:")
				// NB_INPUTS
				.append(inputs.size()).append(':')
				// NB_OUTPUTS
				.append(outputs.size()).append(':')
				// HAS_COMMENT
				.append(hasComment ? 1 : 0).append('\n')
				// issuer pubkey
				.append(srcPubKey).append('\n');

		// Inputs coins
		for (TxSource.Source input : inputs) {
			// INDEX:SOURCE:NUMBER:FINGERPRINT:AMOUNT
			sb.append(0).append(':').append(input.getType()).append(':')
					.append(input.getNumber()).append(':')
					.append(input.getFingerprint()).append(':')
					.append(input.getAmount()).append('\n');
		}

		// Output
		for (TxOutput output : outputs) {
			// ISSUERS:AMOUNT
			sb.append(output.getPubKey()).append(':')
					.append(output.getAmount()).append('\n');
		}

		// Comment
		if (hasComment) {
		sb.append(comments).append('\n');
		}
		return sb.toString();
	}

	public void computeTransactionInputsAndOuputs(String srcPubKey,
			String destPubKey, TxSource.Source[] sources, long amount,
			List<TxSource.Source> inputs, List<TxOutput> outputs) throws InsufficientCreditException{

		long rest = amount;
		long restForHimSelf = 0;

		for (TxSource.Source source : sources) {
			long srcAmount = source.getAmount();
			inputs.add(source);
			if (srcAmount >= rest) {
				restForHimSelf = srcAmount - rest;
				rest = 0;
				break;
			}
			rest -= srcAmount;
		}

		if (rest > 0) {
			throw new InsufficientCreditException(String.format(
					"Insufficient credit. Need %s more units.", rest));
		}

		// outputs
		{
			TxOutput output = new TxOutput();
			output.setPubKey(destPubKey);
			output.setAmount(amount);
			outputs.add(output);
		}
		if (restForHimSelf > 0) {
			TxOutput output = new TxOutput();
			output.setPubKey(srcPubKey);
			output.setAmount(restForHimSelf);
			outputs.add(output);
		}
	}

}
