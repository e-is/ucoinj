package io.ucoin.ucoinj.core.client.service.bma;

import java.util.ArrayList;
import java.util.List;

import io.ucoin.ucoinj.core.client.model.bma.EndpointProtocol;
import io.ucoin.ucoinj.core.client.model.bma.NetworkPeering;
import io.ucoin.ucoinj.core.client.model.bma.NetworkPeers;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.util.ObjectUtils;
import io.ucoin.ucoinj.core.util.StringUtils;

/**
 * Created by eis on 05/02/15.
 */
public class NetworkRemoteServiceImpl extends BaseRemoteServiceImpl implements NetworkRemoteService{


    public static final String URL_BASE = "/network";

    public static final String URL_PEERING = URL_BASE + "/peering";

    public static final String URL_PEERS = URL_BASE + "/peers";

    public static final String URL_PEERING_PEERS = URL_PEERING + "/peers";

    public static final String URL_PEERING_PEERS_LEAF = URL_PEERING + "/peers?leaf=";

    public NetworkRemoteServiceImpl() {
        super();
    }

    public NetworkPeering getPeering(Peer peer) {
        NetworkPeering result = httpService.executeRequest(peer, URL_PEERING, NetworkPeering.class);
        return result;
    }

    @Override
    public List<Peer> getPeers(Peer peer) {
        return findPeers(peer, null, null, null, null);
    }

    @Override
    public List<Peer> findPeers(Peer peer, String status, EndpointProtocol endpointProtocol, Integer currentBlockNumber, String currentBlockHash) {
        ObjectUtils.checkNotNull(peer);

        List<Peer> result = new ArrayList<Peer>();

        NetworkPeers remoteResult = httpService.executeRequest(peer, URL_PEERS, NetworkPeers.class);

        for (NetworkPeers.Peer remotePeer: remoteResult.peers) {
            boolean match = (status == null || status.equalsIgnoreCase(remotePeer.status))
                    && (currentBlockNumber == null || currentBlockNumber.equals(parseBlockNumber(remotePeer)))
                    && (currentBlockHash == null || currentBlockHash.equals(parseBlockHash(remotePeer)));

            if (match) {

                for (NetworkPeering.Endpoint endpoint : remotePeer.endpoints) {

                    match = endpointProtocol == null || endpointProtocol == endpoint.protocol;

                    if (match) {
                        Peer childPeer = toPeer(endpoint);
                        if (childPeer != null) {
                            result.add(childPeer);
                        }
                    }

                }
            }
        }

        return result;
    }

    /* -- Internal methods -- */

    protected Peer toPeer(NetworkPeering.Endpoint source) {
        Peer target = new Peer();
        if (StringUtils.isNotBlank(source.ipv4)) {
            target.setHost(source.ipv4);
        } else if (StringUtils.isNotBlank(source.ipv6)) {
            target.setHost(source.ipv6);
        } else if (StringUtils.isNotBlank(source.url)) {
            target.setHost(source.url);
        } else {
            target = null;
        }
        if (target != null && source.port != null) {
            target.setPort(source.port);
        }
        return target;
    }

    protected Integer parseBlockNumber(NetworkPeers.Peer remotePeer) {
        ObjectUtils.checkNotNull(remotePeer);

        if (remotePeer.block == null) {
            return null;
        }
        int index = remotePeer.block.indexOf("-");
        if (index == -1) {
            return null;
        }

        String str = remotePeer.block.substring(0, index);
        try {
            return Integer.parseInt(str);
        } catch(NumberFormatException e) {
            return null;
        }
    }

    protected String parseBlockHash(NetworkPeers.Peer remotePeer) {
        ObjectUtils.checkNotNull(remotePeer);

        if (remotePeer.block == null) {
            return null;
        }
        int index = remotePeer.block.indexOf("-");
        if (index == -1) {
            return null;
        }

        String hash = remotePeer.block.substring(index+1);
        return hash;
    }
}
