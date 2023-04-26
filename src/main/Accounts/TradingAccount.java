package main.Accounts;

import main.Database.Database;
import main.PortfolioManager.BankManager;
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
    public Map<String, Double> calculateProfits() {
        return BankManager.calculateProfits(accountNumber);
    }


    /**
     * By Jianxiao : checkBalance(optionsAccounts.size()) why check Accounts size?
     * @param optionsAccounts : if a customer has gained more than 10k, he can open an options account
     */
    public void createOptionsAccount(List<OptionsAccount> optionsAccounts){
        if(checkBalance(optionsAccounts.size())){
            System.out.println("You can open an Options Account. Would you like to do so? (y/n)");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if(input.equals("y")){
//                OptionsAccount newAccount = new OptionsAccount();
//                optionsAccounts.add(newAccount);
                System.out.println("Options Account created");
            }
            else{
                System.out.println("Options Account not created");
            }

        }
    }

    public boolean checkBalance(double numOptions){
        if(numOptions==0){
            if(this.balance>=10000){
                return true;
            }
        }
        else{
            if(this.balance>=numOptions*10000){
                return true;
            }
        }
        return false;
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
