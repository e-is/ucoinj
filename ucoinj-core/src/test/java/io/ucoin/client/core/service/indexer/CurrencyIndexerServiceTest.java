package io.ucoin.client.core.service.indexer;

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
import io.ucoin.client.core.config.Configuration;
import io.ucoin.client.core.model.BlockchainParameter;
import io.ucoin.client.core.model.Currency;
import io.ucoin.client.core.model.Peer;
import io.ucoin.client.core.service.BlockchainService;
import io.ucoin.client.core.service.ServiceLocator;
import io.ucoin.client.core.service.search.CurrencyIndexerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CurrencyIndexerServiceTest {

	private static final Logger log = LoggerFactory.getLogger(CurrencyIndexerServiceTest.class);
	@ClassRule
	public static final TestResource resource = TestResource.create();

    private CurrencyIndexerService service;
    private Configuration config;

    @Before
    public void setUp() {
        service = ServiceLocator.instance().getCurrencyIndexerService();
        config = Configuration.instance();
    }

    @Test
    public void createIndex() throws Exception {

        // drop and recreate index
        service.deleteIndex();

        service.createIndex();
    }

    @Test
    public void allInOne() throws Exception {

        createIndex();
        indexCurrency();
        getSuggestions();
        searchCurrencies();
    }

    @Test
	public void indexCurrency() throws Exception {

        // Create a new non-existing currency
        Currency currency = new Currency();
        currency.setCurrencyName("kimamila-test-" + System.currentTimeMillis());
        service.indexCurrency(currency);

        // Update a existing currency, with peer
        {
            Peer peer = new Peer(config.getNodeHost(), config.getNodePort());
            BlockchainService blockchainService = ServiceLocator.instance().getBlockchainService();

            // TODO : use the peer to connect (see android app)
            BlockchainParameter parameter = blockchainService.getParameters();

            currency = new Currency();
            currency.setCurrencyName(parameter.getCurrency());
            currency.setMembersCount(10);
            currency.addPeer(peer);

            service.indexCurrency(currency);
        }
	}

    @Test
    public void getSuggestions() throws Exception {

        // match multi words
        String queryText = "kimamilat";
        List<String> suggestions = service.getSuggestions(queryText);
        assertSuggestions(queryText, suggestions);
    }

    @Test
    public void searchCurrencies() throws Exception {

        // match multi words
        String queryText = "kimamila test";
        List<Currency> currencies = service.searchCurrencies(queryText);
        assertResults(queryText, currencies);

        // match a partial word
        queryText = "kim";
        currencies = service.searchCurrencies(queryText);
        assertResults(queryText, currencies);

        // match with words (using underscore)
        queryText = "meta brouzouf";
        currencies = service.searchCurrencies(queryText);
        // FIXME : the "underscore" should be used as word separator
        //assertResults(queryText, currencies);
    }

	/* -- internal methods */

    protected void assertResults(String queryText, List<Currency> result) {
        log.info(String.format("Results for a search on [%s]", queryText));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);
        for (Currency currency: result) {
            log.info("  - " + currency.getCurrencyName());
        }
    }

    protected void assertSuggestions(String queryText, List<String> result) {
        log.info(String.format("Suggestions for [%s]", queryText));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);
        for (String suggestion: result) {
            log.info("  - " + suggestion);
        }
    }
}
