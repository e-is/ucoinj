package io.ucoin.client.core;

/*
 * #%L
 * UCoin Java Client :: Core API
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



public class TestFixtures {

	public String getCurrency() {
        return "meta_brouzouf";
    }
	
    public String getUid() {
        return "cgeek";
    }

    public String getUserSalt() {
        return "abc";
    }

    public String getUserPassword() {
        return "def";
    }

    /**
     * Seed, compute from Scrypt (salt and password) encode in Base64.<br/>
     * Should correspond to the user's salt and password
     * @return
     */
    public String getUserSeedHash() {
        return "zmPpDdTD0Q+yNgbqWFq5yHgVUny2KIDtbVHz/hNuwew=";
    }
    
    /**
     * Return the user's public key, encode in base 58.<br/>
     * Should correspond to the user's salt and password
     * @return
     */
    public String getUserPublicKey() {
    	return "G2CBgZBPLe6FSFUgpx2Jf1Aqsgta6iib3vmDRA1yLiqU";
    }
    
    /**
     * return the user's private key, encode in base 58.<br/>
     * Should correspond to the user's salt and password
     * @return
     */
    public String getUserSecretKey() {
        return "58LDg8QLmF5pv6Dn9h7X4yFKfMTdP8fdAiWVcyDoTRJu454fwRihCLULH4MW37zncsg4ruoTGJPZneWk22QmG1w4";
    }
    
    /**
     * A signature, that corespond to getSelfTimestamp(), getUid()
     * @return
     */
    public String getSelfSignature() {
    	return "TMgQysT7JwY8XwemskwWb8LBDJybLUsnxqaaUvSteIYpOxRiB92gkFQQcGpBwq4hAwhEiqBAiFkiXIozppDDDg==";
    }
    
    public long getSelfTimestamp() {
    	return 1420881879;
    }
    
    /**
     * Get a public key of another user, encode in base 58.
     * @return
     */
    public String getOtherUserPublicKey() {
    	// = kimamila
        return "5ocqzyDMMWf1V8bsoNhWb1iNwax1e9M7VTUN6navs8of";
    }

}
