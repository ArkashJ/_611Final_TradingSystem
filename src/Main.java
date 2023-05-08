import main.Database.Database;
import main.FrontEnd.ManagerPage;
import main.FrontEnd.UserLoginRegistration;
import main.Initiator.Initiator;
import main.Stocks.Market;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

/*
 * @Description: This is the main class of the program
 * Initiator loads the data from all the txt files into a format readable by the database
 * The Stocks are put into a table
 * Then the main Login/Registration page is built 
 */

public class Main {
    public static void main(String[] args) throws SQLException {
        
        // Load the data from the txt files into the database
        Initiator.initiateAll(false);

        SwingUtilities.invokeLater(() -> {
            // One page for registering manager
            new UserLoginRegistration().run();
            // One page for registering client
            new UserLoginRegistration().run();
        });
    }
}
