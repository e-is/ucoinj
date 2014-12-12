package io.ucoin.client.core.service;

import io.ucoin.client.core.TestConfig;
import io.ucoin.client.core.model.WotLookupResults;
import io.ucoin.client.core.model.WotLookupUId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

public class WotServiceTest {

    private static final Log log = LogFactory.getLog(WotServiceTest.class);
    
    @Test
    public void find() throws Exception {

        WotService service = new WotService(TestConfig.getNodeUrl());
        WotLookupResults results = service.find(TestConfig.getUid());
        Assert.assertNotNull(results);

        // close
        service.close();
    }

    @Test
    public void findByUid() throws Exception {

        WotService service = new WotService(TestConfig.getNodeUrl());
        WotLookupUId result = service.findByUid(TestConfig.getUid());
        Assert.assertNotNull(result);

        // close
        service.close();
    }
}
