package io.ucoin.ucoinj.core.client.model.bma;

import java.io.Serializable;

/**
 * Created by eis on 05/02/15.
 */
public class NetworkPeers implements Serializable {

    public Peer[] peers;

    public String toString() {
        String s = "";
        for(Peer peer : peers) {
            s += peer.toString() + "\n";
        }
        return s;
    }

    public static class Peer implements Serializable {
        public String version;
        public String currency;
        public String status;
        public String block;
        public String signature;
        public String pubkey;
        public NetworkPeering.Endpoint[] endpoints;

        @Override
        public String toString() {
            String s = "version=" + version + "\n" +
                    "currency=" + currency + "\n" +
                    "pubkey=" + pubkey + "\n" +
                    "status=" + status + "\n" +
                    "block=" + block + "\n";
            for(NetworkPeering.Endpoint endpoint: endpoints) {
                s += endpoint.toString() + "\n";
            }
            return s;
        }
    }
}
