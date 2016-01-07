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


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ucoin.ucoinj.core.beans.Bean;
import io.ucoin.ucoinj.core.beans.InitializingBean;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Benoit on 08/04/2015.
 */
public abstract class BaseIndexerService implements Bean, InitializingBean, Closeable {

    private static final Logger log = LoggerFactory.getLogger(BaseIndexerService.class);
    private ElasticSearchService elasticSearchService;

    public BaseIndexerService() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.elasticSearchService = ServiceLocator.instance().getElasticSearchService();
    }

    @Override
    public void close() throws IOException {
        this.elasticSearchService = null;
    }

    /* -- protected methods  -- */

    protected Client getClient() {
        return elasticSearchService.getClient();
    }

    protected ObjectMapper getObjectMapper() {
        return elasticSearchService.getObjectMapper();
    }

    protected boolean existsIndex(String indexes) {
        IndicesExistsRequestBuilder requestBuilder = getClient().admin().indices().prepareExists(indexes);
        IndicesExistsResponse response = requestBuilder.execute().actionGet();

        return response.isExists();
    }

    protected void deleteIndexIfExists(String indexName){
        if (!existsIndex(indexName)) {
            return;
        }
        log.info(String.format("Deleting index [%s]", indexName));

        DeleteIndexRequestBuilder deleteIndexRequestBuilder = getClient().admin().indices().prepareDelete(indexName);
        deleteIndexRequestBuilder.execute().actionGet();
    }

    protected XContentBuilder createDefaultAnalyzer() {
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

            return analyzer;
        } catch(IOException e) {
            throw new TechnicalException("Error while preparing default index analyzer: " + e.getMessage(), e);
        }
    }
}
