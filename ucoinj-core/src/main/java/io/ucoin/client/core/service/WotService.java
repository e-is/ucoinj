package io.ucoin.client.core.service;

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

        // get parameter
        String path = String.format(ProtocolUrls.WOT_LOOKUP, uid);
        HttpGet lookupHttpGet = new HttpGet(getAppendedPath(path));
        WotLookupResults lookupResults = executeRequest(lookupHttpGet, WotLookupResults.class);

        WotLookupUId uniqueResult = getUid(lookupResults, uid);
        if (uniqueResult == null) {
            throw new UCoinTechnicalException("User not found, with uid=" + uid);
        }
        
        return uniqueResult;
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
