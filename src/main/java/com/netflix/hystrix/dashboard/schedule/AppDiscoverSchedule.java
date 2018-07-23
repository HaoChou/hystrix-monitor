package com.netflix.hystrix.dashboard.schedule;

import com.netflix.hystrix.dashboard.data.app.JobDiscoverHandler;
import com.netflix.hystrix.dashboard.data.netty.LocalClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author zhou
 * Created on 2018/7/19
 */
@Component
public class AppDiscoverSchedule {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppDiscoverSchedule.class);
    @Autowired
    JobDiscoverHandler jobDiscoverHandler;

    @Scheduled(fixedRate = 1000*30, initialDelay = 1000*60 )
    private void discover(){
        LOGGER.info("AppDiscoverSchedule start!");
        jobDiscoverHandler.handler();
    }
}
