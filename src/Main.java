import main.Database.Database;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Database.deleteAllTables();
        Database.createTables();
    }
}
