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


import com.fasterxml.jackson.databind.ObjectMapper;
import io.ucoin.ucoinj.core.beans.Bean;
import io.ucoin.ucoinj.core.beans.InitializingBean;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.core.util.StringUtils;
import io.ucoin.ucoinj.elasticsearch.config.Configuration;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Benoit on 08/04/2015.
 */
public class ElasticSearchService implements Bean,InitializingBean, Closeable {

    private static final Logger log = LoggerFactory.getLogger(CurrencyIndexerService.class);
    private Client client;
    private Node node;
    private boolean localNode = false;
    private ObjectMapper objectMapper;

    public ElasticSearchService() {
        node = null;
    }

    @Override
    public void afterPropertiesSet() {

        Configuration config = Configuration.instance();

        // Define Slf4j as ES logging framework
        ESLoggerFactory.setDefaultFactory(new Slf4jESLoggerFactory());

        // Create the object mapper
        objectMapper = new ObjectMapper();

        // Create the local node, if need (useful in dev mode)
        if (config.isEmbedded()) {
            startNode();
        }

        // Create the client
        client = createClient();
    }

    public void startNode() {
        Configuration config = Configuration.instance();
        startNode(
                config.isHttpEnable(),
                config.isLocal());
    }

    public void startNode(boolean enableHttp, boolean local) {

        if (node != null && !localNode) {
            //already running
            return;
        }

        // Close client and node
        try {
            close();
        }
        catch(IOException e) {
            // continue
        }
        client = null;

        node = createNode(enableHttp, local);
        client = createClient();
    }

    public void stopNode() {
        // Close client and node
        try {
            close();
        }
        catch(IOException e) {
            // continue
        }

        // Only recreate the client
        client = createClient();
    }

    @Override
    public void close() throws IOException {

        // Closing the client (if exists)
        if (client != null) {
            if (log.isDebugEnabled()) {
                log.debug("Closing ElasticSearch client");
            }
            client.close();
            client = null;
        }

        // Closing the node (if exists)
        if (node != null) {
            log.info("Closing ElasticSearch node");

            node.close();
            node = null;
        }
    }

    public Client getClient() {
        return client;
    }

    public Node getNode() {
        return node;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /* -- internal methods  -- */

    protected Node createNode(boolean enableHttp, boolean local) {

        Configuration config = Configuration.instance();
        String clusterName = config.getClusterName();
        this.localNode = local;

        // Log starting
        if (local) {
            if (StringUtils.isNotBlank(clusterName)) {
                log.warn(String.format("Starts ElasticSearch as [local] node with cluster name [%s] at [%s]. Local node are not recommended for production deployment.", clusterName, config.getDataDirectory()));
            } else {
                log.warn(String.format("Starts ElasticSearch as [local] node at [%s]. Local node are not recommended for production deployment.", config.getDataDirectory()));
            }
        }
        else {
            if (StringUtils.isNotBlank(clusterName)) {
                log.info(String.format("Starts ElasticSearch node with cluster name [%s] at [%s].", clusterName, config.getDataDirectory()));
            } else {
                log.warn(String.format("Starts ElasticSearch node at [%s] without cluster name. Having no cluster name is not recommended for production deployment.", config.getDataDirectory()));
            }
        }

        Settings settings = Settings.settingsBuilder()
                .put("http.enabled", enableHttp)
                .put("http.host", config.getHost())
                .put("path.home", config.getBasedir())
                .put("path.data", config.getDataDirectory())
                .build();

        // Create a node builder
        NodeBuilder nodeBuilder = NodeBuilder.nodeBuilder()
                .clusterName(clusterName)
                .settings(settings)
                .local(local);

        if (StringUtils.isNotBlank(clusterName)) {
            nodeBuilder.clusterName(clusterName);
        }

        // Start the node from builder
        Node node = nodeBuilder.node().start();

        return node;
    }

    protected Client createClient() {
        if (node != null) {
            return node.client();
        }

        Configuration config = Configuration.instance();
        String host = config.getHost();
        int port = config.getPort();
        String clusterName = config.getClusterName();
        File dataDirectory = new File(config.getDataDirectory(), "client");

        if (log.isDebugEnabled()) {
            log.debug(String.format("Starts ElasticSearch client for node [%s:%s] at [%s]", host, port, dataDirectory));
        }


        Settings.Builder settings = Settings.settingsBuilder()
                .put("path.home", config.getBasedir())
                .put("path.data", dataDirectory);

        if (io.ucoin.ucoinj.core.util.StringUtils.isNotBlank(clusterName)) {
            settings.put("cluster.name", clusterName);
        }
        else {
            settings.put("client.transport.ignore_cluster_name", true);
            log.warn("Ignoring cluster name verification (no cluster name found in configuration). This is not recommended for production deployment.");
        }

        // Create the transport Client
        try {
            return TransportClient.builder()
                    .settings(settings.build())
                    .build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        } catch (UnknownHostException e) {
            throw new TechnicalException(e);
        }
    }
}
