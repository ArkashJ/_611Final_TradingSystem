package main.PortfolioManager;

import main.Database.Database;
import main.log.logSystem;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Description: This class represents trading between customers and the market, a singleton class
 * @Methods: buyStock(int accountNumber, String stockName, int quantity) buy a stock
                - Check if the stock is available in the market from the market table
                - Establish database connection and the currentstock price and quantity from the stocks table
                - Check if there is available stocks, get the account balance from the accounts table
                - Check if the account balance is enough to buy the stock
                - Update the accounts table and customer_stocks table
                - Log the transaction
             sellStock(int accountNumber, String stockName, int quantity) sell a stock
               - Check if the stock is available in the customer_stocks table
               - Establish database connection and the current stock price from the stocks table
               - Check if there is enough stocks to sell
               - Loop through the stocks list and sell the stocks
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
                JOptionPane.showMessageDialog(null, "Stock not available in the market!");
                return false; // Stock not found
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        if (quantity > availableQuantity) {
            JOptionPane.showMessageDialog(null, "Stock not available in the market!");
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
                JOptionPane.showMessageDialog(null, "Account not found!");
                return false; // Account not found
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        double totalCost = stockPrice * quantity;
        if (accountBalance < totalCost) {
            JOptionPane.showMessageDialog(null, "Not enough balance in account.");
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
        // 1. Get the stock of that account in customer_stocks table,按照stockName和accountNumber获取对应的所有行的信息
        //    check if the whole quantity is enough
        int totalQuantityOwned = 0;
        String sql = "SELECT id, account_number, stock, price_bought, quantity FROM customer_stocks WHERE account_number = ? AND stock = ? ORDER BY id";
        List<Integer> stockIds = new ArrayList<>();
        List<Integer> stockQuantities = new ArrayList<>();
        List<Double> stockPrices = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountNumber);
            pstmt.setString(2, stockName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                totalQuantityOwned += rs.getInt("quantity");
                stockIds.add(rs.getInt("id"));
                stockQuantities.add(rs.getInt("quantity"));
                stockPrices.add(rs.getDouble("price_bought"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        if (totalQuantityOwned < quantity) {
            JOptionPane.showMessageDialog(null, "Not enough stocks available in the market!");
            return false; // Not enough stock to sell
        }

        double currentPrice = Database.getStock(stockName).getCurrentPrice();
        double profit = 0;
        int remainingQuantityToSell = quantity;

        for (int i = 0; i < stockIds.size() && remainingQuantityToSell > 0; i++) {
            int stockId = stockIds.get(i);
            int stockQuantity = stockQuantities.get(i);
            double priceBought = stockPrices.get(i);

            int quantityToSell = Math.min(stockQuantity, remainingQuantityToSell);
//            profit += (currentPrice - priceBought) * quantityToSell;
            profit+= currentPrice * quantityToSell;
            remainingQuantityToSell -= quantityToSell;

            if (quantityToSell == stockQuantity) {
                sql = "DELETE FROM customer_stocks WHERE id = ?";
            } else {
                sql = "UPDATE customer_stocks SET quantity = quantity - ? WHERE id = ?";
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, stockId);
                if (quantityToSell != stockQuantity) {
                    pstmt.setInt(1, quantityToSell);
                    pstmt.setInt(2, stockId);
                }
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }

        sql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, profit);
            pstmt.setInt(2, accountNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        // 4. 在Market中加这个份额
        sql = "UPDATE market SET quantity = quantity + ? WHERE stock = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setString(2, stockName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        boolean isLogged = logSystem.logTransaction(accountNumber, stockName, currentPrice, quantity, "SELL");
        if (!isLogged) {
            return false;
        }

        return true;
    }
}
