package com.netflix.hystrix.dashboard.data.netty.thread;

import com.netflix.hystrix.dashboard.data.app.EurekaAppInfo;
import com.netflix.hystrix.dashboard.data.netty.protobuf.Message;
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

    /**
     * 如果第一次连接streamUrl失败，后面重试的间隔
     */
    private static final int RETRY_SECONDS=3;
    private final EurekaAppInfo eurekaAppInfo;
    private final Channel channel;

    public StreamClientRunnable(EurekaAppInfo eurekaAppInfo, Channel channel) {
        this.eurekaAppInfo = eurekaAppInfo;
        this.channel = channel;
    }

    @Override
    public void run() {
        String hystrixStreamUrl = eurekaAppInfo.getHystrixStreamUrl();
        InputStream is = null;
        HttpURLConnection connection = null;
        URL url = null;
        String retryReason;
        //直到channel被关闭
        while (channel.isActive()) {
            try {
                url = new URL(hystrixStreamUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                logger.info("开始尝试连接："+eurekaAppInfo.toString());
                int responseCode = connection.getResponseCode();
                if (200 == responseCode) {
                    logger.info("地址："+eurekaAppInfo.toString()+"连接成功!" );

                    //得到输入流
                    is = connection.getInputStream();
                    int b = -1;
                    StringBuilder sb = new StringBuilder();
                    while ((b = is.read()) != -1&&channel.isActive()) {
                        try {
                            sb.append((char) b);
                            if (b == 10 /** flush buffer on line feed */) {
                                Message.NormalMessage normalMessage = Message.NormalMessage.newBuilder()
                                        .setContent(sb.toString())
                                        .setAppInfo(getMessageAppInfp())
                                        .build();
                                channel.writeAndFlush(normalMessage);
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
                    retryReason="[InputStream流读取到最后]";
                }
                else {
                    retryReason="[responseCode是"+responseCode+"]";
                    logger.warn(hystrixStreamUrl+"的responseCode不是200，是："+responseCode);
                }
            } catch (Exception e) {
                retryReason="[Exception："+e.getMessage()+"]";
                //应用重启时候 会有"java.net.ConnectException: Connection refused (Connection refused)"
                logger.info("Error proxying request: " + hystrixStreamUrl+retryReason);
            } finally {
                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception e) {
                        logger.error("关闭http连接失败！.", e);
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
            logger.warn("地址："+eurekaAppInfo.toString()+"连接失败!准备"+RETRY_SECONDS+"秒后重试,重试原因："+retryReason );

            try {
                //三秒后再重试 减少重试次数
                Thread.sleep(RETRY_SECONDS*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        logger.info("StreamClientRunnable线程退出,url:" +eurekaAppInfo.toString());
    }


    private Message.AppInfo getMessageAppInfp(){
      return
              Message.AppInfo.newBuilder()
              .setAppName(eurekaAppInfo.getAppName())
              .setIpAddr(eurekaAppInfo.getIpAddr())
              .setPort(eurekaAppInfo.getPort())
              .setUri(eurekaAppInfo.getUri())
              .build();
    }
}
