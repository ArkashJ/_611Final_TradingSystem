package main.Accounts;

import main.Stocks.CustomerStocks;
//import main.Utils.Profit;

import java.util.Scanner;


/*
    * @Description: This class is used to create an Options Account
    * The user is promopted to make this account if they have more than 10k in realized gains
 */

public class OptionsAccount extends TradingAccount{
    private String ownerName;
    private int accountNumber;
    private String accountType;
    private CustomerStocks customerStocks;
    private double balance;
//    private Profit profit;
    public OptionsAccount(String ownerName, CustomerStocks customerStocks, double balance, int accountNumber) {
        super(ownerName, customerStocks, balance, accountNumber);
        this.accountType = "OPTIONS";
    }
}
