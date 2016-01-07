package io.ucoin.ucoinj.core.client.service.bma;

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


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.ucoin.ucoinj.core.client.TestResource;
import io.ucoin.ucoinj.core.client.config.Configuration;
import io.ucoin.ucoinj.core.client.model.BasicIdentity;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainBlock;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainParameters;
import io.ucoin.ucoinj.core.client.model.Member;
import io.ucoin.ucoinj.core.client.model.bma.gson.GsonUtils;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.service.ServiceLocator;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockchainRemoteServiceTest {

    private static final Logger log = LoggerFactory.getLogger(BlockchainRemoteServiceTest.class);
    
    @ClassRule
    public static final TestResource resource = TestResource.create();

    private BlockchainRemoteService service;

    @Before
    public void setUp() {
        service = ServiceLocator.instance().getBlockchainRemoteService();
    }

    @Test
    public void getParameters() throws Exception {

        BlockchainParameters result = service.getParameters(createTestPeer());

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getCurrency());
    }
    
    @Test
    public void getBlock() throws Exception {

        BlockchainBlock result = service.getBlock(createTestPeer(), 0);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getCurrency());
        
        for (BlockchainBlock.Identity id: result.getIdentities()) {
            Assert.assertNotNull(id.getUid());
        }
        
        for (BlockchainBlock.Joiner id: result.getJoiners()) {
            Assert.assertNotNull(id.getUid());
        }
    }

    @Test
    public void getBlocksAsJson() throws Exception {

        String[] result= service.getBlocksAsJson(createTestPeer(), 10, 0);

        Assert.assertNotNull(result);
        Assert.assertEquals(10, result.length);

        // Make sure all json are valid blocks
        Gson gson = GsonUtils.newBuilder().create();
        int number = 0;
        for (String jsonBlock: result) {
            try {
                gson.fromJson(jsonBlock, BlockchainBlock.class);
            }
            catch(JsonSyntaxException e) {
                e.printStackTrace();
                Assert.fail(String.format("Invalid block format #%s. See previous error", number));
            }
            number++;
        }

    }

    /* -- Internal methods -- */

    protected Peer createTestPeer() {
        Peer peer = new Peer(
                Configuration.instance().getNodeHost(),
                Configuration.instance().getNodePort());

        return peer;
    }
}
