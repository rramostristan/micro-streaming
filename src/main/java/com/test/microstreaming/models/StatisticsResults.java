package com.test.microstreaming.models;

import java.util.List;

public class StatisticsResults {

    protected double mean;
    protected double median;
    protected int mode;
    protected double standardDeviation;
    protected List<Double> quartiles;
    protected int maximum;
    protected int minimum;

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
}
