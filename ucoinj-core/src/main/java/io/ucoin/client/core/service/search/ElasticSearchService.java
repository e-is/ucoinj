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


import com.fasterxml.jackson.databind.ObjectMapper;
import io.ucoin.client.core.config.Configuration;
import io.ucoin.client.core.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Benoit on 08/04/2015.
 */
public class ElasticSearchService extends BaseService implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(CurrencyIndexerService.class);
    private Client client;
    private ObjectMapper objectMapper;

    public ElasticSearchService() {
    }


    @Override
    public void initialize() {
        super.initialize();

        Configuration config = Configuration.instance();

        // Define Slf4j as ES logging framework
        ESLoggerFactory.setDefaultFactory(new Slf4jESLoggerFactory());

        // Start as local node (useful in dev mode)
        if (config.isNodeElasticSearchLocal()) {

            ImmutableSettings.Builder settings=ImmutableSettings.settingsBuilder();
            settings.put("http.enabled",true);

            // Node as local
            NodeBuilder nodeBuilder = NodeBuilder.nodeBuilder().settings(settings); //.local(true);

            // Cluster name
            String clusterName = config.getNodeElasticSearchLocalClusterName();
            if (StringUtils.isNotBlank(clusterName)) {
                log.warn(String.format("Starts ElasticSearch as [local] node with cluster name [%s]. This is not recommended for production deployment.", clusterName));

                nodeBuilder.clusterName(clusterName);
            }
            else {
                log.warn("Starts ElasticSearch as [local] node. This is not recommended for production deployment.");
            }

            client = nodeBuilder.node().client();
        }

        // Start using a remote node
        else {
            String host = config.getNodeElasticSearchHost();
            int port = config.getNodeElasticSearchPort();

            log.info(String.format("Starts ElasticSearch on node [%s:%s]", host, port));

            // Transport Client
            client = new TransportClient()
                    .addTransportAddress(new InetSocketTransportAddress(host, port));
        }

        objectMapper = new ObjectMapper();
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    public Client getClient() {
        return client;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
