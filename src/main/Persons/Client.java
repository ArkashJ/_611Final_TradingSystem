package main.Persons;

import main.Accounts.OptionsAccount;
import main.Accounts.TradingAccount;
import main.Accounts.TradingAccountFactory;
import main.Enums.UserType;
import main.Stocks.CustomerStocks;
import java.util.List;
import java.util.Random;

/*
    * @Description: This class represents a client, a subclass of Person
    * @Methods:createAccount()      : create a new account
    *          displayAccounts()    : display all accounts
    *          getTradingAccounts() : get all trading accounts
    *          getOptionsAccounts() : get all options accounts
 */

public class Client extends Person{
    private long accountNumber;
    private UserType accountType;
    private List<TradingAccount> accounts;
    private List<OptionsAccount> optionsAccounts;
    private Random r=new Random();

    // ----------------- Constructor -----------------
    public Client(String userName, String password,long accountNumber, UserType accountType) {
        super(userName, password);
        this.accountNumber = accountNumber;
        this.accountType = accountType;
    }

    // ----------------- Create a new account -----------------
    public void createAccount(){
        // if the user is authenticated, create a new account
        if (authenticate( getUserName(), getPassword())){
            int accountNumber = r.nextInt();
            // call the trading account factory to create a new trading account
            TradingAccount account = TradingAccountFactory.createTradingAccount(getUserName(), new CustomerStocks(accountNumber), 0, accountNumber);
            // add the account to the list of accounts
            accounts.add(account);
        }
        else {
            System.out.println("Invalid username or password");
        }
    }

    // ----------------- Display all accounts -----------------
    public void displayAccounts(){
        try {
            if (authenticate(getUserName(), getPassword())) {
                for (TradingAccount account : accounts) {
                    account.viewAccountDetails();
                }
            } else {
                System.out.println("Invalid username or password");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ----------------- Getters for Trading and Options Accounts -----------------
    public List<TradingAccount> getTradingAccounts(){
        return this.accounts;
    }

    public List<OptionsAccount> getOptionsAccounts(){
        return this.optionsAccounts;
    }

    // ------------------ Transfer money between accounts ------------------
    public boolean transfer(double amount, long accountNumber_from,long accountNumber_to) {
        TradingAccount from = null;
        TradingAccount to = null;

        try {
            // get from and to
            for (TradingAccount account : accounts) {
                // find the account to transfer from
                if (account.getAccountNumber() == accountNumber_from) {
                    from = account;
                }
                // find the account to transfer to
                if (account.getAccountNumber() == accountNumber_to) {
                    to = account;
                }
            }
            // check if from and to are valid
            if (from == null || to == null) {
                return false;
            }
            // check if the transfer is successful
            if (from.withdraw(amount)) {
                to.deposit(amount);
                return true;
            }
            // return false if the transfer is unsuccessful
            return false;
        } catch (Exception e) {
            // throw an error if error in getting the account, or error in transfer
            System.out.println(e.getMessage());
            return false;
        }
    }

    // ------------------ Authentication ------------------
    @Override
    public boolean authenticate(String userName, String password) {
        return this.userName.equals(userName) && this.password.equals(password);
    }

}
