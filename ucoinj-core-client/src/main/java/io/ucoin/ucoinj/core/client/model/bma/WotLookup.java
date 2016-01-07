package io.ucoin.ucoinj.core.client.model.bma;

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

public class WotLookup {

    public boolean partial;
    public Result[] results;

    public boolean isPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
    }

    public Result[] getResults() {
        return results;
    }

    public void setResults(Result[] results) {
        this.results = results;
    }

    public String toString() {
        String s = "";
        for (Result result : results) {
            s = "pubkey=" + result.pubkey;
            for (Uid uid : result.uids) {
                s += "\nuid=" + uid.uid;
                s += "\ntimestamp=" + uid.meta.timestamp;
                s += "self=" + uid.self;
            }
        }
        return s;
    }

    public static class Result implements Serializable {

        private static final long serialVersionUID = -39452685440482106L;

        public String pubkey;
        public Uid[] uids;
        public SignedSignature[] signed;

        public String getPubkey() {
            return pubkey;
        }

        public void setPubkey(String pubkey) {
            this.pubkey = pubkey;
        }

        public Uid[] getUids() {
            return uids;
        }

        public void setUids(Uid[] uids) {
            this.uids = uids;
        }

        public SignedSignature[] getSigned() {
            return signed;
        }

        public void setSigned(SignedSignature[] signed) {
            this.signed = signed;
        }
    }

    public class Uid {

        public String uid;
        public Meta meta;
        public String self;
        public OtherSignature[] others;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public Meta getMeta() {
            return meta;
        }

        public void setMeta(Meta meta) {
            this.meta = meta;
        }

        public String getSelf() {
            return self;
        }

        public void setSelf(String self) {
            this.self = self;
        }

        public OtherSignature[] getOthers() {
            return others;
        }

        public void setOthers(OtherSignature[] others) {
            this.others = others;
        }
    }


    public class Meta implements Serializable {
        public Long timestamp;
        public Long block_number;
    }

    public class OtherSignature {

        public String pubkey;
        public Meta meta;
        public String signature;
        public String[] uids;
        public boolean isMember;
        public boolean wasMember;

        public String getPubkey() {
            return pubkey;
        }

        public void setPubkey(String pubkey) {
            this.pubkey = pubkey;
        }

        public Meta getMeta() {
            return meta;
        }

        public void setMeta(Meta meta) {
            this.meta = meta;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String[] getUids() {
            return uids;
        }

        public void setUids(String[] uids) {
            this.uids = uids;
        }

        public boolean isMember() {
            return isMember;
        }

        public void setMember(boolean member) {
            isMember = member;
        }

        public boolean isWasMember() {
            return wasMember;
        }

        public void setWasMember(boolean wasMember) {
            this.wasMember = wasMember;
        }
    }

    public class SignedSignature {

        public String uid;
        public String pubkey;
        public Meta meta;
        public String signature;
        public boolean isMember;
        public boolean wasMember;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getPubkey() {
            return pubkey;
        }

        public void setPubkey(String pubkey) {
            this.pubkey = pubkey;
        }

        public Meta getMeta() {
            return meta;
        }

        public void setMeta(Meta meta) {
            this.meta = meta;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public boolean isMember() {
            return isMember;
        }

        public void setMember(boolean member) {
            isMember = member;
        }

        public boolean isWasMember() {
            return wasMember;
        }

        public void setWasMember(boolean wasMember) {
            this.wasMember = wasMember;
        }
    }

}
