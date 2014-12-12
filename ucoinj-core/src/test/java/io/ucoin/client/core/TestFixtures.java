package io.ucoin.client.core;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TestFixtures {

    public String getNodeUrl() {
        return "http://twiced.fr:9101";
    }

    public String getUid() {
        return "cgeek";
    }

    public String getKeySalt() {
        return "a salt string";
    }

    public String getKeyPassword() {
        return "a password string";
    }

    /**
     * Th expected public key generated from getKeySalt() and getKeyPassword()
     * 
     * @return
     */
    public String getExpectedPublicKey() {
        return "FedYyZ64tvNj7Z7dw2gt5Hssayr9o8t8wPvi16jWAxqY";
    }
}
