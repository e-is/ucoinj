package io.ucoin.ucoinj.web.application;

/*
 * #%L
 * UCoin Java Client :: Web
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


import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

public class WebSession extends AuthenticatedWebSession {
    final static Logger log = LoggerFactory.getLogger(WebSession.class);

    private static final long serialVersionUID = 1L;

    public WebSession(Request request) {
        super(request);
    }

    @Override
    public Roles getRoles() {
        return null;
    }

    @Override
    public boolean authenticate(String username, String password) throws AuthenticationException {
        /*ServiceLocator serviceLocator = ServiceLocator.instance();
        boolean authenticated;
        try {
            Authentication authentication = serviceLocator.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            authenticated = authentication.isAuthenticated();
        } catch (AuthenticationException e) {
            String errorMessage = String.format("Authentication failed for user '%s' with error : %s", username, e.getLocalizedMessage());
            if (log.isDebugEnabled()) {
                log.warn(errorMessage, e);
            }
            else {
                log.warn(errorMessage, e);
            }
            throw e;
        }
        return authenticated;
        */
        return true;
    }



}