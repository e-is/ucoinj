package io.ucoin.ucoinj.web.security;

/*
 * #%L
 * SIH-Adagio :: UI for Core Allegro
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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHelper {

	public static final String USER_ANONYMOUS = "anonymousUser";

	public static String getPrincipalPubkey() {
		String pubkey = getPrincipal();
		if (pubkey == null) {
			return null;
		}
		if (USER_ANONYMOUS.equals(pubkey)) {
			return null;
		}
		return pubkey;
	}

	public static String getPrincipal() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication.getPrincipal() == null) {
			return null;
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof String) {
			return (String) principal;
		} else {
			return null;
		}
	}

	/**
	 * @return
	 */
	public static boolean isAuthenticateNotAnonymous() {
		return getPrincipalPubkey() != null;
	}

	/**
	 * @return
	 */
	public static boolean isAuthenticate() {
		return getPrincipal() != null;
	}


	/**
	 * @return
	 */
	public static boolean isAuthenticateAnonymous() {
		return getPrincipalPubkey() == null;
	}

}
