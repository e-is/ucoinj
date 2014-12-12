package io.ucoin.client.core.config;


import static org.nuiton.i18n.I18n.t;
import io.ucoin.client.core.technical.UCoinTechnicalException;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuiton.config.ApplicationConfig;
import org.nuiton.config.ApplicationConfigHelper;
import org.nuiton.config.ApplicationConfigProvider;
import org.nuiton.config.ArgumentsParserException;
import org.nuiton.util.version.Version;

import com.google.common.base.Charsets;

/**
 * Access to configuration options
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 */
public class Configuration  {
    /** Logger. */
    private static final Log log = LogFactory.getLog(Configuration.class);

    /**
     * Delegate application config.
     */
    protected final ApplicationConfig applicationConfig;

    private static Configuration instance;

    public static Configuration instance() {
        return instance;
    }

    public static void setInstance(Configuration instance) {
        Configuration.instance = instance;
    }

    protected final String[] optionKeyToNotSave;

    protected File configFile;

    public Configuration(ApplicationConfig applicationConfig) {
        super();
        this.applicationConfig = applicationConfig;
        this.optionKeyToNotSave = null;
    }

    public Configuration(String file, String... args) {
        super();
        this.applicationConfig = new ApplicationConfig();
        this.applicationConfig.setEncoding(Charsets.UTF_8.name());
        this.applicationConfig.setConfigFileName(file);

        // get all config providers
        Set<ApplicationConfigProvider> providers =
                ApplicationConfigHelper.getProviders(null,
                        null,
                        null,
                        true);

        // load all default options
        ApplicationConfigHelper.loadAllDefaultOption(applicationConfig,
                providers);

        // Load actions
        for (ApplicationConfigProvider provider : providers) {
            applicationConfig.loadActions(provider.getActions());
        }
        
        // Define Alias
        addAlias(applicationConfig);

        // get all transient and final option keys
        Set<String> optionToSkip =
                ApplicationConfigHelper.getTransientOptionKeys(providers);

        if (log.isDebugEnabled()) {
            log.debug("Option that won't be saved: " + optionToSkip);
        }
        optionKeyToNotSave = optionToSkip.toArray(new String[optionToSkip.size()]);

        try {
            applicationConfig.parse(args);

        } catch (ArgumentsParserException e) {
            throw new UCoinTechnicalException(t("ucoinj.config.parse.error"), e);
        }

        // TODO Review this, this is very dirty to do this...
        File appBasedir = applicationConfig.getOptionAsFile(
                ConfigurationOption.BASEDIR.getKey());

        if (appBasedir == null) {
            appBasedir = new File("");
        }
        if (!appBasedir.isAbsolute()) {
            appBasedir = new File(appBasedir.getAbsolutePath());
        }
        if (appBasedir.getName().equals("..")) {
            appBasedir = appBasedir.getParentFile().getParentFile();
        }
        if (appBasedir.getName().equals(".")) {
            appBasedir = appBasedir.getParentFile();
        }
        if (log.isInfoEnabled()) {
            log.info("Application basedir: " + appBasedir);
        }
        applicationConfig.setOption(
                ConfigurationOption.BASEDIR.getKey(),
                appBasedir.getAbsolutePath());
    }

    /**
     * Add alias to the given ApplicationConfig. <p/>
     * This method could be override to add specific alias
     * 
     * @param applicationConfig
     */
    protected void addAlias(ApplicationConfig applicationConfig) {
        applicationConfig.addAlias("-h", "--option", ConfigurationOption.NODE_HOST.getKey());
        applicationConfig.addAlias("-p", "--option", ConfigurationOption.NODE_PORT.getKey());
        applicationConfig.addAlias("-c", "--option", ConfigurationOption.NODE_CURRENCY.getKey());
        applicationConfig.addAlias("--salt", "--option", ConfigurationOption.USER_SALT.getKey());
        applicationConfig.addAlias("--passwd", "--option", ConfigurationOption.USER_PASSWD.getKey());
     }

    public File getConfigFile() {
        if (configFile == null) {
            File dir = getBasedir();
            if (dir == null || !dir.exists()) {
                dir = new File(applicationConfig.getUserConfigDirectory());
            }
            configFile = new File(dir, applicationConfig.getConfigFileName());
        }
        return configFile;
    }

    /** @return {@link ConfigurationOption#BASEDIR} value */
    public File getBasedir() {
        File result = applicationConfig.getOptionAsFile(ConfigurationOption.BASEDIR.getKey());
        return result;
    }

    /** @return {@link ConfigurationOption#DATA_DIRECTORY} value */
    public File getDataDirectory() {
        File result = applicationConfig.getOptionAsFile(ConfigurationOption.DATA_DIRECTORY.getKey());
        return result;
    }

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public File getTempDirectory() {
        return applicationConfig.getOptionAsFile(ConfigurationOption.TMP_DIRECTORY.getKey());
    }
    
    public File getCacheDirectory() {
        return applicationConfig.getOptionAsFile(ConfigurationOption.CACHE_DIRECTORY.getKey());
    }

   

    public Version getVersion() {
        return applicationConfig.getOptionAsVersion(ConfigurationOption.VERSION.getKey());
    }

    public File getI18nDirectory() {
        return applicationConfig.getOptionAsFile(
                ConfigurationOption.I18N_DIRECTORY.getKey());
    }

    public Locale getI18nLocale() {
        return applicationConfig.getOptionAsLocale(
                ConfigurationOption.I18N_LOCALE.getKey());
    }

    public void setI18nLocale(Locale locale) {
        applicationConfig.setOption(ConfigurationOption.I18N_LOCALE.getKey(), locale.toString());
    }

    public String getNodeCurrency() {
        return applicationConfig.getOption(ConfigurationOption.NODE_CURRENCY.getKey());
    }
    
    public String getNodeProtocol() {
        return applicationConfig.getOption(ConfigurationOption.NODE_PROTOCOL.getKey());
    }
    
    public String getNodeHost() {
        return applicationConfig.getOption(ConfigurationOption.NODE_HOST.getKey());
    }
   
    public int getNodePort() {
        return applicationConfig.getOptionAsInt(ConfigurationOption.NODE_PORT.getKey());
    }
    
    public URL getNodeUrl() {
        return applicationConfig.getOptionAsURL(ConfigurationOption.NODE_URL.getKey());
    }
    
    public int getNodeTimeout() {
        return applicationConfig.getOptionAsInt(ConfigurationOption.NODE_TIMEOUT.getKey());
    }
     
    
}
