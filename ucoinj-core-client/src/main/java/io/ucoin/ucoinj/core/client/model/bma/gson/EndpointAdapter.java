package io.ucoin.ucoinj.core.client.model.bma.gson;

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

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.ucoin.ucoinj.core.client.model.bma.EndpointProtocol;
import io.ucoin.ucoinj.core.client.model.bma.NetworkPeering;
import org.apache.http.conn.util.InetAddressUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class EndpointAdapter extends TypeAdapter<NetworkPeering.Endpoint> {

        @Override
        public NetworkPeering.Endpoint read(JsonReader reader) throws IOException {
            if (reader.peek() == com.google.gson.stream.JsonToken.NULL) {
                reader.nextNull();
                return null;
            }

            String ept = reader.nextString();
            ArrayList<String> parts = new ArrayList<>(Arrays.asList(ept.split(" ")));
            NetworkPeering.Endpoint endpoint = new NetworkPeering.Endpoint();
            endpoint.port = Integer.parseInt(parts.remove(parts.size() - 1));
            for (String word : parts) {
                if (InetAddressUtils.isIPv4Address(word)) {
                    endpoint.ipv4 = word;
                } else if (InetAddressUtils.isIPv6Address(word)) {
                    endpoint.ipv6 = word;
                } else if (word.startsWith("http")) {
                    endpoint.url = word;
                } else {
                    try {
                        endpoint.protocol = EndpointProtocol.valueOf(word);
                    } catch (IllegalArgumentException e) {
                        // skip this part
                    }
                }
            }

            if (endpoint.protocol == null) {
                endpoint.protocol = EndpointProtocol.UNDEFINED;
            }

            return endpoint;
        }

        public void write(JsonWriter writer, NetworkPeering.Endpoint endpoint) throws IOException {
            if (endpoint == null) {
                writer.nullValue();
                return;
            }
            writer.value(endpoint.protocol.name() + " " +
                    endpoint.url + " " +
                    endpoint.ipv4 + " " +
                    endpoint.ipv6 + " " +
                    endpoint.port);
        }
    }