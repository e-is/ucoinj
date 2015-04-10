package io.ucoin.client.ui.application;

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


import io.ucoin.client.ui.config.WebConfiguration;
import io.ucoin.client.ui.pages.home.HomePage;
import io.ucoin.client.ui.pages.login.LoginPage;
import io.ucoin.client.ui.pages.wallet.WalletPage;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.time.Duration;
import org.wicketstuff.rest.utils.mounting.PackageScanner;

public class UcoinApplication extends AuthenticatedWebApplication {
    
    private WebConfiguration config;
    
    public UcoinApplication() {
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
        mountPage("login", LoginPage.class);
        mountPage("wallet", WalletPage.class);

        // Mount rest service, from annotations
        PackageScanner.scanPackage("io.ucoin.client.ui.service.rest");

        getMarkupSettings().setStripWicketTags(true);

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
        return UcoinSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }
    
    public WebConfiguration getConfiguration() {
        return config;
    }
}