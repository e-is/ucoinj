package io.ucoin.ucoinj.core.client.service.local;

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
