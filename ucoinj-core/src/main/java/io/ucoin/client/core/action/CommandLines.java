package io.ucoin.client.core.action;

/*
 * #%L
 * SIH-Adagio :: Core for Allegro
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

import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

public class CommandLines {

	protected CommandLines() {

	}

	public static String readNotBlankInput(String message) {
		String value = readInput(message, null, true);
		return value;
	}

	public static String readInput(String message, String defaultValue, boolean mandatory) {

		Scanner scanIn = new Scanner(System.in);
		String inputValue = null;
		while (inputValue == null) {
			System.out.print(message.trim());
			if (StringUtils.isNotEmpty(defaultValue)) {
				System.out.print(String.format(" [%s]", defaultValue));
			}
			System.out.print(": ");
			inputValue = scanIn.nextLine();
			if (StringUtils.isBlank(inputValue)) {
				// A default exists: use it
				if (StringUtils.isNotEmpty(defaultValue)) {
					inputValue = defaultValue;
				}
				// No default value, but mandatory: prepare for a new iteration
				else if (mandatory) {
					inputValue = null;
				}
			}
		}
		// scanIn.close();

		return inputValue;
	}

}
