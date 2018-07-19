package com.netflix.hystrix.dashboard.data.netty;

import com.netflix.hystrix.dashboard.data.app.EurekaAppInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

/**
 * @author zhou
 * Created on 2018/7/17
 */
@Component
public class NettyStarter {

    private static final Logger logger = LoggerFactory.getLogger(NettyStarter.class);

    public static String LOCAL_ADDRESS="hystrix-stream";
    private LocalServer localServer;
//
//    static {
//
//                java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(Level.INFO);
//        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);
//        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
//        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
//        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "ERROR");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "ERROR");
////        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
//    }

    @PostConstruct
    private void init(){
        localServer =new LocalServer(LOCAL_ADDRESS);
    }

    public static void main(String[] args) throws InterruptedException {

        String url = "http://172.16.36.93:7005/actuator/hystrix.stream";
        new LocalServer(LOCAL_ADDRESS).start();
        EurekaAppInfo eurekaAppInfo = new EurekaAppInfo("ASTROLOGY-TASK","172.16.36.93",7005,"/actuator/hystrix.stream");
        EurekaAppInfo eurekaAppInfo2 = new EurekaAppInfo("elemets-TASK","172.16.36.115",7000,"/actuator/hystrix.stream");
        new LocalClient("测试1",LOCAL_ADDRESS,eurekaAppInfo).start();
        new LocalClient("测试2",LOCAL_ADDRESS,eurekaAppInfo2).start();
    }
}
