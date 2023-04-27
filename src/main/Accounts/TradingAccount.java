package main.Accounts;

import main.Database.Database;
import main.PortfolioManager.BankManager;
import main.Stocks.CustomerStock;
import main.Stocks.CustomerStocks;
import main.Stocks.Stock;
import main.Stocks.StockFactory;
import main.Utils.Profit_Loss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static main.Database.Database.insertAccounts;
//Create a trading account for a customer

/**
 *
 */

public class TradingAccount extends Account implements ITrading{
    private String ownerName;
    private int accountNumber;
    private String accountType;
    private CustomerStocks customerStocks;
    private double balance;
    private final int realizedProfitThreshold = 10000;

    public TradingAccount(String ownerName, CustomerStocks customerStocks, double balance, int accountNumber) {
        this.ownerName = ownerName;
        this.customerStocks = customerStocks;
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.accountType = "Trading";
    }

    @Override
    public void viewAccountDetails() {
        System.out.println("Account Details");
        System.out.println("Balance:\t" + balance);
//        customerStocks.viewStocks();
    }

    public boolean deposit(double amount) {
        balance += amount;
        updateBalance(balance);
        return true;
    };
    public boolean withdraw(double amount) {
        if(balance<amount) {return false;}
        else {
            balance -= amount;
            updateBalance(balance);
            return true;
        }
    };

    //update from database
    public boolean updateBalance(double balance) {
        Connection conn= Database.getConnection();
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, balance);
            pstmt.setInt(2, accountNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    //See personal realized and unrealized profits
    public Map<String, Double> getProfitsForAccount() {
        return BankManager.calculateProfits(accountNumber);
    }


    public List<CustomerStock> getHoldings() {
        return customerStocks.getStocks();
    }

    public double getProfitForStock(String StockName) {
        return BankManager.getTotalProfitForStock(accountNumber, StockName);
    }

    /**
     * By Jianxiao : checkBalance(optionsAccounts.size()) why check Accounts size?
     * @param optionsAccounts : if a customer has gained more than 10k, he can open an options account
     */
    public boolean isEligibleForOptionsAcc(){
        // call the getProfitsForAccount() method to get the profits for the account
        Map<String, Double> profits = getProfitsForAccount();
        double unRealizedProfit = profits.get("realized");
        if (unRealizedProfit >= realizedProfitThreshold){
            System.out.println("You are eligible for an options account. Would you like to open one? (y/n)");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if(input.equals("y")){
                // create an options account
                System.out.println("Options Account created");
                OptionsAccount optionsAccount = new OptionsAccount(ownerName, customerStocks, balance, accountNumber);
                // add the options account to the list of accounts
                insertAccounts(accountNumber, ownerName, balance, "Options");
                return true;
            }
            else{
                System.out.println("Options Account not created");
                return false;
            }
        }
    }

    public boolean checkBalance(Stock stock,int num) {
        return this.balance >= stock.getCurrentPrice() * num;
    }

    public void updateBalance(double amount, String way){
        if(way.equals("s")){
            this.balance+=amount;
        }
        else{
            this.balance-=amount;
        }
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public CustomerStocks getCustomerStocks() {
        return customerStocks;
    }
}
