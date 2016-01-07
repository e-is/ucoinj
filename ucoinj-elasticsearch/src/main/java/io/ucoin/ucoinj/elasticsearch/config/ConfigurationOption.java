package io.ucoin.ucoinj.elasticsearch.config;

/*
 * #%L
 * Tutti :: Persistence
 * $Id: TuttiConfigurationOption.java 1441 2013-12-09 20:13:47Z tchemit $
 * $HeadURL: http://svn.forge.codelutin.com/svn/tutti/trunk/tutti-persistence/src/main/java/fr/ifremer/tutti/TuttiConfigurationOption.java $
 * %%
 * Copyright (C) 2012 - 2013 Ifremer
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

import org.nuiton.config.ConfigOptionDef;
import org.nuiton.util.Version;

import java.io.File;
import java.net.URL;
import java.util.Locale;

import static org.nuiton.i18n.I18n.n;

/**
 * All application configuration options.
 * 
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 */
public enum ConfigurationOption  implements ConfigOptionDef {


    // ------------------------------------------------------------------------//
    // -- READ-ONLY OPTIONS ---------------------------------------------------//
    // ------------------------------------------------------------------------//


    BASEDIR(
            "ucoinj.basedir",
            n("ucoinj.config.option.basedir.description"),
            "${user.home}/.ucoinj-elasticsearch",
            File.class),

    DATA_DIRECTORY(
            "ucoinj.data.directory",
            n("ucoinj.config.option.data.directory.description"),
            "${ucoinj.basedir}/data",
            File.class),

    I18N_DIRECTORY(
            "ucoinj.i18n.directory",
            n("ucoinj.config.option.i18n.directory.description"),
            "${ucoinj.basedir}/i18n",
            File.class),

    VERSION(
            "ucoinj.version",
            n("ucoinj.config.option.version.description"),
            "1.0",
            Version.class),

    // ------------------------------------------------------------------------//
    // -- READ-WRITE OPTIONS ---------------------------------------------------//
    // ------------------------------------------------------------------------//

    I18N_LOCALE(
            "ucoinj.i18n.locale",
            n("ucoinj.config.option.i18n.locale.description"),
            Locale.FRANCE.getCountry(),
            Locale.class,
            false),

    NODE_BMA_HOST(
            "ucoinj.node.host",
            n("ucoinj.config.option.node.host.description"),
            "metab.ucoin.io",
            String.class,
            false),

    NODE_BMA_PORT(
            "ucoinj.node.port",
            n("ucoinj.config.option.node.port.description"),
            "9201",
            Integer.class,
            false),

    NODE_BMA_URL(
            "ucoinj.node.url",
            n("ucoinj.config.option.node.port.description"),
            "${ucoinj.node.protocol}://${ucoinj.node.host}:${ucoinj.node.port}",
            URL.class,
            false),

    HOST(
            "ucoinj.elasticsearch.host",
            n("ucoinj.config.option.elasticsearch.host.description"),
            "localhost",
            String.class,
            false),

    PORT(
            "ucoinj.elasticsearch.port",
            n("ucoinj.config.option.node.elasticsearch.port.description"),
            "9300",
            Integer.class,
            false),

    EMBEDDED_ENABLE(
            "ucoinj.elasticsearch.embedded.enable",
            n("ucoinj.config.option.elasticsearch.embedded.enable.description"),
            "false",
            Boolean.class,
            false),

    LOCAL_ENABLE(
            "ucoinj.elasticsearch.local",
            n("ucoinj.config.option.elasticsearch.local.description"),
            "false",
            Boolean.class,
            false),

    HTTP_ENABLE(
            "ucoinj.elasticsearch.http.enable",
            n("ucoinj.config.option.node.elasticsearch.http.enable.description"),
            "true",
            Boolean.class,
            false),

    CLUSTER_NAME(
            "ucoinj.elasticsearch.cluster.name",
            n("ucoinj.config.option.elasticsearch.cluster.name.description"),
            "ucoinj-elasticsearch",
            String.class,
            false),

    INDEX_BULK_ENABLE(
            "ucoinj.elasticsearch.bulk.enable",
            n("ucoinj.config.option.elasticsearch.bulk.enable.description"),
            "true",
            Boolean.class,
            false),

    INDEX_BULK_SIZE(
            "ucoinj.elasticsearch.bulk.size",
            n("ucoinj.config.option.elasticsearch.bulk.size.description"),
            "1000",
            Integer.class,
            false),

    TASK_EXECUTOR_QUEUE_CAPACITY(
            "ucoinj.elasticsearch.tasks.queueCapacity",
            n("ucoinj.config.option.tasks.queueCapacity.description"),
            "50",
            Integer.class,
            false),

    TASK_EXECUTOR_TIME_TO_IDLE(
            "ucoinj.elasticsearch.tasks.timeToIdle",
            "ucoinj.elasticsearch.tasks.timeToIdle.description",
            "180", // 180s = 3min
            Integer.class,
            false)
    ;

    /** Configuration key. */
    private final String key;

    /** I18n key of option description */
    private final String description;

    /** Type of option */
    private final Class<?> type;

    /** Default value of option. */
    private String defaultValue;

    /** Flag to not keep option value on disk */
    private boolean isTransient;

    /** Flag to not allow option value modification */
    private boolean isFinal;

    ConfigurationOption(String key,
                        String description,
                        String defaultValue,
                        Class<?> type,
                        boolean isTransient) {
        this.key = key;
        this.description = description;
        this.defaultValue = defaultValue;
        this.type = type;
        this.isTransient = isTransient;
        this.isFinal = isTransient;
    }

    ConfigurationOption(String key,
                        String description,
                        String defaultValue,
                        Class<?> type) {
        this(key, description, defaultValue, type, true);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isTransient() {
        return isTransient;
    }

    @Override
    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public void setTransient(boolean newValue) {
        // not used
    }

    @Override
    public void setFinal(boolean newValue) {
        // not used
    }
}
