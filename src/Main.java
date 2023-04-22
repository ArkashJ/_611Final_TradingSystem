import main.Database.Database;
import main.FrontEnd.UserLoginRegistration;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
//        Database.deleteAllTables();
        Database.createTables();
        SwingUtilities.invokeLater(() -> {
            new UserLoginRegistration().run();
        });
    }
}
