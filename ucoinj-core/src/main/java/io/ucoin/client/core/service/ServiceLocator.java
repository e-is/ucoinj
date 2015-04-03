package io.ucoin.client.core.service;

import io.ucoin.client.core.service.search.SearchService;
import io.ucoin.client.core.technical.UCoinTechnicalException;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServiceLocator implements Closeable {


    /* Logger */
    private static final Log log = LogFactory.getLog(ServiceLocator.class);

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

    public SearchService getSearchService() {
        return getService(SearchService.class);
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
