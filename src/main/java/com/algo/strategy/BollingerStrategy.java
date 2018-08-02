package com.algo.strategy;

import ch.algotrader.entity.Position;
import ch.algotrader.entity.trade.MarketOrder;
import ch.algotrader.enumeration.Side;
import ch.algotrader.simulation.Simulator;
import ch.algotrader.simulation.SimulatorImpl;
import com.algo.backtest.BacktestPricePublisher;
import com.algo.entity.Security;
import com.algo.enumeration.SecurityType;
import com.algo.marketdata.ClosePrice;
import com.algo.marketdata.PricePublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ch.algotrader.enumeration.Direction;

/**
 * A simple breaking out trading strategy based on Bollinger bands.
 */
public class BollingerStrategy extends AbstractStrategy implements Strategy {

    private static Logger logger = LogManager.getLogger(BollingerStrategy.class.getName());

    //TODO @Autowired
    private Simulator simulator;
    private BollingerBands bollingerBands;

    static int priceCount;

    @Override
    public void onPriceUpdate(ClosePrice closePrice) {
        if (priceCount < 6) {
            logger.debug("Received price update : " + closePrice);
        }
        priceCount++;

        double dailyClosingPrice = closePrice.getClose();

        // update the current price
        simulator.setCurrentPrice(dailyClosingPrice);

        // update the Bollinger bands
        bollingerBands.add(dailyClosingPrice);

        // start the analysis
        if (priceCount > 30) {
            double average = bollingerBands.getAverage();
            double upperBand = bollingerBands.getUpperBand();
            double lowerBand = bollingerBands.getLowerBand();

            // main trading strategy logic
            Position position = simulator.getPosition();
            if (position == null || position.getQuantity() == 0) {
                double balance = simulator.getCashBalance();
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

    private void placeOrder(Side side, long quantity) {
        MarketOrder order = new MarketOrder(side, quantity);
        logger.debug("Placing market order: " + order);
        simulator.sendOrder(order);
    }

    private void closePosition(Position position) {
        Side side = null;
        switch (position.getDirection()) {
            case LONG:
                side = Side.SELL;
                break;
            case SHORT:
                side = Side.BUY;
                break;
            case FLAT:
                break;
        }
        if (side != null) {
            logger.info("Strategy cash balance: " + simulator.getCashBalance());
            if (position.getQuantity() > 0) {
                placeOrder(side, position.getQuantity());
            } else if (position.getQuantity() < 0) {
                placeOrder(side, -position.getQuantity());
            }

        }

    }

    public static void main(String[] args) {
        init(BollingerStrategy.class);
    }

    @Override
    public void run() {
        logger.info("Running trading strategy based on Bollinger bands");

        simulator = new SimulatorImpl();
        simulator.setCashBalance(1000000.0);

        bollingerBands = new BollingerBands(30, 2.5);

        String userHome = System.getProperty("user.home");

        String testFilePath = userHome + "/algo/resources/EUR.USD.csv";

        Security security = new Security("EUR", "USD", SecurityType.CASH);

        startBacktest(testFilePath, security);

    }

    public void startBacktest(String testFilePath, Security security) {
        PricePublisher pricePublisher = new BacktestPricePublisher(testFilePath, security);
        pricePublisher.addPriceSubscriber(security.getKey(), this);
        ((BacktestPricePublisher) pricePublisher).startPublishing();
    }

}
