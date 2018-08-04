package com.algo.marketdata;

/**
 * A consumer of price updates.
 */
public interface PriceSubscriber {

    /**
     * Call back method for price updates.
     * @param closePrice
     */
    void onPriceUpdate(ClosePrice closePrice);
}
