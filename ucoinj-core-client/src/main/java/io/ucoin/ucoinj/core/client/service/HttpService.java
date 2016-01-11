package io.ucoin.ucoinj.core.client.service;

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
