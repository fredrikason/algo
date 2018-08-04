package com.algo.strategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BollingerBandsTest {

    private BollingerBands bollingerBands;

    @Before
    public void before() {
        bollingerBands = new BollingerBands(5, 2);
    }

    @Test
    public void test() {

        bollingerBands.add(3.0);
        Assert.assertEquals(1, bollingerBands.size());
        bollingerBands.add(4.0);
        Assert.assertEquals(2, bollingerBands.size());
        bollingerBands.add(5.0);
        Assert.assertEquals(3, bollingerBands.size());
        bollingerBands.add(4.0);
        Assert.assertEquals(4, bollingerBands.size());
        bollingerBands.add(6.0);
        Assert.assertEquals(5, bollingerBands.size());
        Assert.assertEquals(4.4, bollingerBands.getAverage(), 0.01);

        Assert.assertEquals(1.019, bollingerBands.getStandardDeviation(), 0.001);
        Assert.assertEquals(2.360, bollingerBands.getLowerBand(), 0.001);
        Assert.assertEquals(6.439, bollingerBands.getUpperBand(), 0.001);

        bollingerBands.add(5.0);
        Assert.assertEquals(5, bollingerBands.size());
        Assert.assertEquals(4.8, bollingerBands.getAverage(), 0.01);

        Assert.assertEquals(0.748, bollingerBands.getStandardDeviation(), 0.001);
        Assert.assertEquals(3.303, bollingerBands.getLowerBand(), 0.001);
        Assert.assertEquals(6.296, bollingerBands.getUpperBand(), 0.001);

    }
}
