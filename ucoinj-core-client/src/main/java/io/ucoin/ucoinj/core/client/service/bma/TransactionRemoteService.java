package io.ucoin.ucoinj.core.client.service.bma;


import io.ucoin.ucoinj.core.beans.Service;
import io.ucoin.ucoinj.core.client.model.bma.TxHistory;
import io.ucoin.ucoinj.core.client.model.bma.TxSource;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.model.local.Wallet;
import io.ucoin.ucoinj.core.client.service.exception.InsufficientCreditException;


public interface TransactionRemoteService extends Service {

	String transfert(Wallet wallet, String destPubKey, long amount,
							String comment) throws InsufficientCreditException;

	TxSource getSources(long currencyId, String pubKey);

    TxSource getSources(Peer peer, String pubKey);

    long getCreditOrZero(long currencyId, String pubKey);

    Long getCredit(long currencyId, String pubKey);

    Long getCredit(Peer peer, String pubKey);

    long computeCredit(TxSource.Source[] sources);

    TxHistory getTxHistory(long currencyId, String pubKey, long fromBlockNumber, long toBlockNumber);
}
