package main.Persons;

public class Manager extends Person{
    private long accountNumber;
    private String accountType;
    public Manager(String userName, String password,long accountNumber, String accountType) {
        super(userName, password);
        this.accountNumber = accountNumber;
        this.accountType = accountType;
    }

    public void returnAccountDetails(){
        if (authenticate( getUserName(), getPassword())){}
        else {
            System.out.println("Invalid username or password");
        }
    }
}
