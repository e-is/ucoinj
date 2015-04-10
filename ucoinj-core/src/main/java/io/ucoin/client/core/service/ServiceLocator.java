package io.ucoin.client.core.service;

/*
 * #%L
 * UCoin Java Client :: Core API
 * %%
 * Copyright (C) 2014 - 2015 EIS
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


import io.ucoin.client.core.service.search.BlockIndexerService;
import io.ucoin.client.core.service.search.CurrencyIndexerService;
import io.ucoin.client.core.service.search.ElasticSearchService;
import io.ucoin.client.core.technical.UCoinTechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServiceLocator implements Closeable {


    /* Logger */
    private static final Logger log = LoggerFactory.getLogger(ServiceLocator.class);

    /**
     * The shared instance of this ServiceLocator.
     */
    private static ServiceLocator instance = new ServiceLocator();
    
    private final Map<Class<?>, Object> serviceCache;


    protected ServiceLocator() {
        // shouldn't be instantiated
        serviceCache = new HashMap<Class<?>, Object>();
    }
    
    public void init() {
        
    }
    
    @Override
    public void close() throws IOException {
    	for(Object service: serviceCache.values()) {
    		if (service instanceof Closeable) {
    			((Closeable)service).close();
    		}
    	}
    }

    /**
     * replace the default shared instance of this Class
     *
     * @param newInstance the new shared service locator instance.
     */
    public static void setInstance(ServiceLocator newInstance) {
        instance = newInstance;
    }

    /**
     * Gets the shared instance of this Class
     *
     * @return the shared service locator instance.
     */
    public static ServiceLocator instance() {
        return instance;
    }
    
    public BlockchainService getBlockchainService() {
        return getService(BlockchainService.class);
    }
    
    public TransactionService getTransactionService() {
        return getService(TransactionService.class);
    }
    

    public CryptoService getCryptoService() {
        return getService(CryptoService.class);
    }

    /* -- ElasticSearch Service-- */

    public CurrencyIndexerService getCurrencyIndexerService() {
        return getService(CurrencyIndexerService.class);
    }

    public ElasticSearchService getElasticSearchService() {
        return getService(ElasticSearchService.class);
    }


    public BlockIndexerService getBlockIndexerService() {
        return getService(BlockIndexerService.class);
    }

    /* -- Internal methods -- */
    protected <S extends BaseService> S getService(Class<S> clazz) {
        if (serviceCache.containsKey(clazz)) {
            return (S)serviceCache.get(clazz);
        }
        try {
            S service = (S)clazz.newInstance();
            serviceCache.put(clazz, service);

            // Call initialization
            service.initialize();

            return service;
        }
        catch (Exception e) {
            throw new UCoinTechnicalException("Could not load service: " + clazz.getName(), e);
        }
    }
}
