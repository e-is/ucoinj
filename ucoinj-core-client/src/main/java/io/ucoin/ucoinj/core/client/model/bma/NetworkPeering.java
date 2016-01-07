package io.ucoin.ucoinj.core.client.model.bma;

import java.io.Serializable;

/**
 * Created by eis on 05/02/15.
 */
public class NetworkPeering implements Serializable {
    private String version;
    private String currency;
    private String status;
    private String block;
    private String signature;
    // not need : private String raw
    private String pubkey;

    public Endpoint[] endpoints;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public String toString() {
        String s = "currency=" + currency + "\n" +
                "pubkey=" + pubkey + "\n" +
                "signature=" + signature + "\n" +
                "block=" + block + "\n";
        for(Endpoint endpoint : endpoints) {
            s += endpoint.toString() + "\n";
        }
        return s;

    }

    public static class Endpoint implements Serializable {
        public EndpointProtocol protocol;
        public String url;
        public String ipv4;
        public String ipv6;
        public Integer port;

        @Override
        public String toString() {
            String s = "protocol=" + protocol.name() + "\n" +
                    "url=" + url + "\n" +
                    "ipv4=" + ipv4 + "\n" +
                    "ipv6=" + ipv6 + "\n" +
                    "port=" + port + "\n";
            return s;
        }
    }


}
