package com.netflix.hystrix.dashboard.schedule;

import com.netflix.hystrix.dashboard.data.app.JobDiscoverHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author zhou
 * Created on 2018/7/19
 */
@ConditionalOnProperty(name = "my-monitor.enable",havingValue = "true")
@Component
public class AppDiscoverSchedule {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppDiscoverSchedule.class);
    @Autowired
    JobDiscoverHandler jobDiscoverHandler;

    @Scheduled(fixedRate = 1000*30, initialDelay = 1000*60 )
    private void discover(){
        LOGGER.debug("AppDiscoverSchedule start!");
        jobDiscoverHandler.handler();
    }
    @PostConstruct
    private void init(){
        System.out.println("!!!");
    }
}
