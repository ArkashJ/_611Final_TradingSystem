package main.log;

import main.Database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/*
    * @Description: This class represents a log system, a singleton class
    * @Methods: logTransaction(int accountNumber, String stockName, double stockPrice, int quantity, String type) : log a transaction
 */

public class logSystem {
    //singleton
    private static logSystem instance = new logSystem();
    private logSystem() {}

    public static logSystem getInstance() {
        return instance;
    }

    // log a transaction
    public static boolean logTransaction(int accountNumber, String stockName, double stockPrice, int quantity, String type) {
        //todo: if the amount is over 10k, inform everyone
        String sql = "INSERT INTO log (account_number, stock, price, quantity, type, time) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn= Database.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountNumber);
            pstmt.setString(2, stockName);
            pstmt.setDouble(3, stockPrice);
            pstmt.setInt(4, quantity);
            pstmt.setString(5, type);
            pstmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
