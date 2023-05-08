package main.Persons;

import main.Enums.UserType;

/*
    * @Description: Customer Factor takes the type of the customer and creates a new customer
    * @Methods: createCustomer(String type,String userName, String password,long accountNumber, UserType accountType)
 */
public class CustomerFactory {
    // Factory design pattern: create Client, Manager, or Person
    public static Person createCustomer(String type,String userName, String password,long accountNumber, UserType accountType) {
        if (type.equals("Client")) {
            System.out.println("Client created");
            return new Client(userName, password,accountNumber,accountType);
        }
        if (type.equals("Manager")) {
            System.out.println("Manager created");
            return new Manager(userName, password,accountNumber,accountType);
        }
        System.out.println("Error, could not create the person entity");
        return null;
    }

}
