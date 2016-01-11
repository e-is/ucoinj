package io.ucoin.ucoinj.core.client.model.local;

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


import io.ucoin.ucoinj.core.util.ObjectUtils;

/**
 * A certification, return by <code>/wot/certified-by/[uid]</code> or <code>/wot/certifiers-of/[uid]</code>
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 *
 */
public class Certification {

    private static final long serialVersionUID = 2204517069552693026L;

    private long currencyId;

    private String uid;

    private String pubkey;

    private long timestamp;

    /**
     * Give the other side certicication
     * (not in protocol: fill by the service)
     */
    private Certification otherEnd;


    /**
     * Indicate whether the certification is valid for membership request.
     * (not in protocol: fill by the service)
     */
    private boolean valid = false;

    /**
     * Given the certification side. If true, certified-by,
     * if false, certifier of
     */
    private boolean isCertifiedBy;

    private boolean isMember;

    public Certification() {
        super();
    }

    public long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(long currencyId) {
        this.currencyId = currencyId;
    }

    public Certification getOtherEnd() {
        return otherEnd;
    }

    public void setOtherEnd(Certification otherEnd) {
        this.otherEnd = otherEnd;
    }

    public boolean isCertifiedBy() {
        return isCertifiedBy;
    }

    public void setCertifiedBy(boolean isCertifiedBy) {
        this.isCertifiedBy = isCertifiedBy;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long certTime) {
        this.timestamp = timestamp;
    }

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

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (o instanceof Certification) {
            Certification wc = (Certification)o;
            return ObjectUtils.equals(timestamp, wc.timestamp);
        }
        return false;
    }
}
