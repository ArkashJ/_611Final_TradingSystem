package main.Database;

import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/trading_system";
    private static final String uname = "root";
    private static final String upassword = "distinctive0930";

//    public static void main(String[] args) throws Exception {
//        Class.forName("com.mysql.cj.jdbc.Driver");
//        Connection conn = DriverManager.getConnection(DB_URL, uname, upassword);
//        createTables(conn);
//        Statement stmt = conn.createStatement();
//        String sql = "SELECT * FROM users";
//        ResultSet rs = stmt.executeQuery(sql);
//        while (rs.next()) {
//            System.out.println(rs.getString("username"));
//        }
//        rs.close();
//        stmt.close();
//        conn.close();
//    }

    private static Database jb=new Database();
    private Database() {}

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, uname, upassword);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void deleteAllTables(Connection conn) {
        String sql = "DROP TABLE IF EXISTS users, accounts, stocks, customer_stocks";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void createTables(Connection conn) {
        String usersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "username VARCHAR(255) NOT NULL UNIQUE,"
                + "password VARCHAR(255) NOT NULL,"
                + "account_number INTEGER NOT NULL UNIQUE," // 添加 UNIQUE 约束
                + "account_type VARCHAR(255) NOT NULL,"
                + "role ENUM('admin', 'user') NOT NULL);";

        String accountsTable = "CREATE TABLE IF NOT EXISTS accounts (\n"
                + "	id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + "	user_id INTEGER NOT NULL,\n"
                + "	balance DOUBLE NOT NULL,\n"
                + "	FOREIGN KEY (user_id) REFERENCES users (id)\n"
                + ");";

        String stocksTable = "CREATE TABLE IF NOT EXISTS stocks (\n"
                + "	id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + "	name VARCHAR(255) NOT NULL,\n"
                + "	symbol VARCHAR(255) NOT NULL,\n"
                + "	price DOUBLE NOT NULL\n"
                + ");";

        String customerStocksTable = "CREATE TABLE IF NOT EXISTS customer_stocks (\n"
                + "	account_number INTEGER NOT NULL,\n"
                + "	symbol VARCHAR(255) NOT NULL,\n"
                + "	quantity INTEGER NOT NULL,\n"
                + "	PRIMARY KEY (account_number, symbol)\n"
                + ");";
        //                + "	FOREIGN KEY (account_number) REFERENCES users (account_number),\n" // 更新 FOREIGN KEY 引用
//                + "	FOREIGN KEY (symbol) REFERENCES stocks (symbol)\n"

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(accountsTable);
            stmt.execute(stocksTable);
            stmt.execute(customerStocksTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


}
