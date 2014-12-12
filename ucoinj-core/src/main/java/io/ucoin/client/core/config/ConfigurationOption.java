package io.ucoin.client.core.config;

/*
 * #%L
 * Tutti :: Persistence
 * $Id: TuttiConfigurationOption.java 1441 2013-12-09 20:13:47Z tchemit $
 * $HeadURL: http://svn.forge.codelutin.com/svn/tutti/trunk/tutti-persistence/src/main/java/fr/ifremer/tutti/TuttiConfigurationOption.java $
 * %%
 * Copyright (C) 2012 - 2013 Ifremer
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.nuiton.i18n.I18n.n;

import java.io.File;
import java.net.URL;
import java.util.Locale;

import org.nuiton.config.ConfigOptionDef;
import org.nuiton.util.Version;

/**
 * All application configuration options.
 * 
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 */
public enum ConfigurationOption implements ConfigOptionDef {

    // ------------------------------------------------------------------------//
    // -- READ-ONLY OPTIONS ---------------------------------------------------//
    // ------------------------------------------------------------------------//

    BASEDIR(
            "ucoinj.basedir",
            n("ucoinj.config.option.basedir.description"),
            "${user.home}/.ucoinj",
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

    TMP_DIRECTORY(
            "ucoinj.tmp.directory",
            n("ucoinj.config.option.tmp.directory.description"),
            "${ucoinj.data.directory}/temp",
            File.class),

    CACHE_DIRECTORY(
            "ucoinj.cache.directory",
            n("ucoinj.config.option.cache.directory.description"),
            "${ucoinj.data.directory}/cache",
            File.class),

    VERSION(
            "ucoinj.version",
            n("ucoinj.config.option.version.description"),
            "1.0",
            Version.class),

    SITE_URL(
            "ucoinj.site.url",
            n("ucoinj.config.option.site.url.description"),
            "http://ucoin.io/ucoinj",
            URL.class),

    ORGANIZATION_NAME(
            "ucoinj.organizationName",
            n("ucoinj.config.option.organizationName.description"),
            "e-is.pro",
            String.class),

    INCEPTION_YEAR(
            "ucoinj.inceptionYear",
            n("ucoinj.config.option.inceptionYear.description"),
            "2011",
            Integer.class),

    USER_SALT(
            "ucoinj.salt",
            n("ucoinj.config.option.salt.description"),
            "",
            String.class),

    USER_PASSWD(
            "ucoinj.passwd",
            n("ucoinj.config.option.passwd.description"),
            "",
            String.class),

    // ------------------------------------------------------------------------//
    // -- DATA CONSTANTS --------------------------------------------------//
    // ------------------------------------------------------------------------//

    // ------------------------------------------------------------------------//
    // -- READ-WRITE OPTIONS --------------------------------------------------//
    // ------------------------------------------------------------------------//

    I18N_LOCALE(
            "ucoinj.i18n.locale",
            n("ucoinj.config.option.i18n.locale.description"),
            Locale.FRANCE.getCountry(),
            Locale.class,
            false),

    NODE_CURRENCY(
            "ucoinj.node.currency",
            n("ucoinj.config.option.node.currency.description"),
            "zeta_brouzouf",
            String.class,
            false),

    NODE_PROTOCOL(
            "ucoinj.node.protocol",
            n("ucoinj.config.option.node.protocol.description"),
            "http",
            String.class,
            false),

    NODE_HOST(
            "ucoinj.node.host",
            n("ucoinj.config.option.node.host.description"),
            "twiced.fr",
            String.class,
            false),

    NODE_PORT(
            "ucoinj.node.port",
            n("ucoinj.config.option.node.port.description"),
            "9101",
            Integer.class,
            false),

    NODE_URL(
            "ucoinj.node.url",
            n("ucoinj.config.option.node.port.description"),
            "${ucoinj.node.protocol}://${ucoinj.node.host}:${ucoinj.node.port}",
            URL.class,
            false),

    NODE_TIMEOUT(
            "ucoinj.node.timeout",
            n("ucoinj.config.option.node.timeout.description"),
            "1500",
            Integer.class,
            false);

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
