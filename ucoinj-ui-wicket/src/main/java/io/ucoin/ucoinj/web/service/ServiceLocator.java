package io.ucoin.ucoinj.web.service;

/*
 * #%L
 * SIH-Adagio :: UI for Core Allegro
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 - 2014 Ifremer
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


import com.google.common.base.Preconditions;
import io.ucoin.ucoinj.web.security.keypair.ChallengeMessageStore;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

public class ServiceLocator extends io.ucoin.ucoinj.elasticsearch.service.ServiceLocator {


    private static ServiceLocator instance;
    static {
        initDefault();
    }

    public static void initDefault(ServletContext servletContext) {
        if (instance != null) {
            instance.setServletContext(servletContext);
        }
        else {
            instance = new ServiceLocator(servletContext);
            io.ucoin.ucoinj.elasticsearch.service.ServiceLocator.setInstance(instance);
        }
    }

    public static void initDefault() {
        instance = new ServiceLocator();
        io.ucoin.ucoinj.elasticsearch.service.ServiceLocator.setInstance(instance);
    }

    public static ServiceLocator instance() {
        return instance;
    }

    private WebApplicationContext appContext;

    public ServiceLocator() {
    }
    public ServiceLocator(ServletContext servletContext) {
        setServletContext(servletContext);
    }

    public ChallengeMessageStore getChallengeMessageStore() {
        return getBean("challengeMessageStore", ChallengeMessageStore.class);
    }

    /* -- internal methods -- */

    protected <T> T getBean(String name, Class<T> type) {
        Preconditions.checkNotNull(appContext, "no application context initaitliszed. Please call initDefault(ServletContext) before getting beans.");
        return appContext.getBean(name, type);
    }


    protected void setServletContext(ServletContext servletContext) {
        Preconditions.checkNotNull(servletContext);
        appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }

}
