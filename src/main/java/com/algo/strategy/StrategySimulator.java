package com.algo.strategy;

import ch.algotrader.entity.Position;
import ch.algotrader.entity.trade.MarketOrder;
import ch.algotrader.enumeration.Side;
import ch.algotrader.simulation.Simulator;
import ch.algotrader.simulation.SimulatorImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;

/**
 * Base class for all strategy simulations.
 */
public abstract class StrategySimulator implements CommandLineRunner {

    protected static Logger logger = LogManager.getLogger(StrategySimulator.class.getName());

    private Simulator simulator;

    public StrategySimulator() {
        simulator = new SimulatorImpl();
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
        logger.debug("placing market order: " + order);
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
            logger.info("current cash balance: " + simulator.getCashBalance());
        }
    }

    /**
     * Subclasses needs to implement how to run the strategy.
     * @param args command line arguments
     */
    public abstract void run(String... args) throws Exception;

}
