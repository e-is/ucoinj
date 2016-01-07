package io.ucoin.ucoinj.elasticsearch.service;

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


import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainBlock;
import io.ucoin.ucoinj.core.client.model.bma.BlockchainParameters;
import io.ucoin.ucoinj.core.client.model.bma.EndpointProtocol;
import io.ucoin.ucoinj.core.client.model.bma.gson.GsonUtils;
import io.ucoin.ucoinj.core.client.model.bma.gson.JsonAttributeParser;
import io.ucoin.ucoinj.core.client.model.elasticsearch.Currency;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.client.service.bma.BlockchainRemoteService;
import io.ucoin.ucoinj.core.client.service.bma.NetworkRemoteService;
import io.ucoin.ucoinj.core.client.service.exception.HttpBadRequestException;
import io.ucoin.ucoinj.core.client.service.exception.JsonSyntaxException;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.core.model.ProgressionModel;
import io.ucoin.ucoinj.core.model.ProgressionModelImpl;
import io.ucoin.ucoinj.core.util.CollectionUtils;
import io.ucoin.ucoinj.core.util.ObjectUtils;
import io.ucoin.ucoinj.core.util.StringUtils;
import io.ucoin.ucoinj.elasticsearch.config.Configuration;
import io.ucoin.ucoinj.elasticsearch.service.exception.DuplicateIndexIdException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.concurrent.EsRejectedExecutionException;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.nuiton.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by Benoit on 30/03/2015.
 */
public class BlockIndexerService extends BaseIndexerService {

    private static final Logger log = LoggerFactory.getLogger(BlockIndexerService.class);

    public static final String INDEX_TYPE_BLOCK = "block";

    private static final int SYNC_MISSING_BLOCK_MAX_RETRY = 5;

    private CurrencyIndexerService currencyIndexerService;

    private BlockchainRemoteService blockchainService;

    private Gson gson;

    private Configuration config;

    public BlockIndexerService() {
        gson = GsonUtils.newBuilder().create();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        currencyIndexerService = ServiceLocator.instance().getCurrencyIndexerService();
        blockchainService = ServiceLocator.instance().getBlockchainRemoteService();
        config = Configuration.instance();
    }

    @Override
    public void close() throws IOException {
        super.close();
        currencyIndexerService = null;
        blockchainService = null;
        config = null;
        gson = null;
    }

    public void indexLastBlocks(Peer peer) {
        indexLastBlocks(peer, new ProgressionModelImpl());
    }

