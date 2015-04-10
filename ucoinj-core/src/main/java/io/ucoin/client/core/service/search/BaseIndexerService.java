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
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ucoin.client.core.service.BaseService;
import io.ucoin.client.core.service.ServiceLocator;
import io.ucoin.client.core.technical.UCoinTechnicalException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Benoit on 08/04/2015.
 */
public abstract class BaseIndexerService extends BaseService{

    private static final Logger log = LoggerFactory.getLogger(BaseIndexerService.class);
    private ElasticSearchService elasticSearchService;

    public BaseIndexerService() {
    }

    @Override
    public void initialize() {
        super.initialize();

        elasticSearchService = ServiceLocator.instance().getElasticSearchService();
    }


    public Client getClient() {
        return elasticSearchService.getClient();
    }

    public ObjectMapper getObjectMapper() {
        return elasticSearchService.getObjectMapper();
    }


    public boolean existsIndex(String indexes) throws JsonProcessingException {
        IndicesExistsRequestBuilder requestBuilder = getClient().admin().indices().prepareExists(indexes);
        IndicesExistsResponse response = requestBuilder.execute().actionGet();

        return response.isExists();
    }

    public void deleteIndexIfExists(String indexes) throws JsonProcessingException {
        if (!existsIndex(indexes)) {
            return;
        }

        DeleteIndexRequestBuilder deleteIndexRequestBuilder = getClient().admin().indices().prepareDelete(indexes);
        DeleteIndexResponse response = deleteIndexRequestBuilder.execute().actionGet();
    }


    protected XContentBuilder getDefaultAnalyzer() {
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
            throw new UCoinTechnicalException("Error while preparing default index analyzer: " + e.getMessage(), e);
        }
    }
}
