package io.ucoin.client.core.service;

public interface ProtocolUrls {

    /* Block chain */
    
    public static final String BLOCKCHAIN_BASE = "/blockchain";
            
    public static final String BLOCKCHAIN_PARAMETERS = BLOCKCHAIN_BASE + "/parameters";

    public static final String BLOCKCHAIN_BLOCK = BLOCKCHAIN_BASE + "/block/%s";


    public static final String BLOCKCHAIN_MEMBERSHIP = BLOCKCHAIN_BASE + "/membership";

    /* Web Of Trust */
    
    public static final String WOT_BASE = "/wot";
    
    public static final String WOT_LOOKUP = WOT_BASE + "/lookup/%s";
    
    public static final String WOT_CERTIFIED_BY = WOT_BASE + "/certified-by/%s";
    
    public static final String WOT_CERTIFIERS_OF = WOT_BASE + "/certifiers-of/%s";
   
    
}
