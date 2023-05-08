package main.Stocks;


import main.Database.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: This class represents a customer's stocks.txt. like a bag
    * CustomerStocks initializes an account number and makes a list of stocks for the customer
    * Constructor:
        * CustomerStocks(int accountNumber) - initializes the accountNumber and the list of stocks.
    * add(String stockName, int stockNumber, double stock_boughtAt) - This function loops through the customers stocks, sees if they exist and then add them to the list, followed by an update to the number of stocks in the list
    * remove(String stockName, int stockNumber) - This function loops through the customers stocks, sees if they exist and then removes them from the list unless the stock number is 0
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
