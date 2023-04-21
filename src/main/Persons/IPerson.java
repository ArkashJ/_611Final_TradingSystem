package main.Persons;

public interface IPerson {
//    boolean deposit(double amount);
//    boolean withdraw(double amount);
    boolean transfer(double amount, String accountNumber);
    boolean authenticate(String userName, String password);
}
