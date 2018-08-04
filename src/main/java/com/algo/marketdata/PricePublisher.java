package com.algo.marketdata;

import com.algo.entity.Security.SecurityKey;

/**
 * A producer for price updates.
 */
public interface PricePublisher {

    /**
     * Registers a price subscriber for a specific security.
     * @param securityKey
     * @param subscriber
     */
    void addPriceSubscriber(SecurityKey securityKey, PriceSubscriber subscriber);
}
