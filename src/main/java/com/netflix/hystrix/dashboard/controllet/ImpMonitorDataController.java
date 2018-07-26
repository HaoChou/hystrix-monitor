package com.netflix.hystrix.dashboard.controllet;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhou
 * Created on 2018/7/26
 */
@RestController
public class ImpMonitorDataController {

    @RequestMapping("/input/point.html")
    public String inputPoint( @RequestBody RemotePointer point){

        System.out.println(JSON.toJSON(point));
        return "1";
    }


    @RequestMapping("/input/point2.html")
    public String inputPoints(  String s){

        System.out.println(JSON.toJSON(s));
        return "1";
    }
}
