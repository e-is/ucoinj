package io.ucoin.client.core.config;

/*
 * #%L
 * SIH-Adagio :: Shared
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

import io.ucoin.client.core.action.CommunityAction;
import io.ucoin.client.core.action.HelpAction;
import io.ucoin.client.core.action.IndexerAction;
import org.nuiton.config.ConfigActionDef;

public enum ConfigurationAction implements ConfigActionDef {

	HELP(HelpAction.class.getName() + "#show", "--help"),

    COMMUNITY_MEMBERS(CommunityAction.class.getName() + "#members", "--members"),

    INDEX_ALL_BLOCK(IndexerAction.class.getName() + "#indexAllBlocks", "--index-blocks"),

    INDEX_LAST_BLOCK(IndexerAction.class.getName() + "#indexLastBlocks", "--update-last-blocks");

	public String action;
	public String[] aliases;

	private ConfigurationAction(String action, String... aliases) {
		this.action = action;
		this.aliases = aliases;
	}

	@Override
	public String getAction() {
		return action;
	}

	@Override
	public String[] getAliases() {
		return aliases;
	}
}
