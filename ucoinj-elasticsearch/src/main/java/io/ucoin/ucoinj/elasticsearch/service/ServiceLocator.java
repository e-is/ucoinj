package io.ucoin.ucoinj.elasticsearch.service;

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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceLocator extends io.ucoin.ucoinj.core.client.service.ServiceLocator {


    /* Logger */
    private static final Logger log = LoggerFactory.getLogger(ServiceLocator.class);

    /**
     * The shared instance of this ServiceLocator.
     */
    private static ServiceLocator instance = new ServiceLocator();

    static {
        io.ucoin.ucoinj.core.client.service.ServiceLocator.setInstance(instance);
    }

    public static ServiceLocator instance() {
        return instance;
    }

    /* -- ElasticSearch Service-- */

    public CurrencyIndexerService getCurrencyIndexerService() {
        return getBean(CurrencyIndexerService.class);
    }

    public ElasticSearchService getElasticSearchService() {
        return getBean(ElasticSearchService.class);
    }

    public BlockIndexerService getBlockIndexerService() {
        return getBean(BlockIndexerService.class);
    }

    public ExecutorService getExecutorService() {
        return getBean(ExecutorService.class);
    }
}
