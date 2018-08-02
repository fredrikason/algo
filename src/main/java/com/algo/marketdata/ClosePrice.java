package com.algo.marketdata;

import com.algo.entity.Security;

import java.util.Date;

/**
 * A close price for security.
 */
public class ClosePrice {

    private Security security;
    private Date dateTime;
    private double close;
    private double open;
    private double low;
    private double high;

    public ClosePrice(Security security, Date dateTime, double close, double open, double low, double high) {
        this.security = security;
        this.dateTime = dateTime;
        this.close = close;
        this.open = open;
        this.low = low;
        this.high = high;
    }

    public Security getSecurity() {
        return security;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public double getClose() {
        return close;
    }

    public double getOpen() {
        return open;
    }

    public double getLow() {
        return low;
    }

    public double getHigh() {
        return high;
    }

    @Override
    public String toString() {
        return "ClosePrice{" +
                "security=" + security +
                ", dateTime=" + dateTime +
                ", close=" + close +
                ", open=" + open +
                ", low=" + low +
                ", high=" + high +
                '}';
    }
}
