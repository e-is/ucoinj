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


import io.ucoin.ucoinj.core.test.TestFixtures;
import io.ucoin.ucoinj.core.util.crypto.Base58;
import io.ucoin.ucoinj.core.util.crypto.SecretBox;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class Ed25519CryptoServiceTest {

    private String message;
    private byte[] messageAsBytes;
    private CryptoService service;

    @Before
    public void setUp() throws UnsupportedEncodingException {
        message = "my message to encrypt !";
        messageAsBytes = message.getBytes("UTF-8");
        service = new Ed25519CryptoServiceImpl();
    }

    @Test
    public void sign() throws Exception {
        SecretBox secretBox = createSecretBox();

        String signature = service.sign(message, Base58.decode(secretBox.getSecretKey()));

        Assert.assertEquals("aAxVThibiZGbpJWrFo8MzZe8RDIoJ1gMC1UIr0utDBQilG44PjA/7o+pOoPAOXgDE3sosGeLHTw1Q/RhFBa4CA==", signature);
    }

	@Test
	public void verify() throws Exception {

        SecretBox secretBox = createSecretBox();

        String signature = service.sign(message, Base58.decode(secretBox.getSecretKey()));

        boolean validSignature = service.verify(message, signature, secretBox.getPublicKey());
        Assert.assertTrue(validSignature);
	}


	/* -- internal methods */

	protected SecretBox createSecretBox() {
        TestFixtures fixtures = new TestFixtures();
		String salt = fixtures.getUserSalt();
		String password = fixtures.getUserPassword();
		SecretBox secretBox = new SecretBox(salt, password);

		return secretBox;
	}
}
