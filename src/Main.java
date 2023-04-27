import main.Database.Database;
import main.FrontEnd.ManagerPage;
import main.FrontEnd.UserLoginRegistration;
import main.Initiator.Initiator;
import main.Stocks.Market;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * todo
 * 1. Add two button in AccountPage that allows User to withdraw and deposit money
 * 2. Add a authentication system for Manager to approve them to create a account ?
 * 3. Notify everyone if some one wins more than 10k and allow him to open a Optional Account
 */

public class Main {
    public static void main(String[] args) throws SQLException {

//        Initiator.initiateAll();

        //build the ManagerPage

        SwingUtilities.invokeLater(() -> {
            new UserLoginRegistration().run();
        });
    }
}
