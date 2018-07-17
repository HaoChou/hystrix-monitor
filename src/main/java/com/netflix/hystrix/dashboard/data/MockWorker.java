package com.netflix.hystrix.dashboard.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;

/**
 * @author zhou
 * Created on 2018/7/13
 */
public class MockWorker implements StreamWorker{

    private static final Logger logger = LoggerFactory.getLogger(MockWorker.class);

    @Override
    public void init() {

    }

    @Override
    public void setConfig(WorkerConfig workerConfig) {

    }

    @Override
    public WorkerConfig getConfig() {
        return null;
    }

    @Override
    public void doWork() {

        String filename = "hystrix.stream";
        String data = getFileFromPackage(filename);

        String lines[] = data.split("\n");

        int delay=500;
        int batch=1;
        int batchCount = 0;
        // loop forever unless the user closes the connection
        for (;;) {
            for (String s : lines) {
                s = s.trim();
                if (s.length() > 0) {
                    try {
                        System.out.println(s);
                        batchCount++;
                    } catch (Exception e) {
                        logger.warn("Exception writing mock data to output.", e);
                        // most likely the user closed the connection
                        return;
                    }
                    if (batchCount == batch) {
                        // we insert the delay whenever we finish a batch
                        try {
                            // simulate the delays we get from the real feed
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                        // reset
                        batchCount = 0;
                    }
                }
            }
        }

    }


    public static void main(String[] args) {


        StreamWorker worker = new MockWorker();

        worker.doWork();

    }


    private String getFileFromPackage(String filename) {
        try {
            String file = "/" + filename;
            InputStream is = this.getClass().getResourceAsStream(file);
            try {
                 /* this is FAR too much work just to get a string from a file */
                BufferedReader in = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                StringWriter s = new StringWriter();
                int c = -1;
                while ((c = in.read()) > -1) {
                    s.write(c);
                }
                return s.toString();
            } finally {
                is.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not find file: " + filename, e);
        }
    }
}
