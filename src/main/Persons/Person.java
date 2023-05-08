package main.Persons;

/*
   * @Description: Defines a person with a username and password
   * @Methods: getUserName() : return the username
   *          getPassword() : return the password
   *          setPassword(String password) : set the password
   *          authenticate(String userName, String password) : authenticate the user
   *          transfer(double amount, String accountNumber) : transfer money to another account
 */

public class Person implements IPerson {
    protected String userName;
    protected String password;

    // ----------------- Constructor -----------------
    public Person(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    // ----------------- Getters and Setters -----------------
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    // ----------------- Transfer & Authenticate -----------------
    @Override
    public boolean transfer(double amount, String accountNumber) {
        return false;
    }

    public boolean authenticate(String userName, String password) {
        return this.userName.equals(userName) && this.password.equals(password);
    }
}
