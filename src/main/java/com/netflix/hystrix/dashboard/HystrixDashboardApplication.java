package com.netflix.hystrix.dashboard;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages = { "com" })
@EnableScheduling


public class HystrixDashboardApplication{

    public static void main(String[] args) {
         SpringApplication.run(HystrixDashboardApplication.class,args);
    }
}
