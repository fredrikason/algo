package com.algo.strategy;

import java.util.LinkedList;
import java.util.Queue;

/**
 * An simple moving average implementation of Bollinger bands.
 */
public class BollingerBands {

    private Queue<Double> prices = new LinkedList<>();
    private int windowLength;
    private double deviations;
    private double sum;
    private double sumSquared;


    /**
     * Constructs a new BollingerBands
     * @param windowLength
     * @param deviations
     */
    public BollingerBands(int windowLength, double deviations) {
        this.windowLength = windowLength;
        this.deviations = deviations;
    }

    /**
     * Returns the number of prices currently in the window
     * @return
     */
    public int size() {
        return prices.size();
    }

    /**
     * Adds a price and updates the price sums.
     * @param price
     */
    public void add(double price) {
        sum += price;
        sumSquared += Math.pow(price, 2);
        prices.add(price);

        if (prices.size() > windowLength)
        {
            double priceToRemove = prices.remove();
            sum -= priceToRemove;
            sumSquared -= Math.pow(priceToRemove, 2);

        }
    }

    /**
     * Returns the standard deviation.
     * @return
     */
    public double getStandardDeviation() {
        return Math.sqrt(sumSquared / windowLength - Math.pow(getAverage(), 2));
    }

    /**
     * Returns the moving average.
     * @return
     */
    public double getAverage() {
        return sum / windowLength;
    }

    /**
     * Returns the upper band.
     * @return
     */
    public double getUpperBand() {
        return getAverage() + (deviations * getStandardDeviation());
    }

    /**
     * Returns the lower band.
     * @return
     */
    public double getLowerBand() {
        return getAverage() - (deviations * getStandardDeviation());
    }

}
