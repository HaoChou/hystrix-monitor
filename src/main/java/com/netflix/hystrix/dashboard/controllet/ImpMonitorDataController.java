package com.netflix.hystrix.dashboard.controllet;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.dashboard.influxdb.LocalInfluxDB;
import org.apache.commons.lang.StringUtils;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author zhou
 * Created on 2018/7/26
 */
@RestController
public class ImpMonitorDataController {

    private static final Logger LOG = LoggerFactory.getLogger(ImpMonitorDataController.class);

    @Autowired
    LocalInfluxDB localInfluxDB;

    @RequestMapping("/input/point.html")
    public String inputPoint( @RequestBody List<RemotePointer> points){
        try {
            if (CollectionUtils.isEmpty(points)) {
                return "empty points";
            }
            LOG.info("收到："+JSON.toJSONString(points));

            for (RemotePointer remotePointer : points) {
                Point point = getRealPoint(remotePointer);
                if (null != point) {
                    LocalInfluxDB.getInfluxDB().write(point);
                }
            }
            return "success";
        }
        catch (Exception e){

            return "Exception happens";
        }
    }

    private Point getRealPoint(RemotePointer remotePointer){
        if(StringUtils.isEmpty(remotePointer.getMeasurement())||CollectionUtils.isEmpty(remotePointer.getTagMaps())){
            LOG.warn("错误的remotePointer"+ JSON.toJSONString(remotePointer));
            return null;
        }
        Point.Builder builder = Point.measurement(remotePointer.getMeasurement()).tag(remotePointer.getTagMaps());

        for(Map.Entry<String,Number> entry: remotePointer.getFieldMaps().entrySet()) {
            builder.addField(entry.getKey(),entry.getValue());
        }
        return builder.build();
    }
}
