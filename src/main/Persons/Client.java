package main.Persons;

import main.Accounts.TradingAccount;
import main.Database.Database;

public class Client extends Person{

    private long accountNumber;
    private String accountType;

    public Client(String userName, String password, long accountNumber, String accountType) {
        super(userName, password);
        this.accountNumber = accountNumber;
        this.accountType = accountType;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void returnAccountDetails() {
        Client authenticatedClient = Database.authenticate(getUserName(), getPassword());
        if (authenticatedClient != null) {
            TradingAccount tradingAccount = Database.getTradingAccount(authenticatedClient.getAccountNumber());
            if (tradingAccount != null) {
                tradingAccount.viewAccountDetails();
            } else {
                System.out.println("Account not found.");
            }
        } else {
            System.out.println("Invalid username or password");
        }
    }


}
