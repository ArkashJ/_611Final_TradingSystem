package main.Persons;

public class CustomerFactory {
    // Factory design pattern: create Client, Manager, or Person
    public static Person createCustomer(String type) {
        if (type.equals("Client")) {
            return new Client();
        } else if (type.equals("Manager")) {
            return new Manager();
        } else {
            return new Person();
        }
    }

}
