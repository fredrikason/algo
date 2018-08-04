package com.algo.strategy;

import ch.algotrader.entity.Position;
import ch.algotrader.enumeration.Side;
import com.algo.backtest.BacktestPricePublisher;
import com.algo.entity.Security;
import com.algo.enumeration.SecurityType;
import com.algo.marketdata.ClosePrice;
import com.algo.marketdata.PricePublisher;
import com.algo.marketdata.PriceSubscriber;
import ch.algotrader.enumeration.Direction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.URL;

/**
 * A simple breaking out trading strategy based on Bollinger bands.
 */
@Configuration
@PropertySource("classpath:BollingerStrategy.properties")
public class BollingerStrategy extends Strategy implements PriceSubscriber {

    @Value("${startingCashBalance}")
    private double cashBalance;

    @Value("${movingAverageLength}")
    private int windowLength;

    @Value("${standardDeviations}")
    private double standardDeviations;

    private BollingerBands bollingerBands;

    private int priceCount;

    @Override
    public void onPriceUpdate(ClosePrice closePrice) {
        if (priceCount < 6) {
            logger.debug("Received price update : " + closePrice);
        }
        priceCount++;

        final double dailyClosingPrice = closePrice.getClose();

        // update the current price
        setCurrentPrice(dailyClosingPrice);

        // update the Bollinger bands
        bollingerBands.add(dailyClosingPrice);

        // start the analysis
        if (priceCount > 30) {
            double average = bollingerBands.getAverage();
            double upperBand = bollingerBands.getUpperBand();
            double lowerBand = bollingerBands.getLowerBand();

            // main trading strategy logic
            Position position = getCurrentPosition();
            if (position == null || position.getQuantity() == 0) {
                double balance = getCurrentCashBalance();
                long quantity = (long) balance;
                if (dailyClosingPrice < lowerBand) {
                    placeOrder(Side.BUY, quantity);
                } else if (dailyClosingPrice > upperBand) {
                    placeOrder(Side.SELL, quantity);
                }
            } else {
                if (position.getDirection().equals(Direction.LONG) && dailyClosingPrice > average) {
                    closePosition(position);
                } else if (position.getDirection().equals(Direction.SHORT) && dailyClosingPrice < average) {
                    closePosition(position);
                }
            }
        }
    }

    public static void main(String[] args) {
        init(BollingerStrategy.class);
    }

    @Override
    public void run() {
        logger.info("Running trading strategy based on Bollinger bands");

        // setup required for test
        setInitialCashBalance(cashBalance);

        bollingerBands = new BollingerBands(windowLength, standardDeviations);

        URL testFileUrl = getClass().getClassLoader().getResource("EUR.USD.csv");

        Security security = new Security("EUR", "USD", SecurityType.CASH);

        startBacktest(testFileUrl, security);

        // finally close position
        Position position = getCurrentPosition();
        if (position != null)
            closePosition(position);

    }

    public void startBacktest(URL testFileUrl, Security security) {
        long startTime = System.currentTimeMillis();
        PricePublisher pricePublisher = new BacktestPricePublisher(testFileUrl.getPath(), security);
        pricePublisher.addPriceSubscriber(security.getKey(), this);
        ((BacktestPricePublisher) pricePublisher).startPublishing();
        long endTime = System.currentTimeMillis();
        logger.info("Backtesting strategy took " + (endTime - startTime) + " milliseconds");
    }

}
