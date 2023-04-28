package main.PortfolioManager;

import main.Database.Database;
import main.Stocks.Stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankManager {
    // make this a singleton
    private static BankManager instance = null;

    private BankManager() {}

    public static BankManager getInstance() {
        if (instance == null) {
            instance = new BankManager();
        }
        return instance;
    }

    /**
     * @Description: Calculate the unrealized and realized profits for each account
     * @return account_number -> (if realized -> profit)
     */
    public static Map<Integer, Map<String, Double>> calculateProfits_ALL() {
        Map<Integer, Map<String, Double>> profits = new HashMap<>();

        String sql = "SELECT l.account_number, l.stock, l.price, l.quantity, l.type, s.currentPrice "
                + "FROM log l "
                + "JOIN stocks s ON l.stock = s.name "
                + "ORDER BY l.account_number, l.time";
        Connection conn = Database.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int accountNumber = rs.getInt("account_number");
                String stock = rs.getString("stock");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                String type = rs.getString("type");
                double currentPrice = rs.getDouble("currentPrice");

                if (!profits.containsKey(accountNumber)) {
                    profits.put(accountNumber, new HashMap<>());
                    profits.get(accountNumber).put("unrealized", 0.0);
                    profits.get(accountNumber).put("realized", 0.0);
                }

                if (type.equals("BUY")) {
//                    double unrealizedProfit = (currentPrice - price) * quantity;
                    profits.get(accountNumber).put("unrealized", profits.get(accountNumber).get("unrealized") + currentPrice * quantity);
                    profits.get(accountNumber).put("realized", profits.get(accountNumber).get("realized") - price * quantity);
                } else if (type.equals("SELL")) {
                    double realizedProfit = (price - currentPrice) * quantity;
                    profits.get(accountNumber).put("realized", profits.get(accountNumber).get("realized") + currentPrice * quantity);
                    profits.get(accountNumber).put("unrealized", profits.get(accountNumber).get("unrealized") - currentPrice * quantity);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return profits;
    }

    /**
     * @Description: Calculate the unrealized and realized profits for a specific account
     * @return (if realized -> profit)
     */
    public static Map<String, Double> calculateProfits(int accountNumber) {
        Map<String, Double> profits = new HashMap<>();
        profits.put("unrealized", 0.0);
        profits.put("realized", 0.0);

        String sql = "SELECT l.stock, l.price, l.quantity, l.type, s.currentPrice "
                + "FROM log l "
                + "JOIN stocks s ON l.stock = s.name "
                + "WHERE l.account_number = ? "
                + "ORDER BY l.time";
        Connection conn = Database.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String stock = rs.getString("stock");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                String type = rs.getString("type");
                double currentPrice = rs.getDouble("currentPrice");

                if (type.equals("BUY")) {
//                    double unrealizedProfit = (currentPrice - price) * quantity;
                    profits.put("unrealized", profits.get("unrealized") + currentPrice * quantity);
                    profits.put("realized", profits.get("realized") - price * quantity);
                } else if (type.equals("SELL")) {
//                    double realizedProfit = (currentPrice - price) * quantity;
                    profits.put("realized", profits.get("realized") + currentPrice * quantity);
                    profits.put("unrealized", profits.get("unrealized") - currentPrice * quantity);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return profits;
    }

    /**
     * @Description: Update the stock price in the database
     * @param stockName
     * @param currentPrice
     * @return true if the stock price is updated successfully
     */
    public static boolean updateStockPrice(String stockName, double currentPrice) {
        String sql = "UPDATE stocks "
                + "SET currentPrice = ?, "
                + "highestPrice = GREATEST(highestPrice, ?), "
                + "lowestPrice = LEAST(lowestPrice, ?) "
                + "WHERE name = ?";
        Connection conn = Database.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, currentPrice);
            pstmt.setDouble(2, currentPrice);
            pstmt.setDouble(3, currentPrice);
            pstmt.setString(4, stockName);

            int updatedRows = pstmt.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * @Description: Calculate profits for a stock in a specific account
     * @return double profit
     */
    public static double getTotalProfitForStock(int accountNumber, String stockName) {
        String query = "SELECT price_bought, quantity FROM customer_stocks WHERE account_number = ? AND stock = ?";
        double totalProfit = 0;
        Connection connection= Database.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, accountNumber);
            preparedStatement.setString(2, stockName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Stock stock = Database.getStock(stockName);
                double currentPrice = stock.getCurrentPrice();

                while (resultSet.next()) {
                    double boughtPrice = resultSet.getDouble("price_bought");
                    int quantity = resultSet.getInt("quantity");
                    totalProfit += (currentPrice - boughtPrice) * quantity;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalProfit;
    }

}
