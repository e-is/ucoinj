package io.ucoin.client.core.service;

import io.ucoin.client.core.TestResource;
import io.ucoin.client.core.model.WotLookupResults;
import io.ucoin.client.core.model.WotLookupUId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

public class WotServiceTest {

    private static final Log log = LogFactory.getLog(WotServiceTest.class);
    @ClassRule
    public static final TestResource resource = TestResource.create();
    
    @Test
    public void find() throws Exception {

        WotService service = new WotService();
        WotLookupResults results = service.find(resource.getFixtures().getUid());
        Assert.assertNotNull(results);

        // close
        service.close();
    }

    @Test
    public void findByUid() throws Exception {

        WotService service = new WotService();
        WotLookupUId result = service.findByUid(resource.getFixtures().getUid());
        Assert.assertNotNull(result);

        // close
        service.close();
    }
}
