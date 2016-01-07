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


import io.ucoin.ucoinj.core.client.model.local.Peer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eis on 05/02/15.
 */
public class Currency implements Serializable {

    private List<Peer> peers = new ArrayList<Peer>();

    private Long id;
    private String currencyName;
    private Integer membersCount;
    private String firstBlockSignature;
    private Account account;
    private Long accountId;
    private String[] tags;
    private String senderPubkey;
    private Long lastUD;

    public Currency() {
    }

    public Currency(String currencyName,
                    String firstBlockSignature,
                    int membersCount,
                    List<Peer> peers) {
        this.currencyName = currencyName;
        this.firstBlockSignature = firstBlockSignature;
        this.membersCount = membersCount;
        this.peers = peers;
    }

    public Currency(String currencyName,
                    String firstBlockSignature,
                    List<Peer> peers) {
        this.currencyName = currencyName;
        this.firstBlockSignature = firstBlockSignature;
        this.membersCount = null;
        this.peers = peers;
    }

    public Long getId() {
        return id;
    }

    public String getCurrencyName()
    {
        return currencyName;
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public String getFirstBlockSignature() {
        return firstBlockSignature;
    }

    public List<Peer> getPeers() {
        return peers;
    }

    public void addPeer(Peer peer) {
        this.peers.add(peer);
    }

    public void setPeers(List<Peer> peers) {
        this.peers = peers;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public void setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
    }

    public void setFirstBlockSignature(String firstBlockSignature) {
        this.firstBlockSignature = firstBlockSignature;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String toString() {
        return currencyName;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getSenderPubkey() {
        return senderPubkey;
    }

    public void setSenderPubkey(String senderPubkey) {
        this.senderPubkey = senderPubkey;
    }
}