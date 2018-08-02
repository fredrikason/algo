package com.algo.strategy;

import com.algo.marketdata.PriceSubscriber;

/**
 * A strategy listening on price updates.
 */
public interface Strategy extends PriceSubscriber {

    void run();
}
