package com.netflix.hystrix.dashboard.data.netty.thread;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.dashboard.data.app.EurekaAppInfo;
import com.netflix.hystrix.dashboard.data.netty.protobuf.Message;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 把数据写入数据库的runnable
 * @author zhou
 * Created on 2018/7/23
 */
public class StreamDataWriteIntoDbRunnable implements Runnable {
    private final InfluxDB influxDB;
    private final  Message.NormalMessage message;

    public StreamDataWriteIntoDbRunnable(InfluxDB influxDB,  Message.NormalMessage message) {
        this.influxDB = influxDB;
        this.message = message;
    }

    @Override
    public void run() {
        String content = message.getContent();
        if(content.length()<6)
        {
            return;
        }
        String jsonStr = content.substring(5);
        try {

            HashMap map = JSON.parseObject(jsonStr, HashMap.class);

            String type = (String) map.get("type");
            if(type.equals("HystrixCommand")) {
                String name = (String) map.get("name");
                String threadPool = (String) map.get("threadPool");
                Long currentTime = (Long) map.get("currentTime");
                Integer rollingCountSuccess = (Integer)map.get("rollingCountSuccess");
                if(message.getAppInfo().contains("http://172.16.36.93:7005/actuator/hystrix.stream")) {
                    //线程安全 走的是BlockQueue
                    influxDB.write(Point.measurement("command")
                            //时间
                            .time(currentTime, TimeUnit.MILLISECONDS)
                            //app信息包括机器信息+端口+ip
                            .tag("appInfo", message.getAppInfo())
                            //线程池名称
                            .tag("threadPool", threadPool)
                            //command名称
                            .tag("name", name)
                            //请求成功数
                            .addField("rollingCountSuccess", rollingCountSuccess)
                            .build());
                    System.out.println("写入成功");
                }
            }else if(type.equals("HystrixThreadPool"))
            {

            }
        }
        catch (Exception e)
        {

        }
//
//        if(content.startsWith("data: {\"type\":\"HystrixCommand\""))
//        {
//
//        }
//        else if(content.startsWith("data: {\"type\":\"HystrixThreadPool\""))
//        {
//            System.out.println("收到HystrixThreadPool消息："+message.getContent());
//
//        }
    }
}
