package com.test.microstreaming.models.message;

import java.util.ArrayList;
import java.util.List;

public class OpenGateDataStreamMessage {

    protected String id;
    protected String feed;
    protected List<OpenGateDatapoint> datapoints = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public List<OpenGateDatapoint> getDatapoints() {
        return datapoints;
    }

    public void setDatapoints(List<OpenGateDatapoint> datapoints) {
        this.datapoints = datapoints;
    }
}
