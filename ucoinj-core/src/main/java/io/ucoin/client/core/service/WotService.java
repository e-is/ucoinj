package io.ucoin.client.core.service;

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


import io.ucoin.client.core.model.*;
import io.ucoin.client.core.technical.UCoinTechnicalException;
import io.ucoin.client.core.technical.crypto.SecretBox;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WotService extends AbstractNetworkService {

    private static final Logger log = LoggerFactory.getLogger(WotService.class);

    public WotService() {
        super();
    }

    public WotLookupResults find(String uidPattern) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Try to find user info by uid: %s", uidPattern));
        }

        // get parameter
        String path = String.format(ProtocolUrls.WOT_LOOKUP, uidPattern);
        HttpGet lookupHttpGet = new HttpGet(getAppendedPath(path));
        WotLookupResults lookupResult = executeRequest(lookupHttpGet, WotLookupResults.class);

        return lookupResult;

    }

    public WotLookupUId findByUid(String uid) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Try to find user info by uid: %s", uid));
        }

        // call lookup
        String path = String.format(ProtocolUrls.WOT_LOOKUP, uid);
        HttpGet lookupHttpGet = new HttpGet(getAppendedPath(path));
        WotLookupResults lookupResults = executeRequest(lookupHttpGet, WotLookupResults.class);

        // Retrieve the exact uid
        WotLookupUId uniqueResult = getUid(lookupResults, uid);
        if (uniqueResult == null) {
            throw new UCoinTechnicalException("User not found, with uid=" + uid);
        }
        
        return uniqueResult;
    }
    
    public WotIdentityCertifications getCertifiedBy(String uid) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Try to get certifications done by uid: %s", uid));
        }

        // call certified-by
        String path = String.format(ProtocolUrls.WOT_CERTIFIED_BY, uid);
        HttpGet httpGet = new HttpGet(getAppendedPath(path));
        WotIdentityCertifications result = executeRequest(httpGet, WotIdentityCertifications.class);
        
        return result;

    }
    
    public WotIdentityCertifications getCertifiersOf(String uid) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Try to get certifications done to uid: %s", uid));
        }

        // call certifiers-of
        String path = String.format(ProtocolUrls.WOT_CERTIFIERS_OF, uid);
        HttpGet httpGet = new HttpGet(getAppendedPath(path));
        WotIdentityCertifications result = executeRequest(httpGet, WotIdentityCertifications.class);
        
        return result;

    }
    
	public void sendSelf(String uid, SecretBox secretBox) throws Exception {
		// http post /wot/add  
        HttpPost httpPost = new HttpPost(getAppendedPath(ProtocolUrls.WOT_ADD));
        
        // compute the self-certification
		String selfCertification = getSelfCertification(secretBox, uid); 
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("pubkey", secretBox.getPublicKey()));
		urlParameters.add(new BasicNameValuePair("self", selfCertification));
		urlParameters.add(new BasicNameValuePair("other", ""));
 
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));		
                
        String selfResult = executeRequest(httpPost, String.class);
        log.info("received from /add: " + selfResult);
	}
	
	public String getSelfCertification(SecretBox secretBox, String uid) throws Exception {
		return getSelfCertification(secretBox, uid, new Date().getTime());
		
	}

	public String getSelfCertification(SecretBox secretBox, String uid, long timestamp) throws Exception {
		// Create the self part to sign
        StringBuilder buffer = new StringBuilder()
                .append("UID:")
                .append(uid)
                .append("\nMETA:TS:")
                .append(timestamp)
                .append('\n');

        // Compute the signature
        String signature = secretBox.sign(buffer.toString());

        // Append the signature
        return buffer.append(signature)
                .append('\n')
                .toString();		
	}
	
	public String getCertification(SecretBox secretBox, String userUid, 
			long userTimestamp, 
			String userSignature) throws Exception {
		
		BlockchainService blockchainServcie = ServiceLocator.instance().getBlockchainService();
		BlockchainBlock currentBlock = blockchainServcie.getCurrentBlock();
		
		return getCertification(secretBox, userUid, userTimestamp, userSignature,
				currentBlock.getNumber(),
				currentBlock.getHash());	
	}
	
	public String getCertification(SecretBox secretBox, String userUid, 
			long userTimestamp, 
			String userSignature,
			int blockNumber,
			String blockHash) throws Exception {
		// Create the self part to sign
        StringBuilder buffer = new StringBuilder()
                .append("UID:")
                .append(userUid)
                .append("\nMETA:TS:")
                .append(userTimestamp)
                .append('\n')
                .append(userSignature)
                .append("\nMETA:TS:")
                .append(blockNumber)
                .append('-')
                .append(blockHash)
                .append('\n');

        // Compute the signature
        String signature = secretBox.sign(buffer.toString());

        // Append the signature
        return buffer.append(signature)
                .append('\n')
                .toString();		
	}

    /* -- Internal methods -- */

    protected WotLookupUId getUid(WotLookupResults lookupResults, String filterUid) {
        if (CollectionUtils.isEmpty(lookupResults.getResults())) {
            return null;
        }

        for (WotLookupResult result : lookupResults.getResults()) {
            if (CollectionUtils.isNotEmpty(result.getUids())) {
                for (WotLookupUId uid : result.getUids()) {
                    if (filterUid.equals(uid.getUid())) {
                        return uid;
                    }
                }
            }
        }
        
        return null;
    }

}
