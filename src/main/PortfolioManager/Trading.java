package main.PortfolioManager;

import main.Database.Database;
import main.log.logSystem;

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
    // ----------------- Singleton -----------------
    private static Trading trading = new Trading();

    // ----------------- Buy stock -----------------
    public static boolean buyStock(int accountNumber, String stockName, int quantity) {
        // 1. Get the price of the stock and check if there's enough quantity in the market
        double stockPrice = 0;
        int availableQuantity = 0;

        // sql query to get the stock information: name, currentPrice
        String sql = "SELECT s.currentPrice, m.quantity FROM stocks s JOIN market m ON s.name = m.stock WHERE s.name = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stockName);
            ResultSet rs = pstmt.executeQuery();
            // Get the variables from the database
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

        // Check if there is enough quantity in the market
        if (quantity > availableQuantity) {
            return false;
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
        // Insufficient balance
        if (accountBalance < totalCost) {
            return false;
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
            // Insert the stock into customer_stocks table
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
            // Rollback the transaction
            System.out.println(e.getMessage());
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                // Rollback failed
                System.out.println(ex.getMessage());
            }
            return false;
        }

        // 5. Get the log system (Implement your own logging system here)
        logSystem.logTransaction(accountNumber, stockName, stockPrice, quantity, "BUY");
        // 6. Return true if the transaction is successful
        return true;
    }

    // ----------------- sellStock -----------------
    public static boolean sellStock(int accountNumber, String stockName, int quantity) {
        // 1. Get the stock's quantity owned by the customer
        //    check if the whole quantity is enough
        int totalQuantityOwned = 0;
        // sql query to get the stock information: id, account_number, stock, price_bought, quantity
        String sql = "SELECT id, account_number, stock, price_bought, quantity FROM customer_stocks WHERE account_number = ? AND stock = ? ORDER BY id";
        List<Integer> stockIds = new ArrayList<>();
        List<Integer> stockQuantities = new ArrayList<>();
        List<Double> stockPrices = new ArrayList<>();

        // Get the variables from the database
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
            return false; // Not enough stock to sell
        }

        // 2. Update the accounts' balance in accounts table and customer_stocks table
        double currentPrice = Database.getStock(stockName).getCurrentPrice();
        double profit = 0;
        int remainingQuantityToSell = quantity;

        // Loop through all the stocks owned by the customer
        for (int i = 0; i < stockIds.size() && remainingQuantityToSell > 0; i++) {
            // Get the stock information
            int stockId = stockIds.get(i);
            int stockQuantity = stockQuantities.get(i);
            double priceBought = stockPrices.get(i);

            // Sell the minimum of the remaining quantity to sell and the quantity of the current stock
            int quantityToSell = Math.min(stockQuantity, remainingQuantityToSell);
            profit += (currentPrice - priceBought) * quantityToSell;
            remainingQuantityToSell -= quantityToSell;

            // Delete the row if there is sufficient stocks to sell
            if (quantityToSell == stockQuantity) {
                sql = "DELETE FROM customer_stocks WHERE id = ?";
            } else {
                sql = "UPDATE customer_stocks SET quantity = quantity - ? WHERE id = ?";
            }
            // Update customer_stocks table
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, stockId);
                if (quantityToSell != stockQuantity) {
                    pstmt.setInt(1, quantityToSell);
                    pstmt.setInt(2, stockId);
                }
                pstmt.executeUpdate();
            } catch (SQLException e) {
                // Throw exception to rollback
                System.out.println(e.getMessage());
                return false;
            }
        }

        // 3. Update the accounts' balance in accounts table
        sql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, profit);
            pstmt.setInt(2, accountNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        // 4. Market
        sql = "UPDATE market SET quantity = quantity + ? WHERE stock = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setString(2, stockName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        // 5. Get the log system (Implement your own logging system here)
        boolean isLogged = logSystem.logTransaction(accountNumber, stockName, currentPrice, quantity, "SELL");
        if (!isLogged) {
            return false;
        }
        // 6. Return true if the transaction is successful
        return true;
    }
}
// ----------------- end of Trading.java -----------------
