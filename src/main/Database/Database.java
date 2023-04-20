package main.Database;
import main.Accounts.TradingAccount;
import main.Persons.Client;
import main.Stocks.CustomerStocks;
import main.Stocks.Stock;

import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:trading_system.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createTables(){
        String usersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY,"
                + "username TEXT NOT NULL UNIQUE,"
                + "password TEXT NOT NULL,"
                + "account_number INTEGER NOT NULL,"
                + "account_type TEXT NOT NULL,"
                + "role TEXT NOT NULL);";


        String accountsTable = "CREATE TABLE IF NOT EXISTS accounts (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	user_id integer NOT NULL,\n"
                + "	balance real NOT NULL,\n"
                + "	FOREIGN KEY (user_id) REFERENCES users (id)\n"
                + ");";

        String stocksTable = "CREATE TABLE IF NOT EXISTS stocks (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + "	symbol text NOT NULL,\n"
                + "	price real NOT NULL\n"
                + ");";

        String customerStocksTable = "CREATE TABLE IF NOT EXISTS customer_stocks (\n"
                + "	account_number integer NOT NULL,\n"
                + "	symbol text NOT NULL,\n"
                + "	quantity integer NOT NULL,\n"
                + "	PRIMARY KEY (account_number, symbol),\n"
                + "	FOREIGN KEY (account_number) REFERENCES users (account_number),\n"
                + "	FOREIGN KEY (symbol) REFERENCES stocks (symbol)\n"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(accountsTable);
            stmt.execute(stocksTable);
            stmt.execute(customerStocksTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Client authenticate(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?;";
        Client user = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new Client(rs.getString("username"), rs.getString("password"),
                            rs.getLong("account_number"), rs.getString("account_type"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    public static TradingAccount getTradingAccount(long accountNumber) {
        String query = "SELECT * FROM accounts WHERE user_id = ?;";
        TradingAccount tradingAccount = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, accountNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String ownerName = rs.getString("owner_name");
                    double balance = rs.getDouble("balance");
                    CustomerStocks customerStocks = getCustomerStocks(accountNumber);
                    tradingAccount = new TradingAccount(ownerName, customerStocks, balance);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return tradingAccount;
    }

    public static CustomerStocks getCustomerStocks(long accountNumber) {
        String query = "SELECT * FROM customer_stocks WHERE account_number = ?;";
        CustomerStocks customerStocks = new CustomerStocks();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, accountNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String symbol = rs.getString("symbol");
                    int quantity = rs.getInt("quantity");
                    Stock stock = getStock(symbol);
                    if (stock != null) {
                        customerStocks.buyStock(stock, quantity);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return customerStocks;
    }

    public static Stock getStock(String symbol) {
        String query = "SELECT * FROM stocks WHERE symbol = ?;";
        Stock stock = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, symbol);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stock = new Stock(rs.getString("symbol"), rs.getString("name"), rs.getDouble("price"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return stock;
    }

    public static void addStock(Stock stock) {
        String query = "INSERT INTO stocks (name, symbol, price) VALUES (?, ?, ?);";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, stock.getName());
            pstmt.setString(2, stock.getSymbol());
            pstmt.setDouble(3, stock.getCurrentPrice());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
