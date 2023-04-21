package main.Persons;

import main.Accounts.OptionsAccount;
import main.Accounts.TradingAccount;

import java.util.List;

public class Client extends Person{

    private List<TradingAccount> accounts;
    private List<OptionsAccount> optionsAccounts;

    public Client(String userName, String password) {
        super(userName, password);
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


}
