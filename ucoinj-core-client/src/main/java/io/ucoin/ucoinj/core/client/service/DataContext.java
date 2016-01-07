package io.ucoin.ucoinj.core.client.service;

import io.ucoin.ucoinj.core.beans.Bean;

import java.io.Closeable;
import java.io.IOException;

/**
 * Hold some contextual data, such as account id
 * Created by blavenie on 29/12/15.
 */
public class DataContext implements Bean, Closeable{

    private long accountId = -1;

    public DataContext() {

    }

    @Override
    public void close() throws IOException {
        clear();
    }

    public void clear() {
        accountId = -1;
    }

    /* -- getter/setter-- */

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    /* -- protected methods -- */
}
