package main.Accounts;

//Used to create a trading account object

import main.Stocks.CustomerStocks;

public class TradingAccountFactory {
    public static TradingAccount createTradingAccount(String ownerName, long accountNumber, String accountType, String userName, String password, CustomerStocks customerStocks, double balance) {
        return new TradingAccount(ownerName, accountNumber, accountType, userName, password, customerStocks, balance);
    }
}
