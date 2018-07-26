package com.netflix.hystrix.dashboard.config;

import com.netflix.hystrix.dashboard.zookeeper.IsMaster;
import com.netflix.hystrix.dashboard.zookeeper.RealMaster;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author zhou
 * Created on 2018/7/25
 */
@Component
@ConfigurationProperties(prefix = "zookeeper")
@Configuration
public class ZkConfig {


    @Bean
    public IsMaster getIsMaster(){
        return new RealMaster();
    }

}
