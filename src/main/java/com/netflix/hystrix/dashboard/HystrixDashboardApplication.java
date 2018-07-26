package com.netflix.hystrix.dashboard;

import com.netflix.hystrix.dashboard.data.app.observer.AppObservable;
import com.netflix.hystrix.dashboard.data.app.observer.HystrixStreamAppObserver;
import com.netflix.hystrix.dashboard.data.netty.LocalServer;
import com.netflix.hystrix.dashboard.threadpool.LocalThreadPoolManger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import static com.netflix.hystrix.dashboard.data.netty.NettyStarter.HYSTRIX_STREAM_LOCAL_ADDRESS;


@SpringBootApplication(scanBasePackages = { "com" })
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients
public class HystrixDashboardApplication{

    public static void main(String[] args) {

        LocalThreadPoolManger.getInstance().getLocalNettyThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                new LocalServer(HYSTRIX_STREAM_LOCAL_ADDRESS).start();
            }
        });
        AppObservable.getInstance().addObserver(new HystrixStreamAppObserver());
         SpringApplication.run(HystrixDashboardApplication.class,args);
    }
}
