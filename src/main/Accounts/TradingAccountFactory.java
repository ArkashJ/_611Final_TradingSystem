package main.Accounts;

//Used to create a trading account object

import main.Stocks.CustomerStocks;

public class TradingAccountFactory {
    public static TradingAccount createTradingAccount(String ownerName, CustomerStocks customerStocks, double balance) {
        return new TradingAccount(ownerName, customerStocks, balance);
    }
}
