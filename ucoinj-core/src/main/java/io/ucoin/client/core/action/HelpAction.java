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

public class HelpAction {

	public void show() {
		StringBuilder sb = new StringBuilder();

		sb.append("Usage:  <commands> <options>\n")
				.append("with <commands>:\n")
				.append(" -h --help                                  Display help\n")
				.append("    --schema-update                         Run database schema update\n")
				.append("    --schema-status    --output <file>      Generate a database status report (pending schema changes)\n")
				.append("    --schema-diff      --output <file>      Generate a database schema diff report (compare database to adagio data model)\n")
				.append("    --schema-changelog --output <file>      Generate a diff into a changelog XML file (compare database to adagio data model)\n")
				.append("    --new-db           --output <directory> Generate a empty Allegro database\n")
				.append("    --import-ref                            Import referential data, from the Adagio central database\n")
				.append("    --import-data                           Import raw data, from the Adagio central database\n")
				.append("\n")
				.append("with <options>:\n")
				.append(" -u --user <user>		           Database user\n")
				.append(" -p --password <pwd> 		       Database password\n")
				.append(" -db --database <db_url> 	       Database JDBC URL ()\n")
				.append("\n")
				.append(" -iu  --import-user <user>        Imported database user\n")
				.append(" -ip  --import-password <pwd>     Imported database password\n")
				.append(" -idb --import-database <db_url>  Imported database JDBC URL\n");

		System.out.println(sb.toString());
	}
}
