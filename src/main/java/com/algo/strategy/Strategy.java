package com.algo.strategy;

import ch.algotrader.entity.Position;
import ch.algotrader.entity.trade.MarketOrder;
import ch.algotrader.enumeration.Side;
import ch.algotrader.simulation.Simulator;
import ch.algotrader.simulation.SimulatorImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

/**
 * Base class for all strategies.
 */
public abstract class Strategy {

    protected static Logger logger = LogManager.getLogger(Strategy.class.getName());

    private Simulator simulator;

    public Strategy() {
        simulator = new SimulatorImpl();
    }

    /**
     * Initialises a strategy setting its properties etc. via Spring Boot
     * @param clazz the strategy to initialise
     */
    public static void init(Class<? extends Strategy> clazz) {
        SpringApplication application = new SpringApplication(clazz);
        ApplicationContext context = application.run();
        Strategy strategy = context.getBean(clazz);
        strategy.run();
    }

    /**
     * Sets the initial cash balance.
     * @param amount
     */
    public void setInitialCashBalance(double amount) {
        simulator.setCashBalance(amount);
    }

    /**
     * Returns the current cash balance in the simulation.
     * @return cash balance
     */
    public double getCurrentCashBalance() {
        return simulator.getCashBalance();
    }

    /**
     * Sets the current price in the simulation.
     * @param price
     */
    public void setCurrentPrice(double price) {
        simulator.setCurrentPrice(price);
    }

    /**
     * Returns the current position in the simulation.
     * @return current position
     */
    public Position getCurrentPosition() {
        return simulator.getPosition();
    }

    /**
     * Place a market order using the simulator.
     * @param side buy or sell flag
     * @param quantity the order quantity
     */
    public void placeOrder(Side side, long quantity) {
        MarketOrder order = new MarketOrder(side, quantity);
        logger.debug("Placing market order: " + order);
        simulator.sendOrder(order);
    }

    /**
     * Closes out a position, if the position is already flat no action is taken.
     * @param position the position to close
     */
    public void closePosition(Position position) {
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
            if (position.getQuantity() > 0) {
                placeOrder(side, position.getQuantity());
            } else if (position.getQuantity() < 0) {
                placeOrder(side, -position.getQuantity());
            }
            logger.info("Strategy cash balance: " + simulator.getCashBalance());
        }
    }

    /**
     * Subclasses needs to implement how to run the strategy.
     */
    public abstract void run();

}
