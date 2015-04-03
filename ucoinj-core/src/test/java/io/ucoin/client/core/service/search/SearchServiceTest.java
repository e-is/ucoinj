package io.ucoin.client.core.service.search;

import io.ucoin.client.core.TestResource;
import io.ucoin.client.core.model.*;
import io.ucoin.client.core.service.BlockchainService;
import io.ucoin.client.core.service.ServiceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

import java.util.List;

public class SearchServiceTest {

	private static final Log log = LogFactory.getLog(SearchServiceTest.class);
	@ClassRule
	public static final TestResource resource = TestResource.create();

    private SearchService service;

    @Before
    public void setUp() {
        service = ServiceLocator.instance().getSearchService();
    }

    @After
    public void tearDown() throws Exception {

        // close
        service.close();
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
        currency.setCurrencyName("bla-test-" + System.currentTimeMillis());
        service.indexCurrency(currency);

        // Update a existing currency, with peer
        {
            Peer peer = new Peer("metab.ucoin.io", 9201);
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
        String queryText = "blat";
        List<String> suggestions = service.getSuggestions(queryText);
        assertSuggestions(queryText, suggestions);
    }

    @Test
    public void searchCurrencies() throws Exception {

        // match multi words
        String queryText = "bla test";
        List<Currency> currencies = service.searchCurrencies(queryText);
        assertResults(queryText, currencies);

        // match a partial word
        queryText = "bl";
        currencies = service.searchCurrencies(queryText);
        assertResults(queryText, currencies);

        // match with words (using underscore)
        queryText = "meta brouzouf";
        currencies = service.searchCurrencies(queryText);
        assertResults(queryText, currencies);
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
