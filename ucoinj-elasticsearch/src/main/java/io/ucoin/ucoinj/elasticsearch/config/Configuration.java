package io.ucoin.ucoinj.elasticsearch.config;

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


import com.google.common.base.Charsets;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import org.nuiton.config.ApplicationConfig;
import org.nuiton.config.ApplicationConfigHelper;
import org.nuiton.config.ApplicationConfigProvider;
import org.nuiton.config.ArgumentsParserException;
import org.nuiton.util.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.Set;

import static org.nuiton.i18n.I18n.t;

/**
 * Access to configuration options
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 */
public class Configuration  {
    /** Logger. */
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

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

        // Cascade the application config to the client module
        io.ucoin.ucoinj.core.client.config.Configuration clientConfig = new io.ucoin.ucoinj.core.client.config.Configuration(instance.getApplicationConfig());
        io.ucoin.ucoinj.core.client.config.Configuration.setInstance(clientConfig);
    }

    protected final String[] optionKeyToNotSave;

    protected File configFile;

    public Configuration(ApplicationConfig applicationConfig) {
        super();
        this.applicationConfig = applicationConfig;
        this.optionKeyToNotSave = null;

        // Override application version
        initVersion(applicationConfig);
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

        // Override application version
        initVersion(applicationConfig);

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
            throw new TechnicalException(t("ucoinj.config.parse.error"), e);
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

        // Init other configuration
        io.ucoin.ucoinj.core.client.config.Configuration coreConfig = new io.ucoin.ucoinj.core.client.config.Configuration(applicationConfig);
        io.ucoin.ucoinj.core.client.config.Configuration.setInstance(coreConfig);
    }

    /**
     * Override the version default option, from the MANIFEST implementation version (if any)
     * @param applicationConfig
     */
    protected void initVersion(ApplicationConfig applicationConfig) {
        // Override application version
        String implementationVersion = this.getClass().getPackage().getSpecificationVersion();
        if (implementationVersion != null) {
            applicationConfig.setDefaultOption(
                    ConfigurationOption.VERSION.getKey(),
                    implementationVersion);
        }
    }

    /**
     * Add alias to the given ApplicationConfig. <p/>
     * This method could be override to add specific alias
     * 
     * @param applicationConfig
     */
    protected void addAlias(ApplicationConfig applicationConfig) {
        applicationConfig.addAlias("-h", "--option", ConfigurationOption.NODE_BMA_HOST.getKey());
        applicationConfig.addAlias("--host", "--option", ConfigurationOption.NODE_BMA_HOST.getKey());
        applicationConfig.addAlias("-p", "--option", ConfigurationOption.NODE_BMA_PORT.getKey());
        applicationConfig.addAlias("--port", "--option", ConfigurationOption.NODE_BMA_PORT.getKey());

        applicationConfig.addAlias("-esh", "--option", ConfigurationOption.HOST.getKey());
        applicationConfig.addAlias("--es-host", "--option", ConfigurationOption.HOST.getKey());
        applicationConfig.addAlias("-esp", "--option", ConfigurationOption.PORT.getKey());
        applicationConfig.addAlias("--es-port", "--option", ConfigurationOption.PORT.getKey());
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

    public String getNodeBmaHost() {
        return applicationConfig.getOption(ConfigurationOption.NODE_BMA_HOST.getKey());
    }

    public int getNodeBmaPort() {
        return applicationConfig.getOptionAsInt(ConfigurationOption.NODE_BMA_PORT.getKey());
    }

    public String getHost() {
        return applicationConfig.getOption(ConfigurationOption.HOST.getKey());
    }

    public int getPort() {
        return applicationConfig.getOptionAsInt(ConfigurationOption.PORT.getKey());
    }

    public boolean isEmbedded() {
        return applicationConfig.getOptionAsBoolean(ConfigurationOption.EMBEDDED_ENABLE.getKey());
    }

    public void setEmbedded(boolean embeddedNode) {
        applicationConfig.setOption(ConfigurationOption.EMBEDDED_ENABLE.getKey(), Boolean.toString(embeddedNode));
    }

    public boolean isLocal() {
        return applicationConfig.getOptionAsBoolean(ConfigurationOption.LOCAL_ENABLE.getKey());
    }

    public boolean isHttpEnable() {
        return applicationConfig.getOptionAsBoolean(ConfigurationOption.HTTP_ENABLE.getKey());
    }

    public String getClusterName() {
        return applicationConfig.getOption(ConfigurationOption.CLUSTER_NAME.getKey());
    }

    public int getTaskExecutorQueueCapacity() {
        return applicationConfig.getOptionAsInt(ConfigurationOption.TASK_EXECUTOR_QUEUE_CAPACITY.getKey());
    }

    public int getTaskExecutorTimeToIdle() {
        return applicationConfig.getOptionAsInt(ConfigurationOption.TASK_EXECUTOR_TIME_TO_IDLE.getKey());
    }

    public boolean isIndexBulkEnable() {
        return applicationConfig.getOptionAsBoolean(ConfigurationOption.INDEX_BULK_ENABLE.getKey());
    }

    public int getIndexBulkSize() {
        return applicationConfig.getOptionAsInt(ConfigurationOption.INDEX_BULK_SIZE.getKey());
    }
}
