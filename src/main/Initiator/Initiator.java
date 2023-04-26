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

public class Initiator {
    //singleton
    private static Initiator initiator = new Initiator();
    private Initiator() {}
    public static Initiator getInstance() {
        return initiator;
    }
    public static void loadStocksFromFile(String filePath) {
        Path file = Paths.get(filePath);
        try (Stream<String> lines = Files.lines(file)) {
            lines.forEach(line -> {
                String[] stockData = line.split(",");
                String stockName = stockData[0];
                String companyName = stockData[1];
                double currentPrice = Double.parseDouble(stockData[2]);
                double lastClosingPrice = Double.parseDouble(stockData[3]);
                double highestPrice = Double.parseDouble(stockData[4]);
                double lowestPrice = Double.parseDouble(stockData[5]);
                int dividend = Integer.parseInt(stockData[6]);
                insertStock(stockName, companyName, currentPrice, lastClosingPrice, highestPrice, lowestPrice, dividend);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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
    public static void insertMarketData() {
        String selectStocksSql = "SELECT name FROM stocks";
        String marketInsertSql = "INSERT INTO market (stock, quantity) VALUES (?, ?)";
        Connection conn = Database.getConnection();
        try (PreparedStatement selectStocksStmt = conn.prepareStatement(selectStocksSql);
             PreparedStatement marketStmt = conn.prepareStatement(marketInsertSql)) {

            ResultSet rs = selectStocksStmt.executeQuery();

            while (rs.next()) {
                String stockName = rs.getString("name");

                // Insert stock data into market table
                marketStmt.setString(1, stockName);
                marketStmt.setInt(2, 1000); // Set initial quantity to 1000 shares
                marketStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void loadAccountsFromFile(String filePath) {
        Path file = Paths.get(filePath);
        try (Stream<String> lines = Files.lines(file)) {
            lines.forEach(line -> {
                String[] data = line.split(",");
                int account_number = Integer.parseInt(data[0]);
                String userName = data[1];
                double balance = Double.parseDouble(data[2]);
                String accountType = data[3];
                insertAccounts(account_number, userName, balance, accountType);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static void loadUsersFromFile(String filePath) {
        Path file = Paths.get(filePath);
        try (Stream<String> lines = Files.lines(file)) {
            lines.forEach(line -> {
                String[] data = line.split(",");
                String userName = data[1];
                String password = data[2];
                int account_number = Integer.parseInt(data[3]);
                String accountType = data[4];
                insertUsers(userName, password, account_number, accountType);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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
}
