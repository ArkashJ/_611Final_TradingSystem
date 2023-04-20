package main.Persons;

public class Person {
    private String userName;
    private String password;

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

    public void viewAccountDetails(String userName, String password) {
        if (this.userName.equals(userName) && this.password.equals(password)) {
            System.out.println("Account Details:");
            System.out.println("User Name: " + userName);
            System.out.println("Account Number: " + accountNumber);
            System.out.println("Account Type: " + accountType);
        } else {
            System.out.println("Incorrect username or password");
        }
    }
}
