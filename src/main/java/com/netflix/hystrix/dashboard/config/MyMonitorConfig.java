package com.netflix.hystrix.dashboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author zhou
 * Created on 2018/7/17
 */
@Component
@ConfigurationProperties(prefix = "my-monitor")
public class MyMonitorConfig {
    private String eurekaAppsUrl;
    private Set<String> ignoreApps;


    public String getEurekaAppsUrl() {
        return eurekaAppsUrl;
    }

    public void setEurekaAppsUrl(String eurekaAppsUrl) {
        this.eurekaAppsUrl = eurekaAppsUrl;
    }

    public Set<String> getIgnoreApps() {
        return ignoreApps;
    }

    public void setIgnoreApps(Set<String> ignoreApps) {
        this.ignoreApps = ignoreApps;
    }


}
