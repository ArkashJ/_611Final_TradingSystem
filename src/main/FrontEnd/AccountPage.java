package main.FrontEnd;

import main.Accounts.TradingAccount;
import main.Stocks.CustomerStocks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AccountPage {
    private JFrame frame;
    private String ownerName;

    public AccountPage(String ownerName) {
        this.ownerName = ownerName;
        frame = new JFrame("Account Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel accountPagePanel = createAccountPage();
        frame.add(accountPagePanel);

        frame.setVisible(true);
    }
    private JPanel createAccountPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        // Add balance input field and label
        JLabel balanceLabel = new JLabel("Initial Balance:");
        JTextField balanceField = new JTextField(20);
        JButton registerNewAccountButton = new JButton("Register New Account");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(balanceLabel, gbc);
        gbc.gridx = 1;
        panel.add(balanceField, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(registerNewAccountButton, gbc);

        registerNewAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the balance value from the input field and convert it to a double
                double balance = Double.parseDouble(balanceField.getText());

                // Randomly generate an account number
                long accountNumber = (long) (Math.random() * 1000000000L);

                String ownerName = AccountPage.this.ownerName;
                CustomerStocks customerStocks = new CustomerStocks();

                TradingAccount tradingAccount = new TradingAccount(ownerName, customerStocks, balance, accountNumber);

                // Add the TradingAccount to the database or other data structures here
                // ...

                JOptionPane.showMessageDialog(null, "New trading account created! Account Number: " + accountNumber);
            }
        });

        return panel;
    }

}
