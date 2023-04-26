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

    //use for database: the same index represents the same stock
    protected List<String> stockNames;
    protected List<Integer> stockNumbers;
    protected List<Double> stock_BoughtPrices;

    protected int accountNumber;


    public CustomerStocks(int accountNumber) {
        this.accountNumber = accountNumber;
        stockNames = new ArrayList<>();
        stockNumbers = new ArrayList<>();
        stock_BoughtPrices = new ArrayList<>();
    }

    public boolean add(String stockName, int stockNumber, double stock_boughtAt) {
        stockNames.add(stockName);
        stockNumbers.add(stockNumber);
        stock_BoughtPrices.add(stock_boughtAt);
        return true;
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



}
