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


import io.ucoin.client.core.model.BlockchainBlock;
import io.ucoin.client.core.model.BlockchainParameter;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BlockchainService extends AbstractNetworkService {

    private static final Logger log = LoggerFactory.getLogger(BlockchainService.class);

    
    
    public BlockchainService() {
        super();
    }
    
    /**
     * get the blockchain parameters (currency parameters)
     * @return
     * @throws Exception
     */
    public BlockchainParameter getParameters() throws Exception {
        // get blockchain parameter
        HttpGet httpGet = new HttpGet(getAppendedPath(ProtocolUrls.BLOCKCHAIN_PARAMETERS));
        BlockchainParameter result = executeRequest(httpGet, BlockchainParameter.class);
        return result;
    }

    /**
     * Retrieve a block, by id (from 0 to current)
     * @param number
     * @return
     * @throws Exception
     */
    public BlockchainBlock getBlock(int number) throws Exception {
        // get blockchain parameter
        String path = String.format(ProtocolUrls.BLOCKCHAIN_BLOCK, number);
        HttpGet httpGet = new HttpGet(getAppendedPath(path));
        BlockchainBlock result = executeRequest(httpGet, BlockchainBlock.class);
        return result;
    }

    /**
     * Retrieve a block, by id (from 0 to current)
     * @param number
     * @return
     * @throws Exception
     */
    public String getBlockAsJson(int number) throws Exception {
        // get blockchain parameter
        String path = String.format(ProtocolUrls.BLOCKCHAIN_BLOCK, number);
        HttpGet httpGet = new HttpGet(getAppendedPath(path));
        return executeRequest(httpGet, String.class);
    }

    /**
     * Retrieve a block, by id (from 0 to current)
     * @param number
     * @return
     * @throws Exception
     */
    public List<BlockchainBlock> getBlocks(int count, int fromNumber) throws Exception {
        // get blockchain parameter
        String path = String.format(ProtocolUrls.BLOCKCHAIN_BLOCKS, count, fromNumber);
        HttpGet httpGet = new HttpGet(getAppendedPath(path));
        List<BlockchainBlock> result = null;
        // FIXME : how to do this ?
        // result = executeRequest(httpGet, List<BlockchainBlock>.class);
        return result;
    }
    
    /**
     * Retrieve the current block
     * @param number
     * @return
     * @throws Exception
     */
    public BlockchainBlock getCurrentBlock() throws Exception {
        // get blockchain parameter
        HttpGet httpGet = new HttpGet(getAppendedPath(ProtocolUrls.BLOCKCHAIN_BLOCK_CURRENT));
        BlockchainBlock result = executeRequest(httpGet, BlockchainBlock.class);
        return result;
    }
    
    /**
     * Request to integrate the wot
     * @throws Exception 
     */
    public void requestMembership() throws Exception {
        
        HttpPost httpPost = new HttpPost(getAppendedPath(ProtocolUrls.BLOCKCHAIN_MEMBERSHIP));

        
//        StringEntity entity = new StringEntity(gson.toJson(form), ContentType.APPLICATION_JSON);
//        httpPost.setEntity(entity);
        
        executeRequest(httpPost, null);
    }
    
    /* -- Internal methods -- */

}
