package com.netflix.hystrix.dashboard.data.netty;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.dashboard.data.app.EurekaAppInfo;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author zhou
 * Created on 2018/7/17
 */
@Component
public class NettyStarter {

    public static String LOCAL_ADDRESS="hystrix-stream";
    private LocalServer localServer;

    @PostConstruct
    private void init(){
        localServer =new LocalServer(LOCAL_ADDRESS);
    }

    public static void main(String[] args) throws InterruptedException {
        String url = "http://172.16.36.93:7005/actuator/hystrix.stream";
        new LocalServer(LOCAL_ADDRESS).start();
        EurekaAppInfo eurekaAppInfo = new EurekaAppInfo("ASTROLOGY-TASK","172.16.36.93",7005,"/actuator/hystrix.stream");
        new LocalClient(LOCAL_ADDRESS,eurekaAppInfo).start();
    }
}
