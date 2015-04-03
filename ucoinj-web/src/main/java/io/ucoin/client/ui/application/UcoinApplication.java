package io.ucoin.client.ui.application;

import io.ucoin.client.ui.pages.home.HomePage;
import io.ucoin.client.ui.pages.login.LoginPage;
import io.ucoin.client.ui.pages.wallet.WalletPage;

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.time.Duration;
import org.wicketstuff.rest.utils.mounting.PackageScanner;

public class UcoinApplication extends AuthenticatedWebApplication {
    
    private UcoinConfiguration config;
    
    public UcoinApplication() {
        config = new UcoinConfiguration();
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
    
    public UcoinConfiguration getConfiguration() {
        return config;
    }
}