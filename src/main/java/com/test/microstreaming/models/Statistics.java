package com.test.microstreaming.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.test.microstreaming.utils.JSONUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document
public class Statistics {

    @Id
    protected String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Madrid")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    protected Date processedAt;
    protected String dataResults;
    protected int messagesProcessed;

    public Statistics() {
        this.processedAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }

    public StatisticsResults getDataResults() {
        return JSONUtils.fromJson(dataResults, StatisticsResults.class);
    }

    public void setDataResults(StatisticsResults dataResults) {
        this.dataResults = JSONUtils.toJSON(dataResults);
    }

    public int getMessagesProcessed() {
        return messagesProcessed;
    }

    public void setMessagesProcessed(int messagesProcessed) {
        this.messagesProcessed = messagesProcessed;
    }
}
