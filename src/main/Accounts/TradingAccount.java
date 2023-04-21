package main.Accounts;

import main.Stocks.CustomerStocks;
import main.Stocks.Stock;
import main.Stocks.StockFactory;
import main.Utils.Profit_Loss;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
//Create a trading account for a customer

public class TradingAccount implements ITrading{
    private String ownerName;
    private CustomerStocks customerStocks;
    private double balance;

    // profit_loss is a class that stores the profit and loss of each possible trade
    private Profit_Loss profit_loss;

    // This is a map that stores the stocks that the customer has bought
    // The key is the stock in the customerStock, the value is the orginal stock in the market
    private HashMap<Stock, Stock> stockMap;

    public TradingAccount(String ownerName, CustomerStocks customerStocks, double balance) {
        this.ownerName = ownerName;

        this.customerStocks = customerStocks;
        this.balance = balance;
        this.stockMap = new HashMap<>();
        this.profit_loss=new Profit_Loss();
    }

    @Override
    public void viewAccountDetails() {
        System.out.println("Account Details");
        System.out.println("Balance:\t" + balance);
        customerStocks.viewStocks();
    }

    //todo : add profit and loss statistics

    /**
     *
     * @Description: buy stocks:
     */

    public void buyStock(Stock stock_origin, int num) {
        if(num<=0) {
            throw new RuntimeException("nums must be larger than 0");
        }
        else{
            if(balance < stock_origin.getCurrentPrice()*num) {
                throw new RuntimeException("You don't have enough money to buy this stock");
            }
            Stock stock=StockFactory.copyStock_withPriceBoughtAt(stock_origin, stock_origin.getCurrentPrice());
            this.customerStocks.add(stock, num);
            this.updateBalance(stock.getCurrentPrice()*num,"b");
            stockMap.put(stock, stock_origin);
        }
    }


    public void sellStock(Stock stock_origin, int num) {
        if(num<=0) {
            throw new RuntimeException("nums must be larger than 0");
        } else {
            int wholeNum= customerStocks.getOriginalStockNum(stock_origin);
            if(wholeNum < num) {
                throw new RuntimeException("You only have " + wholeNum + " stocks of this type");
            }
            List<Stock> stocks = customerStocks.getStocks(stock_origin);
            // remove every stock in the list until the num is 0
            HashMap<Stock, Integer> stockNumMap = customerStocks.getStocks();
            for(Stock stock: stocks) {
                if(num==0) {
                    break;
                }
                int stockNum = stockNumMap.get(stock);
                if(stockNum <= num) {
                    customerStocks.remove(stock,num);
                    num -= stockNum;
                }
                else {
                    customerStocks.remove(stock, num);
                    num = 0;
                }
            }
        }
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
