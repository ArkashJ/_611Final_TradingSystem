package main.FrontEnd;

import main.Database.Database;

import javax.swing.*;


/**

The showPage class is the main class responsible for running the trading application.
It initializes the database and starts the UserLoginRegistration page.
*/


public class showPage {
    public static void main(String[] args) {
//        Database.deleteAllTables();
        Database.createTables();
        SwingUtilities.invokeLater(() -> {
            new UserLoginRegistration().run();
        });
    }
}
