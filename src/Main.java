import main.Database.Database;
import main.FrontEnd.UserLoginRegistration;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Database.deleteAllTables();
        Database.createTables();
        UserLoginRegistration.run();
    }
}
