package com.netflix.hystrix.dashboard.influxdb;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhou
 * Created on 2018/7/23
 */
public class Demo {
    public static void main(String[] args) {
        InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "admin", "admin");
        String dbName = "zhoulocalTest";
        influxDB.createDatabase(dbName);
        influxDB.createDatabase(dbName);
        String rpName = "aRetentionPolicy";

        influxDB.createRetentionPolicy(rpName, dbName, "30d", "1h", 1, true);
        influxDB.setRetentionPolicy(rpName);

        BatchPoints batchPoints = BatchPoints
                .database(dbName)
                .tag("group", "true")
//                .retentionPolicy("default")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        Point point1 = Point.measurement("cpu")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .field("idle", 90L).field("system", 9L)
                .field("system", 1L)
                .build();
        Point point2 = Point.measurement("disk")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .field("used", 80L)
                .field("free", 1L)
                .build();
        batchPoints.point(point1);
        batchPoints.point(point2);
        influxDB.write(batchPoints);
        Query query = new Query("SELECT idle FROM cpu", dbName);
        QueryResult queryResult = influxDB.query(query);
        List<QueryResult.Result> results = queryResult.getResults();
        influxDB.deleteDatabase(dbName);
    }
}
