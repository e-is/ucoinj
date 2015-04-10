package io.ucoin.client.core.model;

/*
 * #%L
 * UCoin Java Client :: Core API
 * %%
 * Copyright (C) 2014 - 2015 EIS
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

public class Peer implements Serializable {

    private Long id;
    private Long currencyId;
    private String host;
    private int port;
    private String url;

    public Peer() {
        // default constructor, need for de-serialization
    }

    public Peer(String host, int port) {
        this.host = host;
        this.port = port;
        this.url = initUrl(host, port);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUrl() {
        return url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public void setPort(int port) {
        this.port = port;
        this.url = initUrl(host, port);
    }

    public void setHost(String host) {
        this.host = host;
        this.url = initUrl(host, port);
    }

    public String toString() {
        return new StringBuilder().append("url=").append(url).append(",")
                .append("host=").append(host).append(",")
                .append("port=").append(port)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (id != null && o instanceof Peer) {
            return id.equals(((Peer)o).getId());
        }
        return super.equals(o);
    }

    /* -- Internal methods -- */

    protected String initUrl(String host, int port) {
        return String.format("http://%s:%s", host, port);
    }
}
