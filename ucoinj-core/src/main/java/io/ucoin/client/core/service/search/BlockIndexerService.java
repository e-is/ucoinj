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
import io.ucoin.client.core.model.BlockchainBlock;
import io.ucoin.client.core.technical.ObjectUtils;
import io.ucoin.client.core.technical.UCoinTechnicalException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Benoit on 30/03/2015.
 */
public class BlockIndexerService extends BaseIndexerService {

    private static final Logger log = LoggerFactory.getLogger(BlockIndexerService.class);

    public static final String INDEX_TYPE_BLOCK = "block";

    public BlockIndexerService() {
    }

    public void deleteIndex(String currencyName) throws JsonProcessingException {
        deleteIndexIfExists(currencyName);
    }

    public void createIndex(String currencyName) throws JsonProcessingException {
        CreateIndexRequestBuilder createIndexRequestBuilder = getClient().admin().indices().prepareCreate(currencyName);
        try {
            Settings indexSettings = ImmutableSettings.settingsBuilder()
                    .put("number_of_shards", 1)
                    .put("number_of_replicas", 1)
                    .put("analyzer", getDefaultAnalyzer())
                    .build();
            createIndexRequestBuilder.setSettings(indexSettings);

            XContentBuilder mapping = XContentFactory.jsonBuilder().startObject().startObject(INDEX_TYPE_BLOCK)
                    .startObject("properties")
                    .startObject("number")
                    .field("type", "integer")
                    .endObject()
                    .startObject("hash")
                    .field("type", "string")
                    .endObject()
                    .startObject("memberCount")
                    .field("type", "integer")
                    .endObject()
                    .endObject()
                    .endObject().endObject();

            createIndexRequestBuilder.addMapping(INDEX_TYPE_BLOCK, mapping);

        }
        catch(IOException ioe) {
            throw new UCoinTechnicalException("Error while preparing index: " + ioe.getMessage(), ioe);
        }
        CreateIndexResponse response = createIndexRequestBuilder.execute().actionGet();
    }

    public void createBlock(BlockchainBlock block) throws DuplicateIndexIdException {
        ObjectUtils.checkNotNull(block, "block could not be null") ;
        ObjectUtils.checkNotNull(block.getCurrency(), "block attribute 'currency' could not be null");
        ObjectUtils.checkNotNull(block.getNumber(), "block attribute 'number' could not be null");

        BlockchainBlock existingBlock = getBlockById(block.getCurrency(), block.getNumber());
        if (existingBlock != null) {
            throw new DuplicateIndexIdException(String.format("Block with number [%s] already exists.", block.getNumber()));
        }

        indexBlock(block);
    }

    /**
     * Create or update a block, depending on its existence and hash
     * @param block
     * @param updateWhenSameHash if true, always update an existing block. If false, update only if hash has changed.
     * @throws DuplicateIndexIdException
     */
    public void saveBlock(BlockchainBlock block, boolean updateWhenSameHash) throws DuplicateIndexIdException {
        ObjectUtils.checkNotNull(block, "block could not be null") ;
        ObjectUtils.checkNotNull(block.getCurrency(), "block attribute 'currency' could not be null");
        ObjectUtils.checkNotNull(block.getNumber(), "block attribute 'number' could not be null");
        ObjectUtils.checkNotNull(block.getHash(), "block attribute 'hash' could not be null");

        BlockchainBlock existingBlock = getBlockById(block.getCurrency(), block.getNumber());

        // Currency not exists, or has changed, so create it
        if (existingBlock == null) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Insert new block [%s]", block.getNumber()));
            }

