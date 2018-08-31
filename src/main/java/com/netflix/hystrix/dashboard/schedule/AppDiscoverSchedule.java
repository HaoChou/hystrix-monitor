package com.netflix.hystrix.dashboard.schedule;

import com.netflix.hystrix.dashboard.data.app.JobDiscoverHandler;

import com.netflix.hystrix.dashboard.zookeeper.IsMaster;
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
@ConditionalOnProperty(name = "env.online",havingValue = "true")
@Component
public class AppDiscoverSchedule {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppDiscoverSchedule.class);
    @Autowired
    JobDiscoverHandler jobDiscoverHandler;
    @Autowired
    private IsMaster isMaster;

    @Scheduled(fixedRate = 1000*30, initialDelay = 1000*30 )
    private void discover(){
        if(isMaster.isMaster()) {
            LOGGER.debug("AppDiscoverSchedule start!");
            jobDiscoverHandler.handler();
        }
    }
    @PostConstruct
    private void init(){
        System.out.println("!!!");
    }
}
