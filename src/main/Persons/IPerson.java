package main.Persons;

/*
    * @Description: This interface represents a person
    * @Methods: transfer(double amount, String accountNumber) : transfer money to another account
    *           authenticate(String userName, String password) : authenticate the user
 */
public interface IPerson {
    boolean transfer(double amount, String accountNumber);
    boolean authenticate(String userName, String password);
}
