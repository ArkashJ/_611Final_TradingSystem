package main.Accounts;

import main.Stocks.Stock;

public interface ITrading {
    public void viewAccountDetails();
    public void buyStock(Stock stock, int num);
    public void sellStock(Stock stock_origin, int num);
    boolean deposit(double amount);
    boolean withdraw(double amount);
}
