package com.netflix.hystrix.dashboard.data.netty.thread;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.dashboard.data.netty.protobuf.Message;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 把数据写入数据库的runnable
 * @author zhou
 * Created on 2018/7/23
 */
public class StreamDataWriteIntoDbRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(StreamDataWriteIntoDbRunnable.class);

    private final InfluxDB influxDB;
    private final  Message.NormalMessage message;

    public StreamDataWriteIntoDbRunnable(InfluxDB influxDB,  Message.NormalMessage message) {
        this.influxDB = influxDB;
        this.message = message;
    }

    @Override
    public void run() {
        String content = message.getContent();
        if(!content.startsWith("data"))
        {
            return;
        }
        String jsonStr = content.substring(5);
        try {

            Message.AppInfo appInfo = message.getAppInfo();
            HashMap map;
            try {
                map = JSON.parseObject(jsonStr, HashMap.class);
            }catch (Exception e)
            {
                logger.error("JSON解析失败！；JSON:"+jsonStr,e);
                return;
            }

            String type = (String) map.get("type");
            if(type.equals("HystrixCommand")) {
                String name = (String) map.get("name");
                String group = (String) map.get("group");
                String threadPool = (String) map.get("threadPool");
                Long currentTime = (Long) map.get("currentTime");
                boolean isCircuitBreakerOpen = (boolean) map.get("isCircuitBreakerOpen");
                Number errorPercentage = (Number) map.get("errorPercentage");
                Number errorCount = (Number) map.get("errorCount");
                Number requestCount = (Number) map.get("requestCount");
                Number rollingCountSuccess = (Number)map.get("rollingCountSuccess");
                Number rollingCountThreadPoolRejected = (Number)map.get("rollingCountThreadPoolRejected");
                Number rollingCountResponsesFromCache = (Number)map.get("rollingCountResponsesFromCache");
                Number rollingCountFallbackSuccess = (Number)map.get("rollingCountFallbackSuccess");
                Number rollingCountTimeout = (Number)map.get("rollingCountTimeout");
                Number rollingCountShortCircuited = (Number)map.get("rollingCountShortCircuited");
                Number rollingCountFailure = (Number)map.get("rollingCountFailure");
                Number rollingCountExceptionsThrown = (Number)map.get("rollingCountExceptionsThrown");
                Number rollingCountFallbackFailure = (Number)map.get("rollingCountFallbackFailure");
                Number rollingCountBadRequests = (Number)map.get("rollingCountBadRequests");
                Number rollingCountFallbackRejection = (Number)map.get("rollingCountFallbackRejection");
                Number currentConcurrentExecutionCount = (Number)map.get("currentConcurrentExecutionCount");
                Number rollingMaxConcurrentExecutionCount = (Number)map.get("rollingMaxConcurrentExecutionCount");
                Number latencyExecute_mean = (Number)map.get("latencyExecute_mean");
                Number latencyTotal_mean = (Number)map.get("latencyTotal_mean");

                    //线程安全 走的是BlockQueue
                    influxDB.write(Point.measurement("hystrix_command")
                            //时间
                            .time(currentTime, TimeUnit.MILLISECONDS)
                            //app信息包括机器信息+端口+ip
                            .tag("appName", appInfo.getAppName())
                            .tag("ipPort", appInfo.getIpAddr()+":"+appInfo.getPort())
                            //线程池名称
                            .tag("threadPool", threadPool)
                            .tag("group", group)
                            //command名称
                            .tag("name", name)
                            //请求成功数
                            .addField("isCircuitBreakerOpen", isCircuitBreakerOpen?1:0)
                            .addField("errorPercentage", errorPercentage)
                            .addField("errorCount", errorCount)
                            .addField("requestCount", requestCount)
                            .addField("rollingCountSuccess", rollingCountSuccess)
                            .addField("rollingCountThreadPoolRejected", rollingCountThreadPoolRejected)
                            .addField("rollingCountResponsesFromCache", rollingCountResponsesFromCache)
                            .addField("rollingCountFallbackSuccess", rollingCountFallbackSuccess)
                            .addField("rollingCountTimeout", rollingCountTimeout)
                            .addField("rollingCountShortCircuited", rollingCountShortCircuited)
                            .addField("rollingCountFailure", rollingCountFailure)
                            .addField("rollingCountExceptionsThrown", rollingCountExceptionsThrown)
                            .addField("rollingCountFallbackFailure", rollingCountFallbackFailure)
                            .addField("rollingCountBadRequests", rollingCountBadRequests)
                            .addField("rollingCountFallbackRejection", rollingCountFallbackRejection)
                            .addField("currentConcurrentExecutionCount", currentConcurrentExecutionCount)
                            .addField("rollingMaxConcurrentExecutionCount", rollingMaxConcurrentExecutionCount)
                            .addField("latencyExecute_mean", latencyExecute_mean)
                            .addField("latencyTotal_mean", latencyTotal_mean)
                            .build());
//                    System.out.println("写入成功");
                logger.info("command 写入成功");

            }else if(type.equals("HystrixThreadPool"))
            {


                String name = (String) map.get("name");
                Long currentTime = (Long) map.get("currentTime");
                Number currentActiveCount = (Number) map.get("currentActiveCount");
                Number currentCompletedTaskCount = (Number) map.get("currentCompletedTaskCount");
                Number currentCorePoolSize = (Number) map.get("currentCorePoolSize");
                Number currentLargestPoolSize = (Number) map.get("currentLargestPoolSize");
                Number currentMaximumPoolSize = (Number) map.get("currentMaximumPoolSize");
                Number currentTaskCount = (Number) map.get("currentTaskCount");
                Number currentPoolSize = (Number) map.get("currentPoolSize");
                Number currentQueueSize = (Number) map.get("currentQueueSize");
                Number rollingCountThreadsExecuted = (Number) map.get("rollingCountThreadsExecuted");
                Number rollingMaxActiveThreads = (Number) map.get("rollingMaxActiveThreads");
                Number rollingCountCommandRejections = (Number) map.get("rollingCountCommandRejections");


                    influxDB.write(Point.measurement("hystrix_threadPool")
                            //时间
                            .time(currentTime, TimeUnit.MILLISECONDS)
                            //app信息包括机器信息+端口+ip
                            .tag("appName", appInfo.getAppName())
                            .tag("ipPort", appInfo.getIpAddr() + ":" + appInfo.getPort())
                            //command名称
                            .tag("name", name)
                            //请求成功数
                            .addField("currentActiveCount", currentActiveCount)
                            .addField("currentCompletedTaskCount", currentCompletedTaskCount)
                            .addField("currentCorePoolSize", currentCorePoolSize)
                            .addField("currentLargestPoolSize", currentLargestPoolSize)
                            .addField("currentMaximumPoolSize", currentMaximumPoolSize)
                            .addField("currentTaskCount", currentTaskCount)
                            .addField("currentPoolSize", currentPoolSize)
                            .addField("currentQueueSize", currentQueueSize)
                            .addField("rollingCountThreadsExecuted", rollingCountThreadsExecuted)
                            .addField("rollingMaxActiveThreads", rollingMaxActiveThreads)
                            .addField("rollingCountCommandRejections", rollingCountCommandRejections)
                            .build());
                logger.info("threadpool 写入成功");
            }
        }
        catch (Exception e) {
            logger.error("error",e);
        }
    }
}
