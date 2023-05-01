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

/*
    * @Description: This class represents the bank manager, a singleton class that manages the bank. The bank manager can see the users, accounts and trades made
    * This class connencts to the sql database to display the table data
    * Constructor: BankManager() - initializes the singleton class if there is no existing one otherwise returns the existing one
    * calculateProfits_ALL() -
        - calculates the profits for all accounts
        - returns a map of account numbers to a map of realized and unrealized profits
        - The key is the account number and the value is a map of realized and unrealized profits
        - We generate a sql query to get the account number, stock, price, quantity, type, and current price
        - Then we connect to the database
        - In a loop, we store the variables from the query into the variables. If there is no profit initialized for this current account, we initialize it as 0 for both realized and unrealized
        - Depending on the conditions, we calculate the unrealized and realized profits and store it in the map
    * updateMarket() -
        - updates the market
        - returns true if the market was updated successfully
        - We generate a sql query to get the stock name, current price, and quantity
        - Then we connect to the database
        - In a loop, we store the variables from the query into the variables. If there is no profit initialized for this current account, we initialize it as 0 for both realized and unrealized
        - Depending on the conditions, we calculate the unrealized and realized profits and store it in the map
    * getTotalProfitForStock(int accountNumber, String stockName) -
        - calculates profit for a specific stock given an account number
        - queries the customer_stocks table from the database and establishes a connection
        - Then it finds the price at which the stock was bought at and generates the profit
 */

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

    // ----------------- Calculate Profit For All Account -----------------
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
                // initializing the variables for the sql table
                int accountNumber = rs.getInt("account_number");
                String stock = rs.getString("stock");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                String type = rs.getString("type");
                double currentPrice = rs.getDouble("currentPrice");

                // if there's no profit associated with the account number, we initialize the realized and unrealized profits as 0
                if (!profits.containsKey(accountNumber)) {
                    profits.put(accountNumber, new HashMap<>());
                    profits.get(accountNumber).put("unrealized", 0.0);
                    profits.get(accountNumber).put("realized", 0.0);
                }

                // if the user buys a stock, we calculate the unrealized and realized profits and store it in the map
                if (type.equals("BUY")) {
//                    double unrealizedProfit = (currentPrice - price) * quantity;
                    profits.get(accountNumber).put("unrealized", profits.get(accountNumber).get("unrealized") + currentPrice * quantity);
                    profits.get(accountNumber).put("realized", profits.get(accountNumber).get("realized") - price * quantity);
                } else if (type.equals("SELL")) {
                    // if the user sells a stock, we calculate the unrealized and realized profits (the formulae are switched from above)
                    double realizedProfit = (price - currentPrice) * quantity;
                    profits.get(accountNumber).put("realized", profits.get(accountNumber).get("realized") + currentPrice * quantity);
                    profits.get(accountNumber).put("unrealized", profits.get(accountNumber).get("unrealized") - currentPrice * quantity);
                }
            }
        } catch (SQLException e) {
            // throw an error if there is an error establishing a connection with the db, or reading or initializing the variables
            System.out.println(e.getMessage());
        }

        return profits;
    }

    // ------------------------ Calculate Profit for Specific User ------------------------
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
                    boolean isProfit = profits.get("realized") - price * quantity > 100000;

                    // TODO: if isProfit, then call event listener in UI, ask user if they want to open an options account

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

    // ------------------------ Update Stock Price ------------------------
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

    // ----------- Profit For Specific Stock -------------
    /**
     * @Description: Calculate profits for a stock in a specific account
     * @return double profit
     */
    public static double getTotalProfitForStock(int accountNumber, String stockName) {
        // qeury customer stocks table to get the price bought and quantity
        String query = "SELECT price_bought, quantity FROM customer_stocks WHERE account_number = ? AND stock = ?";
        double totalProfit = 0;
        // establish connection
        Connection connection= Database.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, accountNumber);
            preparedStatement.setString(2, stockName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // get the current price of the stock
                Stock stock = Database.getStock(stockName);
                double currentPrice = stock.getCurrentPrice();
                // calculate the total profit
                while (resultSet.next()) {
                    double boughtPrice = resultSet.getDouble("price_bought");
                    int quantity = resultSet.getInt("quantity");
                    totalProfit += (currentPrice - boughtPrice) * quantity;
                }
            }
        } catch (SQLException e) {
            // throw an error if there is an error establishing a connection with the db, or reading or initializing the variables
            e.printStackTrace();
        }

        return totalProfit;
    }

}
