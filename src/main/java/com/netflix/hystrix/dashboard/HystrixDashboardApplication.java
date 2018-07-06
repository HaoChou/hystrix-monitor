package com.netflix.hystrix.dashboard;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = { "com" })
public class HystrixDashboardApplication{

    public static void main(String[] args) {
         SpringApplication.run(HystrixDashboardApplication.class,args);
    }
}
