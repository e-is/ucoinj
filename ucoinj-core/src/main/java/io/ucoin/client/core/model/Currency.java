package io.ucoin.client.core.model;

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