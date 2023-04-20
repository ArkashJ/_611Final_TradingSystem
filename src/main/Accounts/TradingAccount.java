package main.Accounts;

import main.Stocks.CustomerStocks;
import main.Stocks.Stock;
//Create a trading account for a customer

public class TradingAccount implements ITrading{
    private String ownerName;
    private CustomerStocks customerStocks;
    private double balance;

    public TradingAccount(String ownerName, CustomerStocks customerStocks, double balance) {
        this.ownerName = ownerName;

        this.customerStocks = customerStocks;
        this.balance = balance;
    }

    @Override
    public void viewAccountDetails() {
        System.out.println("Account Details:");
        System.out.println("Owner Name: " + ownerName);
        System.out.println("Customer Stocks: " + customerStocks);
        System.out.println("Balance: " + balance);
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getBalance() {
        // loop through customer stocks and return the total value
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public CustomerStocks getCustomerStocks() {
        return customerStocks;
    }
}
