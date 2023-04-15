package main.Accounts;

//Used to create a trading account object

public class TradingAccountFactory {

    public static TradingAccount createTradingAccount(String ownerName, long accountNumber, String accountType, String userName, String password) {
        return new TradingAccount(ownerName, accountNumber, accountType, userName, password);
    }
}
