package main.Accounts;

import main.Stocks.CustomerStocks;

//Create a trading account for a customer

public class TradingAccount implements ITrading{
    private String ownerName;
    private long accountNumber;
    private String accountType;
    private String userName;
    private String password;

    private CustomerStocks customerStocks;

    public TradingAccount(String ownerName, long accountNumber, String accountType, String userName, String password) {
        this.ownerName = ownerName;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public void viewAccountDetails() {
        System.out.println("Account Details:");
        System.out.println("Owner Name: " + ownerName);
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Type: " + accountType);
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CustomerStocks getCustomerStocks() {
        return customerStocks;
    }
}
