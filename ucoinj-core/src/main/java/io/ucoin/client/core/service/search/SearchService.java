package io.ucoin.client.core.service.search;

import com.google.common.collect.Lists;
import io.ucoin.client.core.model.Currency;
import io.ucoin.client.core.service.BaseService;
import io.ucoin.client.core.technical.ObjectUtils;
import io.ucoin.client.core.technical.UCoinTechnicalException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.exists.ExistsResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Benoit on 30/03/2015.
 */
public class SearchService extends BaseService implements Closeable {

    public static final Log log = LogFactory.getLog(SearchService.class);

    private Client client;
    private ObjectMapper objectMapper;

    public SearchService() {
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    @Override
    public void initialize() {
        super.initialize();

        // Node Client
        //Node node = NodeBuilder.nodeBuilder().node();
        //Client client = node.client();

        // Transport Client
        client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress("192.168.0.5", 9300));

        objectMapper = new ObjectMapper();
    }

    public boolean existsIndex(String indexes) throws JsonProcessingException {
        IndicesExistsRequestBuilder requestBuilder = client.admin().indices().prepareExists(indexes);
        IndicesExistsResponse response = requestBuilder.execute().actionGet();

        return response.isExists();
    }

    public void deleteIndex() throws JsonProcessingException {
        if (!existsIndex("currency")) {
            return;
        }

        DeleteIndexRequestBuilder deleteIndexRequestBuilder = client.admin().indices().prepareDelete("currency");
        DeleteIndexResponse response = deleteIndexRequestBuilder.execute().actionGet();
    }

    public void createIndex() throws JsonProcessingException {
        CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate("currency");
        try {
            XContentBuilder analyzer = XContentFactory.jsonBuilder().startObject().startObject("analyzer")
                .startObject("custom_french_analyzer")
                .field("tokenizer", "letter")
                .field("filter", "asciifolding", "lowercase", "french_stem", "elision", "stop")
                .endObject()
                .startObject("tag_analyzer")
                .field("tokenizer", "keyword")
                .field("filter", "asciifolding", "lowercase")
                .endObject()
                .endObject().endObject();

            Settings indexSettings = ImmutableSettings.settingsBuilder()
                    .put("number_of_shards", 1)
                    .put("number_of_replicas", 1)
                    .put("analyzer", analyzer)
                    .build();
            createIndexRequestBuilder.setSettings(indexSettings);
            //createIndexRequestBuilder.addAlias(new Alias("currencies"));


            XContentBuilder mapping = XContentFactory.jsonBuilder().startObject().startObject("simple")
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

            createIndexRequestBuilder.addMapping("simple", mapping);

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
            byte[] json = objectMapper.writeValueAsBytes(currency);

            // Preparing indexation
            IndexRequestBuilder indexRequest = client.prepareIndex("currency", "simple")
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
        CompletionSuggestionBuilder suggestionBuilder = new CompletionSuggestionBuilder("simple")
            .text(query)
            .size(10) // limit to 10 results
            .field("tags");

        // Prepare request
        SuggestRequestBuilder suggestRequest = client
                .prepareSuggest("currency")
                .addSuggestion(suggestionBuilder);

        // Execute query
        SuggestResponse response = suggestRequest.execute().actionGet();

        // Read query result
        return toSuggestions(response, "simple", query);
    }

    public List<Currency> searchCurrencies(String query) {
        String[] queryParts = query.split("[\\t ]+");

        // Prepare request
        SearchRequestBuilder searchRequest = client
                .prepareSearch("currency")
                .setTypes("simple")
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
        SearchRequestBuilder searchRequest = client
                .prepareSearch("currency")
                .setTypes("simple")
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

    protected List<Currency> toCurrencies(SearchResponse response, boolean withHighlight) {
        try {
            // Read query result
            SearchHit[] searchHits = response.getHits().getHits();
            List<Currency> result = Lists.newArrayListWithCapacity(searchHits.length);
            for (SearchHit searchHit : searchHits) {
                Currency currency = null;
                if (searchHit.source() != null) {
                    currency = objectMapper.readValue(searchHit.source(), Currency.class);
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
