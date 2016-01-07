package io.ucoin.ucoinj.web.security.keypair;

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
