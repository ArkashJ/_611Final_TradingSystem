package main.Database;

import main.Accounts.OptionsAccount;
import main.Accounts.TradingAccount;
import main.Accounts.TradingAccountFactory;
import main.Enums.UserType;
import main.Stocks.CustomerStocks;
import main.Stocks.Stock;
import main.Stocks.StockFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public static Connection getConnection() {
        return conn;
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
        String sql = "DROP TABLE IF EXISTS users, accounts, stocks, customer_stocks,market,log";
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
                + "account_number INTEGER NOT NULL UNIQUE,"
                + "account_type ENUM('ADMIN', 'USER') NOT NULL)";

        String accountsTable = "CREATE TABLE IF NOT EXISTS accounts (\n"
                + "	account_number INT AUTO_INCREMENT PRIMARY KEY,\n"
                + "	user_name VARCHAR(255) NOT NULL,\n"
                + "	balance DOUBLE NOT NULL,\n"
                + "account_type ENUM('TRADE', 'OPTIONS') NOT NULL,\n"
                + "	FOREIGN KEY (user_name) REFERENCES users (name)\n"
                + ");";

        String stocksTable = "CREATE TABLE IF NOT EXISTS stocks (\n"
                + "	name VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY,\n"
                + "	companyName VARCHAR(255) NOT NULL,\n"
                + "	currentPrice DOUBLE NOT NULL,\n"
                + "lastClosingPrice DOUBLE NOT NULL,\n"
                + "	highestPrice DOUBLE NOT NULL,\n"
                + "	lowestPrice DOUBLE NOT NULL,\n"
                + "	dividend INTEGER NOT NULL\n"
                + ");";

        // stock can be duplicated in customer_stocks table
        String customerStocksTable = "CREATE TABLE IF NOT EXISTS customer_stocks (\n"
                + "id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + "account_number INTEGER NOT NULL,\n"
                + "stock VARCHAR(255) NOT NULL,\n"
                + "price_bought DOUBLE NOT NULL,\n"
                + "FOREIGN KEY (account_number) REFERENCES accounts (account_number),\n"
                + "FOREIGN KEY (stock) REFERENCES stocks (name),\n"
                + "	quantity INTEGER NOT NULL\n"
                + ");";

        String market="CREATE TABLE IF NOT EXISTS market (\n"
                + "id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + "stock VARCHAR(255) NOT NULL,\n"
                + "quantity INTEGER NOT NULL,\n"
                + "FOREIGN KEY (stock) REFERENCES stocks (name)\n"
                + ");";

        String log="CREATE TABLE IF NOT EXISTS log (\n"
                + "id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + "account_number INTEGER NOT NULL,\n"
                + "stock VARCHAR(255) NOT NULL,\n"
                + "price DOUBLE NOT NULL,\n"
                + "quantity INTEGER NOT NULL,\n"
                + "type ENUM('BUY', 'SELL') NOT NULL,\n"
                + "time TIMESTAMP NOT NULL,\n"
                + "FOREIGN KEY (account_number) REFERENCES accounts (account_number),\n"
                + "FOREIGN KEY (stock) REFERENCES stocks (name)\n"
                + ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(accountsTable);
            stmt.execute(stocksTable);
            stmt.execute(customerStocksTable);
            stmt.execute(market);
            stmt.execute(log);
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

    /**
     String accountsTable = "CREATE TABLE IF NOT EXISTS accounts (\n"
     + "	account_number INTEGER NOT NULL UNIQUE PRIMARY KEY,\n"
     + "	user_name VARCHAR(255) NOT NULL,\n"
     + "	balance DOUBLE NOT NULL,\n"
     + "account_type ENUM('TRADE', 'OPTIONS') NOT NULL,\n"
     + "	FOREIGN KEY (user_name) REFERENCES users (name)\n"
     + ");";
     */
    public static boolean createTradingAccount(String userName,double balance) {
        synchronized (conn) {
            try {
                PreparedStatement statement = conn.prepareStatement("INSERT INTO accounts (user_name, balance, account_type) VALUES (?, ?, ?)");
                statement.setString(1, userName);
                statement.setDouble(2, balance);
                statement.setString(3, "TRADE");
                int result = statement.executeUpdate();
                return result > 0;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    //get stock from stock table
    public static Stock getStock(String stockName) {
        Stock stock = null;

        String sql = "SELECT * FROM stocks WHERE name = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stockName);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String companyName = resultSet.getString("companyName");
                double currentPrice = resultSet.getDouble("currentPrice");
                double lastClosingPrice = resultSet.getDouble("lastClosingPrice");
                double highestPrice = resultSet.getDouble("highestPrice");
                double lowestPrice = resultSet.getDouble("lowestPrice");
                int dividend = resultSet.getInt("dividend");

                stock = StockFactory.createStock(name, companyName, currentPrice,lastClosingPrice, highestPrice, lowestPrice, dividend);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stock;
    }

    //get CustomerStocks
    //to get user's certain stock, first get its CustomerStocks, then get its stock by the hashmap
    public static CustomerStocks getCustomerStocks(int accountNumber) {
        CustomerStocks cs=new CustomerStocks(accountNumber);
        String sql = "SELECT stock, quantity,price_bought FROM customer_stocks WHERE account_number = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountNumber);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String stockName = resultSet.getString("stock");
                int quantity = resultSet.getInt("quantity");
                double price_bought = resultSet.getDouble("price_bought");
                cs.add(stockName,quantity,price_bought);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cs;
    }
    public static boolean setCustomerStocks(int accountNumber, String stockName, int quantity, double transactionPrice, boolean isBuy) {
        boolean success = false;
        try {
            if (isBuy) {
                // Buy stocks
                String buySql = "INSERT INTO customer_stocks (account_number, stock, quantity, price_bought) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(buySql)) {
                    pstmt.setInt(1, accountNumber);
                    pstmt.setString(2, stockName);
                    pstmt.setInt(3, quantity);
                    pstmt.setDouble(4, transactionPrice);
                    int rowsAffected = pstmt.executeUpdate();
                    success = rowsAffected > 0;
                }
            } else {
                // Sell stocks
                String sellSql = "SELECT id, quantity, price_bought FROM customer_stocks WHERE account_number = ? AND stock = ? ORDER BY price_bought ASC";
                try (PreparedStatement pstmt = conn.prepareStatement(sellSql)) {
                    pstmt.setInt(1, accountNumber);
                    pstmt.setString(2, stockName);
                    ResultSet resultSet = pstmt.executeQuery();

                    int remainingQuantityToSell = quantity;
                    while (resultSet.next() && remainingQuantityToSell > 0) {
                        int stockId = resultSet.getInt("id");
                        int stockQuantity = resultSet.getInt("quantity");
                        int newQuantity = Math.max(0, stockQuantity - remainingQuantityToSell);

                        String updateSql = "UPDATE customer_stocks SET quantity = ? WHERE id = ?";
                        try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                            updatePstmt.setInt(1, newQuantity);
                            updatePstmt.setInt(2, stockId);
                            updatePstmt.executeUpdate();
                        }

                        if (newQuantity == 0) {
                            String deleteSql = "DELETE FROM customer_stocks WHERE id = ?";
                            try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
                                deletePstmt.setInt(1, stockId);
                                deletePstmt.executeUpdate();
                            }
                        }

                        remainingQuantityToSell -= (stockQuantity - newQuantity);
                    }

                    success = remainingQuantityToSell == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }


    //get a TradingAccount
    public static TradingAccount getTraddingAccount(int account_number) {
        TradingAccount tradingAccount = null;

        String sql = "SELECT * FROM accounts WHERE account_number = ? AND account_type = 'TRADE';";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, account_number);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                int accountNumber = resultSet.getInt("account_number");
                String userName = resultSet.getString("user_name");
                double balance = resultSet.getDouble("balance");
                CustomerStocks cs=getCustomerStocks(accountNumber);
                tradingAccount = TradingAccountFactory.createTradingAccount(userName,cs,balance,account_number);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tradingAccount;
    }

    //get all TradingAccount
    public static List<TradingAccount> getTradingAccountsForUser(String userName) {
        List<TradingAccount> tradingAccounts = new ArrayList<>();

        String sql = "SELECT * FROM accounts WHERE user_name = ? AND account_type = 'TRADE';";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                int accountNumber = resultSet.getInt("account_number");
                double balance = resultSet.getDouble("balance");
                CustomerStocks cs=getCustomerStocks(accountNumber);
                TradingAccount tradingAccount = TradingAccountFactory.createTradingAccount(userName,cs,balance,accountNumber);
                tradingAccounts.add(tradingAccount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tradingAccounts;
    }
    //get all OptionsAccount
    public static List<OptionsAccount> getOptionsAccountForUser(String userName) {
        List<OptionsAccount> tradingAccounts = new ArrayList<>();

        String sql = "SELECT * FROM accounts WHERE user_name = ? AND account_type = 'OPTIONS';";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                int accountNumber = resultSet.getInt("account_number");
                double balance = resultSet.getDouble("balance");
                CustomerStocks cs=getCustomerStocks(accountNumber);
                OptionsAccount tradingAccount = TradingAccountFactory.createOptionsAccount(userName,cs,balance,accountNumber);
                tradingAccounts.add(tradingAccount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tradingAccounts;
    }

}
