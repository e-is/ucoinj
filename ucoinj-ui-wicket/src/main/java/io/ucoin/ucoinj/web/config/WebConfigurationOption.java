package io.ucoin.ucoinj.web.config;

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
public enum WebConfigurationOption implements ConfigOptionDef {

    // ------------------------------------------------------------------------//
    // -- READ-WRITE OPTIONS ---------------------------------------------------//
    // ------------------------------------------------------------------------//

    SECURITY_TYPE(
            "ucoinj.security.type",
            n("ucoinj.config.option.security.type.description"),
            "pubkey",
            String.class,
            false),

    USER_SALT(
            "ucoinj.salt",
            n("ucoinj.config.option.salt.description"),
            "",
            String.class,
            false),

    USER_PASSWORD(
            "ucoinj.password",
            n("ucoinj.config.option.password.description"),
            "",
            String.class,
            false),

    USER_PUBKEY(
            "ucoinj.pubkey",
            n("ucoinj.config.option.pubkey.description"),
            "",
            String.class,
            false),
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

    WebConfigurationOption(String key,
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

    WebConfigurationOption(String key,
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
