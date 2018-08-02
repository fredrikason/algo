package com.algo.strategy;

import java.util.LinkedList;
import java.util.Queue;

/**
 * An simple moving average implementation of Bollinger bands.
 */
public class BollingerBands {
    private Queue<Double> data = new LinkedList<>();
    private int windowLength;
    private double deviations;
    private double sum;


    public BollingerBands(int windowLength, double deviations) {
        this.windowLength = windowLength;
        this.deviations = deviations;
    }

    public void add(double price)
    {
        sum += price;
        data.add(price);

        if (data.size() > windowLength)
        {
            sum -= data.remove();
        }
    }

    public double getAverage() {
        return sum / windowLength;
    }

    public double getDeviation() {
        double avg = getAverage();

        double sum = 0;
        for (double price : data) {
            sum += Math.pow(price - avg, 2);
        }

        return Math.sqrt(sum / windowLength);
    }

    public double getUpperBand() {
        return getAverage() + (deviations * getDeviation());
    }

    public double getLowerBand() {
        return getAverage() - (deviations * getDeviation());
    }

}
