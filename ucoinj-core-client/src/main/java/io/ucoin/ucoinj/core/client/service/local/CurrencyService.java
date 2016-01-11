package io.ucoin.ucoinj.core.client.service.local;

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

import io.ucoin.ucoinj.core.beans.Service;
import io.ucoin.ucoinj.core.client.model.local.Currency;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by eis on 07/02/15.
 */
public interface CurrencyService extends Service {

    Currency save(final Currency currency);

    List<Currency> getCurrencies(long accountId);

    Currency getCurrencyById(long currencyId);

    /**
     * Return a (cached) currency name, by id
     * @param currencyId
     * @return
     */
    String getCurrencyNameById(long currencyId);

    /**
     * Return a currency id, by name
     * @param currencyName
     * @return
     */
    Long getCurrencyIdByName(String currencyName);

    /**
     * Return a (cached) list of currency ids
     * @return
     */
    Set<Long> getCurrencyIds();

    /**
     * Return a (cached) number of registered currencies
     * @return
     */
    int getCurrencyCount();

    /**
     * Fill all cache need for currencies
     * @param context
     */
    void loadCache(long accountId);

    /**
     * Return the value of the last universal dividend
     * @param currencyId
     * @return
     */
    long getLastUD(long currencyId);

    /**
     * Return a map of UD (key=blockNumber, value=amount)
     * @return
     */
    Map<Integer, Long> refreshAndGetUD(long currencyId, long lastSyncBlockNumber);

    /**
     * Return a map of UD (key=blockNumber, value=amount)
     * @return
     */
     Map<Integer, Long> getAllUD(long currencyId);
}
