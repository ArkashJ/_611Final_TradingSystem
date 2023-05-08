package main.Database;

import main.Accounts.OptionsAccount;
import main.Accounts.TradingAccount;
import main.Accounts.TradingAccountFactory;
import main.Enums.UserType;
import main.Stocks.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
    * @Description: This class is used to connect to the database and perform operations on it
        * Singleton class
        * Has a static method getInstance() which returns the instance of the class
    * Has a static method getConnection() which returns the connection to the database
    * @Methods:
    * connect() - connects to the database
    * deleteAllTables() - deletes all the tables in the database
    * createTables() - creates all the tables in the database
    * checkLogin(String username, String password) - checks if the username and password are correct
    * registerUser(String username, String password, UserType userType) - registers a new user
    * getAccountNumber(String username) - returns the account number of the user
    * getTradingAccountsForUser(String username) - returns a list of trading accounts for the user
    * getOptionsAccountsForUser(String username) - returns a list of options accounts for the user
    * getStocksForUser(String username) - returns a list of stocks for the user
    * insertStock(Stock stock) - inserts a stock into the database
    * insertAccounts(int account_number, String userName, double balance,String accountType) - inserts an account into the database
    * insertStockIntoMarket(String stockName, int quantity) - inserts a stock into the market
    * insertStockIntoCustomerStocks(int account_number,String stockName,double price_bought, int quantity) - inserts a stock into the customer stocks
    * submitAccountRequest(String username, String accountType) - submits an account request'
    * removeAccountRequest(int id) - removes an account request
 */



public class Database {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/trading_system";
    private static final String uname = "root";
    private static final String upassword = "distinctive0930";

    private static Database dbInstance = new Database();
    private static Connection conn;

    // ------------------ Constructor ------------------
    private Database() {
        conn = connect();
    }

    public static synchronized Database getInstance() {
        return dbInstance;
    }

    // ----------------- Connection -----------------
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

