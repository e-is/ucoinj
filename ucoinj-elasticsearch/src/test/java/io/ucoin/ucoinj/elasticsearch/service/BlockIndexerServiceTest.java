package io.ucoin.ucoinj.elasticsearch.service;

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


import io.ucoin.ucoinj.core.client.config.Configuration;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainBlock;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.service.bma.BlockchainRemoteService;
import io.ucoin.ucoinj.elasticsearch.TestResource;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BlockIndexerServiceTest {

	private static final Logger log = LoggerFactory.getLogger(BlockIndexerServiceTest.class);

	@ClassRule
	public static final TestResource resource = TestResource.create();

    private BlockIndexerService service;
    private BlockchainRemoteService blockchainRemoteService;
    private Configuration config;
    private Peer peer;

    @Before
    public void setUp() throws Exception {
        service = ServiceLocator.instance().getBlockIndexerService();
        blockchainRemoteService = ServiceLocator.instance().getBlockchainRemoteService();
        config = Configuration.instance();
        peer = createTestPeer();

        initLocalNode();
    }

    @Test
    public void createIndex() throws Exception {
        String currencyName = resource.getFixtures().getCurrency();

        // drop and recreate index
        service.deleteIndex(currencyName);

        service.createIndex(currencyName);
    }


    @Test
	public void indexBlock() throws Exception {
        // Read a block
        BlockchainBlock currentBlock = blockchainRemoteService.getCurrentBlock(peer);

        // Create a new non-existing block
        service.indexBlock(currentBlock, true);

        // Update a existing block
        {
            currentBlock.setMembersCount(1000000);

            service.indexBlock(currentBlock, true);
        }
	}

    @Test
    public void indexCurrentBlock() throws Exception {
        // Create a block with a fake hash
        BlockchainBlock aBlock = blockchainRemoteService.getBlock(peer, 8450);
        service.indexCurrentBlock(aBlock, true);
    }

    @Test
    // FIXME make this works
    @Ignore
    public void searchBlocks() throws Exception {
        String currencyName = resource.getFixtures().getCurrency();

        // Create a block with a fake hash
        BlockchainBlock aBlock = blockchainRemoteService.getCurrentBlock(peer);
        aBlock.setHash("myUnitTestHash");
        service.saveBlock(aBlock, true, true);

        Thread.sleep(5 * 1000); // wait 5s that ES process the block

        // match multi words
        String queryText = aBlock.getHash();
        List<BlockchainBlock> blocks = service.findBlocksByHash(currencyName, queryText);
        //assertResults(queryText, blocks);

        Thread.sleep(5 * 1000); // wait 5s that ES process the block

        BlockchainBlock loadBlock = service.getBlockById(currencyName, aBlock.getNumber());
        Assert.assertNotNull(loadBlock);
        Assert.assertEquals(aBlock.getHash(), loadBlock.getHash());
    }

    @Test
    public void getMaxBlockNumber() throws Exception {
        String currencyName = resource.getFixtures().getCurrency();

        // match multi words
        Integer maxBlockNumber = service.getMaxBlockNumber(currencyName);
        Assert.assertNotNull(maxBlockNumber);
    }


    @Test
    @Ignore
    public void allInOne() throws Exception {

        createIndex();
        indexBlock();
        searchBlocks();
    }

	/* -- internal methods */

    protected void initLocalNode() throws Exception {
        String currencyName = resource.getFixtures().getCurrency();

        // Make sure the index exists
        service.deleteIndex(currencyName);
        service.createIndex(currencyName);

        // Get the first block from peer
        BlockchainBlock firstBlock = blockchainRemoteService.getBlock(peer, 0);

        // Make sure the block has been indexed
        service.indexBlock(firstBlock, true);

    }

    protected void assertResults(String queryText, List<BlockchainBlock> result) {
        log.info(String.format("Results for a search on [%s]", queryText));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);
        for (BlockchainBlock block: result) {
            log.info("  - " + block.getNumber());
        }
    }

    protected Peer createTestPeer() {
        Peer peer = new Peer(
                Configuration.instance().getNodeHost(),
                Configuration.instance().getNodePort());

        return peer;
    }

}
