package main.Stocks;


import main.Database.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: This class represents a customer's stocks. like a bag
 * @Methods: getStock(Stock) if a stock name is given, return all stocks with the same name
 */

public class CustomerStocks {
    // stocks: key -> stock, value -> number of stocks
    protected HashMap<Stock, Integer> stocks;

    //use for database: the same index represents the same stock
    protected List<String> stockNames;
    protected List<Integer> stockNumbers;
    protected List<Double> stock_BoughtPrices;

    protected int accountNumber;
    public HashMap<Stock, Integer> getStocks() {
        return stocks;
    }

    public void setStocks(HashMap<Stock, Integer> stocks) {
        this.stocks = stocks;
    }

    public CustomerStocks(int accountNumber) {
        this.accountNumber = accountNumber;
        stocks = new HashMap<>();
        stockNames = new ArrayList<>();
        stockNumbers = new ArrayList<>();
        stock_BoughtPrices = new ArrayList<>();
    }

    public List<String> getStockNames() {
        return stockNames;
    }

    public void setStockNames(List<String> stockNames) {
        this.stockNames = stockNames;
    }

    public List<Integer> getStockNumbers() {
        return stockNumbers;
    }

    public void setStockNumbers(List<Integer> stockNumbers) {
        this.stockNumbers = stockNumbers;
    }

    public List<Double> getStock_BoughtPrices() {
        return stock_BoughtPrices;
    }

    public void setStock_BoughtPrices(List<Double> stock_BoughtPrices) {
        this.stock_BoughtPrices = stock_BoughtPrices;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public List<Stock> getStocks(String StockName) {
        List<Stock> stocks = new ArrayList<>();
        for (Stock stock : this.stocks.keySet()) {
            if (stock.getName().equals(StockName)) {
                stocks.add(stock);
            }
        }
        return stocks;
    }
    public List<Stock> getStocks(Stock stock_orgin) {
        return getStocks(stock_orgin.getName());
    }

    public void add(Stock stock, int num) {
        if (stocks.containsKey(stock)) {
            stocks.put(stock, stocks.get(stock) + num);
        } else {
            stocks.put(stock, num);
        }
    }

    public void add(Stock stock, int num, double price) {
        if (stocks.containsKey(stock)) {
            stocks.put(stock, stocks.get(stock) + num);
        } else {
            stocks.put(stock, num);
        }
        stockNames.add(stock.getName());
        stockNumbers.add(num);
        stock_BoughtPrices.add(price);
    }
    public void remove(Stock stock, int num) {
        if (stocks.containsKey(stock)) {
            if (stocks.get(stock) > num) {
                stocks.put(stock, stocks.get(stock) - num);
            } else {
                stocks.remove(stock);
            }
        } else {
            throw new RuntimeException("You don't have this stock");
        }
    }


    public int getOriginalStockNum(Stock stock) {
        List<Stock> stocks = getStocks(stock);
        int num = 0;
        for (Stock stock1 : stocks) {
            num += getStockNum(stock1);
        }
        return num;
    }
    public int getStockNum(Stock stock) {
        if (stocks.containsKey(stock)) {
            return stocks.get(stock);
        } else {
            return 0;
        }
    }

    public void viewStocks() {
        System.out.println("Your stocks:");
        System.out.println("StockName\tCompanyName\tCurrentPrice\tNum");
        for (Stock stock : stocks.keySet()) {
            System.out.println(stock.getName()+"\t"+stock.getCompanyName()+"\t"+stock.getCurrentPrice()+"\t"+stocks.get(stock));
        }
    }
}
