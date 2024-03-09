package com.test.microstreaming.models.message;

import java.util.ArrayList;
import java.util.List;

public class OpenGateMessage {

    protected String version;
    protected String device;
    protected String path;
    protected String trustedBoot;
    protected List<OpenGateDataStreamMessage> datastreams = new ArrayList<>();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
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

    public List<OpenGateDataStreamMessage> getDatastreams() {
        return datastreams;
    }

    public void setDatastreams(List<OpenGateDataStreamMessage> datastreams) {
        this.datastreams = datastreams;
    }
}
