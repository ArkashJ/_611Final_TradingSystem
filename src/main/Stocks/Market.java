package main.Stocks;

import main.Database.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: This class represents the market, a singleton class
 * TODO: Add functions for time. You can place order for stocks.txt but can only buy or sell them when the market is open
 * Constructor:
    * Market() - initializes the market. This is a singleton class so that there is only one market

 * getStocks() - returns the list of stocks in the market defined in the marketStock file
     * setStocks(List<MarketStock> stocks) - sets the list of in the market
     * removeStock(String stockName) - removes a stock from the market
     * addStock(String stockName, int quantity) - adds a stock to the market
 * getClosingTime() - returns the closing time of the market
 * getOpeningTime() - returns the opening time of the market
 * setClosingTime(String closingTime) - sets the closing time of the market
 * setOpeningTime(String openingTime) - sets the opening time of the market
 */

public class Market {
    // The market is a singleton class
    private static Market market;
    private static String closingTime;
    private static String openingTime;
    private static List<MarketStock> stocks;
    private Market() {
        market = new Market();
        stocks = new ArrayList<>();
    }

    public String getClosingTime() {
        return closingTime;
    }

    public static void setClosingTime(String closingTime) {
        Market.closingTime = closingTime;
    }

    public static String getOpeningTime() {
        return openingTime;
    }

    public static void setOpeningTime(String openingTime) {
        Market.openingTime = openingTime;
    }

    public static List<MarketStock> getStocks() {
        if (stocks == null) {
            stocks = new ArrayList<>();
        }
        stocks.clear();
        Database.setMarketStocks();
        return stocks;
    }

    public static void setStocks(List<MarketStock> stocks) {
        Market.stocks = stocks;
    }

    public static void removeStock(String stockName) {
        for (MarketStock stock : stocks) {
            if (stock.getStockName().equals(stockName)) {
                stocks.remove(stock);
                break;
            }
        }
    }

    public static void addStock(String stockName, int quantity) {
        for (MarketStock stock : stocks) {
            if (stock.getStockName().equals(stockName)) {
                stock.setQuantity(stock.getQuantity() + quantity);
                break;
            }
        }
        stocks.add(new MarketStock(stockName, quantity));
    }
}
