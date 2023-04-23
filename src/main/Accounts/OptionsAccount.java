package main.Accounts;

import main.Stocks.CustomerStocks;
import main.Utils.Profit;

import java.util.Scanner;

public class OptionsAccount extends Account{
    private String ownerName;
    private int accountNumber;
    private String accountType;
    private CustomerStocks customerStocks;
    private double balance;
    private Profit profit;
    public OptionsAccount(String ownerName, CustomerStocks customerStocks, double balance, int accountNumber) {
        this.ownerName = ownerName;
        this.customerStocks = customerStocks;
        this.balance = balance;
//        this.stockMap = new HashMap<>();
        this.profit=new Profit();
        this.accountNumber = accountNumber;
        this.accountType = "Trading";
    }
}
