package main.Database;

import main.Enums.UserType;

import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/trading_system";
    private static final String uname = "root";
    private static final String upassword = "distinctive0930";

    private static Database dbInstance = new Database();
    private static Connection conn;

    private Database() {
        conn = connect();
    }

    public static synchronized Database getInstance() {
        return dbInstance;
    }

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, uname, upassword);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void deleteAllTables() {
        String sql = "DROP TABLE IF EXISTS users, accounts, stocks, customer_stocks";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void createTables() {
        String usersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "name VARCHAR(255) NOT NULL UNIQUE,"
                + "password VARCHAR(255) NOT NULL,"
                + "account_number INTEGER NOT NULL UNIQUE," // 添加 UNIQUE 约束
                + "account_type ENUM('ADMIN', 'USER') NOT NULL,";

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

    public static boolean checkLogin(String name, String password) {
        synchronized (conn) {
            try {
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE name = ? AND password = ?");
                statement.setString(1, name);
                statement.setString(2, password);

                ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public static boolean registerUser(String name, String password, UserType account_type, int accountNumber) {
        synchronized (conn) {
            try {
                PreparedStatement statement = conn.prepareStatement("INSERT INTO users (name, password, account_type, account_number) VALUES (?, ?, ?, ?)");
                statement.setString(1, name);
                statement.setString(2, password);
                statement.setString(3, account_type.name());
                statement.setInt(4, accountNumber);

                int result = statement.executeUpdate();
                return result > 0;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }


}
