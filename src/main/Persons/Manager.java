package main.Persons;

public class Manager extends Person{

        public Manager(String userName, String password) {
            super(userName, password);
        }

        public void returnAccountDetails(){
            if (authenticate( getUserName(), getPassword())){
            }
            else {
                System.out.println("Invalid username or password");
            }
        }
}
