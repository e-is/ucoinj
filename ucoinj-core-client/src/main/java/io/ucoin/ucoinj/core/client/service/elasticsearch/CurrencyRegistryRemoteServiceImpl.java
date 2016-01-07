package io.ucoin.ucoinj.core.client.service.elasticsearch;

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

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import io.ucoin.ucoinj.core.beans.InitializingBean;
import io.ucoin.ucoinj.core.beans.Service;
import io.ucoin.ucoinj.core.client.config.Configuration;
import io.ucoin.ucoinj.core.client.model.bma.gson.GsonUtils;
import io.ucoin.ucoinj.core.client.model.elasticsearch.Currency;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.model.local.Wallet;
import io.ucoin.ucoinj.core.client.service.ServiceLocator;
import io.ucoin.ucoinj.core.client.service.bma.BaseRemoteServiceImpl;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.core.service.CryptoService;
import io.ucoin.ucoinj.core.util.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Benoit on 06/05/2015.
 */
public class CurrencyRegistryRemoteServiceImpl extends BaseRemoteServiceImpl implements CurrencyRegistryRemoteService, InitializingBean, Closeable{
    private static final Logger log = LoggerFactory.getLogger(CurrencyRegistryRemoteServiceImpl.class);

    private final static String URL_STATUS = "/";
    private final static String URL_ALL_CURRENCY_NAMES = "/currency/simple/_search?_source=currencyName";
    private final static String URL_ADD_CURRENCY = "/rest/currency/add";

    private Configuration config;
    private Peer peer;
    private Gson gson;

    public CurrencyRegistryRemoteServiceImpl() {
        super();
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        config = Configuration.instance();
        peer = new Peer(config.getNodeElasticSearchHost(), config.getNodeElasticSearchPort());
        gson = GsonUtils.newBuilder().create();
    }

    @Override
    public void close() throws IOException {
        super.close();
        config = null;
        peer = null;
        gson = null;
    }

    @Override
    public boolean isNodeAlive() {
        return isNodeAlive(peer);
    }

    @Override
    public boolean isNodeAlive(Peer peer) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Checking if elasticsearch node [%s:%s] is alive...", peer.getHost(), peer.getPort()));
        }

        // get currency
        String jsonResponse;
        try {
            String path = getPath(peer, URL_STATUS);
            jsonResponse = executeRequest(peer, path, String.class);
            int statusCode = GsonUtils.getValueFromJSONAsInt(jsonResponse, "status");
            return statusCode == HttpStatus.SC_OK;
        }
        catch(TechnicalException e) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to get node status: " + e.getMessage(), e);
            }
            return false;
        }
    }

    @Override
    public List<String> getAllCurrencyNames() {
        if (log.isDebugEnabled()) {
            log.debug("Getting all currency names...");
        }

        // get currency
        String path = getPath(peer, URL_ALL_CURRENCY_NAMES);
        String jsonResponse = executeRequest(new HttpGet(path), String.class);

        List<String> currencyNames = GsonUtils.getValuesFromJSONAsString(jsonResponse, "currencyName");

        // Sort into alphabetical order
        Collections.sort(currencyNames);

        return currencyNames;
    }

    @Override
    public void registerNewCurrency(Wallet wallet, Currency currency) {
        if (log.isDebugEnabled()) {
            log.debug("Registering a new currency...");
        }

        String currencyJson = gson.toJson(currency);
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

    @Override
    public void registerNewCurrency(String pubkey, String jsonCurrency, String signature) {
        if (log.isDebugEnabled()) {
            log.debug("Registering a new currency...");
        }

        URIBuilder builder = getURIBuilder(URL_ADD_CURRENCY);
        builder.addParameter("pubkey", pubkey);
        builder.addParameter("currency", jsonCurrency);
        builder.addParameter("sig", signature);

        HttpGet httpGet;
        try {
            httpGet = new HttpGet(builder.build());
        }
        catch(URISyntaxException e) {
            throw new TechnicalException(e);
        }

        String result = executeRequest(httpGet, String.class);

        if (log.isDebugEnabled()) {
            log.debug("Server response, after currency registration: " + result);
        }
    }

    /* -- protected methods -- */

    protected URIBuilder getURIBuilder(String... path)  {
        String pathToAppend = Joiner.on('/').skipNulls().join(path);

        int customQueryStartIndex = pathToAppend.indexOf('?');
        String customQuery = null;
        if (customQueryStartIndex != -1) {
            customQuery = pathToAppend.substring(customQueryStartIndex+1);
            pathToAppend = pathToAppend.substring(0, customQueryStartIndex);
        }

        try {
            URI baseUri = config.getNodeElasticSearchUrl().toURI();
            URIBuilder builder = new URIBuilder(baseUri);

            builder.setPath(baseUri.getPath() + pathToAppend);
            if (StringUtils.isNotBlank(customQuery)) {
                builder.setCustomQuery(customQuery);
            }
            return builder;
        }
        catch(URISyntaxException e) {
            throw new TechnicalException(e);
        }
    }
}
