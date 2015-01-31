package io.ucoin.client.core.service;

import io.ucoin.client.core.model.TxSource;
import io.ucoin.client.core.model.TxSourceResults;
import io.ucoin.client.core.model.Wallet;
import io.ucoin.client.core.model.WotLookupResults;
import io.ucoin.client.core.technical.crypto.KeyPair;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class TransactionService extends AbstractService{

	private static final Log log = LogFactory.getLog(TransactionService.class);

    public TransactionService() {
        super();
    }
    
    public void transfert(Wallet wallet, String destPubKey, double amount, String comments) throws Exception {
    	// http post /tx/process
        HttpPost httpPost = new HttpPost(getAppendedPath(ProtocolUrls.TX_PROCESS));
        
        // compute tranction
		String transaction = getTransaction(wallet, destPubKey, amount, comments); 
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("transaction", transaction));
 
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));		
                
        String selfResult = executeRequest(httpPost, String.class);
        log.info("received from /tx/process: " + selfResult);
    }
    


	public TxSourceResults getSources(String pubKey) throws Exception {
	       if (log.isDebugEnabled()) {
	            log.debug(String.format("Get sources by pubKey: %s", pubKey));
	        }

	        // get parameter
	        String path = String.format(ProtocolUrls.TX_SOURCES, pubKey);
	        HttpGet httpGet = new HttpGet(getAppendedPath(path));
	        TxSourceResults result = executeRequest(httpGet, TxSourceResults.class);

	        // Compute the balance
	        result.setBalance(computeBalance(result.getSources()));
	        
	        return result;

	}
    
    /* -- internal methods -- */
    
    public String getTransaction(Wallet wallet, String destPubKey, double amount, String comments){
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("Version: 1\n")
    	.append("Type: Transaction\n")
    	.append("Currency: ").append(wallet.getCurrency()).append("\n")
    	.append("Issuers:\n")
    	// add issuer pubkey
    	.append(wallet.getPubKeyHash()).append("\n")
    	// Inputs coins
    	.append("Inputs:")
    	//INDEX:SOURCE:NUMBER:FINGERPRINT:AMOUNT
    	// Output
    	.append("Outputs:")
    	//PUBLIC_KEY:AMOUNT
    	.append("Comment: ").append(comments).append("\n")
    	//SIGNATURES
    	.append("\n");
    	
    	return sb.toString();
    }
    
    protected double computeBalance(List<TxSource> sources) {
    	if (sources == null) {
    		return 0d;
    	}
    	
    	double balance = 0d;
    	for (TxSource source: sources) {
    		balance += source.getAmount();
    	}
    	return balance;
    }
}
