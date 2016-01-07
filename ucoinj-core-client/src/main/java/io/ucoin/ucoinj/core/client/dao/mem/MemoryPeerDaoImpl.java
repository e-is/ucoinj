package io.ucoin.ucoinj.core.client.dao.mem;

import io.ucoin.ucoinj.core.client.dao.PeerDao;
import io.ucoin.ucoinj.core.client.model.bma.NetworkPeers;
import io.ucoin.ucoinj.core.client.model.local.Peer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by blavenie on 29/12/15.
 */
public class MemoryPeerDaoImpl implements PeerDao {

    private Map<Long, Peer> peersByCurrencyId = new HashMap<>();

    @Override
    public Peer create(Peer entity) {
        long id = getMaxId() + 1;
        entity.setId(id);

        peersByCurrencyId.put(id, entity);

        return entity;
    }

    @Override
    public Peer update(Peer entity) {
        peersByCurrencyId.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Peer getById(long id) {
        return peersByCurrencyId.get(id);
    }

    @Override
    public void remove(Peer entity) {
        peersByCurrencyId.remove(entity.getId());
    }

    @Override
    public List<Peer> getPeersByCurrencyId(long currencyId) {

        List<Peer> result = new ArrayList<>();

        for(Peer peer: peersByCurrencyId.values()) {
            if (peer.getCurrencyId() == currencyId) {
                result.add(peer);
            }
        }

        return result;
    }

    /* -- internal methods -- */

    protected long getMaxId() {
        long currencyId = -1;
        for (Long anId : peersByCurrencyId.keySet()) {
            if (anId > currencyId) {
                currencyId = anId;
            }
        }

        return currencyId;
    }
}
