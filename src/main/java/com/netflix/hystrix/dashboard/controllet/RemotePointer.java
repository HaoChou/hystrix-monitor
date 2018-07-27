package com.netflix.hystrix.dashboard.controllet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhou
 * Created on 2018/7/26
 */
public class RemotePointer {

    private  String measurement;
    private  Map<String,String> tagMaps;
    private  Map<String,Number> fieldMaps;

    public RemotePointer() {
        tagMaps=new HashMap<>();
        fieldMaps=new HashMap<>();
    }

    public RemotePointer measurement(String measurement){
        this.measurement=measurement;
        return this;
    }

    public RemotePointer field(String fieldName,Number number){
        fieldMaps.put(fieldName,number);
        return this;
    }

    public RemotePointer tag(String fieldName,String number){
        tagMaps.put(fieldName,number);
        return this;
    }


    public String getMeasurement() {
        return measurement;
    }

    public Map<String, String> getTagMaps() {
        return tagMaps;
    }

    public Map<String, Number> getFieldMaps() {
        return fieldMaps;
    }
}
