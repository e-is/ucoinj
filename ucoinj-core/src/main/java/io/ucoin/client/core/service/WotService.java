package io.ucoin.client.core.service;

import io.ucoin.client.core.model.WotIdentityCertifications;
import io.ucoin.client.core.model.WotLookupResult;
import io.ucoin.client.core.model.WotLookupResults;
import io.ucoin.client.core.model.WotLookupUId;
import io.ucoin.client.core.technical.UCoinTechnicalException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;

public class WotService extends AbstractService {

    private static final Log log = LogFactory.getLog(WotService.class);

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
