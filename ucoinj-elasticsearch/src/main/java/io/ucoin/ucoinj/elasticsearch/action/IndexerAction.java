package io.ucoin.ucoinj.elasticsearch.action;

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

import io.ucoin.ucoinj.core.client.model.bma.BlockchainBlock;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainParameters;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.service.bma.BlockchainRemoteService;
import io.ucoin.ucoinj.elasticsearch.config.Configuration;
import io.ucoin.ucoinj.elasticsearch.service.BlockIndexerService;
import io.ucoin.ucoinj.elasticsearch.service.ServiceLocator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexerAction {
	/* Logger */
	private static final Logger log = LoggerFactory.getLogger(IndexerAction.class);

    public void indexLastBlocks() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Configuration config = Configuration.instance();
                Peer peer = checkConfigAndGetPeer(config);
                BlockIndexerService blockIndexerService = ServiceLocator.instance().getBlockIndexerService();

                blockIndexerService.indexLastBlocks(peer);
            }
        };

        ServiceLocator.instance().getExecutorService().execute(runnable);
    }

    public void resetAllBlocks() {
        BlockchainRemoteService blockchainService = ServiceLocator.instance().getBlockchainRemoteService();
        BlockIndexerService indexerService = ServiceLocator.instance().getBlockIndexerService();
        Configuration config = Configuration.instance();
        Peer peer = checkConfigAndGetPeer(config);

        try {
            // Get the currency name from node
            BlockchainParameters parameter = blockchainService.getParameters(peer);
            if (parameter == null) {
                log.error(String.format("Could not connect to node [%s:%s]",
                        config.getNodeBmaHost(), config.getNodeBmaPort()));
                return;
            }
            String currencyName = parameter.getCurrency();

            log.info(String.format("Reset data for index [%s]", currencyName));

            // Check if index exists
            boolean indexExists = indexerService.existsIndex(currencyName);
            if (indexExists) {
                log.debug(String.format("Deleting index [%s]", currencyName));
                indexerService.deleteIndex(currencyName);

                log.debug(String.format("Creating index [%s]", currencyName));
                indexerService.createIndex(currencyName);
            }

            log.info(String.format("Successfully reset data for index [%s]", currencyName));
        } catch(Exception e) {
            log.error("Error during reset data: " + e.getMessage(), e);
        }
    }

    /* -- -- */

    protected Peer checkConfigAndGetPeer(Configuration config) {
        if (StringUtils.isBlank(config.getNodeBmaHost())) {
            log.error("ERROR: node host is required");
            System.exit(-1);
            return null;
        }
        if (config.getNodeBmaPort() <= 0) {
            log.error("ERROR: node port is required");
            System.exit(-1);
            return null;
        }

        Peer peer = new Peer(config.getNodeBmaHost(), config.getNodeBmaPort());
        return peer;
    }
}
