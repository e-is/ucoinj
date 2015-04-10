package io.ucoin.client.core.service.indexer;

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
import io.ucoin.client.core.config.Configuration;
import io.ucoin.client.core.model.BlockchainBlock;
import io.ucoin.client.core.service.BlockchainService;
import io.ucoin.client.core.service.ServiceLocator;
import io.ucoin.client.core.service.search.BlockIndexerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BlockIndexerServiceTest {

	private static final Logger log = LoggerFactory.getLogger(BlockIndexerServiceTest.class);
	@ClassRule
	public static final TestResource resource = TestResource.create();

    private BlockIndexerService service;
    private BlockchainService blockchainService;
    private Configuration config;

    @Before
    public void setUp() {
        service = ServiceLocator.instance().getBlockIndexerService();
        blockchainService = ServiceLocator.instance().getBlockchainService();
        config = Configuration.instance();
    }

    @Test
    public void createIndex() throws Exception {

        // drop and recreate index
        service.deleteIndex(config.getNodeCurrency());

        service.createIndex(config.getNodeCurrency());
    }

    @Test
    public void allInOne() throws Exception {

        createIndex();
        indexBlock();
        searchBlocks();
    }

    @Test
	public void indexBlock() throws Exception {
        // Read a block
        BlockchainBlock currentBlock = blockchainService.getCurrentBlock();

        // Create a new non-existing block
        service.indexBlock(currentBlock);

        // Update a existing block
        {
            currentBlock.setMembersCount(1000000);

            service.indexBlock(currentBlock);
        }
	}

    @Test
    public void searchBlocks() throws Exception {

        // Create a block with a fake hash
        BlockchainBlock aBlock = blockchainService.getCurrentBlock();
        aBlock.setHash("myUnitTestHash");
        service.saveBlock(aBlock, true);

        // match multi words
        String queryText = aBlock.getHash();
        List<BlockchainBlock> blocks = service.findBlocksByHash(config.getNodeCurrency(), queryText);
        assertResults(queryText, blocks);
    }

	/* -- internal methods */

    protected void assertResults(String queryText, List<BlockchainBlock> result) {
        log.info(String.format("Results for a search on [%s]", queryText));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);
        for (BlockchainBlock block: result) {
            log.info("  - " + block.getNumber());
        }
    }

}
