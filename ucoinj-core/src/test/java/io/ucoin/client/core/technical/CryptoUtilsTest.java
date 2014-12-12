package io.ucoin.client.core.technical;

import io.ucoin.client.core.TestConfig;
import io.ucoin.client.core.technical.crypto.nacl.NaCl;
import io.ucoin.client.core.technical.crypto.nacl.curve25519xsalsa20poly1305;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

import com.lambdaworks.codec.Base64;
import com.lambdaworks.crypto.SCrypt;

public class CryptoUtilsTest {

    private static final Log log = LogFactory.getLog(CryptoUtilsTest.class);
    
    public static int SEED_LENGTH = 32; // Length of the key
    public static int crypto_sign_BYTES = 64;
    
    static final int crypto_secretbox_KEYBYTES = 32;
    static final int crypto_secretbox_NONCEBYTES = 24;
    static final int crypto_secretbox_ZEROBYTES = 32;
    static final int crypto_secretbox_BOXZEROBYTES = 16;
    static final int crypto_secretbox_BEFORENMBYTES = 32;
    
    private byte[] precomputed = new byte[crypto_secretbox_BEFORENMBYTES];
    
    @Test
    @Ignore  
    // FIXME BLA : should be implemented using Kalium (but works only on Linux OS ?) 
    public void testScrypt() throws UnsupportedEncodingException, GeneralSecurityException {

        String salt = TestConfig.getKeySalt();
        String password = TestConfig.getKeyPassword();
        
        byte[] P, S;
        int N, r, p, dkLen;
        String DK;

        // empty key & salt test missing because unsupported by JCE

        S = TestConfig.getKeySalt().getBytes("UTF-8");
        P = TestConfig.getKeyPassword().getBytes("UTF-8");
        N = 4096;
        r = 16;
        p = 1;
        DK = "fdbabe1c9d3472007856e7190d01e9fe7c6ad7cbc8237830e77376634b3731622eaf30d92e22a3886ff109279d9830dac727afb94a83ee6d8360cbdfa2cc0640";

        byte[] chain = SCrypt.scrypt(P, S, N, r, p, SEED_LENGTH);
        String hash = new String(Base64.encode(chain));
        System.out.println(hash);
        
        byte[] nonce = new byte[32];
        byte[] input = NaCl.getBinary(hash);
        byte[] paddedinput = new byte[input.length + crypto_secretbox_ZEROBYTES];
        byte[] output = new byte[input.length + crypto_secretbox_ZEROBYTES];
        
        System.arraycopy(input, 0, paddedinput, crypto_secretbox_ZEROBYTES, input.length);

        curve25519xsalsa20poly1305.crypto_box_afternm(output, paddedinput, paddedinput.length, nonce, this.precomputed);
        
        char[] resultKey = Base64.encode(output);
        System.out.println(resultKey);
        
        // TODO BLA : fixe this
        Assert.assertEquals(TestConfig.getExpectedPublicKey(), new String(resultKey));
        //.crypto_sign_ed25519_seed_keypair();
        
        
//        String enc = SCryptUtil.scrypt("neb", N, r, p);
//        System.out.println(enc);
        
//        Mac mac = Mac.getInstance("ed");
//        mac.init(new SecretKeySpec(passwd, "HmacSHA256"));
        //assertArrayEquals(CryptoTestUtil.decode(DK), chain);
        
    }

}
