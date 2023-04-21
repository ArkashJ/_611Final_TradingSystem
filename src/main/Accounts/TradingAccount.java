package main.Accounts;

import main.Stocks.CustomerStocks;
import main.Stocks.Stock;

import java.util.List;
import java.util.Scanner;
//Create a trading account for a customer

public class TradingAccount implements ITrading{
    private String ownerName;
    private CustomerStocks customerStocks;
    private double balance;

    public TradingAccount(String ownerName, CustomerStocks customerStocks, double balance) {
        this.ownerName = ownerName;

        this.customerStocks = customerStocks;
        this.balance = balance;
    }

    @Override
    public void viewAccountDetails() {
        System.out.println("Account Details");
        System.out.println("Balance:\t" + balance);
        customerStocks.viewStocks();
    }

    public void buyStock(Stock stock, int num) {
        if(num<=0) {
            throw new RuntimeException("nums must be larger than 0");
        }
        else{
            if (this.customerStocks.getStocks().containsKey(stock)) {
                this.customerStocks.getStocks().put(stock, this.customerStocks.getStocks().get(stock) + num);
            } else {
                this.customerStocks.getStocks().put(stock, num);
            }
            this.updateBalance(stock.getCurrentPrice(),"b");
        }


    }

    public void sellStock(Stock stock, int num,List<OptionsAccount> optionsAccounts) {
        if (this.customerStocks.getStocks().containsKey(stock)) {
            if (this.customerStocks.getStocks().get(stock) >= num) {
                this.customerStocks.getStocks().put(stock, this.customerStocks.getStocks().get(stock) - num);
                this.updateBalance(stock.getCurrentPrice(),"s");
                createOptionsAccount(optionsAccounts);
                return;
            }
        }
        throw new RuntimeException(stock.getName()+"sell num is larger than the number of you have");
    }

    public void createOptionsAccount(List<OptionsAccount> optionsAccounts){
        if(checkBalance(optionsAccounts.size())){
            System.out.println("You can open an Options Account. Would you like to do so? (y/n)");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if(input.equals("y")){
                OptionsAccount newAccount = new OptionsAccount();
                optionsAccounts.add(newAccount);
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

    public CustomerStocks getCustomerStocks() {
        return customerStocks;
    }
}
