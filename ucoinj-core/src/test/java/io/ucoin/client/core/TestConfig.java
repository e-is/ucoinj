package io.ucoin.client.core;

public class TestConfig {

    public static String getNodeUrl() {
        return "http://twiced.fr:9101";
    }
    
    public static String getUid() {
        return "cgeek";
    }
    
    public static String getKeySalt() {
        return "a salt string";
    }
    
    public static String getKeyPassword() {
        return "a password string";
    }

    /**
     * Th expected public key generated from getKeySalt() and getKeyPassword()
     * @return
     */
    public static String getExpectedPublicKey() {
        return "FedYyZ64tvNj7Z7dw2gt5Hssayr9o8t8wPvi16jWAxqY";
    }
}
