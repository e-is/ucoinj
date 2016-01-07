package io.ucoin.ucoinj.core.client.dao.mem;

import io.ucoin.ucoinj.core.client.dao.CurrencyDao;
import io.ucoin.ucoinj.core.client.model.local.Currency;
import io.ucoin.ucoinj.core.util.CollectionUtils;

import java.util.*;

/**
 * Created by blavenie on 29/12/15.
 */
public class MemoryCurrencyDaoImpl implements CurrencyDao {


    private Map<Long, Currency> currencies = new HashMap<>();

    private Map<Long, Map<Integer, Long>> currencyUDsByBlock = new HashMap<>();

    @Override
    public Currency create(final Currency entity) {

        long id = getMaxId() + 1;
        entity.setId(id);

        currencies.put(id, entity);

        return entity;
    }

    @Override
    public Currency update(final Currency currency) {
        currencies.put(currency.getId(), currency);
        return currency;
    }

    @Override
    public void remove(final Currency currency) {
        currencies.remove(currency.getId());
    }

    @Override
    public List<Currency> getCurrencies(long accountId) {
        List<Currency> result = new ArrayList<>();
        result.addAll(currencies.values());
        return result;
    }

    @Override
    public Currency getById(long currencyId) {
        return currencies.get(currencyId);
    }

    @Override
    public String getCurrencyNameById(long currencyId) {
        Currency currency = getById(currencyId);
        if (currency == null) {
            return null;
        }
        return currency.getCurrencyName();
    }

    @Override
    public Long getCurrencyIdByName(String currencyName) {
        for(Currency currency: currencies.values()) {
            if (currencyName.equalsIgnoreCase(currency.getCurrencyName())) {
                return currency.getId();
            }
        }
        return null;
    }

    @Override
    public Set<Long> getCurrencyIds() {
        return currencies.keySet();
    }

    @Override
    public int getCurrencyCount() {
        return currencies.size();
    }

    @Override
    public long getLastUD(long currencyId) {
        Currency currency = getById(currencyId);
        if (currency == null) {
            return -1;
        }
        return currency.getLastUD();
    }

    @Override
    public Map<Integer, Long> getAllUD(long currencyId) {

        return currencyUDsByBlock.get(currencyId);
    }

    @Override
    public void insertUDs(Long currencyId,  Map<Integer, Long> newUDs) {
        Map<Integer, Long> udsByBlock = currencyUDsByBlock.get(currencyId);
        if (udsByBlock == null) {
            udsByBlock = new HashMap<>();
            currencyUDsByBlock.put(currencyId, udsByBlock);
        }
        udsByBlock.putAll(newUDs);
    }

    /* -- internal methods -- */

    protected long getMaxId() {
        long currencyId = -1;
        for (Long anId : currencies.keySet()) {
            if (anId > currencyId) {
                currencyId = anId;
            }
        }

        return currencyId;
    }
}
