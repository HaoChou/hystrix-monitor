package com.netflix.hystrix.dashboard.http;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhou
 * Created on 2018/7/18
 */
public class ProxyConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(ProxyConnectionManager.class);

    private final static PoolingClientConnectionManager threadSafeConnectionManager = new PoolingClientConnectionManager();
    public final static HttpClient httpClient = new DefaultHttpClient(threadSafeConnectionManager);

    static {
        logger.debug("Initialize ProxyConnectionManager");
            /* common settings */
        HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, 10000);

            /* number of connections to allow */
        threadSafeConnectionManager.setDefaultMaxPerRoute(400);
        threadSafeConnectionManager.setMaxTotal(400);
    }
}
