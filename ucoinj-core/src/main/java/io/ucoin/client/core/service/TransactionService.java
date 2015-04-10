package io.ucoin.client.core.service;

/*
 * #%L
 * UCoin Java Client :: Core API
 * %%
 * Copyright (C) 2014 - 2015 EIS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import io.ucoin.client.core.model.TxOutput;
import io.ucoin.client.core.model.TxSource;
import io.ucoin.client.core.model.TxSourceResults;
import io.ucoin.client.core.model.Wallet;
import io.ucoin.client.core.technical.UCoinTechnicalException;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TransactionService extends AbstractNetworkService {

	private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

	public CryptoService cryptoService;

	public TransactionService() {
		super();
	}

	@Override
	public void initialize() {
		cryptoService = ServiceLocator.instance().getCryptoService();
	}

	public void transfert(Wallet wallet, String destPubKey, long amount,
			String comments) throws Exception {
		// http post /tx/process
		HttpPost httpPost = new HttpPost(
				getAppendedPath(ProtocolUrls.TX_PROCESS));

		// compute transaction
		String transaction = getSignedTransaction(wallet, destPubKey, amount,
				comments);

		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"Will send transaction document: \n------\n%s------",
					transaction));
		}

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("transaction", transaction));

		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

		String selfResult = executeRequest(httpPost, String.class);
		log.info("received from /tx/process: " + selfResult);
	}

	public TxSourceResults getSources(String pubKey) {
		if (log.isDebugEnabled()) {
			log.debug(String.format("Get sources by pubKey: %s", pubKey));
		}

		// get parameter
		String path = String.format(ProtocolUrls.TX_SOURCES, pubKey);
		HttpGet httpGet = new HttpGet(getAppendedPath(path));
		TxSourceResults result = executeRequest(httpGet, TxSourceResults.class);

		// Compute the balance
		result.setBalance(computeBalance(result.getSources()));

		return result;

	}

	/* -- internal methods -- */

	public String getSignedTransaction(Wallet wallet, String destPubKey,
			long amount, String comments) {

		String transaction = getTransaction(wallet, destPubKey, amount,
				comments);
		String signature = cryptoService.sign(transaction, wallet.getSecKey());

		return new StringBuilder().append(transaction).append(signature)
				.append('\n').toString();
	}

	public String getTransaction(Wallet wallet, String destPubKey, long amount,
			String comments) {

		// Retrieve the wallet sources
		TxSourceResults sourceResults = getSources(wallet.getPubKeyHash());
		if (sourceResults == null) {
			throw new UCoinTechnicalException("Unable to load user sources.");
		}

		List<TxSource> sources = sourceResults.getSources();
		if (sources == null || sources.isEmpty()) {
			throw new InsufficientCredit(
					"Insufficient credit : no credit found.");
		}

		List<TxSource> txInputs = new ArrayList<TxSource>();
		List<TxOutput> txOutputs = new ArrayList<TxOutput>();
		computeInputsAndOuputs(wallet.getPubKeyHash(), destPubKey, sources,
				amount, txInputs, txOutputs);

		return getTransaction(wallet.getCurrency(), wallet.getPubKeyHash(),
				destPubKey, txInputs, txOutputs, comments);
	}

	public String getTransaction(String currency, String srcPubKey,
			String destPubKey, List<TxSource> inputs, List<TxOutput> outputs,
			String comments) {

		StringBuilder sb = new StringBuilder();
		sb.append("Version: 1\n").append("Type: Transaction\n")
				.append("Currency: ").append(currency).append('\n')
				.append("Issuers:\n")
				// add issuer pubkey
				.append(srcPubKey).append('\n');

		// Inputs coins
		sb.append("Inputs:\n");
		for (TxSource input : inputs) {
			// INDEX:SOURCE:NUMBER:FINGERPRINT:AMOUNT
			sb.append(0).append(':').append(input.getType()).append(':')
					.append(input.getNumber()).append(':')
					.append(input.getFingerprint()).append(':')
					.append(input.getAmount()).append('\n');
		}

		// Output
		sb.append("Outputs:\n");
		for (TxOutput output : outputs) {
			// PUBLIC_KEY:AMOUNT
			sb.append(output.getPubKey()).append(':')
					.append(output.getAmount()).append('\n');
		}

		// Comment
		sb.append("Comment: ").append(comments).append('\n');

		return sb.toString();
	}

	public void computeInputsAndOuputs(String srcPubKey, String destPubKey,
			List<TxSource> sources, long amount, List<TxSource> inputs,
			List<TxOutput> outputs) {

		long rest = amount;
		long restForHimSelf = 0;

		for (TxSource source : sources) {
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
			throw new InsufficientCredit(String.format(
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

	protected long computeBalance(List<TxSource> sources) {
		if (sources == null) {
			return 0;
		}

		long balance = 0;
		for (TxSource source : sources) {
			balance += source.getAmount();
		}
		return balance;
	}
}
