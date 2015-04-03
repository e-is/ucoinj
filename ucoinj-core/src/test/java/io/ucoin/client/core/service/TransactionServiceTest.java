package io.ucoin.client.core.service;


import io.ucoin.client.core.TestResource;
import io.ucoin.client.core.model.BasicIdentity;
import io.ucoin.client.core.model.Identity;
import io.ucoin.client.core.model.TxSourceResults;
import io.ucoin.client.core.model.Wallet;
import io.ucoin.client.core.model.WotCertification;
import io.ucoin.client.core.model.WotCertificationTime;
import io.ucoin.client.core.model.WotIdentityCertifications;
import io.ucoin.client.core.model.WotLookupResults;
import io.ucoin.client.core.model.WotLookupUId;
import io.ucoin.client.core.technical.crypto.CryptoUtils;
import io.ucoin.client.core.technical.crypto.SecretBox;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

public class TransactionServiceTest {

	private static final Log log = LogFactory.getLog(TransactionServiceTest.class);
	@ClassRule
	public static final TestResource resource = TestResource.create();
	
	private TransactionService service;
	
	@Before
	public void setUp() {
		service = ServiceLocator.instance().getTransactionService();
	}

	@Test
	public void transfert() throws Exception {

		
		service.transfert(
				createTestWallet(),
				resource.getFixtures().getOtherUserPublicKey(),
				1,
				"my comments" + System.currentTimeMillis());

		// close
		service.close();
	}
	
	@Test
	public void getSources() throws Exception {

		String pubKey = resource.getFixtures().getUserPublicKey();
		
		TransactionService service = new TransactionService();
		TxSourceResults sourceResults = service.getSources(pubKey);

		Assert.assertNotNull(sourceResults);
		Assert.assertNotNull(sourceResults.getSources());
		Assert.assertEquals(resource.getFixtures().getCurrency(), sourceResults.getCurrency());
		Assert.assertEquals(pubKey, sourceResults.getPubkey());
		Assert.assertTrue(sourceResults.getBalance() > 0d);
		// close
		service.close();
	}


	/* -- internal methods */

	protected Wallet createTestWallet() {
		Wallet wallet = new Wallet(
				resource.getFixtures().getCurrency(),
				resource.getFixtures().getUid(),
				CryptoUtils.decodeBase58(resource.getFixtures().getUserPublicKey()),
				CryptoUtils.decodeBase58(resource.getFixtures().getUserSecretKey()));
		
		return wallet;
	}
}
