package io.ucoin.ucoinj.core.client.dao;

import io.ucoin.ucoinj.core.client.model.local.Peer;

import java.util.List;

/**
 * Created by blavenie on 29/12/15.
 */
public interface PeerDao extends EntityDao<Peer> {

    List<Peer> getPeersByCurrencyId(long currencyId);
}
