package io.ucoin.client.core.action;

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

public class HelpAction {

	public void show() {
		StringBuilder sb = new StringBuilder();

		sb.append("Usage:  <commands> <options>\n")
				.append("with <commands>:\n")
				.append(" -h --help                                  Display help\n")
				.append("    --import-data                           Import raw data, from the Adagio central database\n")
				.append("\n")
				.append("with <options>:\n")
				.append(" -h --host <user>		           Node host\n")
				.append(" -p --port <pwd> 		           Node port\n")
				.append("\n")
				.append(" -esh  --es-host <user>            ElasticSearch host\n")
				.append(" -esp  --es-port <pwd>             ElasticSearch port\n");

		System.out.println(sb.toString());
	}
}
