package io.ucoin.client.core.service.indexer.client;

import io.ucoin.client.core.TestResource;
import io.ucoin.client.core.config.Configuration;
import io.ucoin.client.core.model.Currency;
import io.ucoin.client.core.model.Wallet;
import io.ucoin.client.core.service.CryptoService;
import io.ucoin.client.core.service.ServiceLocator;
import io.ucoin.client.core.service.search.client.CurrencyIndexerRestClientService;
import io.ucoin.client.core.technical.crypto.CryptoUtils;
import io.ucoin.client.core.technical.gson.GsonUtils;
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
public class CurrencyIndexerRestClientServiceTest {
    private static final Logger log = LoggerFactory.getLogger(CurrencyIndexerRestClientServiceTest.class);

    @ClassRule
    public static final TestResource resource = TestResource.create();

    private CurrencyIndexerRestClientService service;
    private Configuration config;

    @Before
    public void setUp() {
        service = ServiceLocator.instance().getCurrencyIndexerRestClientService();
        config = Configuration.instance();
    }

    @Test
    public void isNodeAlive() {
        boolean isNodeAlive = service.isNodeAlive();
        Assert.assertTrue(isNodeAlive);
    }

    @Test
    public void getAllCurrencyNames() {
        List<String> currencyNames = service.getAllCurrencyNames();
        for (String currencyName: currencyNames) {
            log.info("  - " + currencyName);
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

        service.registerNewCurrency(pubKey, currencyJson, signature);
    }

    /* -- -- */

    protected Wallet createTestWallet() {
        Wallet wallet = new Wallet(
                resource.getFixtures().getCurrency(),
                resource.getFixtures().getUid(),
                CryptoUtils.decodeBase58(resource.getFixtures().getUserPublicKey()),
                CryptoUtils.decodeBase58(resource.getFixtures().getUserSecretKey()));

        return wallet;
    }
}
