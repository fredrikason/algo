package com.algo.marketdata;

import com.algo.entity.Security.SecurityKey;

/**
 * A producer for price updates.
 */
public interface PricePublisher {

    void addPriceSubscriber(SecurityKey securityKey, PriceSubscriber subscriber);
}
