package io.ucoin.client.core.service;

import io.ucoin.client.core.config.Configuration;
import io.ucoin.client.core.technical.UCoinTechnicalException;
import io.ucoin.client.core.technical.gson.GsonUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

public abstract class AbstractService implements Closeable {

    private static final Log log = LogFactory.getLog(AbstractService.class);

    protected URI baseUri;
    protected Integer baseTimeOut;
    protected final Gson gson;
    protected final CloseableHttpClient httpClient;

    public AbstractService() {
        super();
        Configuration config = Configuration.instance();

        this.gson = GsonUtils.newBuilder().create();
        this.baseTimeOut = config.getNodeTimeout();
        this.httpClient = initHttpClient(config);
        this.baseUri = initNodeURI(config);
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    /* -- Internal methods -- */

    protected CloseableHttpClient initHttpClient(Configuration config) {
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(getRequestConfig())
                // .setDefaultCredentialsProvider(getCredentialsProvider())
                .build();
        return httpClient;
    }

    protected URI initNodeURI(Configuration config) {
        try {
            URI nodeURI = config.getNodeUrl().toURI(); 
        
            return nodeURI;
        } catch (URISyntaxException ex) {
            throw new UCoinTechnicalException(ex);
        } 
    }

    protected URI getAppendedPath(String... path) throws URISyntaxException {
        String pathToAppend = Joiner.on('/').skipNulls().join(path);

        URIBuilder builder = new URIBuilder(baseUri);
        builder.setPath(baseUri.getPath() + pathToAppend);
        return builder.build();
    }

    protected RequestConfig getRequestConfig() {
        // build request config for timeout
        return RequestConfig.custom().setSocketTimeout(baseTimeOut).setConnectTimeout(baseTimeOut).build();
    }

    @SuppressWarnings("unchecked")
    protected <T> T executeRequest(HttpUriRequest request, Class<? extends T> resultClass) {
        Preconditions.checkNotNull(httpClient);
        T result = null;

        if (log.isDebugEnabled()) {
            log.debug("Executing request : " + request.getRequestLine());
        }

        try {
            try (CloseableHttpResponse response = httpClient.execute(request)) {

                if (log.isDebugEnabled()) {
                    log.debug("Received response : " + response.getStatusLine());
                }

                switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK: {
                    result = (T) parseResponse(response, resultClass);

                    EntityUtils.consume(response.getEntity());
                    break;
                }
                case HttpStatus.SC_UNAUTHORIZED:
                case HttpStatus.SC_FORBIDDEN:
                    throw new UCoinTechnicalException("ucoin.client.authentication");
                default:
                    throw new UCoinTechnicalException("ucoin.client.status" + response.getStatusLine().toString());
                }

            }
        } catch (ConnectException e) {
            throw new UCoinTechnicalException("ucoin.client.core.connect", e);
        }
        catch (IOException e) {
            throw new UCoinTechnicalException(e.getMessage(), e);
        }

        return result;
    }

    protected Object parseResponse(CloseableHttpResponse response, Class<?> ResultClass) throws IOException {
        Object result;
        
        boolean stringOutput = ResultClass.equals(String.class);
        
        // If trace enable, log the response before parsing
        if (log.isTraceEnabled() || stringOutput) {
            try (InputStream content = response.getEntity().getContent()) {
                String stringContent = getContentAsString(content);
                if (log.isTraceEnabled()) {
                	log.trace("Parsing response:\n" + stringContent);
                }
                
                if (stringOutput) {
                	return stringContent;
                }
                
                result = gson.fromJson(stringContent, ResultClass);
            } 
        }
        
        // trace not enable
        else {
            try (InputStream content = response.getEntity().getContent()) {
                Reader reader = new InputStreamReader(content, Charsets.UTF_8);
                result = gson.fromJson(reader, ResultClass);                
            }
        }
        

        if (result == null) {
            throw new UCoinTechnicalException("ucoin.client.core.emptyResponse");
        }

        if (log.isDebugEnabled()) {
            log.debug("response: " + ToStringBuilder.reflectionToString(result, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        return result;
    }
    
    protected String getContentAsString(InputStream content) throws IOException {
        Reader reader = new InputStreamReader(content, Charsets.UTF_8);
        StringBuilder result = new StringBuilder();
        char[] buf = new char[64];
        int len = 0;
        while((len = reader.read(buf)) != -1) {
            result.append(buf, 0, len);
        }
        return result.toString();
    }
}
