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


import io.ucoin.ucoinj.core.beans.Bean;
import io.ucoin.ucoinj.core.util.crypto.KeyPair;


/**
 * Crypto services (sign...)
 * Created by eis on 10/01/15.
 */
public interface CryptoService extends Bean {
    byte[] getSeed(String salt, String password);

    /**
     * Returns a new signing key pair generated from salt and password.
     * The salt and password must contain enough entropy to be secure.
     *
     * @param salt
     * @param password
     * @return
     */
    KeyPair getKeyPair(String salt, String password);

    /**
     * Returns a new signing key pair generated from salt and password.
     * The salt and password must contain enough entropy to be secure.
     *
     * @param seed
     * @return
     */
    KeyPair getKeyPairFromSeed(byte[] seed);

    String sign(String message, byte[] secretKey);

    String sign(String message, String secretKey);

    boolean verify(String message, String signature, String publicKey);
}
