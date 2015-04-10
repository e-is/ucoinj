package io.ucoin.client.core.service.search;

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


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import io.ucoin.client.core.model.Currency;
import io.ucoin.client.core.service.ServiceLocator;
import io.ucoin.client.core.technical.ObjectUtils;
import io.ucoin.client.core.technical.UCoinTechnicalException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Benoit on 30/03/2015.
 */
public class CurrencyIndexerService extends BaseIndexerService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyIndexerService.class);

    public static final String INDEX_NAME = "currency";

    public static final String INDEX_TYPE_SIMPLE = "simple";

    public CurrencyIndexerService() {
    }

    public void deleteIndex() throws JsonProcessingException {
        deleteIndexIfExists(INDEX_NAME);
    }

    public void createIndex() throws JsonProcessingException {
        CreateIndexRequestBuilder createIndexRequestBuilder = getClient().admin().indices().prepareCreate(INDEX_NAME);
        try {

            Settings indexSettings = ImmutableSettings.settingsBuilder()
                    .put("number_of_shards", 1)
                    .put("number_of_replicas", 1)
                    .put("analyzer", getDefaultAnalyzer())
                    .build();
            createIndexRequestBuilder.setSettings(indexSettings);
            //createIndexRequestBuilder.addAlias(new Alias("currencies"));


            XContentBuilder mapping = XContentFactory.jsonBuilder().startObject().startObject(INDEX_TYPE_SIMPLE)
                    .startObject("properties")
                    .startObject("currencyName")
                    .field("type", "string")
                    .endObject()
                    .startObject("memberCount")
                    .field("type", "integer")
                    .endObject()
                    .startObject("tags")
                    .field("type", "completion")
                    .field("index_analyzer", "simple")
                            //.field("index_analyzer", "biword")
                    .field("search_analyzer", "simple")
                    .field("preserve_separators", "false")
                    .endObject()
                    .endObject()
                    .endObject().endObject();

            createIndexRequestBuilder.addMapping(INDEX_TYPE_SIMPLE, mapping);

        }
        catch(IOException ioe) {
            throw new UCoinTechnicalException("Error while preparing index: " + ioe.getMessage(), ioe);
        }
        CreateIndexResponse response = createIndexRequestBuilder.execute().actionGet();
    }


    public void indexCurrency(Currency currency) {
        try {
            ObjectUtils.checkNotNull(currency.getCurrencyName());

            // Fill tags
            if (ArrayUtils.isEmpty(currency.getTags())) {
                String currencyName = currency.getCurrencyName();
                String[] tags = currencyName.split("[-\\t@#_ ]+");
                List<String> tagsList = Lists.newArrayList(tags);
                tagsList.add(currencyName.replaceAll("[-\\t@#_ ]+", " "));
                currency.setTags(tagsList.toArray(new String[tagsList.size()]));
            }

            // Serialize into JSON
            byte[] json = getObjectMapper().writeValueAsBytes(currency);

            // Preparing indexation
            IndexRequestBuilder indexRequest = getClient().prepareIndex(INDEX_NAME, INDEX_TYPE_SIMPLE)
                    .setId(currency.getCurrencyName())
                    .setSource(json);

            // Execute indexation
            indexRequest
                    .setRefresh(true)
                    .execute().actionGet();

        } catch(JsonProcessingException e) {
            throw new UCoinTechnicalException(e);
        }
    }

    public List<String> getSuggestions(String query) {
        CompletionSuggestionBuilder suggestionBuilder = new CompletionSuggestionBuilder(INDEX_TYPE_SIMPLE)
            .text(query)
            .size(10) // limit to 10 results
            .field("tags");

        // Prepare request
        SuggestRequestBuilder suggestRequest = getClient()
                .prepareSuggest(INDEX_NAME)
                .addSuggestion(suggestionBuilder);

        // Execute query
        SuggestResponse response = suggestRequest.execute().actionGet();

        // Read query result
        return toSuggestions(response, INDEX_TYPE_SIMPLE, query);
    }

    public List<Currency> searchCurrencies(String query) {
        String[] queryParts = query.split("[\\t ]+");

        // Prepare request
        SearchRequestBuilder searchRequest = getClient()
                .prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE_SIMPLE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        // If only one term, search as prefix
        if (queryParts.length == 1) {
            searchRequest.setQuery(QueryBuilders.prefixQuery("currencyName", query));
        }

        // If more than a word, search on terms match
        else {
            searchRequest.setQuery(QueryBuilders.matchQuery("currencyName", query));
        }

        // Sort as score/memberCount
        searchRequest.addSort("_score", SortOrder.DESC)
                .addSort("memberCount", SortOrder.DESC);

        // Highlight matched words
        searchRequest.setHighlighterTagsSchema("styled")
            .addHighlightedField("currencyName")
            .addFields("currencyName")
            .addFields("*", "_source");

        // Execute query
        SearchResponse searchResponse = searchRequest.execute().actionGet();

        // Read query result
        return toCurrencies(searchResponse, true);
    }

    public Currency getCurrencyById(String currencyId) {

        // Prepare request
        SearchRequestBuilder searchRequest = getClient()
                .prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE_SIMPLE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        // If more than a word, search on terms match
        searchRequest.setQuery(QueryBuilders.matchQuery("_id", currencyId));

        // Execute query
        SearchResponse searchResponse = searchRequest.execute().actionGet();
        List<Currency> currencies = toCurrencies(searchResponse, false);

        if (CollectionUtils.isEmpty(currencies)) {
            return null;
        }

        // Return the unique result
        return CollectionUtils.extractSingleton(currencies);
    }

    /* -- Internal methods -- */

    protected void createCurrency(Currency currency) throws DuplicateIndexIdException, JsonProcessingException {
        ObjectUtils.checkNotNull(currency, "currency could not be null") ;
        ObjectUtils.checkNotNull(currency.getCurrencyName(), "currency attribute 'currencyName' could not be null");

        Currency existingCurrency = getCurrencyById(currency.getCurrencyName());
        if (existingCurrency != null) {
            throw new DuplicateIndexIdException(String.format("Currency with name [%s] already exists.", currency.getCurrencyName()));
        }

        // register to currency
        indexCurrency(currency);

        // Create sub indexes
        ServiceLocator.instance().getBlockIndexerService().createIndex(currency.getCurrencyName());
    }

    protected void saveCurrency(Currency currency, String senderPubkey) throws DuplicateIndexIdException {
        ObjectUtils.checkNotNull(currency, "currency could not be null") ;
        ObjectUtils.checkNotNull(currency.getCurrencyName(), "currency attribute 'currencyName' could not be null");

        Currency existingCurrency = getCurrencyById(currency.getCurrencyName());

        // Currency not exists, so create it
        if (existingCurrency == null) {
            // make sure to fill the sender
            currency.setSenderPubkey(senderPubkey);

            // Save it
            indexCurrency(currency);
        }

        // Exists, so check the owner signature
        else {
            if (!Objects.equals(currency.getSenderPubkey(), senderPubkey)) {
                throw new AccessDeniedException("Could not change currency, because it has been registered by another public key.");
            }

            // Make sure the sender is not changed
            currency.setSenderPubkey(senderPubkey);

            // Save changes
            indexCurrency(currency);
        }
    }



    protected List<Currency> toCurrencies(SearchResponse response, boolean withHighlight) {
        try {
            // Read query result
            SearchHit[] searchHits = response.getHits().getHits();
            List<Currency> result = Lists.newArrayListWithCapacity(searchHits.length);
            for (SearchHit searchHit : searchHits) {
                Currency currency = null;
                if (searchHit.source() != null) {
                    currency = getObjectMapper().readValue(searchHit.source(), Currency.class);
                }
                else {
                    currency = new Currency();
                    SearchHitField field = searchHit.getFields().get("currencyName");
                    currency.setCurrencyName((String)field.getValue());
                }
                result.add(currency);

                // If possible, use highlights
                if (withHighlight) {
                    Map<String, HighlightField> fields = searchHit.getHighlightFields();
                    for (HighlightField field : fields.values()) {
                        String currencyNameHighLight = field.getFragments()[0].string();
                        currency.setCurrencyName(currencyNameHighLight);
                    }
                }
            }

            return result;
        } catch(IOException e) {
            throw new UCoinTechnicalException("Error while reading currency search result: " + e.getMessage(), e);
        }
    }


    protected List<String> toSuggestions(SuggestResponse response, String suggestionName, String query) {
        if (response.getSuggest() == null
                || response.getSuggest().getSuggestion(suggestionName) == null) {
            return null;
        }

        // Read query result
        Iterator<? extends Suggest.Suggestion.Entry.Option> iterator =
                response.getSuggest().getSuggestion(suggestionName).iterator().next().getOptions().iterator();

        List<String> result = Lists.newArrayList();
        while (iterator.hasNext()) {
            Suggest.Suggestion.Entry.Option next = iterator.next();
            String suggestion = next.getText().string();
            result.add(suggestion);
        }

        return result;
    }
}
