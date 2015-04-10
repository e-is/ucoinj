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
import io.ucoin.client.core.model.TxSourceResults;
import io.ucoin.client.core.model.Wallet;
import io.ucoin.client.core.technical.crypto.CryptoUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionServiceTest {

	private static final Logger log = LoggerFactory.getLogger(TransactionServiceTest.class);
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
