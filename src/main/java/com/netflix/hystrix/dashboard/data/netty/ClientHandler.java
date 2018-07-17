package com.netflix.hystrix.dashboard.data.netty;

import com.netflix.hystrix.dashboard.stream.ProxyStreamServlet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * @author zhou
 * Created on 2018/7/13
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {

    private LocalClient localClient;

    public ClientHandler(LocalClient localClient) {
        this.localClient = localClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("client [" + localClient.getUrl() + "] connected");
//        ctx.writeAndFlush("client [" + localClient.getClientNam() + "] Netty rocks").addListener(future -> {
//            System.out.println("client [" + localClient.getClientNam() + "] write has been finished");
//        });

        ctx.writeAndFlush("client [" + localClient.getUrl() + "] connected");
        HttpGet httpget = null;
        InputStream is = null;

        try {
            httpget = new HttpGet(localClient.getUrl());

            HttpClient client = ProxyConnectionManager.httpClient;
            HttpResponse httpResponse = client.execute(httpget);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                is = httpResponse.getEntity().getContent();
                int b = -1;
                StringBuilder sb = new StringBuilder();
                while ((b = is.read()) != -1) {
                    try {
                        sb.append((char)b);
                        if (b == 10 /** flush buffer on line feed */) {
                            ctx.writeAndFlush(sb.toString());
                            sb=new StringBuilder();
                        }
                    } catch (Exception e) {
                        if (e.getClass().getSimpleName().equalsIgnoreCase("ClientAbortException")) {
                            // don't throw an exception as this means the user closed the connection
//                            logger.debug("Connection closed by client. Will stop proxying ...");
                            // break out of the while loop
                            break;
                        } else {
                            // received unknown error while writing so throw an exception
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (Exception e) {
//            logger.error("Error proxying request: " + url, e);
        } finally {
            if (httpget != null) {
                try {
                    httpget.abort();
                } catch (Exception e) {
//                    logger.error("failed aborting proxy connection.", e);
                }
            }

            // httpget.abort() MUST be called first otherwise is.close() hangs (because data is still streaming?)
            if (is != null) {
                // this should already be closed by httpget.abort() above
                try {
                    is.close();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("client received : " + msg);
    }


    private static class ProxyConnectionManager {
        private final static PoolingClientConnectionManager threadSafeConnectionManager = new PoolingClientConnectionManager();
        private final static HttpClient httpClient = new DefaultHttpClient(threadSafeConnectionManager);

        static {
            /* common settings */
            HttpParams httpParams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
            HttpConnectionParams.setSoTimeout(httpParams, 10000);

            /* number of connections to allow */
            threadSafeConnectionManager.setDefaultMaxPerRoute(400);
            threadSafeConnectionManager.setMaxTotal(400);
        }
    }
}