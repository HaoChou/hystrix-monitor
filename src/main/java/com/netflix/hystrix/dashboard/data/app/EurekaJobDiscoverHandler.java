package com.netflix.hystrix.dashboard.data.app;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.dashboard.config.MyMonitorConfig;
import com.netflix.hystrix.dashboard.data.app.observer.AppObservable;
import com.netflix.hystrix.dashboard.threadpool.LocalThreadPoolManger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author zhou
 * Created on 2018/7/17
 */
@Component
public class EurekaJobDiscoverHandler implements JobDiscoverHandler {

    private static final Logger logger = LoggerFactory.getLogger(EurekaJobDiscoverHandler.class);

    @Autowired
    MyMonitorConfig myMonitorConfig;

    private String eurekaAppsUrl;

    private static String HYSTRIX_STREAM_URI = "/actuator/hystrix.stream";
    private Vector<EurekaAppInfo> eurekaAppInfos = new Vector<>();

    @PostConstruct
    void init() {
        this.eurekaAppsUrl = myMonitorConfig.getEurekaAppsUrl();
        handler();
    }

    @Override
    public void handler() {
        URL url = null;
        HttpURLConnection connection = null;
        InputStream is = null;
        String eureakaAppInfoResult = null;
        try {
            url = new URL(eurekaAppsUrl);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            if (200 == connection.getResponseCode()) {
                //得到输入流
                is = connection.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = is.read(buffer))) {
                    byteArrayOutputStream.write(buffer, 0, len);
                    byteArrayOutputStream.flush();
                }
                eureakaAppInfoResult = byteArrayOutputStream.toString("utf-8");
                HashMap map = JSON.parseObject(eureakaAppInfoResult, HashMap.class);
                Map applications = (Map) map.get("applications");
                List<Map> application = (List<Map>) applications.get("application");

                for (Map tempMap : application) {
                    String name = (String) tempMap.get("name");
                    List<Map> instances = (List<Map>) tempMap.get("instance");
                    for (Map tempInstance : instances) {
                        String ip = (String) tempInstance.get("ipAddr");
                        Map port = (Map) tempInstance.get("port");
                        Integer portInteger = (Integer) port.get("$");
                        System.out.println("name:" + name + ",ip:" + ip + ":" + portInteger);
                        EurekaAppInfo eurekaAppInfo = new EurekaAppInfo(name, ip, portInteger, HYSTRIX_STREAM_URI);
                        LocalThreadPoolManger.getInstance().getAppDiscoverThreadPool().execute(new CheckStatusRunnable(eurekaAppInfos,eurekaAppInfo));
                    }
                }
            }
            System.out.println(JSON.toJSON(eurekaAppInfos));

        } catch (Exception e) {
            logger.error("解析错误,eureakaAppInfoResult:" + eureakaAppInfoResult, e);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ignore) {

                }
            }

        }

    }

    private class CheckStatusRunnable implements Runnable {

        private final Vector eurekaAppInfos;
        private final EurekaAppInfo appInfo;

        public CheckStatusRunnable(Vector eurekaAppInfos, EurekaAppInfo eurekaAppInfo) {
            this.eurekaAppInfos = eurekaAppInfos;
            this.appInfo = eurekaAppInfo;
        }

        @Override
        public void run() {
            boolean status = checkStatus(appInfo);
            if (status) {
                AppObservable.getInstance().addAppAndNotify(appInfo);
            }
            else {
                AppObservable.getInstance().removeAppAndNotify(appInfo);
            }
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
            logger.info("检查状态失败"+eurekaAppInfo.getAppName()+eurekaAppInfo.getHystrixStreamUrl()+e.getMessage());
            return false;
        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
