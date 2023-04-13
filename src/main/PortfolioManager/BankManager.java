package main.PortfolioManager;

public class BankManager {
    // make this a singleton
    private static BankManager instance = null;

    private BankManager() {
    }

    public static BankManager getInstance() {
        if (instance == null) {
            instance = new BankManager();
        }
        return instance;
    }

    public void deposit(double amount) {

    }
}
