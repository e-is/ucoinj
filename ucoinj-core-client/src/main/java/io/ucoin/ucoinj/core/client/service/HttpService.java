package io.ucoin.ucoinj.core.client.service;

import io.ucoin.ucoinj.core.beans.Service;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.service.exception.PeerConnectionException;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Created by blavenie on 29/12/15.
 */
public interface HttpService extends Service {

    void connect(Peer peer) throws PeerConnectionException;

    boolean isConnected();

    <T> T executeRequest(HttpUriRequest request, Class<? extends T> resultClass) ;

    <T> T executeRequest(String absolutePath, Class<? extends T> resultClass) ;

    <T> T executeRequest(Peer peer, String absolutePath, Class<? extends T> resultClass);

    String getPath(Peer peer, String absolutePath);

    String getPath(String absolutePath);
}
