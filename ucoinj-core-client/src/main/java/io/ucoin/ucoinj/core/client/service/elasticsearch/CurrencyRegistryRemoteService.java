package io.ucoin.ucoinj.core.client.service.elasticsearch;

/*
 * #%L
 * UCoin Java Client :: ElasticSearch Indexer
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
import io.ucoin.ucoinj.core.client.model.elasticsearch.Currency;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.model.local.Wallet;

import java.util.List;

/**
 * Created by Benoit on 06/05/2015.
 */
public interface CurrencyRegistryRemoteService extends Service {

    /**
     * Test if elasticsearch node defined in config is alive
     * @return
     */
    boolean isNodeAlive();

    /**
     * Test if elasticsearch node from the given endpoint is alive
     * @return
     */
    boolean isNodeAlive(Peer peer);

    List<String> getAllCurrencyNames();

    void registerNewCurrency(Wallet wallet, Currency currency);

    void registerNewCurrency(String pubkey, String jsonCurrency, String signature);
}
