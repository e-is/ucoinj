package io.ucoin.ucoinj.web.config;

/*
 * #%L
 * Tutti :: Persistence
 * $Id: TuttiConfigurationProvider.java 1418 2013-12-01 21:18:22Z tchemit $
 * $HeadURL: http://svn.forge.codelutin.com/svn/tutti/trunk/tutti-persistence/src/main/java/fr/ifremer/tutti/TuttiConfigurationProvider.java $
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

import io.ucoin.ucoinj.elasticsearch.config.ConfigurationAction;
import io.ucoin.ucoinj.elasticsearch.config.ConfigurationOption;
import org.nuiton.config.ApplicationConfigProvider;
import org.nuiton.config.ConfigActionDef;
import org.nuiton.config.ConfigOptionDef;

import java.util.Locale;

import static org.nuiton.i18n.I18n.l;

/**
 * Config provider (for site generation).
 * 
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 */
public class WebConfigurationProvider implements ApplicationConfigProvider {

	@Override
	public String getName() {
		return "ucoinj-web";
	}

	@Override
	public String getDescription(Locale locale) {
		return l(locale, "ucoinj-web.config");
	}

	@Override
	public ConfigOptionDef[] getOptions() {
		return WebConfigurationOption.values();
	}

	@Override
	public ConfigActionDef[] getActions() {
		return new ConfigActionDef[0];
	}	
}
