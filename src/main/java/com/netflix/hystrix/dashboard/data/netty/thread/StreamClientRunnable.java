package com.netflix.hystrix.dashboard.data.netty.thread;

import com.netflix.hystrix.dashboard.http.ProxyConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author zhou
 * Created on 2018/7/18
 */
public class StreamClientRunnable  implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(StreamClientRunnable.class);

    private final String hystrixStreamUrl;
    private final Channel channel;

    public StreamClientRunnable(String hystrixStreamUrl, Channel channel) {
        this.hystrixStreamUrl = hystrixStreamUrl;
        this.channel = channel;
    }

    @Override
    public void run() {
        InputStream is = null;
        HttpURLConnection connection = null;
        URL url = null;
        //直到channel被关闭
        while (channel.isActive()) {
            try {
                url = new URL(hystrixStreamUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                if (200 == connection.getResponseCode()) {
                    //得到输入流
                    is = connection.getInputStream();
                    int b = -1;
                    StringBuilder sb = new StringBuilder();
                    while ((b = is.read()) != -1&&channel.isActive()) {
                        try {
                            sb.append((char) b);
                            if (b == 10 /** flush buffer on line feed */) {
                                channel.writeAndFlush(sb.toString());
                                sb = new StringBuilder();
                            }
                        } catch (Exception e) {
                            if (e.getClass().getSimpleName().equalsIgnoreCase("ClientAbortException")) {
                                //可忽略的异常
                                logger.info("ClientAbortException",e);
                                break;
                            } else {
                                // received unknown error while writing so throw an exception
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error proxying request: " + hystrixStreamUrl, e);
            } finally {
                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception e) {
                        logger.error("failed aborting proxy connection.", e);
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
            }
        }

        logger.info("StreamClientRunnable exit url:" +hystrixStreamUrl);
    }
}
