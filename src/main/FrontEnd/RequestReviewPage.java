package main.FrontEnd;

import main.Database.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestReviewPage {
    private JFrame frame;

    public void run() {
        frame = new JFrame("Request Review");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create a table to display account requests
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "User Name", "Initial Balance"}, 0);
        JTable requestTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(requestTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Load account requests
        loadAccountRequests(tableModel);

        // Create approve and reject buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners for approve and reject buttons
        approveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = requestTable.getSelectedRow();
                if (selectedRow != -1) {
                    int requestId = (int) tableModel.getValueAt(selectedRow, 0);
                    String userName = (String) tableModel.getValueAt(selectedRow, 1);
                    double initialBalance = (double) tableModel.getValueAt(selectedRow, 2);

                    if (Database.createTradingAccount(userName, initialBalance) && Database.removeAccountRequest(requestId)) {
                        JOptionPane.showMessageDialog(frame, "Account request approved and account created.");
                        tableModel.removeRow(selectedRow);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to approve account request.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = requestTable.getSelectedRow();
                if (selectedRow != -1) {
                    int requestId = (int) tableModel.getValueAt(selectedRow, 0);

                    if (Database.removeAccountRequest(requestId)) {
                        JOptionPane.showMessageDialog(frame, "Account request rejected.");
                        tableModel.removeRow(selectedRow);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to reject account request.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void loadAccountRequests(DefaultTableModel tableModel) {
        String sql = "SELECT * FROM account_requests;";
        Connection conn = Database.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String userName = resultSet.getString("user_name");
                double initialBalance = resultSet.getDouble("initial_balance");
                String type=resultSet.getString("account_type");

                tableModel.addRow(new Object[]{id, userName, initialBalance,type});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}