package io.ucoin.client.core;

import io.ucoin.client.core.config.Configuration;
import io.ucoin.client.core.technical.UCoinTechnicalException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuiton.config.ApplicationConfig;
import org.nuiton.i18n.I18n;
import org.nuiton.i18n.init.DefaultI18nInitializer;
import org.nuiton.i18n.init.UserI18nInitializer;

public class UCoinClientCore {

    private static final Log log = LogFactory.getLog(UCoinClientCore.class);
    

    public static void main(String[] args) {
        if (log.isInfoEnabled()) {
            log.info("Starting UCOin Java Client :: Core with arguments: " + Arrays.toString(args));
        }

        // By default, display help
        if (args == null || args.length == 0) {
            args = new String[] { "--help" };
        }

        // Could override config file name (useful for dev)
        String configFile = "ucoinj.config";
        if (System.getProperty(configFile) != null) {
            configFile = System.getProperty(configFile);
            configFile = configFile.replaceAll("\\\\", "/");
        }
        
        // Create configuration
        Configuration config = new Configuration(configFile, args) {
            protected void addAlias(ApplicationConfig applicationConfig) {
                super.addAlias(applicationConfig);
                // Add custom alias
            };
        };
        Configuration.setInstance(config);

        // Init i18n
        try {
            initI18n(config);
        } catch (IOException e) {
            throw new UCoinTechnicalException("i18n initialization failed", e);
        }

        try {
            config.getApplicationConfig().doAllAction();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //IOUtils.closeQuietly(ServiceLocator.instance());
    }

    protected static void initI18n(Configuration config) throws IOException {

        // --------------------------------------------------------------------//
        // init i18n
        // --------------------------------------------------------------------//
        File i18nDirectory = new File(config.getDataDirectory(), "i18n");
        if (i18nDirectory.exists()) {
            // clean i18n cache
            FileUtils.cleanDirectory(i18nDirectory);
        }

        FileUtils.forceMkdir(i18nDirectory);

        if (log.isDebugEnabled()) {
            log.debug("I18N directory: " + i18nDirectory);
        }

        Locale i18nLocale = config.getI18nLocale();

        if (log.isInfoEnabled()) {
            log.info(String.format("Starts i18n with locale [%s] at [%s]",
                    i18nLocale, i18nDirectory));
        }
        I18n.init(new UserI18nInitializer(
                i18nDirectory, new DefaultI18nInitializer(getI18nBundleName())),
                i18nLocale);
    }

    protected static String getI18nBundleName() {
        return "ucoinj-core-i18n";
    }
}
