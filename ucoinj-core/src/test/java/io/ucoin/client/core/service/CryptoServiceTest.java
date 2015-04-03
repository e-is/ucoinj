package io.ucoin.client.core.service;

import io.ucoin.client.core.TestResource;
import io.ucoin.client.core.model.*;
import io.ucoin.client.core.technical.crypto.Base58;
import io.ucoin.client.core.technical.crypto.CryptoUtils;
import io.ucoin.client.core.technical.crypto.SecretBox;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.abstractj.kalium.NaCl.Sodium.XSALSA20_POLY1305_SECRETBOX_NONCEBYTES;

public class CryptoServiceTest {

	private static final Log log = LogFactory.getLog(CryptoServiceTest.class);
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
