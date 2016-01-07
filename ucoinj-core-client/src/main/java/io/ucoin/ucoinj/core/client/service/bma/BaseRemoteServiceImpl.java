package io.ucoin.ucoinj.core.client.service.bma;

import io.ucoin.ucoinj.core.beans.InitializingBean;
import io.ucoin.ucoinj.core.beans.Service;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.service.HttpService;
import io.ucoin.ucoinj.core.client.service.local.PeerService;
import io.ucoin.ucoinj.core.client.service.ServiceLocator;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

/**
 * Created by eis on 05/02/15.
 */
public abstract class BaseRemoteServiceImpl implements Service, InitializingBean{

    protected HttpService httpService;
    protected PeerService peerService;

    public static final String PROTOCOL_VERSION = "1";

    @Override
    public void afterPropertiesSet() {
        httpService = ServiceLocator.instance().getHttpService();
        peerService = ServiceLocator.instance().getPeerService();
    }

    @Override
    public void close() throws IOException {
        httpService = null;
        peerService = null;
    }

    public <T> T executeRequest(Peer peer, String absolutePath, Class<? extends T> resultClass)  {
        return httpService.executeRequest(peer, absolutePath, resultClass);
    }

    public <T> T executeRequest(long currencyId, String absolutePath, Class<? extends T> resultClass)  {
        Peer peer = peerService.getActivePeerByCurrencyId(currencyId);
        return httpService.executeRequest(peer, absolutePath, resultClass);
    }

    public <T> T executeRequest(HttpUriRequest request, Class<? extends T> resultClass)  {
        return httpService.executeRequest(request, resultClass);
    }

    public String getPath(long currencyId, String aPath) {
        Peer peer = peerService.getActivePeerByCurrencyId(currencyId);
        return httpService.getPath(peer, aPath);
    }

    public String getPath(Peer peer, String aPath) {
        return httpService.getPath(peer, aPath);
    }
}
