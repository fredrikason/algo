package com.algo.marketdata;

/**
 * A consumer of price updates.
 */
public interface PriceSubscriber {

    void onPriceUpdate(ClosePrice closePrice);
}
