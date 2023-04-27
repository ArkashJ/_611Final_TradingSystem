import main.Database.Database;
import main.FrontEnd.ManagerPage;
import main.FrontEnd.UserLoginRegistration;
import main.Initiator.Initiator;
import main.Stocks.Market;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {

//        Initiator.initiateAll();

        //build the ManagerPage

        SwingUtilities.invokeLater(() -> {
//            new UserLoginRegistration().run();
            new ManagerPage().run();
        });
    }
}
