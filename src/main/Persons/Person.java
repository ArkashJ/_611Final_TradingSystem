package main.Persons;

public class Person implements IPerson {
    protected String userName;
    protected String password;

    public Person(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public boolean transfer(double amount, String accountNumber) {
        return false;
    }

    public boolean authenticate(String userName, String password) {
        return this.userName.equals(userName) && this.password.equals(password);
    }
}