    public void indexLastBlocks(Peer peer, ProgressionModel progressionModel) {
        boolean bulkIndex = config.isIndexBulkEnable();

        progressionModel.setStatus(ProgressionModel.Status.RUNNING);
        progressionModel.setTotal(100);
        long timeStart = System.currentTimeMillis();

        try {
            // Get the currency name from node
            BlockchainParameters parameter = blockchainService.getParameters(peer);
            if (parameter == null) {
                progressionModel.setStatus(ProgressionModel.Status.FAILED);
                log.error(String.format("Could not connect to node [%s:%s]",
                        config.getNodeBmaHost(), config.getNodeBmaPort()));
                return;
            }
            String currencyName = parameter.getCurrency();

            progressionModel.setTask(I18n.t("ucoinj.blockIndexerService.indexLastBlocks.task", currencyName, peer.getHost(), peer.getPort()));
            log.info(I18n.t("ucoinj.blockIndexerService.indexLastBlocks.task",
                    currencyName, config.getNodeBmaHost(), config.getNodeBmaPort()));

            // Create index currency if need
            currencyIndexerService.createIndexIfNotExists();

            Currency currency = currencyIndexerService.getCurrencyById(currencyName);
            if (currency == null) {
                currencyIndexerService.indexCurrencyFromPeer(peer);
            }

            // Check if index exists
            createIndexIfNotExists(currencyName);

            // Then index all blocks
            BlockchainBlock currentBlock = blockchainService.getCurrentBlock(peer);

            if (currentBlock != null) {
                int maxBlockNumber = currentBlock.getNumber();

                // Get the last indexed block number
                int startNumber = 0;

                int currentBlockNumber = -1;
                BlockchainBlock indexedCurrentBlock = getCurrentBlock(currencyName);
                if (indexedCurrentBlock != null && indexedCurrentBlock.getNumber() != null) {
                    currentBlockNumber = indexedCurrentBlock.getNumber();

                    // Previous block could have been not indexed : so start at the max(number)
                    indexedCurrentBlock = getBlockById(currencyName, currentBlockNumber);
                    // If exists on blockchain, so can use it
                    if (indexedCurrentBlock != null) {
                        startNumber = currentBlockNumber + 1;
                    }
                }

                // Before to start at '0' (first block), try to use the max(number)
                if (startNumber <= 1 ){
                    startNumber = getMaxBlockNumber(currencyName) + 1;
                }

                if (startNumber <= maxBlockNumber) {
                    Collection<String> missingBlocks = bulkIndex
                            ? indexBlocksUsingBulk(peer, currencyName, startNumber, maxBlockNumber, progressionModel)
                            : indexBlocksNoBulk(peer, currencyName, startNumber, maxBlockNumber, progressionModel);


                    // If some blocks are missing, try to get it using other peers
                    if (CollectionUtils.isNotEmpty(missingBlocks)) {
                        progressionModel.setTask(I18n.t("ucoinj.blockIndexerService.indexLastBlocks.otherPeers.task", currencyName));
                        missingBlocks = indexMissingBlocksFromOtherPeers(peer, currentBlock, missingBlocks, 1);
                    }

                    if (CollectionUtils.isEmpty(missingBlocks)) {
                        log.info(String.format("All blocks indexed [%s ms]", (System.currentTimeMillis() - timeStart)));
                        progressionModel.setStatus(ProgressionModel.Status.SUCCESS);
                    }
                    else {
                        log.warn(String.format("Could not indexed all blocks. Missing %s blocks.", missingBlocks.size()));
                        progressionModel.setStatus(ProgressionModel.Status.FAILED);
                    }
                }
                else {
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Current block from peer [%s:%s] is #%s. Index is up to date.", peer.getHost(), peer.getPort(), maxBlockNumber));
                    }
                    progressionModel.setStatus(ProgressionModel.Status.SUCCESS);
                }
            }
        } catch(Exception e) {
            log.error("Error during indexation: " + e.getMessage(), e);
            progressionModel.setStatus(ProgressionModel.Status.FAILED);
        }
    }

    public void deleteIndex(String currencyName) {
        deleteIndexIfExists(currencyName);
    }

    public boolean existsIndex(String currencyName) {
        return super.existsIndex(currencyName);
    }

    public void createIndexIfNotExists(String currencyName) {
        if (!existsIndex(currencyName)) {
            createIndex(currencyName);
        }
    }

    public void createIndex(String currencyName) {
        log.info(String.format("Creating index [%s]", currencyName));

        CreateIndexRequestBuilder createIndexRequestBuilder = getClient().admin().indices().prepareCreate(currencyName);
        Settings indexSettings = Settings.settingsBuilder()
                .put("number_of_shards", 1)
                .put("number_of_replicas", 1)
                .put("analyzer", createDefaultAnalyzer())
                .build();
        createIndexRequestBuilder.setSettings(indexSettings);
        createIndexRequestBuilder.addMapping(INDEX_TYPE_BLOCK, createIndexMapping());
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

        indexBlock(block, false);
    }

    /**
     * Create or update a block, depending on its existence and hash
     * @param block
     * @param updateWhenSameHash if true, always update an existing block. If false, update only if hash has changed.
     * @param wait wait indexation end
     * @throws DuplicateIndexIdException
     */
    public void saveBlock(BlockchainBlock block, boolean updateWhenSameHash, boolean wait) throws DuplicateIndexIdException {
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
            indexBlock(block, wait);
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
                indexBlock(block, wait);
            }
        }
    }

    public void indexBlock(BlockchainBlock block, boolean wait) {
        ObjectUtils.checkNotNull(block);
        ObjectUtils.checkArgument(StringUtils.isNotBlank(block.getCurrency()));
        ObjectUtils.checkNotNull(block.getHash());
        ObjectUtils.checkNotNull(block.getNumber());

        // Serialize into JSON
        // WARN: must use GSON, to have same JSON result (e.g identities and joiners field must be converted into String)
        String json = gson.toJson(block);

        // Preparing indexation
        IndexRequestBuilder indexRequest = getClient().prepareIndex(block.getCurrency(), INDEX_TYPE_BLOCK)
                .setId(block.getNumber().toString())
                .setSource(json);

        // Execute indexation
        ActionFuture<IndexResponse> futureResponse = indexRequest
                .setRefresh(true)
                .execute();

        if (wait) {
            futureResponse.actionGet();
        }
    }

    /**
     *
     * @param currencyName
     * @param number the block number
     * @param json block as JSON
     */
    public void indexBlockAsJson(String currencyName, int number, byte[] json, boolean refresh, boolean wait) {
        ObjectUtils.checkNotNull(json);
        ObjectUtils.checkArgument(json.length > 0);

        // Preparing indexation
        IndexRequestBuilder indexRequest = getClient().prepareIndex(currencyName, INDEX_TYPE_BLOCK)
                .setId(String.valueOf(number))
                .setSource(json);

        // Execute indexation
        if (!wait) {
            indexRequest
                    .setRefresh(refresh)
                    .execute();
        }
        else {
            indexRequest
                    .setRefresh(refresh)
                    .execute().actionGet();
        }
    }

    /**
     *
     * @param currentBlock
     */
    public void indexCurrentBlock(BlockchainBlock currentBlock, boolean wait) {
        ObjectUtils.checkNotNull(currentBlock);
        ObjectUtils.checkArgument(StringUtils.isNotBlank(currentBlock.getCurrency()));
        ObjectUtils.checkNotNull(currentBlock.getHash());
        ObjectUtils.checkNotNull(currentBlock.getNumber());

        // Serialize into JSON
        // WARN: must use GSON, to have same JSON result (e.g identities and joiners field must be converted into String)
        String json = gson.toJson(currentBlock);

        indexCurrentBlockAsJson(currentBlock.getCurrency(), json.getBytes(), true, wait);
    }

   /**
    *
    * @param currencyName
    * @param currentBlockJson block as JSON
    */
    public void indexCurrentBlockAsJson(String currencyName, byte[] currentBlockJson, boolean refresh, boolean wait) {
        ObjectUtils.checkNotNull(currentBlockJson);
        ObjectUtils.checkArgument(currentBlockJson.length > 0);

        // Preparing indexation
        IndexRequestBuilder indexRequest = getClient().prepareIndex(currencyName, INDEX_TYPE_BLOCK)
                .setId("current")
                .setSource(currentBlockJson);

        // Execute indexation
        if (!wait) {
            boolean acceptedInPool = false;
            while(!acceptedInPool)
                try {
                    indexRequest
                            .setRefresh(refresh)
                            .execute();
                    acceptedInPool = true;
                }
                catch(EsRejectedExecutionException e) {
                    // not accepted, so wait
                    try {
                        Thread.sleep(1000); // 1s
                    }
                    catch(InterruptedException e2) {
                        // silent
                    }
                }

        } else {
            indexRequest
                    .setRefresh(refresh)
                    .execute().actionGet();
        }
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

    public int getMaxBlockNumber(String currencyName) {
        // Prepare request
        SearchRequestBuilder searchRequest = getClient()
                .prepareSearch(currencyName)
                .setTypes(INDEX_TYPE_BLOCK)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        // Get max(number)
        searchRequest.addAggregation(AggregationBuilders.max("max_number").field("number"));

        // Execute query
        SearchResponse searchResponse = searchRequest.execute().actionGet();

        // Read query result
        Max result = searchResponse.getAggregations().get("max_number");
        if (result == null) {
            return -1;
        }

        return (result.getValue() == Double.NEGATIVE_INFINITY)
                ? -1
                : (int)result.getValue();
    }

    public BlockchainBlock getBlockById(String currencyName, int number) {
        return getBlockByIdStr(currencyName, String.valueOf(number));
    }

    public BlockchainBlock getCurrentBlock(String currencyName) {
        return getBlockByIdStr(currencyName, "current");
    }

    /* -- Internal methods -- */


    public XContentBuilder createIndexMapping() {
        try {
            XContentBuilder mapping = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject(INDEX_TYPE_BLOCK)
                    .startObject("properties")

                    // block number
                    .startObject("number")
                    .field("type", "integer")
                    .endObject()

                    // hash
                    .startObject("hash")
                    .field("type", "string")
                    .endObject()

                    // membercount
                    .startObject("memberCount")
                    .field("type", "integer")
                    .endObject()

                    // membersChanges
                    .startObject("membersChanges")
                    .field("type", "string")
                    .endObject()

                    // identities:
                    //.startObject("identities")
                    //.endObject()

                    .endObject()
                    .endObject().endObject();

            return mapping;
        }
        catch(IOException ioe) {
            throw new TechnicalException("Error while getting mapping for block index: " + ioe.getMessage(), ioe);
        }
    }

    public BlockchainBlock getBlockByIdStr(String currencyName, String blockId) {

        // Prepare request
        SearchRequestBuilder searchRequest = getClient()
                .prepareSearch(currencyName)
                .setTypes(INDEX_TYPE_BLOCK)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        // If more than a word, search on terms match
        searchRequest.setQuery(QueryBuilders.matchQuery("_id", blockId));

        // Execute query
        try {
            SearchResponse searchResponse = searchRequest.execute().actionGet();
            List<BlockchainBlock> currencies = toBlocks(searchResponse, false);
            if (CollectionUtils.isEmpty(currencies)) {
                return null;
            }

            // Return the unique result
            return CollectionUtils.extractSingleton(currencies);
        }
        catch(JsonSyntaxException e) {
            throw new TechnicalException(String.format("Error while getting indexed block #%s for currency [%s]", blockId, currencyName), e);
        }

    }

    protected List<BlockchainBlock> toBlocks(SearchResponse response, boolean withHighlight) {
        // Read query result
        SearchHit[] searchHits = response.getHits().getHits();
        List<BlockchainBlock> result = Lists.newArrayListWithCapacity(searchHits.length);
        for (SearchHit searchHit : searchHits) {
            BlockchainBlock block;
            if (searchHit.source() != null) {
                String jsonString = new String(searchHit.source());
                try {
                    block = GsonUtils.newBuilder().create().fromJson(jsonString, BlockchainBlock.class);
                } catch(Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Error while parsing block from JSON:\n" + jsonString);
                    }
                    throw new JsonSyntaxException("Error while read block from JSON: " + e.getMessage(), e);
                }
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
    }


    public Collection<String> indexBlocksNoBulk(Peer peer, String currencyName, int firstNumber, int lastNumber, ProgressionModel progressionModel) {
        Set<String> missingBlockNumbers = new LinkedHashSet<>();

        for (int curNumber = firstNumber; curNumber <= lastNumber; curNumber++) {
            if (curNumber != 0 && curNumber % 1000 == 0) {

                // Check is stopped
                if (progressionModel.isCancel()) {
                    progressionModel.setStatus(ProgressionModel.Status.STOPPED);
                    if (log.isInfoEnabled()) {
                        log.info(I18n.t("ucoinj.blockIndexerService.indexLastBlocks.stopped"));
                    }
                    return missingBlockNumbers;
                }

                // Report progress
                reportIndexBlocksProgress(progressionModel, firstNumber, lastNumber, curNumber);
            }

            try {
                String blockAsJson = blockchainService.getBlockAsJson(peer, curNumber);
                indexBlockAsJson(currencyName, curNumber, blockAsJson.getBytes(), false, true /*wait*/);

                // If last block
                if (curNumber == lastNumber - 1) {
                    // update the current block
                    indexCurrentBlockAsJson(currencyName, blockAsJson.getBytes(), true, true /*wait*/);
                }
            }
            catch(Throwable t) {
                log.debug(String.format("Error while getting block #%s: %s. Skipping this block.", curNumber, t.getMessage()));
                missingBlockNumbers.add(String.valueOf(curNumber));
            }
        }

        return missingBlockNumbers;
    }

    public Collection<String> indexBlocksUsingBulk(Peer peer, String currencyName, int firstNumber, int lastNumber, ProgressionModel progressionModel) {
        Set<String> missingBlockNumbers = new LinkedHashSet<>();

        Client client = getClient();
        boolean debug = log.isDebugEnabled();

        int batchSize = config.getIndexBulkSize();
        JsonAttributeParser blockNumberParser = new JsonAttributeParser("number");

        for (int batchFirstNumber = firstNumber; batchFirstNumber < lastNumber; ) {
            // Check if stop (e.g. ask by user)
            if (progressionModel.isCancel()) {
                progressionModel.setStatus(ProgressionModel.Status.STOPPED);
                if (log.isInfoEnabled()) {
                    log.info(I18n.t("ucoinj.blockIndexerService.indexLastBlocks.stopped"));
                }
                return missingBlockNumbers;
            }

            String[] blocksAsJson = null;
            try {
                blocksAsJson = blockchainService.getBlocksAsJson(peer, batchSize, batchFirstNumber);
            } catch(HttpBadRequestException e) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Error while getting blocks from #%s (count=%s): %s. Skipping blocks.", batchFirstNumber, batchSize, e.getMessage()));
                }
            }

            // Peer send no blocks
            if (CollectionUtils.isEmpty(blocksAsJson)) {

                // Add range to missing blocks
                missingBlockNumbers.add(batchFirstNumber + "-" + (batchFirstNumber+batchSize));

                // Update counter
                batchFirstNumber += batchSize;
            }

            // Process received blocks
            else {
                BulkRequestBuilder bulkRequest = client.prepareBulk();
                for (String blockAsJson : blocksAsJson) {
                    int itemNumber = blockNumberParser.getValueAsInt(blockAsJson);

                    // update curNumber with max number;
                    if (itemNumber > batchFirstNumber) {
                        batchFirstNumber = itemNumber;
                    }

                    // Add to bulk
                    bulkRequest.add(client.prepareIndex(currencyName, INDEX_TYPE_BLOCK, String.valueOf(itemNumber))
                            .setRefresh(false)
                            .setSource(blockAsJson)
                    );

                    // If last block : also update the current block
                    if (itemNumber == lastNumber) {
                        bulkRequest.add(client.prepareIndex(currencyName, INDEX_TYPE_BLOCK, "current")
                                .setRefresh(true)
                                .setSource(blockAsJson)
                        );
                    }
                }

                if (bulkRequest.numberOfActions() > 0) {

                    // Flush the bulk if not empty
                    BulkResponse bulkResponse = bulkRequest.get();

                    // If failures, continue but save missing blocks
                    if (bulkResponse.hasFailures()) {
                        // process failures by iterating through each bulk response item
                        for (BulkItemResponse itemResponse : bulkResponse) {
                            boolean skip = !itemResponse.isFailed()
                                    || Objects.equal("current", itemResponse.getId())
                                    || missingBlockNumbers.contains(Integer.parseInt(itemResponse.getId()));
                            if (!skip) {
                                int itemNumber = Integer.parseInt(itemResponse.getId());
                                if (debug) {
                                    log.debug(String.format("Error while getting block #%s: %s. Skipping this block.", itemNumber, itemResponse.getFailureMessage()));
                                }
                                missingBlockNumbers.add(itemResponse.getId());
                            }
                        }
                    }
                }
            }

            // Report progress
            reportIndexBlocksProgress(progressionModel, firstNumber, lastNumber, batchFirstNumber);

        }

        return missingBlockNumbers;
    }

    /**
     * Get blocks from other peers.
     * WARNING: given list must be ordered (with ascending order)
     * @param peer
     * @param currentBlock
     * @param sortedMissingBlocks
     * @param tryCounter
     */
    protected Collection<String> indexMissingBlocksFromOtherPeers(Peer peer, BlockchainBlock currentBlock, Collection<String> sortedMissingBlocks, int tryCounter) {
        ObjectUtils.checkNotNull(peer);
        ObjectUtils.checkNotNull(currentBlock);
        ObjectUtils.checkNotNull(currentBlock.getHash());
        ObjectUtils.checkNotNull(currentBlock.getNumber());
        ObjectUtils.checkArgument(CollectionUtils.isNotEmpty(sortedMissingBlocks));
        ObjectUtils.checkArgument(tryCounter >= 1);

        NetworkRemoteService networkRemoteService = ServiceLocator.instance().getNetworkRemoteService();
        BlockchainRemoteService blockchainRemoteService = ServiceLocator.instance().getBlockchainRemoteService();
        String currencyName = currentBlock.getCurrency();
        boolean debug = log.isDebugEnabled();

        Set<String> newMissingBlocks = new LinkedHashSet<>();
        newMissingBlocks.addAll(sortedMissingBlocks);

        if (debug) {
            log.debug(String.format("Missing blocks are: %s", newMissingBlocks.toString()));
        }

        // Select other peers, in filtering on the same blockchain version

        // TODO : a activer quand les peers seront bien mis à jour (UP/DOWN, block, hash...)
        //List<Peer> otherPeers = networkRemoteService.findPeers(peer, "UP", EndpointProtocol.BASIC_MERKLED_API,
        //        currentBlock.getNumber(), currentBlock.getHash());
        List<Peer> otherPeers = networkRemoteService.findPeers(peer, null, EndpointProtocol.BASIC_MERKLED_API,
                null, null);

        for(Peer childPeer: otherPeers) {
            if (log.isInfoEnabled()) {
                log.info(String.format("Trying to get missing blocks from other peer [%s:%s]...", childPeer.getHost(), childPeer.getPort()));
            }
            try {
                for(String blockNumberStr: ImmutableSet.copyOf(sortedMissingBlocks)) {

                    boolean isBlockRange = blockNumberStr.indexOf('-') != -1;

                    // Get using bulk
                    if (isBlockRange) {
                        String[] rangeParts = blockNumberStr.split("-");
                        int firstNumber = Integer.parseInt(rangeParts[0]);
                        int lastNumber = Integer.parseInt(rangeParts[1]);

                        // Remove current blocks range
                        newMissingBlocks.remove(blockNumberStr);

                        Collection<String> bulkMissingBlocks = indexBlocksUsingBulk(childPeer, currencyName, firstNumber, lastNumber, new ProgressionModelImpl());

                        // Re add if new missing blocks
                        if (CollectionUtils.isNotEmpty(bulkMissingBlocks)) {
                            newMissingBlocks.addAll(bulkMissingBlocks);
                        }
                    }

                    // Get blocks one by one
                    else {
                        int blockNumber = Integer.parseInt(blockNumberStr);
                        String blockAsJson = blockchainRemoteService.getBlockAsJson(childPeer, blockNumber);
                        if (StringUtils.isNotBlank(blockAsJson)) {
                            if (debug) {
                                log.trace("Found missing block #%s on peer [%s:%s].", blockNumber, childPeer.getHost(), childPeer.getPort());
                            }

                            // Index the missing block
                            indexBlockAsJson(currencyName, blockNumber, blockAsJson.getBytes(), false, true/*wait*/);

                            // Remove this block number from the final missing list
                            newMissingBlocks.remove(blockNumber);
                        }
                    }
                }

                if (CollectionUtils.isEmpty(newMissingBlocks)) {
                    break;
                }

                // Update the list, for the next iteration
                sortedMissingBlocks =  newMissingBlocks;
            }
            catch(TechnicalException e) {
                if (debug) {
                    log.debug("Error while getting blocks from peer [%s:%s]: %s. Skipping this peer.", childPeer.getHost(), childPeer.getPort(), e.getMessage());
                }

                continue; // skip this peer
            }
        }


        if (CollectionUtils.isEmpty(newMissingBlocks)) {
            return null;
        }

        tryCounter++;
        if (tryCounter >= SYNC_MISSING_BLOCK_MAX_RETRY) {
            // Max retry : stop here
            log.error("Some blocks are still missing, after %s try: %s", SYNC_MISSING_BLOCK_MAX_RETRY, newMissingBlocks.toArray(new String[0]));
            return newMissingBlocks;
        }

        if (debug) {
            log.debug("Some blocks are still missing: %s. Will retry later (%s/%s)...", newMissingBlocks.toArray(new String[0]), tryCounter, SYNC_MISSING_BLOCK_MAX_RETRY);
        }
        try {
            Thread.sleep(60 *1000); // wait 1 min

        }
        catch (InterruptedException e) {
            return null; // stop here
        }

        // retrying, with the new new blockchain
        BlockchainBlock newCurrentBlock =  blockchainRemoteService.getCurrentBlock(peer);
        return indexMissingBlocksFromOtherPeers(peer, newCurrentBlock, newMissingBlocks, tryCounter);
    }

    protected void reportIndexBlocksProgress(ProgressionModel progressionModel, int firstNumber, int lastNumber, int curNumber) {
        int pct = (curNumber - firstNumber) * 100 / (lastNumber - firstNumber);
        progressionModel.setCurrent(pct);

        progressionModel.setMessage(I18n.t("ucoinj.blockIndexerService.indexLastBlocks.progress", curNumber, lastNumber, pct));
        if (log.isInfoEnabled()) {
            log.info(I18n.t("ucoinj.blockIndexerService.indexLastBlocks.progress", curNumber, lastNumber, pct));
        }

    }
}
