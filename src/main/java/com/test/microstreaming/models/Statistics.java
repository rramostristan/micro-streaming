package com.test.microstreaming.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Statistics {

    @Id
    protected String deviceId;
    protected String path;
    protected String trustedBoot;
    protected String version;
    protected List<DataStream> dataStreams = new ArrayList<>();

    public Statistics() {}

    public Statistics(String deviceId, String path, String trustedBoot, String version) {
        this.deviceId = deviceId;
        this.path = path;
        this.trustedBoot = trustedBoot;
        this.version = version;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTrustedBoot() {
        return trustedBoot;
    }

    public void setTrustedBoot(String trustedBoot) {
        this.trustedBoot = trustedBoot;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
