package main.Persons;

public class CustomerFactory {
    // Factory design pattern: create Client, Manager, or Person
    public static Person createCustomer(String type) {
        if (type.equals("Client")) {
            System.out.println("Client created");
            return new Client();
        }
        if (type.equals("Manager")) {
            System.out.println("Manager created");
            return new Manager();
        }
        System.out.println("Error, could not create the person entity");
        return null;
    }

}
