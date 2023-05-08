package main.FrontEnd;

import main.Database.Database;

import javax.swing.*;

public class showPage {
    public static void main(String[] args) {
//        Database.deleteAllTables();
        Database.createTables();
        SwingUtilities.invokeLater(() -> {
            new UserLoginRegistration().run();
        });
    }
}
