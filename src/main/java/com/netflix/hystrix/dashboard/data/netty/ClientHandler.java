package com.netflix.hystrix.dashboard.data.netty;

import com.netflix.hystrix.dashboard.data.netty.thread.StreamClientRunnable;
import com.netflix.hystrix.dashboard.stream.ProxyStreamServlet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
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

    private final LocalClient localClient;
    private static DefaultEventExecutorGroup eventExecutors =new DefaultEventExecutorGroup(2);

    public ClientHandler(LocalClient localClient) {
        this.localClient = localClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("client [" + localClient.getUrl() + "] connected");

        ctx.writeAndFlush("client [" + localClient.getUrl() + "] connected");

//        new Thread(new StreamClientRunnable(localClient.getUrl(),ctx.channel())).start();
        eventExecutors.submit(new StreamClientRunnable(localClient.getUrl(),ctx.channel()));
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