package io.ucoin.ucoinj.core.client.service.local;

import io.ucoin.ucoinj.core.beans.InitializingBean;
import io.ucoin.ucoinj.core.client.dao.PeerDao;
import io.ucoin.ucoinj.core.client.model.local.Currency;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.service.ServiceLocator;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.core.util.CollectionUtils;
import io.ucoin.ucoinj.core.util.ObjectUtils;
import io.ucoin.ucoinj.core.util.StringUtils;
import io.ucoin.ucoinj.core.util.cache.Cache;
import io.ucoin.ucoinj.core.util.cache.SimpleCache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eis on 07/02/15.
 */
public class PeerServiceImpl implements PeerService, InitializingBean {

    private Cache<Long, List<Peer>> peersByCurrencyIdCache;
    private Cache<Long, Peer> activePeerByCurrencyIdCache;

    private CurrencyService currencyService;
    private PeerDao peerDao;

    public PeerServiceImpl() {
        super();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        currencyService = ServiceLocator.instance().getCurrencyService();
        peerDao = ServiceLocator.instance().getBean(PeerDao.class);
    }

    @Override
    public void close() throws IOException {
        currencyService = null;
        peerDao = null;
        peersByCurrencyIdCache = null;
        activePeerByCurrencyIdCache = null;
    }

    public Peer save(final Peer peer) {
        ObjectUtils.checkNotNull(peer);
        ObjectUtils.checkNotNull(peer.getCurrencyId());
        ObjectUtils.checkArgument(StringUtils.isNotBlank(peer.getHost()));
        ObjectUtils.checkArgument(peer.getPort() >= 0);

        Peer result;
        // Create
        if (peer.getId() == null) {
            result = peerDao.create(peer);
        }

        // or update
        else {
            peerDao.update(peer);
            result = peer;
        }

        // update cache (if already loaded)
        if (peersByCurrencyIdCache != null) {
            List<Peer> peers = peersByCurrencyIdCache.get(peer.getCurrencyId());
            if (peers == null) {
                peers = new ArrayList<Peer>();
                peersByCurrencyIdCache.put(peer.getCurrencyId(), peers);
                peers.add(peer);
            }
            else if (!peers.contains(peer)) {
                peers.add(peer);
            }
        }

        return result;
    }


    public Peer getPeerById(long peerId) {
        return peerDao.getById(peerId);
    }

    /**
     * Return a (cached) active peer, by currency id
     * @param currencyId
     * @return
     */
    public Peer getActivePeerByCurrencyId(long currencyId) {
        // Check if cache as been loaded
        if (activePeerByCurrencyIdCache == null) {

            activePeerByCurrencyIdCache = new SimpleCache<Long, Peer>() {
                @Override
                public Peer load(Long currencyId) {
                    List<Peer> peers = peerDao.getPeersByCurrencyId(currencyId);
                    if (CollectionUtils.isEmpty(peers)) {
                        String currencyName = currencyService.getCurrencyNameById(currencyId);
                        throw new TechnicalException(String.format(
                                "No peers configure for currency [%s]",
                                currencyName != null ? currencyName : currencyId));
                    }

                    return peers.get(0);
                }
            };
        }

        return activePeerByCurrencyIdCache.get(currencyId);
    }

    /**
     * Return a (cached) peer list, by currency id
     * @param currencyId
     * @return
     */
    public List<Peer> getPeersByCurrencyId(long currencyId) {
        // Check if cache as been loaded
        if (peersByCurrencyIdCache == null) {
            throw new TechnicalException("Cache not initialize. Please call loadCache() before getPeersByCurrencyId().");
        }
        // Get it from cache
        return peersByCurrencyIdCache.get(currencyId);
    }

    /**
     * Fill all cache need for currencies
     * @param accountId
     */
    public void loadCache(long accountId) {
        if (peersByCurrencyIdCache != null) {
            return;
        }

        peersByCurrencyIdCache = new SimpleCache<Long, List<Peer>>() {
            @Override
            public List<Peer> load(Long currencyId) {
                return peerDao.getPeersByCurrencyId(currencyId);
            }
        };

        List<Currency> currencies = ServiceLocator.instance().getCurrencyService().getCurrencies(accountId);

        for (Currency currency: currencies) {
            // Get peers from DB
            List<Peer> peers = getPeersByCurrencyId(currency.getId());

            // Then fill the cache
            if (CollectionUtils.isNotEmpty(peers)) {
                peersByCurrencyIdCache.put(currency.getId(), peers);
            }
        }
    }

}
