package io.ucoin.ucoinj.elasticsearch.service;

/*
 * #%L
 * UCoin Java Client :: ElasticSearch Indexer
 * %%
 * Copyright (C) 2014 - 2016 EIS
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

import io.ucoin.ucoinj.core.client.model.local.Wallet;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainParameters;
import io.ucoin.ucoinj.core.client.service.bma.BlockchainRemoteService;
import io.ucoin.ucoinj.core.service.CryptoService;
import io.ucoin.ucoinj.core.client.model.bma.gson.GsonUtils;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.util.crypto.CryptoUtils;
import io.ucoin.ucoinj.elasticsearch.TestResource;
import io.ucoin.ucoinj.elasticsearch.config.Configuration;
import io.ucoin.ucoinj.core.client.model.elasticsearch.Currency;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Benoit on 06/05/2015.
 */
public class CurrencyIndexerServiceTest {
    private static final Logger log = LoggerFactory.getLogger(CurrencyIndexerService.class);

    @ClassRule
    public static final TestResource resource = TestResource.create();

    private CurrencyIndexerService service;
    private Configuration config;
    private Peer peer;

    @Before
    public void setUp() throws Exception {
        service = ServiceLocator.instance().getCurrencyIndexerService();
        config = Configuration.instance();
        peer = createTestPeer();

        if (config.isLocal()) {
            initLocalNode();
        }
    }

    @Test
    public void registerCurrency() {
        Currency currency = new Currency();
        currency.setCurrencyName("register-test-" + System.currentTimeMillis());

        String currencyJson = GsonUtils.newBuilder().create().toJson(currency);

        String pubKey = resource.getFixtures().getUserPublicKey();
        String secretKey = resource.getFixtures().getUserSecretKey();

        CryptoService cryptoService = ServiceLocator.instance().getCryptoService();
        String signature = cryptoService.sign(currencyJson, secretKey);

        service.registerCurrency(pubKey, currencyJson, signature);
    }


    @Test
    public void createIndex() throws Exception {

        // drop and recreate index
        service.deleteIndex();

        service.createIndex();
    }

    @Test
    public void getAllCurrencyNames() {
        List<String> currencyNames = service.getAllCurrencyNames();
        for (String currencyName: currencyNames) {
            log.info("  - " + currencyName);
        }
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
            Peer peer = createTestPeer();
            BlockchainRemoteService blockchainService = ServiceLocator.instance().getBlockchainRemoteService();

            BlockchainParameters parameter = blockchainService.getParameters(peer);

            currency = new Currency();
            currency.setCurrencyName(parameter.getCurrency());
            currency.setMembersCount(10);
            currency.setPeers(new Peer[]{peer});

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


    /* -- internal methods -- */

    protected void initLocalNode() throws Exception {
        service.deleteIndex();
        service.createIndex();
        indexCurrency();
    }

    protected Wallet createTestWallet() {
        Wallet wallet = new Wallet(
                resource.getFixtures().getCurrency(),
                resource.getFixtures().getUid(),
                CryptoUtils.decodeBase58(resource.getFixtures().getUserPublicKey()),
                CryptoUtils.decodeBase58(resource.getFixtures().getUserSecretKey()));

        return wallet;
    }

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

    protected Peer createTestPeer() {
        Configuration config = Configuration.instance();

        Peer peer = new Peer(
                config.getNodeBmaHost(),
                config.getNodeBmaPort());

        return peer;
    }
}
