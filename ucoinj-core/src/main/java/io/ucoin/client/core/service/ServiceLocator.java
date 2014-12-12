package io.ucoin.client.core.service;

import io.ucoin.client.core.technical.UCoinTechnicalException;

import java.util.ServiceLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServiceLocator {


    /* Logger */
    private static final Log log = LogFactory.getLog(ServiceLocator.class);

    /**
     * The shared instance of this ServiceLocator.
     */
    private static ServiceLocator instance = new ServiceLocator();


    protected ServiceLocator() {
        // shouldn't be instantiated
        
    }
    
    public void init() {
        
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
    
    /* -- Internal methods -- */
    protected <S> S getService(Class<S> clazz) {
        ServiceLoader<S> loader = ServiceLoader.load(clazz);
        for (S service : loader) {
            return service;
        }
        
        throw new UCoinTechnicalException("No such service found : " + clazz.getName());
    }
}
