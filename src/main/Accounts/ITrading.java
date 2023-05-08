package main.Accounts;

import main.Stocks.Stock;
/*
    * Interface for the trading account
    * This interface will be implemented by the TradingAccount class
    * deposit and withdraw
 */

public interface ITrading {
    public void viewAccountDetails();
    boolean deposit(double amount);
    boolean withdraw(double amount);
}
