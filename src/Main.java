import main.Database.Database;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection conn= Database.connect();
        Database.deleteAllTables(conn);
        Database.createTables(conn);
    }
}
