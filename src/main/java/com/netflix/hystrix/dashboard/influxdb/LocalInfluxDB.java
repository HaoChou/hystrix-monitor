package com.netflix.hystrix.dashboard.influxdb;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.dashboard.config.InfluxdbConfig;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author zhou
 * Created on 2018/7/23
 */
@Component
@ConditionalOnProperty(name = "env.online",havingValue = "true")
public class LocalInfluxDB {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalInfluxDB.class);

    private static LocalInfluxDB INSTANCE;
    @Autowired
    InfluxdbConfig config;

    private static InfluxDB influxDB;
    private static String dbName = "hystrixDefault";
    private static String rpName = "aRetentionPolicy";



    @PostConstruct
    private void init(){
        influxDB = InfluxDBFactory.connect(config.getUrl(), config.getUsername(), config.getPassword());
        //// Flush every 2000 Points, at least every 100ms
//        influxDB.enableBatch(2000, 100, TimeUnit.MILLISECONDS);

        dbName=config.getDbname();
//        influxDB.createDatabase(dbName);
        //要设置数据库 否则会报错：Expecting a non-empty string for database
        influxDB.setDatabase(dbName);
        influxDB.createRetentionPolicy(rpName, dbName, "7d", "1h", 1, true);
        influxDB.setRetentionPolicy(rpName);
        influxDB.enableBatch(BatchOptions.DEFAULTS);
        LOGGER.info("influxDB 初始化完成："+"dbName:"+dbName+"url:"+config.getUrl());

    }


    public void writeCommandData(String commandContent,String appInfo){

        try {

            HashMap map = JSON.parseObject(commandContent, HashMap.class);


            String name = (String) map.get("name");
            String threadPool = (String) map.get("threadPool");
            Long currentTime = (Long) map.get("currentTime");
            Long rollingCountSuccess = (Long) map.get("rollingCountSuccess");
            //线程安全 走的是BlockQueue
            influxDB.write(Point.measurement("command")
                    //时间
                    .time(currentTime, TimeUnit.MILLISECONDS)
                    //app信息包括机器信息+端口+ip
                    .tag("appInfo", appInfo)
                    //线程池名称
                    .tag("threadPool", threadPool)
                    //command名称
                    .tag("name", name)
                    //请求成功数
                    .addField("rollingCountSuccess", rollingCountSuccess)
                    .build());
        }
        catch (Exception e){

        }

    }

    @PreDestroy
    private void distory(){
        if(null!=influxDB){
            influxDB.close();
        }
    }

    @Autowired
    private void setInstance(LocalInfluxDB localInfluxDB){
        INSTANCE = localInfluxDB;
    }


    public static InfluxDB getInfluxDB()
    {
        if(influxDB==null){
            throw new NullPointerException("influxDB 还没有被初始化");
        }
        return influxDB;
    }
}
