package io.ucoin.client.core.service;

import io.ucoin.client.core.model.BlockchainBlock;
import io.ucoin.client.core.model.BlockchainParameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

public class BlockchainService extends AbstractNetworkService {

    private static final Log log = LogFactory.getLog(BlockchainService.class);

    
    
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
