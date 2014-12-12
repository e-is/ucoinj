package io.ucoin.client.core.technical.crypto;

import java.security.KeyPairGenerator;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.apache.commons.codec.binary.Base64;

public class CryptoUtils {

    public static byte[] generateKeys(String salt, String password) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        //keyGen.initialize(512);
        byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
        return encodeBase64(publicKey);
        /*.getEncoded();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < publicKey.length; ++i) {
            result.append(Integer.toHexString(0x0100 + (publicKey[i] & 0x00FF)).substring(1));
        }

        return encodeBase64(result.toString());*/
    }

    /**
     * Use Ed55219 pattern
     * @param data
     * @param password
     * @param salt
     * @param noIterations
     * @return
     */
    public static byte[] encrypt(byte[] data, char[] password,
            byte[] salt, int noIterations) {
        try {
            String method = "PBEWithMD5AndTripleDES";
            SecretKeyFactory kf = SecretKeyFactory.getInstance(method);
            PBEKeySpec keySpec = new PBEKeySpec(password);
            SecretKey key = kf.generateSecret(keySpec);
            Cipher ciph = Cipher.getInstance(method);
            PBEParameterSpec params = new PBEParameterSpec(salt, noIterations);
            return ciph.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Spurious encryption error");
        }
    }
    
    public static byte[] encodeBase64(byte[] data) {
        byte[] encodedBytes = Base64.encodeBase64(data);
        return encodedBytes;        
    }
}
