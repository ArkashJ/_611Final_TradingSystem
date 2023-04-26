import main.Database.Database;
import main.FrontEnd.UserLoginRegistration;
import main.Initiator.Initiator;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {

        boolean reset = true;

        String dir_path = "src/main/txtfiles/";

        if(reset) {
            Database.deleteAllTables();
            Database.createTables();
            //initialize the database

            //1. stocks.txt
            Initiator.loadStocksFromFile(dir_path+"stocks.txt");

            //2. market
            Initiator.insertMarketData();
//            System.out.println(Database.getConnection().isValid(2));

            //3. load users
            Initiator.loadUsersFromFile(dir_path+"users.txt");

            //3. load accounts
            Initiator.loadAccountsFromFile(dir_path+"accounts.txt");
        }
        else {
            SwingUtilities.invokeLater(() -> {
                new UserLoginRegistration().run();
            });
        }
    }
}
