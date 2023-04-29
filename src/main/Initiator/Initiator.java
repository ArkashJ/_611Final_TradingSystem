package main.Initiator;

import main.Database.Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

/*
    * @Description: This class represents the initiator of the database, a singleton class
    * @Methods: initiateAll() : initiate the database
    *           loadStocksFromFile(String path) : load stocks from file
    *           insertMarketData() : insert market data
    *           loadUsersFromFile(String path) : load users from file
    *           loadAccountsFromFile(String path) : load accounts from file
    *           loadCustomerStocksFromFile(String path) : load customer stocks from file
 */
public class Initiator {
    //singleton
    private static Initiator initiator = new Initiator();
    private Initiator() {}
    public static Initiator getInstance() {
        return initiator;
    }

    // ----------------- Initiate the database -----------------å
    public static void initiateAll() {
        boolean reset = true;
        // the directory of the txt files
        String dir_path = "src/main/txtfiles/";
        // if the database is already initiated, don't reset it
        // on a reset request from the user, delete all tables and make a new one
        if(reset) {
            // delete all tables and make a new one
            Database.deleteAllTables();
            Database.createTables();
            //initialize the database

            //1. stocks.txt
            loadStocksFromFile(dir_path + "stocks.txt");
            //2. market
            insertMarketData();
            //3. load users
            loadUsersFromFile(dir_path + "users.txt");
            //3. load accounts
            loadAccountsFromFile(dir_path + "accounts.txt");
            //4. load CustomerStocks
            loadCustomerStocksFromFile(dir_path + "customer_stocks.txt");
        }
    }

    // ----------------- Load Stocks from file -----------------
    public static void loadStocksFromFile(String filePath) {
        Path file = Paths.get(filePath);
        try (Stream<String> lines = Files.lines(file)) {
            lines.forEach(line -> {
                // Split the line by comma
                String[] stockData = line.split(",");
                // get the stock and company name
                String stockName = stockData[0];
                String companyName = stockData[1];
                // Get the variables: currentPrice, lastClosingPrice, highestPrice, lowestPrice, dividend
                double currentPrice = Double.parseDouble(stockData[2]);
                double lastClosingPrice = Double.parseDouble(stockData[3]);
                double highestPrice = Double.parseDouble(stockData[4]);
                double lowestPrice = Double.parseDouble(stockData[5]);
                int dividend = Integer.parseInt(stockData[6]);
                // Insert the stock into the database
                Database.insertStock(stockName, companyName, currentPrice, lastClosingPrice, highestPrice, lowestPrice, dividend);
            });
        } catch (IOException e) {
            // throw error if file not found
            e.printStackTrace();
        }
    }

    // ----------------- Insert Data into the Market -----------------
    public static void insertMarketData() {
        String selectStocksSql = "SELECT name FROM stocks";
        Connection conn = Database.getConnection();
        try (PreparedStatement selectStocksStmt = conn.prepareStatement(selectStocksSql)) {
            ResultSet rs = selectStocksStmt.executeQuery();

            while (rs.next()) {
                String stockName = rs.getString("name");
                // Insert stock data into market table
                Database.insertStockIntoMarket(stockName,1000);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ----------------- Load Accounts from file -----------------
    public static void loadAccountsFromFile(String filePath) {
        Path file = Paths.get(filePath);
        try (Stream<String> lines = Files.lines(file)) {
            lines.forEach(line -> {
                String[] data = line.split(",");
                int account_number = Integer.parseInt(data[0]);
                String userName = data[1];
                double balance = Double.parseDouble(data[2]);
                String accountType = data[3];
                Database.insertAccounts(account_number, userName, balance, accountType);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------- Load Users from file -----------------
    public static void loadUsersFromFile(String filePath) {
        Path file = Paths.get(filePath);
        try (Stream<String> lines = Files.lines(file)) {
            lines.forEach(line -> {
                String[] data = line.split(",");
                String userName = data[1];
                String password = data[2];
                int account_number = Integer.parseInt(data[3]);
                String accountType = data[4];
                Database.insertUsers(userName, password, account_number, accountType);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------- Load CustomerStocks from file -----------------
    public static void loadCustomerStocksFromFile(String filePath) {
        Path file = Paths.get(filePath);
        try (Stream<String> lines = Files.lines(file)) {
            lines.forEach(line -> {
                String[] data = line.split(",");
                int account_number = Integer.parseInt(data[1]);
                String stockName = data[2];
                double price = Double.parseDouble(data[3]);
                int quantity = Integer.parseInt(data[4]);
                Database.insertStockIntoCustomerStocks(account_number, stockName,price, quantity);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
