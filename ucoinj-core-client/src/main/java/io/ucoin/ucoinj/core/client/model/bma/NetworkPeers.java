package io.ucoin.ucoinj.core.client.model.bma;

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
