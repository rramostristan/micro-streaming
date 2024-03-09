package com.test.microstreaming.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Statistics {

    @Id
    protected String deviceId;
    protected List<DataStream> dataStreams = new ArrayList<>();

    public Statistics() {}

    public Statistics(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<DataStream> getDataStreams() {
        return dataStreams;
    }

    public void setDataStreams(List<DataStream> dataStreams) {
        this.dataStreams = dataStreams;
    }
}
