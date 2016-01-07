package io.ucoin.ucoinj.web.security.keypair;

import io.ucoin.ucoinj.core.service.CryptoService;
import io.ucoin.ucoinj.web.service.ServiceLocator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;

/**
 * Created by blavenie on 06/01/16.
 */
public class PubkeyAuthenticationProvider implements AuthenticationProvider, InitializingBean{

    private AuthenticationUserDetailsService authenticationUserDetailsService;
    private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
    private CryptoService cryptoService;
    private ChallengeMessageStore challengeMessageStore;

    public PubkeyAuthenticationProvider() {
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.authenticationUserDetailsService, "An authenticationUserDetailsService must be set");
        cryptoService = cryptoService == null ? ServiceLocator.instance().getCryptoService() : cryptoService;
        Assert.notNull(this.cryptoService, "An cryptoService must be set");
        Assert.notNull(this.challengeMessageStore, "An challengeMessageStore must be set");
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(!this.supports(authentication.getClass())) {
            return null;
        } else if(authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)authentication;
            if (token.getPrincipal() == null || token.getCredentials() == null) {
                throw new BadCredentialsException("Invalid authentication token. Missing principal or credentials");
            }
            String pubkey = token.getPrincipal().toString();

            checkSignature(token.getPrincipal().toString(), token.getCredentials().toString());
            PubkeyAuthenticationToken result = new PubkeyAuthenticationToken(pubkey);

            UserDetails userDetails = authenticationUserDetailsService.loadUserDetails(result);
            if (userDetails != null) {
                this.userDetailsChecker.check(userDetails);
                result = new PubkeyAuthenticationToken(pubkey, userDetails.getAuthorities());
                result.setAuthenticated(true);
                result.setDetails(userDetails);
            }
            return result;
        }
        throw new BadCredentialsException("Failed to authenticate this token. Unknown token class.");
    }

    public void setAuthenticationUserDetailsService(AuthenticationUserDetailsService authenticationUserDetailsService) {
        this.authenticationUserDetailsService = authenticationUserDetailsService;
    }

    public void setCryptoService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public void setChallengeMessageStore(ChallengeMessageStore challengeMessageStore) {
        this.challengeMessageStore = challengeMessageStore;
    }

    public boolean supports(Class<? extends Object> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /*-- internal method --*/

    private boolean checkSignature(String pubkey, String sm) throws BadCredentialsException{

        if (sm.indexOf('|') == -1) {
            throw new BadCredentialsException("Invalid password. Must be <signature>|<message>");
        }
        String signature = sm.substring(0, sm.indexOf('|'));
        String message = sm.substring(sm.indexOf('|')+1);

        boolean valid = challengeMessageStore.validateChallenge(message)
                && cryptoService.verify(message, signature, pubkey);
        if (!valid) {
            throw new BadCredentialsException("Invalid signature. Not signed by this pubkey or message to signed expired.");
        }

        return true;
    }
}
