package io.ucoin.ucoinj.core.client.service.bma;

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


import io.ucoin.ucoinj.core.client.TestResource;
import io.ucoin.ucoinj.core.client.config.Configuration;
import io.ucoin.ucoinj.core.client.model.BasicIdentity;
import io.ucoin.ucoinj.core.client.model.bma.WotCertification;
import io.ucoin.ucoinj.core.client.model.bma.WotLookup;
import io.ucoin.ucoinj.core.client.model.local.Identity;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.model.local.Wallet;
import io.ucoin.ucoinj.core.client.service.ServiceLocator;
import io.ucoin.ucoinj.core.util.CollectionUtils;
import io.ucoin.ucoinj.core.util.crypto.CryptoUtils;
import io.ucoin.ucoinj.core.util.crypto.SecretBox;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WotRemoteServiceTest {

	private static final Logger log = LoggerFactory.getLogger(WotRemoteServiceTest.class);

	@ClassRule
	public static final TestResource resource = TestResource.create();

	private WotRemoteService service;

	@Before
	public void setUp() {
		service = ServiceLocator.instance().getWotRemoteService();
	}

	@Test
	public void getIdentity() throws Exception {
        Set<Long> currencyIds = new HashSet<>();
        currencyIds.add(resource.getFixtures().getDefaultCurrencyId());
		List<Identity> result = service
				.findIdentities(currencyIds, resource.getFixtures().getUid());
		Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);
	}

	@Test
	public void findByUid() throws Exception {
		WotLookup.Uid result = service
				.findByUid(resource.getFixtures().getDefaultCurrencyId(),
						resource.getFixtures().getUid());
		Assert.assertNotNull(result);
	}

	@Test
	public void getCertifiedBy() throws Exception {
		WotRemoteService service = ServiceLocator.instance().getWotRemoteService();
		WotCertification result = service.getCertifiedBy(
				resource.getFixtures().getDefaultCurrencyId(),
				resource.getFixtures().getUid());

		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getUid());
		Assert.assertNotNull(result.getPubkey());

		Assert.assertTrue(
				String.format(
						"Test user (uid=%s) should have some certifications return by %s",
						resource.getFixtures().getUid(),
						"certified-by"), CollectionUtils
						.isNotEmpty(result.getCertifications()));

		for (WotCertification.Certification cert : result.getCertifications()) {
			Assert.assertNotNull(cert.getUid());

			WotCertification.CertTime certTime = cert.getCert_time();
			Assert.assertNotNull(certTime);
			Assert.assertTrue(certTime.getBlock() >= 0);
			Assert.assertNotNull(certTime.getMedianTime() >= 0);
		}
	}

	@Test
	public void getCertifiersOf() throws Exception {
		WotCertification result = service.getCertifiersOf(
				resource.getFixtures().getDefaultCurrencyId(),
				resource.getFixtures().getUid());

		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getUid());
		Assert.assertNotNull(result.getPubkey());

		Assert.assertTrue(
				String.format(
						"Test user (uid=%s) should have some certifications return by %s",
						resource.getFixtures().getUid(),
						"certifiers-of"),
                CollectionUtils.isNotEmpty(result.getCertifications()));

		for (WotCertification.Certification cert : result.getCertifications()) {
			Assert.assertNotNull(cert.getUid());

			WotCertification.CertTime certTime = cert.getCert_time();
			Assert.assertNotNull(certTime);
			Assert.assertTrue(certTime.getBlock() >= 0);
			Assert.assertNotNull(certTime.getMedianTime() >= 0);
		}
	}

	@Test
	public void sendSelf() throws Exception {

		SecretBox secretBox = createSecretBox();

		String uid = resource.getFixtures().getUid();
		long timestamp = resource.getFixtures().getSelfTimestamp();
		Wallet wallet = createTestWallet();

		String selfCertification = service.getSelfCertification(
				wallet.getSecKey(),
				wallet.getUid(),
				timestamp);

		String expectedCertification = String.format(
				"UID:%s\nMETA:TS:%s\n%s\n", uid, timestamp, resource
						.getFixtures().getSelfSignature());

		Assert.assertEquals(expectedCertification, selfCertification);
	}

	@Test
	public void getCertification() throws Exception {

		SecretBox secretBox = createSecretBox();
		Wallet wallet = createTestWallet();

		String userUid = "kimamila";
		long userTimestamp = 1418377981;
		String userSelf = "pVO6YMhZMl5pPxa33hpFCNljIZ0fO6HMPp9d+uW+DVT4DJXP+tQ5XzLfvOaT3uH+3Slx3BiuH/fADleSp873Cg==";
		int blockNumber = 3328;
		String blockHash = "0837171AD6CE72B7DAD0409A230D43A9CCFFE0DC";

		String selfCertification = service.getCertification(wallet.getPubKey(),
				wallet.getSecKey(), userUid,
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

	protected Wallet createTestWallet() {
		Wallet wallet = new Wallet(
				resource.getFixtures().getCurrency(),
				resource.getFixtures().getUid(),
				CryptoUtils.decodeBase58(resource.getFixtures().getUserPublicKey()),
				CryptoUtils.decodeBase58(resource.getFixtures().getUserSecretKey()));

		return wallet;
	}

	protected SecretBox createSecretBox() {
		String salt = resource.getFixtures().getUserSalt();
		String password = resource.getFixtures().getUserPassword();
		SecretBox secretBox = new SecretBox(salt, password);

		return secretBox;
	}


	protected Peer createTestPeer() {
		Peer peer = new Peer(
				Configuration.instance().getNodeHost(),
				Configuration.instance().getNodePort());

		return peer;
	}
}
