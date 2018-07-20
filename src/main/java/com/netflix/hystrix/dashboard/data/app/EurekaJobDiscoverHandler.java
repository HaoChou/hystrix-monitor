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

                List<EurekaAppInfo> eurekaAppInfoList = new ArrayList<>();
                for (Map tempMap : application) {
                    String name = (String) tempMap.get("name");
                    List<Map> instances = (List<Map>) tempMap.get("instance");
                    for (Map tempInstance : instances) {
                        String ip = (String) tempInstance.get("ipAddr");
                        Map port = (Map) tempInstance.get("port");
                        Integer portInteger = (Integer) port.get("$");
                        if(myMonitorConfig.getIgnoreApps()!=null&&myMonitorConfig.getIgnoreApps().contains(name)){
                            logger.info("根据配置[my-monitor.ignore-apps]忽略app："+name);
                            continue;
                        }
                        try {
                            eurekaAppInfoList.add(new EurekaAppInfo(name, ip, portInteger, HYSTRIX_STREAM_URI));
                        }catch (Exception e){

                        }
                    }
                }

                LocalEurekaAppRegister.getInstance().tryRegisterApp(eurekaAppInfoList);

            }
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
}
