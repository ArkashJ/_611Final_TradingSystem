package main.Stocks;


import main.Database.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: This class represents a customer's stocks.txt. like a bag
 */

public class CustomerStocks {

    protected List<CustomerStock> customerStocks;
    protected int accountNumber;


    public CustomerStocks(int accountNumber) {
        this.accountNumber = accountNumber;
        customerStocks = new ArrayList<>();
    }

    public boolean add(String stockName, int stockNumber, double stock_boughtAt) {
        for(CustomerStock stock : customerStocks) {
            if(stock.getStockName().equals(stockName)) {
                stock.setStockNumber(stock.getStockNumber() + stockNumber);
                stock.setStockBoughtPrice(stock_boughtAt);
                return true;
            }
        }
        customerStocks.add(new CustomerStock(stockName, stockNumber, stock_boughtAt));
        return true;
    }

    public void remove(String stockName, int stockNumber) {
        for(CustomerStock stock : customerStocks) {
            if(stock.getStockName().equals(stockName)) {
                stock.setStockNumber(stock.getStockNumber() - stockNumber);
                if (stock.getStockNumber() == 0) {
                    customerStocks.remove(stock);
                }
                break;
            }
        }
    }

    public List<CustomerStock> getStocks() {
        return customerStocks;
    }

    public CustomerStock getStock(String stockName) {
        for (CustomerStock stock : customerStocks) {
            if (stock.getStockName().equals(stockName)) {
                return stock;
            }
        }
        return null;
    }


    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }



}
