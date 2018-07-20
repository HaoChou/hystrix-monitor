package com.netflix.hystrix.dashboard.data.app.observer;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.dashboard.data.app.EurekaAppInfo;
import com.netflix.hystrix.dashboard.data.app.observer.AppObservable;
import com.netflix.hystrix.dashboard.data.netty.LocalClient;
import com.netflix.hystrix.dashboard.data.netty.NettyStarter;
import com.netflix.hystrix.dashboard.threadpool.LocalThreadPoolManger;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  hystrix流的观察者 负责开启流的读取任务和删除任务
 *
 * @author zhou
 * Created on 2018/7/17
 */
public class HystrixStreamAppObserver implements Observer {

    private Map<String/*app的streamUrl 唯一*/,LocalClient> app2NettyClient = new ConcurrentHashMap();

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleChannelInboundHandler.class);

    @Override
    public void update(Observable o, Object arg) {

        if(o instanceof AppObservable && arg instanceof NotifyMessage) {

            LOGGER.info("观察者收到消息："+ JSON.toJSONString(arg));

            AppObservable observable = (AppObservable) o;
            NotifyMessage notifyMessage = (NotifyMessage) arg;

            if(notifyMessage.getTypeEnum().equals(MessageTypeEnum.APP_ADDED)){
                if(notifyMessage.getArg() instanceof EurekaAppInfo){
                    final EurekaAppInfo eurekaAppInfo = (EurekaAppInfo) notifyMessage.getArg();
                    LOGGER.info("准备上线："+eurekaAppInfo.toString());
                    LocalClient localClient = new LocalClient(eurekaAppInfo.getAppName(), NettyStarter.HYSTRIX_STREAM_LOCAL_ADDRESS, eurekaAppInfo);
                    app2NettyClient.putIfAbsent(eurekaAppInfo.getHystrixStreamUrl(),localClient);
                    LocalThreadPoolManger.getInstance().getLocalNettyThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            localClient.start();
                        }
                    });

                }
            }
            else if( notifyMessage.getTypeEnum().equals(MessageTypeEnum.APP_REMOVED) ){
                //app数据流接口下线了 停掉任务，并且删除
                if(notifyMessage.getArg() instanceof EurekaAppInfo) {
                    final EurekaAppInfo eurekaAppInfo = (EurekaAppInfo) notifyMessage.getArg();
                    LOGGER.info("准备下线："+eurekaAppInfo.toString());
                    LocalClient localClient = app2NettyClient.get(eurekaAppInfo.getHystrixStreamUrl());
                    if(null!=localClient){
                        localClient.shutdown();
                        app2NettyClient.remove(eurekaAppInfo.getHystrixStreamUrl());
                    }
                }
            }

        }
    }
}
