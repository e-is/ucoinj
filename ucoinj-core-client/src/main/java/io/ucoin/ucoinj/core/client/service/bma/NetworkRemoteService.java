package io.ucoin.ucoinj.core.client.service.bma;

import io.ucoin.ucoinj.core.beans.Service;
import io.ucoin.ucoinj.core.client.model.bma.EndpointProtocol;
import io.ucoin.ucoinj.core.client.model.bma.NetworkPeering;
import io.ucoin.ucoinj.core.client.model.local.Peer;

import java.util.List;

/**
 * Created by eis on 05/02/15.
 */
public interface NetworkRemoteService extends Service {

    NetworkPeering getPeering(Peer peer);

    List<Peer> getPeers(Peer peer);

    List<Peer> findPeers(Peer peer, String status, EndpointProtocol endpointProtocol, Integer currentBlockNumber, String currentBlockHash);
}
