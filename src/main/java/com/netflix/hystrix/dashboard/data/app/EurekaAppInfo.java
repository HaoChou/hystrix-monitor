package com.netflix.hystrix.dashboard.data.app;
/**
 * @author zhou
 * Created on 2018/7/17
 */
public class EurekaAppInfo {
    private String appName;
    private String ipAddr;
    private Integer port;
    private String uri;

    public EurekaAppInfo(String appName, String ipAddr, Integer port, String uri) {
        this.appName = appName;
        this.ipAddr = ipAddr;
        this.port = port;
        this.uri = uri;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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