    // ----------------- Delete Tables -----------------
    public static void deleteAllTables() {
        String sql = "DROP TABLE IF EXISTS users, accounts, stocks, customer_stocks,market,log,account_requests";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


     // ----------------- Create Tables -----------------
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

        String accountRequestsTable = "CREATE TABLE IF NOT EXISTS account_requests ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "user_name VARCHAR(255) NOT NULL,"
                + "initial_balance DOUBLE NOT NULL,"
                + "account_type ENUM('TRADE', 'OPTIONS') NOT NULL,"
                + "FOREIGN KEY (user_name) REFERENCES users(name) ON DELETE CASCADE)";


        try (Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(accountsTable);
            stmt.execute(stocksTable);
            stmt.execute(customerStocksTable);
            stmt.execute(market);
            stmt.execute(log);
            stmt.execute(accountRequestsTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

     // ----------------- CheckLogin -----------------
    public static boolean checkLogin1(String name, String password) {
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

    // ----------------- CheckLogin -----------------
    public static UserType checkLogin(String name, String password) {
        synchronized (conn) {
            try {
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE name = ? AND password = ?");
                statement.setString(1, name);
                statement.setString(2, password);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return UserType.valueOf(resultSet.getString("account_type").toUpperCase());
                }
                return null;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

     // ----------------- Register A User -----------------
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

    // ----------------- Create A Trading Account -----------------
    /**
     * get
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

    // ----------------- Get Stock -----------------
    //get stock from stock table
    public static Stock getStock(String stockName) {
        Stock stock = null;

        if (stockName.equals(null)) return null;

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

    // ----------------- Get Customer Stocks -----------------
    //get CustomerStocks
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

    // ----------------- Set Market Stocks -----------------
    //get all Market Stocks
    public static void setMarketStocks() {
        String sql = "SELECT stock, quantity FROM market";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            List<MarketStock> marketStocks = new ArrayList<>();
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String stockName = resultSet.getString("stock");
                int quantity = resultSet.getInt("quantity");
                MarketStock stock = new MarketStock(stockName,quantity);
                marketStocks.add(stock);
            }
            Market.setStocks(marketStocks);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----------------- Get Trading Account -----------------
    //get a TradingAccount
    public static TradingAccount getTradingAccount(int account_number) {
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

    // ----------------- Get Trading Account for this User -----------------
    //get all TradingAccount given a userName
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

     

     // ----------------- Get Options Account -----------------
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

   


    //-------------------------------------------------------------------------------------------------------
    // -- INSERTS --
    // ----------------- Insert Stock -----------------
    public static void insertStock(String stockName, String companyName, double currentPrice, double lastClosingPrice, double highestPrice, double lowestPrice, int dividend) {
        String sql = "INSERT INTO stocks (name, companyName, currentPrice, lastClosingPrice, highestPrice, lowestPrice, dividend) VALUES (?, ?, ?, ?, ?, ?, ?)";
        // dont put conn in try-with-resources, otherwise it will be closed before we can use it
        Connection conn = Database.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stockName);
            pstmt.setString(2, companyName);
            pstmt.setDouble(3, currentPrice);
            pstmt.setDouble(4, lastClosingPrice);
            pstmt.setDouble(5, highestPrice);
            pstmt.setDouble(6, lowestPrice);
            pstmt.setInt(7, dividend);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ----------------- Insert User -----------------
    public static void insertUsers(String userName,String password,int account_number,String accountType) {
        String sql= "INSERT INTO users (name, password, account_number, account_type) VALUES (?, ?, ?, ?)";
        Connection conn = Database.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.setInt(3, account_number);
            pstmt.setString(4, accountType);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ----------------- Insert Account -----------------
    public static void insertAccounts(int account_number, String userName, double balance,String accountType) {
        String sql = "INSERT INTO accounts (account_number, user_name, balance, account_type) VALUES (?, ?, ?, ?)";
        Connection conn = Database.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, account_number);
            pstmt.setString(2, userName);
            pstmt.setDouble(3, balance);
            pstmt.setString(4, accountType);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ----------------- Insert Stocks in Market -----------------
    public static void insertStockIntoMarket(String stockName, int quantity) {
        String checkStockSql = "SELECT * FROM market WHERE stock = ?";
        String updateQuantitySql = "UPDATE market SET quantity = quantity + ? WHERE stock = ?";
        String insertStockSql = "INSERT INTO market (stock, quantity) VALUES (?, ?)";

        Connection conn = Database.getConnection();

        try (PreparedStatement checkStockStmt = conn.prepareStatement(checkStockSql);
             PreparedStatement updateQuantityStmt = conn.prepareStatement(updateQuantitySql);
             PreparedStatement insertStockStmt = conn.prepareStatement(insertStockSql)) {

            // Check if the stock exists in the market
            checkStockStmt.setString(1, stockName);
            ResultSet rs = checkStockStmt.executeQuery();

            if (rs.next()) {
                // If stock exists, update the quantity
                updateQuantityStmt.setInt(1, quantity);
                updateQuantityStmt.setString(2, stockName);
                updateQuantityStmt.executeUpdate();
            } else {
                // If stock doesn't exist, insert a new record
                insertStockStmt.setString(1, stockName);
                insertStockStmt.setInt(2, quantity);
                insertStockStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

     // ----------------- Insert Stocks in Customer Stocks -----------------
    public static void insertStockIntoCustomerStocks(int account_number,String stockName,double price_bought, int quantity) {
        String sql= "INSERT INTO customer_stocks (account_number, stock, price_bought, quantity) VALUES (?, ?, ?, ?)";
        Connection conn = Database.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, account_number);
            pstmt.setString(2, stockName);
            pstmt.setDouble(3, price_bought);
            pstmt.setInt(4, quantity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


     /**
     *  submit request to request table
     */
    // ----------------- Submit Request -----------------
    public static boolean submitAccountRequest(String userName, double initialBalance, String type) {
        synchronized (conn) {
            try {
                PreparedStatement statement = conn.prepareStatement("INSERT INTO account_requests (user_name, initial_balance,account_type) VALUES (?, ?,?)");
                statement.setString(1, userName);
                statement.setDouble(2, initialBalance);
                statement.setString(3, type);
                int rowsAffected = statement.executeUpdate();
                return rowsAffected == 1;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

     /**
     *  remove request by id
     */
    // ----------------- Remove Request -----------------
    public static boolean removeAccountRequest(int id) {
        synchronized (conn) {
            try {
                PreparedStatement statement = conn.prepareStatement("DELETE FROM account_requests WHERE id = ?");
                statement.setInt(1, id);
                int rowsAffected = statement.executeUpdate();
                return rowsAffected == 1;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }
}
