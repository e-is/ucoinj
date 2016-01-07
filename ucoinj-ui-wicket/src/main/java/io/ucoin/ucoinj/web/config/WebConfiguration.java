package io.ucoin.ucoinj.web.config;

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

import io.ucoin.ucoinj.core.client.config.Configuration;
import io.ucoin.ucoinj.core.client.config.ConfigurationOption;
import io.ucoin.ucoinj.core.client.service.ServiceLocator;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.core.util.StringUtils;
import io.ucoin.ucoinj.core.util.crypto.CryptoUtils;
import org.nuiton.config.ApplicationConfig;
import org.nuiton.util.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.util.Locale;
import java.util.Properties;

public class WebConfiguration extends PropertyPlaceholderConfigurer {

    private static final String CONFIG_FILE_NAME = "ucoinj-web.config";

    private static final String CONFIG_FILE_ENV_PROPERTY = CONFIG_FILE_NAME;

    private static final String CONFIG_FILE_JNDI_NAME = "java:comp/env/" + CONFIG_FILE_NAME;

    /* Logger */
    private static final Logger log = LoggerFactory.getLogger(WebConfiguration.class);

    private static WebConfiguration instance;

    public static WebConfiguration instance() {
        return instance;
    }

    static {
        String configFile = getWebConfigFile();
        if (log.isDebugEnabled()) {
            log.debug(String.format("Loading configuration from file [%s]", configFile));
        }
        if (new File(configFile).exists() == false) {
            log.warn(String.format("Configuration file not found [%s]. Make sure the path is correct.", configFile));
        }
        instance = new WebConfiguration(configFile);
    }

    private Configuration delegate;

    public WebConfiguration(String file, String... args) {
        delegate = new Configuration(file, args);
        Configuration.setInstance(delegate);

        io.ucoin.ucoinj.elasticsearch.config.Configuration esConfig = new io.ucoin.ucoinj.elasticsearch.config.Configuration(getApplicationConfig());
        io.ucoin.ucoinj.elasticsearch.config.Configuration.setInstance(esConfig);

        // Init Crypto (NaCL lib...)
        initCrypto();
    }

    public Version getVersion() {
        return delegate.getVersion();
    }

    public Locale getI18nLocale() {
        return delegate.getI18nLocale();
    }

    public ApplicationConfig getApplicationConfig() {
        return delegate.getApplicationConfig();
    }

    public String getVersionAsString() {
        return getVersion().toString();
    }

    public String getUserPubkey() {
        String pubkey = getApplicationConfig().getOption(WebConfigurationOption.USER_PUBKEY.getKey());
        if (StringUtils.isBlank(pubkey)) {
            return null;
        }

        // Compute the key (from salt/password)
        pubkey = computeUserPubkey();
        if (StringUtils.isBlank(pubkey)) {
            return null;
        }

        // Store computed pubkey back into config
        getApplicationConfig().setOption(WebConfigurationOption.USER_PUBKEY.getKey(), pubkey);
        return pubkey;
    }

    public String getNodeHost() {
        return delegate.getNodeHost();
    }

    public int getNodePort() {
        return delegate.getNodePort();
    }

    /* -- Internal methods -- */
    protected static String getWebConfigFile() {
        // Could override config file name (useful for dev)
        String configFile = CONFIG_FILE_NAME;
        if (System.getProperty(CONFIG_FILE_ENV_PROPERTY) != null) {
            configFile = System.getProperty(CONFIG_FILE_ENV_PROPERTY);
            configFile = configFile.replaceAll("\\\\", "/");
        }
        else {
            try {
                InitialContext ic = new InitialContext();
                String jndiPathToConfFile = (String) ic.lookup(CONFIG_FILE_JNDI_NAME);
                if (StringUtils.isNotBlank(jndiPathToConfFile)) {
                    configFile = jndiPathToConfFile;
                }
            } catch (NamingException e) {
                log.warn(String.format("Error while reading JNDI initial context. Skip configuration path override, from context [%s]", CONFIG_FILE_JNDI_NAME));
            }
        }

        return configFile;
    }

    protected void initCrypto() {
        if (log.isInfoEnabled()) {
            log.info("Starts Sodium (NaCL) library");
        }

        try {
            // This call will load the sodium library
            ServiceLocator.instance().getCryptoService();
        } catch (Throwable e) {
            throw new TechnicalException("Crypto lib (NaCL) initialization failed. Make sure sodium has been installed (or add library into a ./lib directory).", e);
        }
    }

    /**
     * Compute the user pubkey, from salt+password in config.
     * If salt or password are missed, return null
     * @return
     */
    protected String computeUserPubkey() {
        String salt = getApplicationConfig().getOption(WebConfigurationOption.USER_SALT.getKey());
        String password = getApplicationConfig().getOption(WebConfigurationOption.USER_PASSWORD.getKey());
        if (io.ucoin.ucoinj.core.util.StringUtils.isNotBlank(salt)
                && io.ucoin.ucoinj.core.util.StringUtils.isNotBlank(password)) {
            byte[] pubKey = ServiceLocator.instance().getCryptoService().getKeyPair(salt, password).getPubKey();
            return CryptoUtils.encodeBase58(pubKey);
        }
        else {
            return null;
        }
    }

    @Override
    protected String resolvePlaceholder(String placeholder, Properties props) {
        if (getApplicationConfig() == null) {
            throw new TechnicalException(
                    "delegate.getApplicationConfig() must not be null. Please initialize Configuration instance with a not null applicationConfig BEFORE starting Spring.");
        }

        // Try to resolve placeholder from application configuration
        String optionValue = getApplicationConfig().getOption(placeholder);
        if (optionValue != null) {
            return optionValue;
        }

        // If not found in configuration, delegate to the default Spring mecanism
        return super.resolvePlaceholder(placeholder, props);
    }
}
