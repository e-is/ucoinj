package io.ucoin.client.core.service;

import io.ucoin.client.core.TestResource;
import io.ucoin.client.core.model.BlockchainBlock;
import io.ucoin.client.core.model.BlockchainParameter;
import io.ucoin.client.core.model.Identity;
import io.ucoin.client.core.model.Member;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

public class BlockchainServiceTest {

    private static final Log log = LogFactory.getLog(BlockchainServiceTest.class);
    
    @ClassRule
    public static final TestResource resource = TestResource.create();

    
    @Test
    public void getParameters() throws Exception {

        BlockchainService blockchainService = new BlockchainService();
        BlockchainParameter result = blockchainService.getParameters();
        
        // close
        blockchainService.close();
        
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getCurrency());
    }
    
    @Test
    public void getBlock() throws Exception {

        BlockchainService blockchainService = new BlockchainService();
        BlockchainBlock result = blockchainService.getBlock(0);
        // close
        blockchainService.close();
        
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getCurrency());
        
        for (Identity id: result.getIdentities()) {
            Assert.assertNotNull(id.getUid());
        }
        
        for (Member id: result.getJoiners()) {
            Assert.assertNotNull(id.getUid());
        }
    }
}
