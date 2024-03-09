package com.test.microstreaming.models;

import java.util.List;

public class StatisticsResults {

    public StatisticsResults() {
        processedAt = System.currentTimeMillis();
    }

    protected double media;
    protected double mediana;
    protected int moda;
    protected double desviacionEstandar;
    protected List<Double> cuartiles;
    protected int maximo;
    protected int minimo;
    protected long processedAt;

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
