package io.ucoin.ucoinj.core.service;

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


import com.lambdaworks.crypto.SCrypt;
import io.ucoin.ucoinj.core.beans.Bean;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.core.util.crypto.KeyPair;
import jnr.ffi.byref.LongLongByReference;
import org.abstractj.kalium.NaCl;
import org.abstractj.kalium.NaCl.Sodium;

import java.security.GeneralSecurityException;

import static io.ucoin.ucoinj.core.util.crypto.CryptoUtils.*;


/**
 * Crypto services (sign...)
 * Created by eis on 10/01/15.
 */
public class Ed25519CryptoServiceImpl implements CryptoService {

    // Length of the seed key (generated deterministically, use to generate the 64 key pair).
    private static int SEED_BYTES = 32;
    // Length of a signature return by crypto_sign
    private static int SIGNATURE_BYTES = 64;
    // Length of a public key
    private static int PUBLICKEY_BYTES = 32;
    // Length of a secret key
    private static int SECRETKEY_BYTES = 64;

    // Scrypt parameter
    private static int SCRYPT_PARAMS_N = 4096;
    private static int SCRYPT_PARAMS_r = 16;
    private static int SCRYPT_PARAMS_p = 1;

    private final Sodium naCl;

    public Ed25519CryptoServiceImpl() {
        naCl = NaCl.sodium();
    }

    @Override
    public byte[] getSeed(String salt, String password) {
        try {
            byte[] seed = SCrypt.scrypt(
                    decodeAscii(password),
                    decodeAscii(salt),
                    SCRYPT_PARAMS_N, SCRYPT_PARAMS_r,
                    SCRYPT_PARAMS_p, SEED_BYTES);
            return seed;
        } catch (GeneralSecurityException e) {
            throw new TechnicalException(
                    "Unable to salt password, using Scrypt library", e);
        }
    }

    @Override
    public KeyPair getKeyPair(String salt, String password) {
        return getKeyPairFromSeed(getSeed(salt, password));
    }


    @Override
    public KeyPair getKeyPairFromSeed(byte[] seed) {
        byte[] secretKey = zeros(SECRETKEY_BYTES);
        byte[] publicKey = zeros(PUBLICKEY_BYTES);
        isValid(naCl.crypto_sign_ed25519_seed_keypair(publicKey, secretKey, seed),
                "Failed to generate a key pair");

        return new KeyPair(publicKey, secretKey);
    }

    @Override
    public String sign(String message, byte[] secretKey) {
        byte[] messageBinary = decodeUTF8(message);
        return encodeBase64(
                sign(messageBinary, secretKey)
        );
    }

    @Override
    public String sign(String message, String secretKey) {
        byte[] messageBinary = decodeUTF8(message);
        byte[] secretKeyBinary = decodeBase58(secretKey);
        return encodeBase64(
                sign(messageBinary, secretKeyBinary)
        );
    }

    @Override
    public boolean verify(String message, String signature, String publicKey) {
        byte[] messageBinary = decodeUTF8(message);
        byte[] signatureBinary = decodeBase64(signature);
        byte[] publicKeyBinary = decodeBase58(publicKey);
        return verify(messageBinary, signatureBinary, publicKeyBinary);
    }

    /* -- Internal methods -- */

    protected byte[] sign(byte[] message, byte[] secretKey) {
        byte[] signature = prependZeros(SIGNATURE_BYTES, message);
        LongLongByReference smLen = new LongLongByReference(0);
        naCl.crypto_sign_ed25519(signature, smLen, message, message.length, secretKey);
        signature = slice(signature, 0, SIGNATURE_BYTES);

        checkLength(signature, SIGNATURE_BYTES);
        return signature;
    }

    protected boolean verify(byte[] message, byte[] signature, byte[] publicKey) {
        byte[] sigAndMsg = new byte[SIGNATURE_BYTES + message.length];
        for (int i = 0; i < SIGNATURE_BYTES; i++) sigAndMsg[i] = signature[i];
        for (int i = 0; i < message.length; i++) sigAndMsg[i+SIGNATURE_BYTES] = message[i];

        byte[] buffer = new byte[SIGNATURE_BYTES + message.length];
        LongLongByReference bufferLength = new LongLongByReference(0);

        int result = naCl.crypto_sign_ed25519_open(buffer, bufferLength, sigAndMsg, sigAndMsg.length, publicKey);
        boolean validSignature = (result == 0);

        return validSignature;
    }

}
