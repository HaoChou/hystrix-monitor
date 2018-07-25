package com.netflix.hystrix.dashboard.threadpool;


import java.util.concurrent.*;

/**
 * @author zhou
 * Created on 2018/7/19
 */
public class LocalThreadPoolManger {

    private static LocalThreadPoolManger INSTANCE = new LocalThreadPoolManger();

    public static LocalThreadPoolManger getInstance(){
        return INSTANCE;
    }

    //app发现的线程池
    private final ExecutorService appDiscoverThreadPool;

    //netty现场吃
    private final ExecutorService localNettyThreadPool;

    /**
     * 业务线程出
     */
    private final ExecutorService bizThreadPool;

    private LocalThreadPoolManger()
    {
        appDiscoverThreadPool = new ThreadPoolExecutor(0,10,60, TimeUnit.SECONDS,new LinkedBlockingQueue<>());
        localNettyThreadPool = new ThreadPoolExecutor(10,200,60, TimeUnit.SECONDS,new LinkedBlockingQueue<>());
        bizThreadPool = new ThreadPoolExecutor(10,200,60, TimeUnit.SECONDS,new LinkedBlockingQueue<>());
    }

    public ExecutorService getAppDiscoverThreadPool() {
        return appDiscoverThreadPool;
    }

    public ExecutorService getLocalNettyThreadPool() {
        return localNettyThreadPool;
    }

    public ExecutorService getBizThreadPool() {
        return bizThreadPool;
    }
}
