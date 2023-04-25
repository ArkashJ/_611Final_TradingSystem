package main.Persons;

public class Client extends Person{
    private long accountNumber;
    private UserType accountType;
    private List<TradingAccount> accounts;
    private List<OptionsAccount> optionsAccounts;
    private Random r=new Random();

    public Client(String userName, String password,long accountNumber, UserType accountType) {

        super(userName, password);
        this.accountNumber = accountNumber;
        this.accountType = accountType;
    }

    public void createAccount(){
        if (authenticate( getUserName(), getPassword())){
            // todo: create a new account with different ID
            int accountNumber = r.nextInt();
            TradingAccount account = TradingAccountFactory.createTradingAccount(getUserName(), new CustomerStocks(accountNumber), 0, accountNumber);
            accounts.add(account);
        }
        else {
            System.out.println("Invalid username or password");
        }
    }

    public void returnAccountDetails(){
        if (authenticate( getUserName(), getPassword())){
            System.out.println("Account Number: " + getAccountNumber());
            System.out.println("Account Type: " + getAccountType());
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
