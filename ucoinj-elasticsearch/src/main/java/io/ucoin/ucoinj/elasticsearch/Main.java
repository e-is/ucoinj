package io.ucoin.ucoinj.elasticsearch;

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


import com.google.common.collect.Lists;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.core.util.CollectionUtils;
import io.ucoin.ucoinj.core.util.CommandLinesUtils;
import io.ucoin.ucoinj.core.util.StringUtils;
import io.ucoin.ucoinj.elasticsearch.config.Configuration;
import io.ucoin.ucoinj.elasticsearch.config.ConfigurationAction;
import io.ucoin.ucoinj.elasticsearch.service.ServiceLocator;
import io.ucoin.ucoinj.elasticsearch.util.Desktop;
import io.ucoin.ucoinj.elasticsearch.util.DesktopPower;
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
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Main main = new Main();
        main.run(args);
    }

    public void run(String[] args) {
        if (log.isInfoEnabled()) {
            log.info("Starting uCoinj :: ElasticSearch Indexer with arguments " + Arrays.toString(args));
        }

        // By default, display help
        if (args == null || args.length == 0) {
            args = new String[] { "--help" };
        }

        List<String> arguments = Lists.newArrayList(Arrays.asList(args));
        arguments.removeAll(Arrays.asList(ConfigurationAction.HELP.aliases));

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
            throw new TechnicalException("i18n initialization failed", e);
        }

        // Add hook on system
        addShutdownHook();

        // Run all actions
        try {
            config.getApplicationConfig().doAllAction();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (arguments.size() > 0) {

            // Check if auto-quit if need
            boolean quit = true;
            for (String startAlias: ConfigurationAction.START.aliases) {
                if (arguments.contains(startAlias)) {
                    quit = false;
                    break;
                }
            }

            // If scheduling is running, wait quit instruction
            if (!quit) {
                while (!quit) {
                    String userInput = CommandLinesUtils.readInput("*** uCoinj :: Elasticsearch successfully started *** >> To quit, press [Q] or enter\n", "Q", true);
                    quit = StringUtils.isNotBlank(userInput) && "Q".equalsIgnoreCase(userInput);
                }
            }
        }

        // shutdown
        shutdown();

        log.info("uCoinj :: ElasticSearch Indexer successfully stopped");
        //System.exit(-1);
    }

    /* -- protected methods -- */

    /**
     * Shutdown all services
     */
    protected static void shutdown() {
        if (ServiceLocator.instance() != null) {
            try {
                ServiceLocator.instance().close();
            }
            catch(IOException e) {
                // Silent is gold
            }
        }
    }

    protected void initI18n(Configuration config) throws IOException {

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

    protected String getI18nBundleName() {
        return "ucoinj-elasticsearch-i18n";
    }

    /**
     * Add an OS shutdown hook, to close application on shutdown
     */
    private void addShutdownHook() {

        // Use shutdownHook to close context on System.exit
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                shutdown();
            }
        }));

        // Add DesktopPower to hook computer shutdown
        DesktopPower desktopPower = Desktop.getDesktopPower();
        if (desktopPower != null) {

            desktopPower.addListener(new DesktopPower.Listener() {
                @Override
                public void quit() {
                   if (ServiceLocator.instance() != null) {
                       try {
                           ServiceLocator.instance().close();
                       }
                       catch(IOException e) {
                           // Silent is gold
                       }
                   }
                }
            });
        }

    }
}
