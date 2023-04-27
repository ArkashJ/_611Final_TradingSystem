package main.PortfolioManager;

import main.Accounts.TradingAccount;
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
            return false; // Not enough stock to sell
        }

        // 2. 将股票按照顺序卖出股票，直到卖出的股票数量达到quantity
        // 当customer_stocks表中的某行股票数量减到0时，删除该条记录
        double currentPrice = Database.getStock(stockName).getCurrentPrice();
        double profit = 0;
        int remainingQuantityToSell = quantity;

        for (int i = 0; i < stockIds.size() && remainingQuantityToSell > 0; i++) {
            int stockId = stockIds.get(i);
            int stockQuantity = stockQuantities.get(i);
            double priceBought = stockPrices.get(i);

            int quantityToSell = Math.min(stockQuantity, remainingQuantityToSell);
            profit += (currentPrice - priceBought) * quantityToSell;
            remainingQuantityToSell -= quantityToSell;

            if (quantityToSell == stockQuantity) {
                sql = "DELETE FROM customer_stocks WHERE id = ?";
            } else {
                sql = "UPDATE customer_stocks SET quantity = quantity - ? WHERE id = ?";
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, quantityToSell);
                if (quantityToSell != stockQuantity) {
                    pstmt.setInt(2, stockId);
                }
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }

        // 3. 在对每行做操作的时候顺便记录profit，假设customer_stocks的某行卖出了n个，那么profit就加上stockName对应的（currenPrice - price_bought） * n
        // 最后将profit加到account的balance上
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
