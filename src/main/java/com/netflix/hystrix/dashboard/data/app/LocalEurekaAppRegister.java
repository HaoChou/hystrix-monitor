package com.netflix.hystrix.dashboard.data.app;

import com.netflix.hystrix.dashboard.data.app.observer.AppObservable;
import com.netflix.hystrix.dashboard.threadpool.LocalThreadPoolManger;
import io.netty.util.internal.ConcurrentSet;
import jdk.management.resource.internal.inst.FileOutputStreamRMHooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author zhou
 * Created on 2018/7/20
 */
public class LocalEurekaAppRegister {


    private static final Logger logger = LoggerFactory.getLogger(LocalEurekaAppRegister.class);

    private static LocalEurekaAppRegister INSTANCE = new LocalEurekaAppRegister();
    private LocalEurekaAppRegister(){
    }


//    private static Set<EurekaAppInfo> ALL_APPS = new HashSet();
    private static Set<EurekaAppInfo> ONLINE_APPS = new ConcurrentSet<>();
//    private static Set<EurekaAppInfo> OFFLINE_APPS = new HashSet();

    public static LocalEurekaAppRegister getInstance(){
        return INSTANCE;
    }

    public void tryRegisterApp(List<EurekaAppInfo> eurekaAppInfos){
        if(null==eurekaAppInfos||eurekaAppInfos.size()==0){
            return;
        }
        //把ONLINE_APPS内的都校验一遍
        tryUnRegister();
        for (EurekaAppInfo appInfo: eurekaAppInfos){
            //把ONLINE_APPS外的都校验一遍
            registerApp(appInfo);
        }
    }

    private void registerApp(EurekaAppInfo eurekaAppInfo){
        if(ONLINE_APPS.contains(eurekaAppInfo))
        {
            return;
        }
        logger.debug("CheckStatusRunnable任务 检查是否可以注册:"+eurekaAppInfo.toString()+"已经提交");
        LocalThreadPoolManger.getInstance().getAppDiscoverThreadPool().execute(new CheckStatusRunnable(eurekaAppInfo));
    }

    private void tryUnRegister(){
        Iterator<EurekaAppInfo> iterator = ONLINE_APPS.iterator();
        while (iterator.hasNext()){
            final EurekaAppInfo appInfo= iterator.next();
            logger.debug("CheckStatusRunnable任务 检查在线状态:"+appInfo.toString()+"已经提交");
            LocalThreadPoolManger.getInstance().getAppDiscoverThreadPool().execute(new CheckStatusRunnable(appInfo));
        }
    }

    private class CheckStatusRunnable implements Runnable {

        private final EurekaAppInfo appInfo;

        public CheckStatusRunnable(EurekaAppInfo eurekaAppInfo) {
            this.appInfo = eurekaAppInfo;
        }

        @Override
        public void run() {
            long star = System.currentTimeMillis();
            if(null==appInfo){
                return;
            }
            boolean status = checkStatus(appInfo);
            if (status) {
                if(!ONLINE_APPS.contains(appInfo)) {
                    ONLINE_APPS.add(appInfo);
                    AppObservable.getInstance().addAppAndNotify(appInfo);
                }
            }
            else {
                if(ONLINE_APPS.contains(appInfo)) {
                    ONLINE_APPS.remove(appInfo);
                    AppObservable.getInstance().removeAppAndNotify(appInfo);
                }
            }
            logger.debug("CheckStatusRunnable任务 "+appInfo.toString()+"已结束 耗时："  +( System.currentTimeMillis()-star));
        }
    }


    private boolean checkStatus(EurekaAppInfo eurekaAppInfo) {

        HttpURLConnection connection = null;
        try {
            URL url = new URL(eurekaAppInfo.getHystrixStreamUrl());

            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            connection.setRequestMethod("GET");
            if (200 == connection.getResponseCode()) {
                return true;
            }

            return false;

        } catch (Exception e) {
            logger.info("检查状态失败"+eurekaAppInfo.toString()+" 原因:"+e.getMessage());
            return false;
        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 关闭所有流client
     */
    public static void closeAllStreamClient(){

        for(EurekaAppInfo appInfo: ONLINE_APPS){
            AppObservable.getInstance().removeAppAndNotify(appInfo);
        }
    }

}
