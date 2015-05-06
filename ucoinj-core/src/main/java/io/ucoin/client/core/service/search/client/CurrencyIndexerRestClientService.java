package io.ucoin.client.core.service.search.client;

import io.ucoin.client.core.config.Configuration;
import io.ucoin.client.core.model.Currency;
import io.ucoin.client.core.model.Wallet;
import io.ucoin.client.core.service.AbstractNetworkService;
import io.ucoin.client.core.service.CryptoService;
import io.ucoin.client.core.service.ServiceLocator;
import io.ucoin.client.core.technical.UCoinTechnicalException;
import io.ucoin.client.core.technical.gson.GsonUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Benoit on 06/05/2015.
 */
public class CurrencyIndexerRestClientService extends AbstractNetworkService{
    private static final Logger log = LoggerFactory.getLogger(CurrencyIndexerRestClientService.class);

    public CurrencyIndexerRestClientService() {
        super();
    }

    public boolean isNodeAlive() {
        if (log.isDebugEnabled()) {
            log.debug("Check if node is alive...");
        }

        // get currency
        HttpGet httpGet = new HttpGet(getAppendedPath("/"));
        String jsonString = executeRequest(httpGet, String.class);

        int statusCode = GsonUtils.getValueFromJSONAsInt(jsonString, "status");

        return statusCode == HttpStatus.SC_OK;
    }

    public List<String> getAllCurrencyNames() {
        if (log.isDebugEnabled()) {
            log.debug("Getting all currency names...");
        }

        // get currency
        HttpGet httpGet = new HttpGet(getAppendedPath("/currency/simple/_search?_source=currencyName"));
        String jsonString = executeRequest(httpGet, String.class);

        List<String> currencyNames = GsonUtils.getValuesFromJSONAsString(jsonString, "currencyName");

        // Sort into alphabetical order
        Collections.sort(currencyNames);

        return currencyNames;
    }

    public void registerNewCurrency(Wallet wallet, Currency currency) {
        if (log.isDebugEnabled()) {
            log.debug("Registering a new currency...");
        }

        String currencyJson = getGson().toJson(currency);
        CryptoService cryptoService = ServiceLocator.instance().getCryptoService();
        String signature = cryptoService.sign(currencyJson, wallet.getSecKey());

        registerNewCurrency(
                wallet.getPubKeyHash(),
                currencyJson,
                signature);

        // get currency
        //HttpGet httpGet = new HttpGet(getAppendedPath("/currency/simple/_search?_source=currencyName"));
        //String jsonString = executeRequest(httpGet, String.class);

    }

    public void registerNewCurrency(String pubkey, String jsonCurrency, String signature) {
        if (log.isDebugEnabled()) {
            log.debug("Registering a new currency...");
        }


        URIBuilder builder = getURIBuilder("/rest/currency/add");
        builder.addParameter("pubkey", pubkey);
        builder.addParameter("currency", jsonCurrency);
        builder.addParameter("sig", signature);

        HttpGet httpGet;
        try {
            httpGet = new HttpGet(builder.build());
        }
        catch(URISyntaxException e) {
            throw new UCoinTechnicalException(e);
        }

        String result = executeRequest(httpGet, String.class);

        if (log.isDebugEnabled()) {
            log.debug("Server response, after currency registration: " + result);
        }

    }

    /* -- -- */

    protected URI initNodeURI(Configuration config) {
        try {
            URI nodeURI = config.getNodeElasticSearchRestUrl().toURI();

            return nodeURI;
        } catch (URISyntaxException ex) {
            throw new UCoinTechnicalException(ex);
        }
    }

}
