package main.Persons;

import main.Enums.UserType;

/*
    * @Description: This class represents a manager, a subclass of Person
    * @Methods: returnAccountDetails() : return the account details
    * Manager extends Person and sets the account number and account type
 */

public class Manager extends Person{
    private long accountNumber;
    private UserType accountType;

    // ----------------- Constructor -----------------
    public Manager(String userName, String password,long accountNumber, UserType accountType) {
        super(userName, password);
        this.accountNumber = accountNumber;
        this.accountType = accountType;
    }

    // ----------------- Return the account details -----------------
    public void returnAccountDetails(){
        if (authenticate( getUserName(), getPassword())){}
        else {
            System.out.println("Invalid username or password");
        }
    }
}
