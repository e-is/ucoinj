package io.ucoin.ucoinj.core.beans;

/*
 * #%L
 * UCoin Java :: Core Shared
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

import io.ucoin.ucoinj.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created by blavenie on 18/12/15.
 */
public class BeanFactory implements Closeable{


    private static final Logger log = LoggerFactory.getLogger(BeanFactory.class);

    private final Map<Class<?>, Object> beansCache;
    private final ServiceLoader<Bean> beansLoader;

    public BeanFactory() {
        beansCache = new HashMap<>();
        beansLoader = ServiceLoader.load(Bean.class);
    }

    public <S extends Bean> S getBean(Class<S> clazz) {
        if (beansCache.containsKey(clazz)) {
            return (S)beansCache.get(clazz);
        }
        S bean = newBean(clazz);
        beansCache.put(clazz, bean);

        // Call initialization
        if (bean instanceof InitializingBean){
            if (log.isDebugEnabled()) {
                log.debug(String.format("Initializing bean of type [%s]", clazz.getName()));
            }
            try {
                ((InitializingBean) bean).afterPropertiesSet();
            }
            catch(Exception e) {
                throw new TechnicalException(String.format("Unable to initialize bean of type [%s]", clazz.getName()), e);
            }
        }

        return bean;
    }


    public <S extends Bean> S newBean(Class<S> clazz) {

        for (Bean bean: beansLoader) {

            if (clazz.isInstance(bean)) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Creating new bean of type [%s]", clazz.getName()));
                }
                return (S)bean;
            }
        }

        throw new TechnicalException(String.format("Unable to create bean with type [%s]: not configured for the service loader [%s]", clazz.getName(), Bean.class.getCanonicalName()));
    }

    @Override
    public void close() throws IOException {
        for(Object bean: beansCache.values()) {
            if (bean instanceof Closeable) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Closing bean of type [%s]...", bean.getClass().getName()));
                }
                try {
                    ((Closeable) bean).close();
                }
                catch (Exception e) {
                    // continue
                }
            }
        }
    }
}
