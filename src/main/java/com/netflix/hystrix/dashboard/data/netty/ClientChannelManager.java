package com.netflix.hystrix.dashboard.data.netty;

import com.netflix.hystrix.dashboard.data.app.EurekaAppInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhou
 * Created on 2018/7/17
 */
public class ClientChannelManager {

    public static ClientChannelManager INSTANCE= new ClientChannelManager();

    private static Map<String/*url*/,DelegateChannel> DelegateChannelMap = new ConcurrentHashMap();

    private ClientChannelManager(){
        //私有构造方法 保证只有一个实例
    }
    public static ClientChannelManager getInstance(){
        return INSTANCE;
    }


    public void addDelegateChannel(DelegateChannel delegateChannel){
        DelegateChannelMap.putIfAbsent(delegateChannel.getHystrixUrl(),delegateChannel);
    }

    public void removeDelegateChannel(DelegateChannel delegateChannel){
        DelegateChannelMap.remove(delegateChannel.getHystrixUrl());
    }

}
