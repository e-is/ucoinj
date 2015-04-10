package io.ucoin.client.core.service;

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


import io.ucoin.client.core.TestResource;
import io.ucoin.client.core.technical.crypto.Base58;
import io.ucoin.client.core.technical.crypto.CryptoUtils;
import io.ucoin.client.core.technical.crypto.SecretBox;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class CryptoServiceTest {

	private static final Logger log = LoggerFactory.getLogger(CryptoServiceTest.class);
	@ClassRule
	public static final TestResource resource = TestResource.create();

    private byte[] message;

    @Before
    public void setUp() throws UnsupportedEncodingException {
        message = "my message to encrypt !".getBytes("UTF-8");

    }

    @Test
    public void sign() throws Exception {

        CryptoService service = new CryptoService();
        SecretBox secretBox = createSecretBox();

        byte[] signatureBinary = service.sign(message, Base58.decode(secretBox.getSecretKey()));
        String signature = CryptoUtils.encodeBase64(signatureBinary);

        Assert.assertEquals("aAxVThibiZGbpJWrFo8MzZe8RDIoJ1gMC1UIr0utDBQilG44PjA/7o+pOoPAOXgDE3sosGeLHTw1Q/RhFBa4CA==", signature);
    }

	@Test
	public void verify() throws Exception {

		CryptoService service = new CryptoService();
        SecretBox secretBox = createSecretBox();

        byte[] signatureBinary = service.sign(message, Base58.decode(secretBox.getSecretKey()));
        String signature = CryptoUtils.encodeBase64(signatureBinary);

        String messageString = new String(message, "UTF-8");
        boolean validSignature = service.verify(messageString, signature, secretBox.getPublicKey());
        Assert.assertTrue(validSignature);
	}


	/* -- internal methods */

	protected SecretBox createSecretBox() {
		String salt = resource.getFixtures().getUserSalt();
		String password = resource.getFixtures().getUserPassword();
		SecretBox secretBox = new SecretBox(salt, password);

		return secretBox;
	}
}
