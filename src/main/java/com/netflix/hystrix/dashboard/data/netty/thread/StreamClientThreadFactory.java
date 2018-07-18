package com.netflix.hystrix.dashboard.data.netty.thread;

import java.util.concurrent.ThreadFactory;

/**
 * @author zhou
 * Created on 2018/7/18
 */
public class StreamClientThreadFactory implements ThreadFactory {


    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r);
    }
}
