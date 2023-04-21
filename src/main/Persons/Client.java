package main.Persons;

import main.Accounts.OptionsAccount;
import main.Accounts.TradingAccount;
import main.Accounts.TradingAccountFactory;
import main.Stocks.CustomerStocks;

import java.util.List;
import java.util.Random;

public class Client extends Person{
    private long accountNumber;
    private String accountType;
    private List<TradingAccount> accounts;
    private List<OptionsAccount> optionsAccounts;
    private Random r=new Random();

    public Client(String userName, String password,long accountNumber, String accountType) {

        super(userName, password);
        this.accountNumber = accountNumber;
        this.accountType = accountType;
    }

    public void createAccount(){
        if (authenticate( getUserName(), getPassword())){
            // todo: create a new account with different ID
            long accountNumber = r.nextLong();
            TradingAccount account = TradingAccountFactory.createTradingAccount(getUserName(), new CustomerStocks(), 0, accountNumber, accountType);
            accounts.add(account);
        }
        else {
            System.out.println("Invalid username or password");
        }
    }

    public void displayAccounts(){
        if (authenticate( getUserName(), getPassword())){
            for(TradingAccount account : accounts){
                account.viewAccountDetails();
            }
        }
        else {
            System.out.println("Invalid username or password");
        }
    }

    public List<TradingAccount> getTradingAccounts(){
        return this.accounts;
    }

    public List<OptionsAccount> getOptionsAccounts(){
        return this.optionsAccounts;
    }

    public boolean transfer(double amount, long accountNumber_from,long accountNumber_to) {
        TradingAccount from = null;
        TradingAccount to = null;
        // get from and to
        for(TradingAccount account : accounts){
            if(account.getAccountNumber() == accountNumber_from){
                from = account;
            }
            if(account.getAccountNumber() == accountNumber_to){
                to = account;
            }
        }
        if(from == null || to == null){
            return false;
        }
        if(from.withdraw(amount)){
            to.deposit(amount);
            return true;
        }
        return false;
    }
    @Override
    public boolean authenticate(String userName, String password) {
        return this.userName.equals(userName) && this.password.equals(password);
    }


}
