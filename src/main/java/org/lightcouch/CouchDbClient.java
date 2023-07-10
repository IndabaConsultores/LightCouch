/*
 * Copyright (C) 2011 lightcouch.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.lightcouch;

import java.io.Closeable;
import java.io.IOException;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

/**
 * Presents a <i>client</i> to CouchDB database server.
 * <p>
 * This class is the main object to use to gain access to the APIs.
 * <h3>Usage Example:</h3>
 * <p>
 * Create a new client instance:
 * 
 * <pre>
 * CouchDbClient dbClient = new CouchDbClient();
 * </pre>
 * 
 * <p>
 * Start using the API by the client:
 * 
 * <p>
 * Documents <code>CRUD</code> APIs is accessed by the client directly, eg.:
 * {@link CouchDbClientBase#find(Class, String) dbClient.find(Foo.class, "doc-id")}
 * <p>
 * View APIs {@link View dbClient.view()}
 * <p>
 * Change Notifications {@link Changes dbClient.changes()}
 * <p>
 * Replication {@link Replication dbClient.replication()} and {@link Replicator dbClient.replicator()}
 * <p>
 * DB server {@link CouchDbContext dbClient.context()}
 * <p>
 * Design documents {@link CouchDbDesign dbClient.design()}
 * 
 * <p>
 * At the end of a client usage; it's useful to call: {@link #shutdown()} to ensure proper release of resources.
 * 
 * @see CouchDbClientAndroid
 * @since 0.0.2
 * @author Ahmed Yehia
 *
 */
public class CouchDbClient extends CouchDbClientBase implements Closeable {

    /**
     * Constructs a new instance of this class, expects a configuration file named <code>couchdb.properties</code> to be
     * available in your application default classpath.
     */
    public CouchDbClient() {
        super();
    }

    /**
     * Constructs a new instance of this class.
     * 
     * @param configFileName The configuration file name.
     */
    public CouchDbClient(String configFileName) {
        super(new CouchDbConfig(configFileName));
    }

    /**
     * Constructs a new instance of this class.
     * 
     * @param dbName The database name.
     * @param createDbIfNotExist To create a new database if it does not already exist.
     * @param protocol The protocol to use (i.e http or https)
     * @param host The database host address
     * @param port The database listening port
     * @param username The Username credential
     * @param password The Password credential
     */
    public CouchDbClient(String dbName, boolean createDbIfNotExist, String protocol, String host, int port,
            String username, String password) {
        super(new CouchDbConfig(
                new CouchDbProperties(dbName, createDbIfNotExist, protocol, host, port, username, password)));
    }

    /**
     * Constructs a new instance of this class.
     * 
     * @param properties An object containing configuration properties.
     * @see CouchDbProperties
     */
    public CouchDbClient(CouchDbProperties properties) {
        super(new CouchDbConfig(properties));
    }

    /**
     * @return {@link CloseableHttpClient} instance.
     */
    @Override
    CloseableHttpClient createHttpClient(CouchDbProperties props, CredentialsProvider credentialsProvider) {
        try {
            Registry<ConnectionSocketFactory> registry = createRegistry(props);
            PoolingHttpClientConnectionManager ccm = createConnectionManager(props, registry);

            HttpClientBuilder clientBuilder = HttpClients.custom().setConnectionManager(ccm).setDefaultRequestConfig(
                    RequestConfig.custom().setResponseTimeout(Timeout.ofMilliseconds(props.getSocketTimeout()))
                            .setCookieSpec(StandardCookieSpec.STRICT).build());
            if (props.getProxyHost() != null) {
                clientBuilder.setProxy(new HttpHost(props.getProxyHost(), props.getProxyPort()));
            }

            if (credentialsProvider != null) {
                clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }

            registerInterceptors(clientBuilder);
            return clientBuilder.build();
        } catch (Exception e) {
            throw new IllegalStateException("Error Creating HTTPClient: ", e);
        }
    }

    @Override
    BasicCredentialsProvider initializeCredentials(CouchDbProperties props) {
        BasicCredentialsProvider credsProvider = null;
        if (props.getUsername() != null) {
            credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(props.getHost(), props.getPort()),
                    new UsernamePasswordCredentials(props.getUsername(), props.getPassword().toCharArray()));
            props.clearPassword();
        }
        return credsProvider;
    }

    @Override
    HttpContext createContext() {
        HttpContext context = new BasicHttpContext();
        AuthCache authCache = new BasicAuthCache();
        BasicScheme auth = new BasicScheme();
        if (super.credentialsProvider != null) {
            Credentials credentials = super.credentialsProvider.getCredentials(new AuthScope(host), context);
            auth.initPreemptive(credentials);
            authCache.put(host, auth);
            context.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
        }
        return context;
    }

    private PoolingHttpClientConnectionManager createConnectionManager(CouchDbProperties props,
            Registry<ConnectionSocketFactory> registry) {

        PoolingHttpClientConnectionManager ccm;

        if (props.getConnectionTTL() > 0) {
            ccm = new PoolingHttpClientConnectionManager(registry, PoolConcurrencyPolicy.STRICT,
                    TimeValue.ofMilliseconds(props.getConnectionTTL()), null);
        } else {
            ccm = new PoolingHttpClientConnectionManager(registry);
        }

        if (props.getMaxConnections() != 0) {
            ccm.setMaxTotal(props.getMaxConnections());
            ccm.setDefaultMaxPerRoute(props.getMaxConnections());
        }
        ConnectionConfig.Builder connectionConfigBuilder =
                ConnectionConfig.custom().setConnectTimeout(Timeout.ofMilliseconds(props.getConnectionTimeout()));

        if (props.getSocketTimeout() > 0) {
            connectionConfigBuilder.setSocketTimeout(Timeout.ofMilliseconds(props.getSocketTimeout()));
        }

        ccm.setDefaultConnectionConfig(connectionConfigBuilder.build());
        return ccm;
    }

    private Registry<ConnectionSocketFactory> createRegistry(CouchDbProperties props)
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        RegistryBuilder<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create();

        if ("https".equals(props.getProtocol())) {
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            return registry.register("https", new SSLConnectionSocketFactory(sslcontext, new NoopHostnameVerifier()))
                    .build();
        } else {
            return registry.register("http", PlainConnectionSocketFactory.INSTANCE).build();
        }
    }

    /**
     * Adds request/response interceptors for logging and validation.
     * 
     * @param clientBuilder
     */
    private void registerInterceptors(HttpClientBuilder clientBuilder) {
        clientBuilder.addRequestInterceptorFirst(new HttpRequestInterceptor() {
            public void process(final HttpRequest request, final EntityDetails entity, final HttpContext context)
                    throws IOException {
                if (log.isInfoEnabled()) {
                    log.info("> " + request.getMethod() + " " + URLDecoder.decode(request.getRequestUri(), "UTF-8"));
                }
            }

        });
        clientBuilder.addResponseInterceptorFirst(new HttpResponseInterceptor() {
            public void process(final HttpResponse response, final EntityDetails entity, final HttpContext context)
                    throws IOException {
                if (log.isInfoEnabled()) {
                    log.info("< Status: " + response.getCode());
                }
                validate(response);
            }
        });
    }

    public void shutdown() {
        try {
            httpClient.close();
        } catch (IOException e) {
            // SILENT Close
        }
    }

    public void close() throws IOException {
        shutdown();
    }
}
