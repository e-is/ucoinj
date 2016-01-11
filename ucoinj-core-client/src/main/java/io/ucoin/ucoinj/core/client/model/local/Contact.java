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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A wallet is a user account
 * Created by eis on 13/01/15.
 */
public class Contact implements LocalEntity, Serializable {

    private long id;
    private long accountId;
    private String name;
    private long phoneContactId = 0;
    private List<Identity> identities = new ArrayList<Identity>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Identity> getIdentities() {
        return identities;
    }

    public void setIdentities(List<Identity> identities) {
        this.identities = identities;
    }

    public void addIdentity(Identity identity) {
        this.identities.add(identity);
    }

    public Long getPhoneContactId() {
        return phoneContactId;
    }

    public void setPhoneContactId(Long phoneContactId) {
        this.phoneContactId = phoneContactId;
    }

    @Override
    public String toString() {
        return name;
    }

    public void copy(Contact contact) {
        this.id = contact.id;
        this.accountId = contact.accountId;
        this.name = contact.name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof  Contact) {
            Contact bi = (Contact)o;
            return  ObjectUtils.equals(this.id, bi.id)
                    && ObjectUtils.equals(this.accountId, bi.accountId)
                    && ObjectUtils.equals(this.name, bi.name);
        }
        return false;
    }

    public boolean hasIdentityForCurrency(long currencyId) {
        for(Identity identity:identities) {
            if (identity.getCurrencyId() != null && identity.getCurrencyId().longValue() == currencyId) {
                return true;
            }
        }
        return false;
    }
}
