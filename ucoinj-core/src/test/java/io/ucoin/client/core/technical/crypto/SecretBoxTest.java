package io.ucoin.client.core.technical.crypto;

import static org.abstractj.kalium.NaCl.sodium;
import static org.abstractj.kalium.NaCl.Sodium.SIGNATURE_BYTES;
import static org.abstractj.kalium.NaCl.Sodium.XSALSA20_POLY1305_SECRETBOX_NONCEBYTES;
import static org.abstractj.kalium.crypto.Util.slice;
import io.ucoin.client.core.TestResource;

import java.io.UnsupportedEncodingException;

import jnr.ffi.byref.LongLongByReference;

import org.abstractj.kalium.crypto.Util;
import org.abstractj.kalium.keys.KeyPair;
import org.abstractj.kalium.keys.SigningKey;
import org.abstractj.kalium.keys.VerifyKey;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;


public class SecretBoxTest {

	private static final Log log = LogFactory.getLog(SecretBoxTest.class);
	
	private byte[] message;
	private byte[] nonce = new byte[XSALSA20_POLY1305_SECRETBOX_NONCEBYTES];


	@ClassRule
	public static final TestResource resource = TestResource.create();
	
	@Before
	public void setUp() throws UnsupportedEncodingException {
		message = "my message to encrypt !".getBytes("UTF-8");

	}
	
	@Test
	public void TO_REMOVETest() {
		String expectedBase64Hash = resource.getFixtures().getUserSeedHash();
		String expectedSecKeyHash = CryptoUtils.encodeBase64((CryptoUtils.decodeBase58(resource.getFixtures().getUserSecretKey())));
		String expectedPubKeyHash = CryptoUtils.encodeBase64((CryptoUtils.decodeBase58(resource.getFixtures().getUserPublicKey()
				)));
		
		System.out.println("expectedSeed(base64)        : " + expectedBase64Hash);
		System.out.println("expectedSecKeyHash(base64): " + expectedSecKeyHash);
		System.out.println("expectedPubKeyHash(base64): " + expectedPubKeyHash);
	}

	@Test
	public void computeSeedFromSaltAndPassword()
			throws UnsupportedEncodingException {

		String salt = resource.getFixtures().getUserSalt();
		String password = resource.getFixtures().getUserPassword();
		String expectedBase64Hash = resource.getFixtures().getUserSeedHash();

		byte[] seed = SecretBox.computeSeedFromSaltAndPassword(salt, password);
		String hash = CryptoUtils.encodeBase64(seed);

		Assert.assertEquals(expectedBase64Hash, hash);
	}

	@Test
	public void getPublicKey() throws Exception {

		SecretBox secretBox = createSecretBox();

		Assert.assertEquals(resource.getFixtures().getUserPublicKey(),
				secretBox.getPublicKey());
	}
	
	@Test
	public void getSecretKey() throws Exception {

		SecretBox secretBox = createSecretBox();

		Assert.assertEquals(resource.getFixtures().getUserSecretKey(),
				secretBox.getSecretKey());
	}

	@Test
	public void encryptAndDecrypt() throws Exception {

		SecretBox secretBox = createSecretBox();

		byte[] message = "my message to encrypt !".getBytes("UTF-8");

		// encrypt
		byte[] cypherMessage = secretBox.encrypt(nonce, message);

		// decrypt
		byte[] decryptedMessage = secretBox.decrypt(nonce, cypherMessage);
		
		assertEquals(message, decryptedMessage);
	}


	@Test
	public void sign() throws AddressFormatException, UnsupportedEncodingException {

		SecretBox secretBox = createSecretBox();

		String utf8Message = String.format("UID:%s\nMETA:TS:%s\n",
				resource.getFixtures().getUid(),
				"1420881879");
		byte[] message = CryptoUtils.decodeUTF8(utf8Message);
		String expectedSignatureBase64 = "TMgQysT7JwY8XwemskwWb8LBDJybLUsnxqaaUvSteIYpOxRiB92gkFQQcGpBwq4hAwhEiqBAiFkiXIozppDDDg==";

		// Call sign
		byte[] signature = secretBox.sign(message);
		String signatureBase64 = CryptoUtils.encodeBase64(signature);
		
		log.debug("expected signature: " + expectedSignatureBase64);
		log.debug("  actual signature: " + signatureBase64);

		Assert.assertEquals(
				expectedSignatureBase64,
				signatureBase64);
	}
	
	
	@Test
	@Ignore
	public void verifyKey() throws AddressFormatException {

		SecretBox secretBox = createSecretBox();
		String pubKey = secretBox.getPublicKey();
		
		VerifyKey verifyKey = new VerifyKey(Base58.decode(pubKey));
		
		// TODO
	}

	/* -- Internal methods -- */

	protected static void assertEquals(byte[] expectedData, byte[] actualData) {
		Assert.assertEquals(
				CryptoUtils.encodeBase64(expectedData),
				CryptoUtils.encodeBase64(actualData));
	}

	protected SecretBox createSecretBox() {
		String salt = resource.getFixtures().getUserSalt();
		String password = resource.getFixtures().getUserPassword();
		SecretBox secretBox = new SecretBox(salt, password);

		return secretBox;
	}
	
	protected SigningKey createSigningKey() {
		String salt = resource.getFixtures().getUserSalt();
		String password = resource.getFixtures().getUserPassword();
		byte[] seed = SecretBox.computeSeedFromSaltAndPassword(salt, password);

		return new SigningKey(seed);
	}

}
