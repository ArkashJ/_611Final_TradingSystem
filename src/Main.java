import main.Database.Database;
import main.FrontEnd.UserLoginRegistration;
import main.Initiator.Initiator;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {

        boolean reset = true;

        if(reset) {
            Database.deleteAllTables();
            Database.createTables();
            //initialize the database
            //1. stocks
            Initiator.loadStocksFromFile("src/main/txtfiles/stocks");
            //2. market
            Initiator.insertMarketData();
        }
        else {
            SwingUtilities.invokeLater(() -> {
                new UserLoginRegistration().run();
            });
        }
    }
}
