package main.Accounts;

import main.Stocks.Stock;

public interface ITrading {
    public void viewAccountDetails();
    boolean deposit(double amount);
    boolean withdraw(double amount);
}
