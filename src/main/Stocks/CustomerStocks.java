package main.Stocks;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: This class represents a customer's stocks. like a bag
 */

public class CustomerStocks {
    // stocks: key -> stock, value -> number of stocks
    protected HashMap<Stock, Integer> stocks;
    public HashMap<Stock, Integer> getStocks() {
        return stocks;
    }

    public void setStocks(HashMap<Stock, Integer> stocks) {
        this.stocks = stocks;
    }

    public CustomerStocks() {
        stocks = new HashMap<>();
    }

    // getStock: need to catch exception : not found
    public Stock getStock(String StockName) {
        for (Stock stock : stocks.keySet()) {
            if (stock.getName().equals(StockName)) {
                return stock;
            }
        }
        throw new RuntimeException( StockName+"Stock not found");
    }

    public void buyStock(Stock stock, int num) {
        if(num<=0) {
            throw new RuntimeException("nums must be larger than 0");
        }
        if (stocks.containsKey(stock)) {
            stocks.put(stock, stocks.get(stock) + num);
        } else {
            stocks.put(stock, num);
        }
    }

    public void sellStock(Stock stock, int num) {
        if (stocks.containsKey(stock)) {
            if (stocks.get(stock) >= num) {
                stocks.put(stock, stocks.get(stock) - num);
                return;
            }
        }
        throw new RuntimeException(stock.getName()+"sell num is larger than the number of you have");
    }

    public void viewStocks() {
        System.out.println("Your stocks:");
        System.out.println("StockName\tCompanyName\tCurrentPrice\tNum");
        for (Stock stock : stocks.keySet()) {
            System.out.println(stock.getName()+"\t"+stock.getCompanyName()+"\t"+stock.getCurrentPrice()+"\t"+stocks.get(stock));
        }
    }
}
