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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuiton.config.ApplicationConfig;
import org.nuiton.i18n.I18n;
import org.nuiton.i18n.init.DefaultI18nInitializer;
import org.nuiton.i18n.init.UserI18nInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class WebConfiguration extends Configuration {

    private static final String CONFIG_FILE_NAME = "ucoin-web.config";

    private static final String CONFIG_FILE_ENV_PROPERTY = CONFIG_FILE_NAME;

    private static final String CONFIG_FILE_JNDI_NAME = "java:comp/env/" + CONFIG_FILE_NAME;


    /* Logger */
    private static final Logger log = LoggerFactory.getLogger(WebConfiguration.class);

    static {
        instance = new WebConfiguration(getWebConfigFile());
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

        // Init i18n
        /*try {
            initI18n();
        } catch (IOException e) {
            throw new UCoinTechnicalException("i18n initialization failed", e);
        }*/
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

    protected void initI18n() throws IOException {

        // --------------------------------------------------------------------//
        // init i18n
        // --------------------------------------------------------------------//
        File i18nDirectory = new File(getDataDirectory(), "i18n");
        if (i18nDirectory.exists()) {
            // clean i18n cache
            FileUtils.cleanDirectory(i18nDirectory);
        }

        FileUtils.forceMkdir(i18nDirectory);

        if (log.isDebugEnabled()) {
            log.debug("I18N directory: " + i18nDirectory);
        }

        Locale i18nLocale = getI18nLocale();

        if (log.isInfoEnabled()) {
            log.info(String.format("Starts i18n with locale [%s] at [%s]",
                i18nLocale, i18nDirectory));
        }
        I18n.init(new UserI18nInitializer(
            i18nDirectory, new DefaultI18nInitializer(getI18nBundleName())),
            i18nLocale);
    }

    protected static String getI18nBundleName() {
        return "ucoin-web-i18n";
    }

}
