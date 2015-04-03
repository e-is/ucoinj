package io.ucoin.client.core.service;

import io.ucoin.client.core.TestResource;
import io.ucoin.client.core.model.BasicIdentity;
import io.ucoin.client.core.model.Identity;
import io.ucoin.client.core.model.WotCertification;
import io.ucoin.client.core.model.WotCertificationTime;
import io.ucoin.client.core.model.WotIdentityCertifications;
import io.ucoin.client.core.model.WotLookupResults;
import io.ucoin.client.core.model.WotLookupUId;
import io.ucoin.client.core.technical.crypto.SecretBox;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

public class WotServiceTest {

	private static final Log log = LogFactory.getLog(WotServiceTest.class);
	@ClassRule
	public static final TestResource resource = TestResource.create();

	@Test
	public void find() throws Exception {

		WotService service = new WotService();
		WotLookupResults results = service
				.find(resource.getFixtures().getUid());
		Assert.assertNotNull(results);

		// close
		service.close();
	}

	@Test
	public void findByUid() throws Exception {

		WotService service = new WotService();
		WotLookupUId result = service
				.findByUid(resource.getFixtures().getUid());
		Assert.assertNotNull(result);

		// close
		service.close();
	}

	@Test
	public void getCertifiedBy() throws Exception {

		WotService service = new WotService();
		WotIdentityCertifications result = service.getCertifiedBy(resource
				.getFixtures().getUid());
		assertBasicIdentity(result, false);

		Assert.assertTrue(
				String.format(
						"Test user (uid=%s) should have some certifications return by %s",
						resource.getFixtures().getUid(),
						ProtocolUrls.WOT_CERTIFIED_BY), CollectionUtils
						.isNotEmpty(result.getCertifications()));

		for (WotCertification cert : result.getCertifications()) {
			Assert.assertNotNull(cert.getUid());

			WotCertificationTime certTime = cert.getCert_time();
			Assert.assertNotNull(certTime);
			Assert.assertTrue(certTime.getBlock() >= 0);
			Assert.assertNotNull(certTime.getMedianTime() >= 0);
		}

		// close
		service.close();
	}

	@Test
	public void getCertifiersOf() throws Exception {

		WotService service = new WotService();
		WotIdentityCertifications result = service.getCertifiersOf(resource
				.getFixtures().getUid());
		assertBasicIdentity(result, false);

		Assert.assertTrue(
				String.format(
						"Test user (uid=%s) should have some certifications return by %s",
						resource.getFixtures().getUid(),
						ProtocolUrls.WOT_CERTIFIERS_OF), CollectionUtils
						.isNotEmpty(result.getCertifications()));

		for (WotCertification cert : result.getCertifications()) {
			Assert.assertNotNull(cert.getUid());

			WotCertificationTime certTime = cert.getCert_time();
			Assert.assertNotNull(certTime);
			Assert.assertTrue(certTime.getBlock() >= 0);
			Assert.assertNotNull(certTime.getMedianTime() >= 0);
		}

		// close
		service.close();
	}

	@Test
	public void getSelfCertification() throws Exception {

		WotService service = new WotService();
		SecretBox secretBox = createSercretBox();

		String uid = resource.getFixtures().getUid();
		long timestamp = resource.getFixtures().getSelfTimestamp();

		String selfCertification = service.getSelfCertification(secretBox, uid,
				timestamp);

		String expectedCertification = String.format(
				"UID:%s\nMETA:TS:%s\n%s\n", uid, timestamp, resource
						.getFixtures().getSelfSignature());

		Assert.assertEquals(expectedCertification, selfCertification);

		// close
		service.close();
	}

	@Test
	public void getCertification() throws Exception {

		WotService service = new WotService();
		SecretBox secretBox = createSercretBox();

		String userUid = "kimamila";
		long userTimestamp = 1418377981;
		String userSelf = "pVO6YMhZMl5pPxa33hpFCNljIZ0fO6HMPp9d+uW+DVT4DJXP+tQ5XzLfvOaT3uH+3Slx3BiuH/fADleSp873Cg==";
		int blockNumber = 3328;
		String blockHash = "0837171AD6CE72B7DAD0409A230D43A9CCFFE0DC";

		String selfCertification = service.getCertification(secretBox, userUid,
				userTimestamp, userSelf, blockNumber, blockHash);

		String expectedCertification = String
				.format("UID:%s\nMETA:TS:%s\n%s\nMETA:TS:%s-%s\n%s\n",
						userUid,
						userTimestamp,
						userSelf,
						blockNumber,
						blockHash,
						"wOAbhxPzlnwmKgMXirPjxNno5tsHN95KMSUrVrZSLPcXn69cFg6ZbiWpSKVSFcHVVuZ4rhRvi46RFvVT/yFuDA==");

		Assert.assertEquals(expectedCertification, selfCertification);

		// close
		service.close();
	}

	/* -- internal methods */

	protected void assertBasicIdentity(BasicIdentity identity,
			boolean withSignature) {

		Assert.assertNotNull(identity);
		Assert.assertNotNull(identity.getUid());
		Assert.assertNotNull(identity.getPubkey());
		if (withSignature) {
			Assert.assertNotNull(identity.getSignature());
		} else {
			Assert.assertNull(identity.getSignature());
		}

	}

	protected void assertIdentity(Identity identity) {
		assertBasicIdentity(identity, true);

		Assert.assertTrue(identity.getTimestamp() > 0);

	}

	protected SecretBox createSercretBox() {
		String salt = resource.getFixtures().getUserSalt();
		String password = resource.getFixtures().getUserPassword();
		SecretBox secretBox = new SecretBox(salt, password);

		return secretBox;
	}
}
