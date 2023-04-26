package main.PortfolioManager;

import main.Database.Database;
import main.log.logSystem;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;


/**
 * @Description: This class represents trading between customers and the market, a singleton class
 * @Methods: buyStock(int accountNumber, String stockName, int quantity) buy a stock
 *          sellStock(int accountNumber, String stockName, int quantity) sell a stock
 */
public class Trading {
    // The trading is a singleton class
    private Trading() {}
    private static Connection conn = Database.getConnection();
    private static Trading trading = new Trading();
    public static boolean buyStock(int accountNumber, String stockName, int quantity) {
        // 1. Get the price of the stock and check if there's enough quantity in the market
        double stockPrice = 0;
        int availableQuantity = 0;
        String sql = "SELECT s.currentPrice, m.quantity FROM stocks s JOIN market m ON s.name = m.stock WHERE s.name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stockName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                stockPrice = rs.getDouble("currentPrice");
                availableQuantity = rs.getInt("quantity");
            } else {
                return false; // Stock not found
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        if (quantity > availableQuantity) {
            return false; // Not enough stock available in the market
        }

        // 2. Check if the account's balance is enough
        double accountBalance = 0;
        sql = "SELECT balance FROM accounts WHERE account_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                accountBalance = rs.getDouble("balance");
            } else {
                return false; // Account not found
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        double totalCost = stockPrice * quantity;
        if (accountBalance < totalCost) {
            return false; // Insufficient balance
        }

        // 3. Update the accounts' balance in accounts table and customer_stocks table
        try {
            conn.setAutoCommit(false);
            sql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, totalCost);
                pstmt.setInt(2, accountNumber);
                pstmt.executeUpdate();
            }

            sql = "INSERT INTO customer_stocks (account_number, stock, price_bought, quantity) VALUES (?, ?, ?, ?) ";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, accountNumber);
                pstmt.setString(2, stockName);
                pstmt.setDouble(3, stockPrice);
                pstmt.setInt(4, quantity);
                pstmt.executeUpdate();
            }

            // 4. Update the stock's quantity in the market
            sql = "UPDATE market SET quantity = quantity - ? WHERE stock = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, quantity);
                pstmt.setString(2, stockName);
                pstmt.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            return false;
        }

        // 5. Get the log system (Implement your own logging system here)
        logSystem.logTransaction(accountNumber, stockName, stockPrice, quantity, "BUY");

        return true;
    }
    public static boolean sellStock(int accountNumber, String stockName, int quantity) {
        // 1. Get the stock of that account in customer_stocks table, add them all up
        int totalQuantityOwned = 0;
        double stockPrice = 0;
        String sql = "SELECT SUM(quantity) as total, price_bought FROM customer_stocks WHERE account_number = ? AND stock = ? GROUP BY stock";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountNumber);
            pstmt.setString(2, stockName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                totalQuantityOwned = rs.getInt("total");
                stockPrice = rs.getDouble("price_bought");
            } else {
                return false; // No stock found for the given accountNumber and stockName
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        // Check if the stock of that quantity is enough
        if (totalQuantityOwned < quantity) {
            return false; // Not enough stock to sell
        }

        // 2. Update the accounts' balance in accounts table and customer_stocks table
        // by deleting the stock from customer_stocks table until the quantity is enough
        double totalAmount = stockPrice * quantity;
        sql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, totalAmount);
            pstmt.setInt(2, accountNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        sql = "UPDATE customer_stocks SET quantity = quantity - ? WHERE account_number = ? AND stock = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, accountNumber);
            pstmt.setString(3, stockName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        // 3. Update the stock's quantity in the market
        sql = "UPDATE market SET quantity = quantity + ? WHERE stock = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setString(2, stockName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        // 4. Log the transaction in the log table
        boolean isLogged = logSystem.logTransaction(accountNumber, stockName, stockPrice, quantity, "SELL");
        if (!isLogged) {
            return false;
        }

        return true;
    }

}
