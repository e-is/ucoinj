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


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 */
public class UcoinjUserDetailsImpl implements UcoinjUserDetails {

    private static final long serialVersionUID = 1L;
    
    private String pubkey;
    
    private String password = "";

    private Collection<? extends GrantedAuthority> authorities;

    public UcoinjUserDetailsImpl(String pubkey, boolean isAdmin) {
        this.pubkey = pubkey;
        this.password = "";
        this.authorities = isAdmin
                ? createAllAuthorities()
                : createAuthorities(Sets.newHashSet(UcoinjGrantedAuthority.ROLE_USER.name()));
    }

    @Override
    public String getPubkey() {
        return pubkey;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return pubkey;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /* -- Internal methods -- */

    protected Collection<? extends GrantedAuthority> createAllAuthorities() {
        List<SimpleGrantedAuthority> authorities = Lists.newArrayList(
                new SimpleGrantedAuthority(UcoinjGrantedAuthority.ROLE_ADMIN.name()),
                new SimpleGrantedAuthority(UcoinjGrantedAuthority.ROLE_USER.name())
        );
        return authorities;
    }

    protected Collection<? extends GrantedAuthority> createAuthorities(Set<String> roles) {
        List<SimpleGrantedAuthority> authorities = Lists.newArrayListWithExpectedSize(roles.size());
        for (String role: roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
