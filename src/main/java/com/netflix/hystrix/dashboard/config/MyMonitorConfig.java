package com.netflix.hystrix.dashboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhou
 * Created on 2018/7/17
 */
@Component
@ConfigurationProperties(prefix = "my-monitor")
public class MyMonitorConfig {
    private String eurekaAppsUrl;

    public String getEurekaAppsUrl() {
        return eurekaAppsUrl;
    }

    public void setEurekaAppsUrl(String eurekaAppsUrl) {
        this.eurekaAppsUrl = eurekaAppsUrl;
    }

}
