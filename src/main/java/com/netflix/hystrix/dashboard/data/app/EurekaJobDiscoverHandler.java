package com.netflix.hystrix.dashboard.data.app;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.dashboard.config.MyMonitorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhou
 * Created on 2018/7/17
 */
@Component
public class EurekaJobDiscoverHandler implements JobDiscoverHandler {
    @Autowired
    MyMonitorConfig myMonitorConfig;

    private String eurekaAppsUrl;

    private static String HYSTRIX_STREAM_URI= "/actuator/hystrix.stream";
    private List<EurekaAppInfo> eurekaAppInfos = new ArrayList<>();

    @PostConstruct
    void init() {
        this.eurekaAppsUrl = myMonitorConfig.getEurekaAppsUrl();
        handler();
    }

    @Override
    public void handler() {
        URL url = null;
        try {
            url = new URL(eurekaAppsUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            if (200 == connection.getResponseCode()) {
                //得到输入流
                InputStream is = connection.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = is.read(buffer))) {
                    byteArrayOutputStream.write(buffer, 0, len);
                    byteArrayOutputStream.flush();
                }
                String result = byteArrayOutputStream.toString("utf-8");
                HashMap map = JSON.parseObject(result, HashMap.class);
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


                        boolean hasHystrixStream = checkStatus("http://" + ip + ":" + portInteger + HYSTRIX_STREAM_URI);
                        if(hasHystrixStream){
                            EurekaAppInfo eurekaAppInfo = new EurekaAppInfo(name, ip, portInteger, HYSTRIX_STREAM_URI);
                            eurekaAppInfos.add(eurekaAppInfo);
                        }
                    }
                }


            }

            System.out.println(JSON.toJSON(eurekaAppInfos));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkStatus(String urlIn) {

        try {
            URL url = new URL(urlIn);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            connection.setRequestMethod("GET");
            if (200 == connection.getResponseCode()) {
                return true;
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
