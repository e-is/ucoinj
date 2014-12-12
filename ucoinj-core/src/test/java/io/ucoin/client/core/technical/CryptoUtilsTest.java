package io.ucoin.client.core.technical;

import io.ucoin.client.core.TestResource;
import io.ucoin.client.core.technical.crypto.nacl.NaCl;
import io.ucoin.client.core.technical.crypto.nacl.curve25519xsalsa20poly1305;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import com.lambdaworks.codec.Base64;
import com.lambdaworks.crypto.SCrypt;

public class CryptoUtilsTest {

    private static final Log log = LogFactory.getLog(CryptoUtilsTest.class);
    @ClassRule
    public static final TestResource resource = TestResource.create();
    
    public static int SEED_LENGTH = 32; // Length of the key
    public static int crypto_sign_BYTES = 64;
    
    private byte[] precomputed = new byte[curve25519xsalsa20poly1305.crypto_secretbox_BEFORENMBYTES];
    
    @Test
    @Ignore  
    // FIXME BLA : should be implemented using Kalium (but works only on Linux OS ?) 
    public void testScrypt() throws UnsupportedEncodingException, GeneralSecurityException {

        String salt = resource.getFixtures().getKeySalt();
        String password = resource.getFixtures().getKeyPassword();
        
        byte[] P, S;
        int N, r, p, dkLen;
        String DK;

        // empty key & salt test missing because unsupported by JCE

        S = resource.getFixtures().getKeySalt().getBytes("UTF-8");
        P = resource.getFixtures().getKeyPassword().getBytes("UTF-8");
        N = 4096;
        r = 16;
        p = 1;
        DK = "fdbabe1c9d3472007856e7190d01e9fe7c6ad7cbc8237830e77376634b3731622eaf30d92e22a3886ff109279d9830dac727afb94a83ee6d8360cbdfa2cc0640";

        byte[] chain = SCrypt.scrypt(P, S, N, r, p, SEED_LENGTH);
        String hash = new String(Base64.encode(chain));
        System.out.println("Scrypt return: " + hash);
        
        byte[] nonce = new byte[32];
        byte[] input = NaCl.getBinary(hash);
        byte[] paddedinput = new byte[input.length + curve25519xsalsa20poly1305.crypto_secretbox_ZEROBYTES];
        byte[] output = new byte[input.length + curve25519xsalsa20poly1305.crypto_secretbox_ZEROBYTES];
        
        System.arraycopy(input, 0, paddedinput, curve25519xsalsa20poly1305.crypto_secretbox_ZEROBYTES, input.length);

        curve25519xsalsa20poly1305.crypto_box_afternm(output, paddedinput, paddedinput.length, nonce, this.precomputed);
        
        curve25519xsalsa20poly1305.crypto_box_getpublickey(output, chain);
        
        char[] resultKey = Base64.encode(output);
        System.out.println("Expected public key:" + new String(resource.getFixtures().getExpectedPublicKey()));
        System.out.println("Result is: " + new String(resultKey));
        
        // TODO BLA : fix this
        Assert.assertEquals(resource.getFixtures().getExpectedPublicKey(), new String(resultKey));
        
    }
}
