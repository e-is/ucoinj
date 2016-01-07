package io.ucoin.ucoinj.core.client.dao;

import io.ucoin.ucoinj.core.beans.Bean;
import io.ucoin.ucoinj.core.client.model.local.Currency;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by eis on 07/02/15.
 */
public interface CurrencyDao extends Bean, EntityDao<Currency> {

    Currency create(final Currency currency);

    Currency update(final Currency currency);

    void remove(final Currency currency);

    List<Currency> getCurrencies(long accountId);

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
     * Return the value of the last universal dividend
     * @param currencyId
     * @return
     */
    long getLastUD(long currencyId);

    /**
     * Return a map of UD (key=blockNumber, value=amount)
     * @return
     */
     Map<Integer, Long> getAllUD(long currencyId);

     void insertUDs(Long currencyId,  Map<Integer, Long> newUDs);
}
