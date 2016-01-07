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
import io.ucoin.ucoinj.core.client.model.bma.EndpointProtocol;
import io.ucoin.ucoinj.core.client.model.bma.NetworkPeering;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.service.ServiceLocator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NetworkRemoteServiceTest {

	private static final Logger log = LoggerFactory.getLogger(NetworkRemoteServiceTest.class);

	@ClassRule
	public static final TestResource resource = TestResource.create();
	
	private NetworkRemoteService service;
	private Peer peer;
	
	@Before
	public void setUp() {
		service = ServiceLocator.instance().getNetworkRemoteService();
		peer = createTestPeer();
	}

	@Test
	public void getPeering() throws Exception {

		NetworkPeering result = service.getPeering(peer);

		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getPubkey());
		Assert.assertNotNull(result.getSignature());
		Assert.assertNotNull(result.getCurrency());
	}


	@Test
	public void findPeers() throws Exception {

		List<Peer> result = service.findPeers(peer, null, EndpointProtocol.BASIC_MERKLED_API, null, null);

		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() > 0);
	}

	@Test
	public void getPeers() throws Exception {

		List<Peer> result = service.getPeers(peer);

		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() > 0);
	}

	/* -- internal methods */

    protected Peer createTestPeer() {
        Peer peer = new Peer(
                Configuration.instance().getNodeHost(),
                Configuration.instance().getNodePort());

        return peer;
    }
}
