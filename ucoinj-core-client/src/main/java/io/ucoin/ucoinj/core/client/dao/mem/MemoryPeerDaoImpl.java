package io.ucoin.ucoinj.core.client.dao.mem;

/*
 * #%L
 * UCoin Java :: Core Client API
 * %%
 * Copyright (C) 2014 - 2016 EIS
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
