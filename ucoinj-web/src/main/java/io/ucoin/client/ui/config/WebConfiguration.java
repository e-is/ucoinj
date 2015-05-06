package io.ucoin.client.ui.config;

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

import io.ucoin.client.core.config.Configuration;
import io.ucoin.client.core.service.ServiceLocator;
import io.ucoin.client.core.technical.UCoinTechnicalException;
import org.apache.commons.lang3.StringUtils;
import org.nuiton.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;

public class WebConfiguration extends Configuration {

    private static final String CONFIG_FILE_NAME = "ucoinj-web.config";

    private static final String CONFIG_FILE_ENV_PROPERTY = CONFIG_FILE_NAME;

    private static final String CONFIG_FILE_JNDI_NAME = "java:comp/env/" + CONFIG_FILE_NAME;


    /* Logger */
    private static final Logger log = LoggerFactory.getLogger(WebConfiguration.class);

    static {
        String configFile = getWebConfigFile();
        if (log.isDebugEnabled()) {
            log.debug(String.format("Loading configuration from file [%s]", configFile));
        }
        if (new File(configFile).exists() == false) {
            log.warn(String.format("Configuration file not found [%s]. Make sure the path is correct.", configFile));
        }
        instance = new WebConfiguration(configFile);
        initDefault();
    }

    private static final WebConfiguration instance;

    public static void initDefault() {
        setInstance(instance);
    }

    public static WebConfiguration instance() {
        return instance;
    }

    public WebConfiguration(ApplicationConfig applicationConfig) {
        super(applicationConfig);
    }

    public WebConfiguration(String file, String... args) {
        super(file, args);

        // Init Crypto (NaCL lib...)
        initCrypto();
    }

    public String getVersionAsString() {
        return getVersion().toString();
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
            throw new UCoinTechnicalException("Crypto lib (NaCL) initialization failed. Make sure sodium has been installed (or add library into a ./lib directory).", e);
        }
    }
}
