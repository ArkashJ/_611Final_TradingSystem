package main.Persons;

import main.Enums.UserType;

public class Manager extends Person{
    private long accountNumber;
    private UserType accountType;
    public Manager(String userName, String password,long accountNumber, UserType accountType) {
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
