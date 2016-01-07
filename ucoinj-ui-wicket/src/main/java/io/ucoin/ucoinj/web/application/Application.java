package io.ucoin.ucoinj.web.application;

/*
 * #%L
 * UCoin Java Client :: Web
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


import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.elasticsearch.config.ConfigurationOption;
import io.ucoin.ucoinj.elasticsearch.service.CurrencyIndexerService;
import io.ucoin.ucoinj.web.config.WebConfiguration;
import io.ucoin.ucoinj.web.pages.admin.JobManagerPage;
import io.ucoin.ucoinj.web.pages.admin.ToolsPage;
import io.ucoin.ucoinj.web.pages.home.HomePage;
import io.ucoin.ucoinj.web.pages.login.LoginPage;
import io.ucoin.ucoinj.web.pages.registry.CurrencyPage;
import io.ucoin.ucoinj.web.pages.registry.CurrencyRegistryPage;
import io.ucoin.ucoinj.web.pages.wallet.WalletPage;
import io.ucoin.ucoinj.web.service.ServiceLocator;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component("wicketApplication")
public class Application extends AuthenticatedWebApplication {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private WebConfiguration config;
    
    public Application() {
        config = WebConfiguration.instance();
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();
        // add the capability to gather extended browser info stuff like screen resolution
        getRequestCycleSettings().setGatherExtendedBrowserInfo(true);
        // set the default page timeout
        getRequestCycleSettings().setTimeout(Duration.minutes(10));
        // set the UTF-8 charset
        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

        mountPage("home", getHomePage());
        mountPage("currency/${currencyName}", CurrencyPage.class);
        mountPage("admin/tools", ToolsPage.class);
        mountPage("admin/jobs", JobManagerPage.class);
        mountPage("login", LoginPage.class);
        mountPage("wallet", WalletPage.class);
        mountPage("currency-form", CurrencyRegistryPage.class);

        getMarkupSettings().setStripWicketTags(true);

        // Starting services
        startServices();
    }
    
    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class getHomePage() {
        return HomePage.class;
    }
    
    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return WebSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }
    
    public WebConfiguration getConfiguration() {
        return config;
    }



    /* -- protected methods -- */

    protected void startServices() {
        try {
            // Make sure the service locator is initialized
            ServiceLocator.initDefault(getServletContext());
        }
        catch(TechnicalException e) {
            log.error("Error during services initialization: " + e.getMessage());
            throw new TechnicalException("Error during services initialization: " + e.getMessage(), e);
        }

        // local node
        boolean localEsNode = config.getApplicationConfig().getOptionAsBoolean(ConfigurationOption.LOCAL_ENABLE.getKey());
        if (localEsNode) {

            // Make sure main index exists
            CurrencyIndexerService currencyIndexerService = ServiceLocator.instance().getCurrencyIndexerService();
            currencyIndexerService.createIndexIfNotExists();

            // Make sure currency from default peer exists
            try {
                Peer peer = new Peer(config.getNodeHost(), config.getNodePort());
                currencyIndexerService.indexCurrencyFromPeer(peer);
            }
            catch(Exception e) {
            }
        }
    }
}