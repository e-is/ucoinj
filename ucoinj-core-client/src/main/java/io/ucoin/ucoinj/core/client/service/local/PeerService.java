package io.ucoin.ucoinj.core.client.service.local;

import io.ucoin.ucoinj.core.beans.Service;
import io.ucoin.ucoinj.core.client.model.local.Peer;

import java.util.List;

/**
 * Created by eis on 07/02/15.
 */
public interface PeerService extends Service {

    Peer save(final Peer peer);

    Peer getPeerById(long peerId);

    /**
     * Return a (cached) active peer, by currency id
     * @param currencyId
     * @return
     */
    Peer getActivePeerByCurrencyId(long currencyId);

    /**
     * Return a (cached) peer list, by currency id
     * @param currencyId
     * @return
     */
    List<Peer> getPeersByCurrencyId(long currencyId);

    /**
     * Fill all cache need for currencies
     * @param context
     * @param accountId
     */
    void loadCache(long accountId);
}
