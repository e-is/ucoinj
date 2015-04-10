package io.ucoin.client.core.action;

/*
 * #%L
 * SIH-Adagio :: Shared
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 - 2014 Ifremer
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

import io.ucoin.client.core.config.Configuration;
import io.ucoin.client.core.model.BlockchainBlock;
import io.ucoin.client.core.model.BlockchainParameter;
import io.ucoin.client.core.service.BlockchainService;
import io.ucoin.client.core.service.ServiceLocator;
import io.ucoin.client.core.service.search.BlockIndexerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexerAction {
	/* Logger */
	private static final Logger log = LoggerFactory.getLogger(IndexerAction.class);

	public void indexAllBlocks() {
        BlockchainService blockchainService = ServiceLocator.instance().getBlockchainService();
        BlockIndexerService indexerService = ServiceLocator.instance().getBlockIndexerService();
        Configuration config = Configuration.instance();

        try {
            // Get the currency name from node
            BlockchainParameter parameter = blockchainService.getParameters();
            if (parameter == null) {
                log.error(String.format("Could not connect to node [%s:%s]",
                        config.getNodeHost(), config.getNodePort()));
                return;
            }
            String currencyName = parameter.getCurrency();

            log.info(String.format("Starting to index blocks of [%s] from node [%s:%s]...",
                    parameter.getCurrency(), config.getNodeHost(), config.getNodePort()));

            // Delete the index
            indexerService.deleteIndex(currencyName);

            // Then index all blocks
            BlockchainBlock currentBlock = blockchainService.getCurrentBlock();

            if (currentBlock != null) {
                int blockCount = currentBlock.getNumber() + 1;

                for (int blockNumber = 0; blockNumber < blockCount; blockNumber++) {
                    if (blockNumber != 0 && blockNumber % 100 == 0) {
                        log.info(String.format("Indexing block number [%s]...", blockNumber));
                    }

                    String blockAsJson = blockchainService.getBlockAsJson(blockNumber);
                    indexerService.indexBlockAsJson(currencyName, blockNumber, blockAsJson.getBytes());
                }

                log.info("All blocks processed");
            }
        } catch(Exception e) {
            log.error("Error during indexation: " + e.getMessage(), e);
        }
	}

    public void updateAllBlocks() {
        BlockchainService blockchainService = ServiceLocator.instance().getBlockchainService();
        BlockIndexerService indexerService = ServiceLocator.instance().getBlockIndexerService();
        Configuration config = Configuration.instance();

        try {
            // Get the currency name from node
            BlockchainParameter parameter = blockchainService.getParameters();
            if (parameter == null) {
                log.error(String.format("Could not connect to node [%s:%s]",
                        config.getNodeHost(), config.getNodePort()));
                return;
            }
            String currencyName = parameter.getCurrency();

            log.info(String.format("Starting to index blocks of [%s] from node [%s:%s]...",
                    parameter.getCurrency(), config.getNodeHost(), config.getNodePort()));

            // Check if index exists
            boolean indexExists = indexerService.existsIndex(currencyName);
            if (!indexExists) {
                log.error("No index [%s] currently exists on ES node.");
                System.exit(-1);
            }

            // Then index all blocks
            BlockchainBlock currentBlock = blockchainService.getCurrentBlock();

            if (currentBlock != null) {
                int blockCount = currentBlock.getNumber() + 1;

                for (int blockNumber = 0; blockNumber < blockCount; blockNumber++) {
                    if (blockNumber != 0 && blockNumber % 100 == 0) {
                        log.info(String.format("Updating block number [%s]...", blockNumber));

                        log.info("TODO");
                    }

                    // TODO : check is has code as change...
                    //String blockAsJson = blockchainService.getBlockAsJson(blockNumber);
                    //indexerService.indexBlockAsJson(currencyName, blockNumber, blockAsJson.getBytes());
                }

                log.info("All blocks processed");
            }
        } catch(Exception e) {
            log.error("Error during indexation: " + e.getMessage(), e);
        }
    }

    public void indexLastBlocks() {
        BlockchainService blockchainService = ServiceLocator.instance().getBlockchainService();
        BlockIndexerService indexerService = ServiceLocator.instance().getBlockIndexerService();
        Configuration config = Configuration.instance();

        try {
            // Get the currency name from node
            BlockchainParameter parameter = blockchainService.getParameters();
            if (parameter == null) {
                log.error(String.format("Could not connect to node [%s:%s]",
                        config.getNodeHost(), config.getNodePort()));
                return;
            }
            String currencyName = parameter.getCurrency();

            log.info(String.format("Starting to index last blocks of [%s] from node [%s:%s]...",
                    currencyName, config.getNodeHost(), config.getNodePort()));

            // Check if index exists
            boolean indexExists = indexerService.existsIndex(currencyName);
            if (!indexExists) {
                log.error("No index [%s] currently exists on ES node.");
                System.exit(-1);
            }

            // Then index all blocks
            BlockchainBlock currentBlock = blockchainService.getCurrentBlock();


            if (currentBlock != null) {
                int blockCount = currentBlock.getNumber() + 1;

                // Get the last indexed block number
                int startNumber = 0;
                BlockchainBlock lastIndexedBlock = indexerService.getCurrentBlock(currencyName);
                if (lastIndexedBlock != null) {
                    startNumber = lastIndexedBlock.getNumber();
                }

                String currentBlockAsJson = null;
                for (int blockNumber = startNumber; blockNumber < blockCount; blockNumber++) {
                    if (blockNumber != 0 && blockNumber % 100 == 0) {
                        log.info(String.format("Indexing block number [%s]...", blockNumber));
                    }

                    String blockAsJson = blockchainService.getBlockAsJson(blockNumber);
                    indexerService.indexBlockAsJson(currencyName, blockNumber, blockAsJson.getBytes());

                    if (blockNumber == blockCount - 1) {
                        // update the current block
                        indexerService.indexCurrentBlockAsJson(currencyName, blockAsJson.getBytes());
                    }
                }

                log.info("All blocks processed");
            }
        } catch(Exception e) {
            log.error("Error during indexation: " + e.getMessage(), e);
        }
    }
}