            // Create new block
            indexBlock(block);
        }

        // Exists, so check the owner signature
        else {
            boolean doUpdate = false;
            if (updateWhenSameHash) {
                doUpdate = true;
                if (log.isTraceEnabled() && doUpdate) {
                    log.trace(String.format("Update block [%s]", block.getNumber()));
                }
            }
            else {
                doUpdate = !StringUtils.equals(existingBlock.getHash(), block.getHash());
                if (log.isTraceEnabled()) {
                    if (doUpdate) {
                        log.trace(String.format("Update block [%s]: hash has been changed, old=[%s] new=[%s]", block.getNumber(), existingBlock.getHash(), block.getHash()));
                    }
                    else {
                        log.trace(String.format("Skipping update block [%s]: hash is up to date.", block.getNumber()));
                    }
                }
            }

            // Update existing block
            if (doUpdate) {
                indexBlock(block);
            }
        }
    }

    public void indexBlock(BlockchainBlock block) {
        try {
            ObjectUtils.checkNotNull(block.getCurrency());
            ObjectUtils.checkNotNull(block.getHash());
            ObjectUtils.checkNotNull(block.getNumber());

            // Serialize into JSON
            byte[] json = getObjectMapper().writeValueAsBytes(block);

            // Preparing indexation
            IndexRequestBuilder indexRequest = getClient().prepareIndex(block.getCurrency(), INDEX_TYPE_BLOCK)
                    .setId(block.getNumber().toString())
                    .setSource(json);

            // Execute indexation
            indexRequest
                    .setRefresh(true)
                    .execute().actionGet();

        } catch(JsonProcessingException e) {
            throw new UCoinTechnicalException(e);
        }
    }

    /**
     *
     * @param currencyName
     * @param number the block number
     * @param json block as JSON
     */
    public void indexBlockAsJson(String currencyName, int number, byte[] json) {
        ObjectUtils.checkNotNull(json);
        ObjectUtils.checkArgument(json.length > 0);

        // Preparing indexation
        IndexRequestBuilder indexRequest = getClient().prepareIndex(currencyName, INDEX_TYPE_BLOCK)
                .setId(String.valueOf(number))
                .setSource(json);

        // Execute indexation
        indexRequest
                .setRefresh(true)
                .execute().actionGet();
    }

   /**
    *
    * @param currencyName
    * @param json block as JSON
    */
    public void indexCurrentBlockAsJson(String currencyName, byte[] json) {
        ObjectUtils.checkNotNull(json);
        ObjectUtils.checkArgument(json.length > 0);

        // Preparing indexation
        IndexRequestBuilder indexRequest = getClient().prepareIndex(currencyName, INDEX_TYPE_BLOCK)
                .setId("current")
                .setSource(json);

        // Execute indexation
        indexRequest
                .setRefresh(true)
                .execute().actionGet();
    }

    public List<BlockchainBlock> findBlocksByHash(String currencyName, String query) {
        String[] queryParts = query.split("[\\t ]+");

        // Prepare request
        SearchRequestBuilder searchRequest = getClient()
                .prepareSearch(currencyName)
                .setTypes(INDEX_TYPE_BLOCK)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        // If only one term, search as prefix
        if (queryParts.length == 1) {
            searchRequest.setQuery(QueryBuilders.prefixQuery("hash", query));
        }

        // If more than a word, search on terms match
        else {
            searchRequest.setQuery(QueryBuilders.matchQuery("hash", query));
        }

        // Sort as score/memberCount
        searchRequest.addSort("_score", SortOrder.DESC)
                .addSort("number", SortOrder.DESC);

        // Highlight matched words
        searchRequest.setHighlighterTagsSchema("styled")
                .addHighlightedField("hash")
                .addFields("hash")
                .addFields("*", "_source");

        // Execute query
        SearchResponse searchResponse = searchRequest.execute().actionGet();

        // Read query result
        return toBlocks(searchResponse, true);
    }

    public BlockchainBlock getBlockById(String currencyName, int number) {
        return getBlockByIdStr(currencyName, String.valueOf(number));
    }

    public BlockchainBlock getCurrentBlock(String currencyName) {
        return getBlockByIdStr(currencyName, "current");
    }

    /* -- Internal methods -- */

    public BlockchainBlock getBlockByIdStr(String currencyName, String blockId) {

        // Prepare request
        SearchRequestBuilder searchRequest = getClient()
                .prepareSearch(currencyName)
                .setTypes(INDEX_TYPE_BLOCK)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        // If more than a word, search on terms match
        searchRequest.setQuery(QueryBuilders.matchQuery("_id", blockId));

        // Execute query
        SearchResponse searchResponse = searchRequest.execute().actionGet();
        List<BlockchainBlock> currencies = toBlocks(searchResponse, false);

        if (CollectionUtils.isEmpty(currencies)) {
            return null;
        }

        // Return the unique result
        return CollectionUtils.extractSingleton(currencies);
    }

    protected List<BlockchainBlock> toBlocks(SearchResponse response, boolean withHighlight) {
        try {
            // Read query result
            SearchHit[] searchHits = response.getHits().getHits();
            List<BlockchainBlock> result = Lists.newArrayListWithCapacity(searchHits.length);
            for (SearchHit searchHit : searchHits) {
                BlockchainBlock block = null;
                if (searchHit.source() != null) {
                    block = getObjectMapper().readValue(searchHit.source(), BlockchainBlock.class);
                }
                else {
                    block = new BlockchainBlock();
                    SearchHitField field = searchHit.getFields().get("hash");
                    block.setHash((String) field.getValue());
                }
                result.add(block);

                // If possible, use highlights
                if (withHighlight) {
                    Map<String, HighlightField> fields = searchHit.getHighlightFields();
                    for (HighlightField field : fields.values()) {
                        String blockNameHighLight = field.getFragments()[0].string();
                        block.setHash(blockNameHighLight);
                    }
                }
            }

            return result;
        } catch(IOException e) {
            throw new UCoinTechnicalException("Error while reading block search result: " + e.getMessage(), e);
        }
    }

}
