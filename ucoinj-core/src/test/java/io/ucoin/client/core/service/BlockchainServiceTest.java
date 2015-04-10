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


import io.ucoin.client.core.TestResource;
import io.ucoin.client.core.model.BasicIdentity;
import io.ucoin.client.core.model.BlockchainBlock;
import io.ucoin.client.core.model.BlockchainParameter;
import io.ucoin.client.core.model.Member;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockchainServiceTest {

    private static final Logger log = LoggerFactory.getLogger(BlockchainServiceTest.class);
    
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
        
        for (BasicIdentity id: result.getIdentities()) {
            Assert.assertNotNull(id.getUid());
        }
        
        for (Member id: result.getJoiners()) {
            Assert.assertNotNull(id.getUid());
        }
    }
}
