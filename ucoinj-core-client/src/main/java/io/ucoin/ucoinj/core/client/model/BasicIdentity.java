package io.ucoin.ucoinj.core.client.model;

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

/**
 * Basic information on a identity.
 * 
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 *
 */
public class BasicIdentity implements Serializable {

    private static final long serialVersionUID = 8080689271400316984L;

    private String pubkey;

    private String signature;

    private String uid;

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSelf() {
        return signature;
    }

    public void setSelf(String signature) {
        this.signature = signature;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
        .append("uid=").append(uid)
        .append(",pubkey=").append(pubkey)
        .append(",signature").append(signature);

        return sb.toString();
    }
}
