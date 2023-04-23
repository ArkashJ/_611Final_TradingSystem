package main.FrontEnd;

import main.Database.Database;
import main.Accounts.TradingAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AccountPage {
    private String userName;

    public AccountPage(String userName) {
        this.userName = userName;
    }

    public void run() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Account Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            JTabbedPane tabbedPane = new JTabbedPane();
            JPanel viewAccountsPanel = createViewAccountsPanel();
            JPanel createAccountPanel = createCreateAccountPanel();

            tabbedPane.addTab("View Accounts", viewAccountsPanel);
            tabbedPane.addTab("Create Account", createAccountPanel);
            frame.add(tabbedPane);

            frame.setVisible(true);
        });
    }

    private JPanel createViewAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        List<TradingAccount> accounts = Database.getTradingAccountsForUser(this.userName);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (TradingAccount account : accounts) {
            listModel.addElement("Account Number: " + account.getAccountNumber() + " | Balance: " + account.getBalance());
        }
        JList<String> accountList = new JList<>(listModel);
        panel.add(new JScrollPane(accountList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCreateAccountPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel balanceLabel = new JLabel("Initial Balance:");
        JTextField balanceField = new JTextField(20);
        JButton createAccountButton = new JButton("Create Account");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(balanceLabel, gbc);
        gbc.gridx = 1;
        panel.add(balanceField, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(createAccountButton, gbc);

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double initialBalance = Double.parseDouble(balanceField.getText());
//                if (Database.createTradingAccount(userId, initialBalance)) {
//                    JOptionPane.showMessageDialog(null, "New trading account created!");
//                } else {
//                    JOptionPane.showMessageDialog(null, "Failed to create a new trading account. Please try again.");
//                }
            }
        });

        return panel;
    }
}
