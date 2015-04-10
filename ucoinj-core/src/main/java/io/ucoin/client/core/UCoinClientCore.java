package io.ucoin.client.core;

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


import io.ucoin.client.core.config.Configuration;
import io.ucoin.client.core.technical.UCoinTechnicalException;
import org.apache.commons.io.FileUtils;
import org.nuiton.config.ApplicationConfig;
import org.nuiton.i18n.I18n;
import org.nuiton.i18n.init.DefaultI18nInitializer;
import org.nuiton.i18n.init.UserI18nInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

public class UCoinClientCore {

    private static final Logger log = LoggerFactory.getLogger(UCoinClientCore.class);
    

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
