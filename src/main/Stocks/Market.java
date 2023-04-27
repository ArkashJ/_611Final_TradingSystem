package main.Stocks;

import java.util.HashMap;

/**
 * @Description: This class represents the market, a singleton class
 * TODO: Add functions for time. You can place order for stocks.txt but can only buy or sell them when the market is open
 */

public class Market {
    // The market is a singleton class
    private static Market market;
    private String closingTime;
    private String openingTime;
    // stocks.txt: key -> stock, value -> number of stocks.txt
    private static HashMap<Stock, Integer> stocks;
    private Market() {
        market = new Market();
        stocks = new HashMap<>();
    }
    
    public static void buyStocks(Stock stock, int num) {
        if(num<=0) {
            throw new RuntimeException("nums must be larger than 0");
        }
        if (stocks.containsKey(stock)) {
            stocks.put(stock, stocks.get(stock) + num);
        } else {
            stocks.put(stock, num);
        }
    }

    public static void sellStocks(Stock stock, int num) {
        if (stocks.containsKey(stock)) {
            if (stocks.get(stock) >= num) {
                stocks.put(stock, stocks.get(stock) - num);
                return;
            }
            else {
                throw new RuntimeException(stock.getName()+"sell num is larger than the number of you have");
            }
        }
        else {
            throw new RuntimeException(stock.getName()+"not found");
        }
    }

    public HashMap<Stock, Integer> getStocks() {
        return this.stocks;
    }

}
