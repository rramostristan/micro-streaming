package com.test.microstreaming.models;

import java.util.Date;
import java.util.List;

public class StatisticsResults {

    public StatisticsResults() {
        processedAt = new Date();
    }

    protected double mean;
    protected double median;
    protected int mode;
    protected double standardDeviation;
    protected List<Double> quartiles;
    protected int maximum;
    protected int minimum;
    protected Date processedAt;

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public List<Double> getQuartiles() {
        return quartiles;
    }

    public void setQuartiles(List<Double> quartiles) {
        this.quartiles = quartiles;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public Date getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }
}
