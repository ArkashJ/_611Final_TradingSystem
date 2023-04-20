package main.Persons;

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
}
