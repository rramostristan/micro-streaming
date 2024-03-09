package com.test.microstreaming.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.test.microstreaming.utils.JSONUtils;

public class DataStream {

    protected String id;
    protected String feed;
    protected String results;

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

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public StatisticsResults getResultsObject() {
        return JSONUtils.fromJson(results, new TypeReference<>() {});
    }

    public void setResults(StatisticsResults results) {
        this.results = JSONUtils.toJSON(results);
    }
}
