package io.ucoin.ucoinj.web.security.keypair;

/*
 * #%L
 * SIH-Adagio :: UI for Core Allegro
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


import io.ucoin.ucoinj.core.util.ObjectUtils;
import io.ucoin.ucoinj.web.config.WebConfiguration;
import io.ucoin.ucoinj.web.security.UcoinjUserDetailsImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @author Benoit Lavenier
 */
@Service("pubkeyAuthenticationUserDetailsService")
@Lazy
public class PubkeyUserDetailsServiceImpl implements AuthenticationUserDetailsService<PubkeyAuthenticationToken> {

    @Resource
    WebConfiguration config;

    public UserDetails loadUserDetails(PubkeyAuthenticationToken pubkeyAuthentication) throws UsernameNotFoundException {
        String pubkey = pubkeyAuthentication.getPubkey();
        boolean isAdmin = ObjectUtils.equals(pubkey, config.getUserPubkey());

        return new UcoinjUserDetailsImpl(pubkey, isAdmin);
    }
}
