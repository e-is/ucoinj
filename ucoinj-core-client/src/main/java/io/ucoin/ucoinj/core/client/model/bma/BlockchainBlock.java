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

/**
 * A block from the blockchain.
 * 
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 */
public class BlockchainBlock implements Serializable {

    private static final long serialVersionUID = -5598140972293452669L;
    
    private String version;
    private Integer nonce;
    private Integer powMin;
    private Integer number;
	private Integer time;
    private Integer medianTime;
    private Integer membersCount;
    private Long monetaryMass;
    private String currency;
    private String issuer;
    private String signature;
    private String hash;
    private String parameters;
    private String previousHash;
    private String previousIssuer;
    private Integer dividend;
    private String[] membersChanges;
    private Identity[] identities;
    private Joiner[] joiners;
    private String[] actives;
    private String[] leavers;
    private String[] excluded;
    private String[] certifications;
//            private int actives": [],
//            private int transactions": [],

//  raw": "Version: 1\nType: Block\nCurrency: zeta_brouzouf\nNonce: 8233\nNumber: 1\nDate: 1416589860\nConfirmedDate: 1416589860\nIssuer: HnFcSms8jzwngtVomTTnzudZx7SHUQY8sVE1y8yBmULk\nPreviousHash: 00006CD96A01378465318E48310118AC6B2F3625\nPreviousIssuer: HnFcSms8jzwngtVomTTnzudZx7SHUQY8sVE1y8yBmULk\nMembersCount: 4\nIdentities:\nJoiners:\nActives:\nLeavers:\nExcluded:\nCertifications:\nTransactions:\n"
    //private String raw;

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public Integer getNonce() {
        return nonce;
    }
    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }

    public Integer getPowMin() {
        return powMin;
    }

    public void setPowMin(Integer powMin) {
        this.powMin = powMin;
    }

    public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
    public Integer getTime() {
        return time;
    }
    public void setTime(Integer time) {
        this.time = time;
    }
    public Integer getMedianTime() {
        return medianTime;
    }
    public void setMedianTime(Integer medianTime) {
        this.medianTime = medianTime;
    }
    public Integer getMembersCount() {
        return membersCount;
    }
    public void setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
    }

    public Long getMonetaryMass() {
        return monetaryMass;
    }

    public void setMonetaryMass(Long monetaryMass) {
        this.monetaryMass = monetaryMass;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getIssuer() {
        return issuer;
    }
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }
    public String getHash() {
        return hash;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }
    public String getParameters() {
        return parameters;
    }
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
    public String getPreviousHash() {
        return previousHash;
    }
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    public String getPreviousIssuer() {
        return previousIssuer;
    }
    public void setPreviousIssuer(String previousIssuer) {
        this.previousIssuer = previousIssuer;
    }
    public Integer getDividend() {
        return dividend;
    }
    public void setDividend(Integer dividend) {
        this.dividend = dividend;
    }
    public Identity[] getIdentities() {
        return identities;
    }
    public void setIdentities(Identity[] identities) {
        this.identities = identities;
    }
    public Joiner[] getJoiners() {
        return joiners;
    }
    public void setJoiners(Joiner[] joiners) {
        this.joiners = joiners;
    }

    public String toString() {
        String s = "version=" + version;
        s += "\nnonce=" + nonce;
        s += "\nnumber=" + number;
        s += "\npowMin" + powMin;
        s += "\ntime=" + time;
        s += "\nmedianTime=" + medianTime;
        s += "\nmembersCount=" + membersCount;
        s += "\nmonetaryMass=" + monetaryMass;
        s += "\ncurrency=" + currency;
        s += "\nissuer=" + issuer;
        s += "\nsignature=" + signature;
        s += "\nhash=" + hash;
        s += "\nparameters=" + parameters;
        s += "\npreviousHash=" + previousHash;
        s += "\npreviousIssuer=" + previousIssuer;
        s += "\ndividend=" + dividend;
        s += "\nmembersChanges:";
        if (membersChanges != null) {
            for (String m : membersChanges) {
                s += "\n\t" + m;
            }
        }
        s += "\nidentities:";
        if (identities != null) {
            for (Identity i : identities) {
                s += "\n\t" + i.toString();
            }
        }
        s += "\njoiners:";
        if (joiners != null) {
            for (Joiner j : joiners) {
                s += "\n\t" + j.toString();
            }
        }
        s += "\nleavers:";
        if (leavers != null) {
            for (String l : leavers) {
                s += "\n\t" + l;
            }
        }
        s += "\nexcluded:";
        if (excluded != null) {
            for (String e : excluded) {
                s += "\n\t" + e;
            }
        }
        s += "\ncertifications:";
        if (certifications != null) {
            for (String c : certifications) {
                s += "\n\t" + c;
            }
        }

        return s;
    }

    public static class Identity implements Serializable {

        private static final long serialVersionUID = 8080689271400316984L;

        private String pubkey;

        private String signature;

        private String uid;

        private long timestamp = -1;

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

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder()
                    .append(":").append(pubkey)
                    .append(":").append(signature)
                    .append(":").append(timestamp)
                    .append("").append(uid);

            return sb.toString();
        }
    }

    public static class Joiner extends Identity {

        private static final long serialVersionUID = 8448049949323699700L;
        private String pubkey;

        private String signature;

        private String uid;

        private long timestamp = -1;

        private String number;

        private String hash;

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

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder()
                    .append(":").append(pubkey)
                    .append(":").append(signature)
                    .append(":").append(number)
                    .append(":").append(hash)
                    .append(":").append(timestamp)
                    .append(":").append(uid);

            return sb.toString();
        }
    }
}
