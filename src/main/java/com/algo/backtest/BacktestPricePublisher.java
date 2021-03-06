package com.algo.backtest;

import com.algo.entity.Security;
import com.algo.entity.Security.SecurityKey;
import com.algo.marketdata.ClosePrice;
import com.algo.marketdata.PricePublisher;
import com.algo.marketdata.PriceSubscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A simple price publisher used for backtesting a strategy.
 */
public class BacktestPricePublisher implements PricePublisher {

    private static Logger logger = LogManager.getLogger(BacktestPricePublisher.class.getName());

    private Map<SecurityKey, List<PriceSubscriber>> priceSubscriberMap;

    private TestFile testFile;

    public BacktestPricePublisher(String testFilePath, Security security) {
        testFile = new TestFile(testFilePath, security);
        priceSubscriberMap = new HashMap<>();
    }

    @Override
    public void addPriceSubscriber(SecurityKey securityKey, PriceSubscriber priceSubscriber) {
        if (!priceSubscriberMap.containsKey(securityKey)) {
            priceSubscriberMap.put(securityKey, new ArrayList<PriceSubscriber>());
        }
        priceSubscriberMap.get(securityKey).add(priceSubscriber);
    }

    public void startPublishing() {
        try {
            testFile.readNextPrice();
            while (true) {
                if (testFile.isEndOfFile()) {
                    break;
                }
                publishPrice(testFile.getPrice());
                testFile.readNextPrice();
            }
        } catch (FileNotFoundException e) {
            logger.error("Unable to find file", e);
        } catch (IOException e) {
            logger.error("Unable to read file", e);
        } finally {
            testFile.close();
        }
    }

    private void publishPrice(ClosePrice price) {
        SecurityKey securityKey = price.getSecurity().getKey();
        if (null != priceSubscriberMap.get(securityKey)) {
            for (PriceSubscriber priceSubscriber : priceSubscriberMap.get(securityKey)) {
                priceSubscriber.onPriceUpdate(price);
            }
        }
    }

    class TestFile {
        private BufferedReader reader;
        private String filePath;
        private ClosePrice price;
        private Security security;
        private boolean endOfFile;
        private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        private int lineCount = 0;

        public TestFile(String filePath, Security security) {
            this.filePath = filePath;
            this.security = security;
        }

        public ClosePrice getPrice() {
            return price;
        }

        public void readNextPrice() throws IOException {
            if (null == reader) {
                reader = new BufferedReader(new FileReader(filePath));
            }
            while (true) {
                String line = reader.readLine();
                lineCount++;
                if (1 == lineCount) {
                    // throw away header
                    continue;
                }
                if (null == line) {
                    endOfFile = true;
                    return;
                }
                price = createPriceFromLine(line);
                break;
            }
        }

        public boolean isEndOfFile() {
            return endOfFile;
        }

        public void close() {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.debug("Unable to close reader", e);
                }
            }
        }

        private ClosePrice createPriceFromLine(String line) {
            // dateTime, open, low, high, close
            String[] lineItems = line.split(",");
            Date dateTime = null;
            try {
                dateTime = format.parse(lineItems[0]);
            } catch (ParseException e) {
                logger.error("Invalid date format: " + lineItems[0]);
                endOfFile = true;
                return null;
            }
            double open = Double.parseDouble(lineItems[1]);
            double low = Double.parseDouble(lineItems[2]);
            double high = Double.parseDouble(lineItems[3]);
            double close = Double.parseDouble(lineItems[4]);
            return new ClosePrice(security, dateTime, close, open, low, high);
        }
    }

}
