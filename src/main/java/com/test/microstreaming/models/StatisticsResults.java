package com.test.microstreaming.models;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class StoredData {

    public StoredData() {
        processedAt = System.currentTimeMillis();
    }

    protected String id;
    protected String feed;
    protected double media;
    protected double mediana;
    protected int moda;
    protected double desviacionEstandar;
    protected List<Double> cuartiles;
    protected int maximo;
    protected int minimo;
    protected long processedAt;


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

    public double getMedia() {
        return media;
    }

    public void setMedia(double media) {
        this.media = media;
    }

    public double getMediana() {
        return mediana;
    }

    public void setMediana(double mediana) {
        this.mediana = mediana;
    }

    public int getModa() {
        return moda;
    }

    public void setModa(int moda) {
        this.moda = moda;
    }

    public double getDesviacionEstandar() {
        return desviacionEstandar;
    }

    public void setDesviacionEstandar(double desviacionEstandar) {
        this.desviacionEstandar = desviacionEstandar;
    }

    public List<Double> getCuartiles() {
        return cuartiles;
    }

    public void setCuartiles(List<Double> cuartiles) {
        this.cuartiles = cuartiles;
    }

    public int getMaximo() {
        return maximo;
    }

    public void setMaximo(int maximo) {
        this.maximo = maximo;
    }

    public int getMinimo() {
        return minimo;
    }

    public void setMinimo(int minimo) {
        this.minimo = minimo;
    }

    public long getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(long processedAt) {
        this.processedAt = processedAt;
    }
}
