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

    public boolean authenticate(String userName, String password) {
        return this.userName.equals(userName) && this.password.equals(password);
    }
}
