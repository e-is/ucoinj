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

import io.ucoin.ucoinj.core.beans.InitializingBean;
import io.ucoin.ucoinj.core.client.dao.CurrencyDao;
import io.ucoin.ucoinj.core.client.model.local.Currency;
import io.ucoin.ucoinj.core.client.service.ServiceLocator;
import io.ucoin.ucoinj.core.client.service.bma.BlockchainRemoteService;
import io.ucoin.ucoinj.core.util.ObjectUtils;
import io.ucoin.ucoinj.core.util.StringUtils;
import io.ucoin.ucoinj.core.util.cache.Cache;
import io.ucoin.ucoinj.core.util.cache.SimpleCache;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by eis on 07/02/15.
 */
public class CurrencyServiceImpl implements CurrencyService, InitializingBean {


    private static final long UD_CACHE_TIME_MILLIS = 5 * 60 * 1000; // = 5 min

    private Cache<Long, Currency> mCurrencyCache;
    private Cache<Long, Long> mUDCache;

    private BlockchainRemoteService blockchainRemoteService;
    private CurrencyDao currencyDao;

    public CurrencyServiceImpl() {
        super();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        blockchainRemoteService = ServiceLocator.instance().getBlockchainRemoteService();
        currencyDao = ServiceLocator.instance().getBean(CurrencyDao.class);

        // Load cache from account
        long accountId = ServiceLocator.instance().getDataContext().getAccountId();
        if (accountId != -1) {
            loadCache(accountId);
        }
    }

    @Override
    public void close() throws IOException {
        currencyDao = null;
        blockchainRemoteService = null;
    }

    public Currency save(final Currency currency) {
        ObjectUtils.checkNotNull(currency);
        ObjectUtils.checkArgument(StringUtils.isNotBlank(currency.getCurrencyName()));
        ObjectUtils.checkArgument(StringUtils.isNotBlank(currency.getFirstBlockSignature()));
        ObjectUtils.checkNotNull(currency.getMembersCount());
        ObjectUtils.checkArgument(currency.getMembersCount().intValue() >= 0);
        ObjectUtils.checkNotNull(currency.getLastUD());
        ObjectUtils.checkArgument(currency.getLastUD().longValue() > 0);

        ObjectUtils.checkArgument((currency.getAccount() != null && currency.getAccount().getId() != null)
            || currency.getAccountId() != null, "One of 'currency.account.id' or 'currency.accountId' is mandatory.");

        Currency result;

        // Create
        if (currency.getId() == null) {
            result = currencyDao.create(currency);

            // Update the cache (if already initialized)
            if (mCurrencyCache != null) {
                mCurrencyCache.put(currency.getId(), currency);
            }
        }

        // or update
        else {
            currencyDao.update(currency);

            result = currency;
        }

        return result;
    }

    public List<Currency> getCurrencies(long accountId) {
        return currencyDao.getCurrencies(accountId);
    }


    public Currency getCurrencyById(long currencyId) {
        return mCurrencyCache.get(currencyId);
    }

    /**
     * Return a (cached) currency name, by id
     * @param currencyId
     * @return
     */
    public String getCurrencyNameById(long currencyId) {
        Currency currency = mCurrencyCache.getIfPresent(currencyId);
        if (currency == null) {
            return null;
        }
        return currency.getCurrencyName();
    }

    /**
     * Return a currency id, by name
     * @param currencyName
     * @return
     */
    public Long getCurrencyIdByName(String currencyName) {
        ObjectUtils.checkArgument(StringUtils.isNotBlank(currencyName));

        // Search from currencies
        for (Map.Entry<Long, Currency> entry : mCurrencyCache.entrySet()) {
            Currency currency = entry.getValue();
            if (ObjectUtils.equals(currencyName, currency.getCurrencyName())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Return a (cached) list of currency ids
     * @return
     */
    public Set<Long> getCurrencyIds() {
        return mCurrencyCache.keySet();
    }

    /**
     * Return a (cached) number of registered currencies
     * @return
     */
    public int getCurrencyCount() {
        return mCurrencyCache.entrySet().size();
    }


    /**
     * Fill all cache need for currencies
     * @param accountId
     */
    public void loadCache(long accountId) {
        if (mCurrencyCache == null || mUDCache == null) {
            // Create and fill the currency cache
            List<Currency> currencies = getCurrencies(accountId);
            if (mCurrencyCache == null) {

                mCurrencyCache = new SimpleCache<Long, Currency>() {
                    @Override
                    public Currency load(Long currencyId) {
                        return currencyDao.getById(currencyId);
                    }
                };

                // Fill the cache
                for (Currency currency : currencies) {
                    mCurrencyCache.put(currency.getId(), currency);
                }
            }

            // Create the UD cache
            if (mUDCache == null) {

                mUDCache = new SimpleCache<Long, Long>(UD_CACHE_TIME_MILLIS) {
                    @Override
                    public Long load(final Long currencyId) {
                        // Retrieve the last UD from the blockchain
                        final long lastUD = blockchainRemoteService.getLastUD(currencyId);

                        // Update currency
                        Currency currency = getCurrencyById(currencyId);
                        if (!ObjectUtils.equals(currency.getLastUD(), lastUD)) {
                            currency.setLastUD(lastUD);
                            currencyDao.update(currency);
                        }

                        return lastUD;
                    }
                };
            }
        }
    }

    /**
     * Return the value of the last universal dividend
     * @param currencyId
     * @return
     */
    public long getLastUD(long currencyId) {
        return mUDCache.get(currencyId);
    }

    /**
     * Return a map of UD (key=blockNumber, value=amount)
     * @return
     */
    public Map<Integer, Long> refreshAndGetUD(long currencyId, long lastSyncBlockNumber) {

        // Retrieve new UDs from blockchain
        Map<Integer, Long> newUDs = blockchainRemoteService.getUDs(currencyId, lastSyncBlockNumber + 1);

        // If any, insert new into DB
        if (newUDs != null && newUDs.size() > 0) {
            currencyDao.insertUDs(currencyId, newUDs);
        }

        return getAllUD(currencyId);
    }

    /**
     * Return a map of UD (key=blockNumber, value=amount)
     * @return
     */
    public Map<Integer, Long> getAllUD(long currencyId) {
        return currencyDao.getAllUD(currencyId);
    }

}
