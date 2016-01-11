package io.ucoin.ucoinj.web.security.keypair;

/*
 * #%L
 * uCoinj :: UI Wicket
 * %%
 * Copyright (C) 2014 - 2016 EIS
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

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by blavenie on 06/01/16.
 */
public class PubkeyAuthenticationToken extends AbstractAuthenticationToken {

    private final String pubkey;

    public PubkeyAuthenticationToken(String pubkey) {
        super(null);
        this.pubkey = pubkey;
    }

    public PubkeyAuthenticationToken(String pubkey, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.pubkey = pubkey;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return pubkey;
    }

    @Override
    public Object getPrincipal() {
        return pubkey;
    }

    public String getPubkey() {
        return pubkey;
    }
}
