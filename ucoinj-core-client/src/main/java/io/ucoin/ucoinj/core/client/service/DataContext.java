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
