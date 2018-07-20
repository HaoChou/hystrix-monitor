package com.netflix.hystrix.dashboard.data.app;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhou
 * Created on 2018/7/17
 */
public class EurekaAppInfo {
    private final String appName;
    private final String ipAddr;
    private final Integer port;
    private final String uri;

    public EurekaAppInfo(String appName, String ipAddr, Integer port, String uri) {
        if(null==appName||null==ipAddr||null==port||null==uri){
            throw new NullPointerException("EurekaAppInfo构造函数的参数不能为null");
        }
        this.appName = appName;
        this.ipAddr = ipAddr;
        this.port = port;
        this.uri = uri;
    }

    public String getAppName() {
        return appName;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public Integer getPort() {
        return port;
    }

    public String getUri() {
        return uri;
    }

    public String getHystrixStreamUrl(){
        return "http://"+ipAddr+":"+port+ uri;
    }

    @Override
    public int hashCode(){
        return getHystrixStreamUrl().hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof EurekaAppInfo)) {
            return false;
        }
        EurekaAppInfo eurekaAppInfo = (EurekaAppInfo)obj;
        return (this.getHystrixStreamUrl().equals(eurekaAppInfo.getHystrixStreamUrl()));
    }

    @Override
    public String toString(){
        return appName+":"+getHystrixStreamUrl();
    }

//    public static void main(String[] args) {
//        Vector set = new Vector();
//        Set set = new Vector();
//
//        EurekaAppInfo eurekaAppInfo = new EurekaAppInfo("ASTROLOGY-TASK","172.16.36.93",7005,"/actuator/hystrix.stream");
//        EurekaAppInfo eurekaAppInfo2 = new EurekaAppInfo("ASTROLOGY-TASK","172.16.36.93",7005,"/actuator/hystrix.stream");
//        System.out.println(eurekaAppInfo.equals(eurekaAppInfo2));
//
//
//        set.add(eurekaAppInfo);
//
//        System.out.println(set.contains(eurekaAppInfo2));
//        set.add(eurekaAppInfo2);
//
//
//        System.out.println(set.size());
//    }
}
