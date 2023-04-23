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
    private JList<String> accountList;
    private DefaultListModel<String> listModel;
    private JFrame frame;
    private UserLoginRegistration loginPage;

    public AccountPage(String userName,UserLoginRegistration us) {
        this.userName = userName;
        this.loginPage=us;
        accountList = new JList<>();
        listModel = new DefaultListModel<>();
    }

    public void run() {
        frame = new JFrame("Account Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel viewAccountsPanel = createViewAccountsPanel();
        JPanel createAccountPanel = createCreateAccountPanel();

        tabbedPane.addTab("View Accounts", viewAccountsPanel);
        tabbedPane.addTab("Create Account", createAccountPanel);
        frame.add(tabbedPane,BorderLayout.CENTER);
        frame.add(createLogoutPanel(), BorderLayout.NORTH);

        frame.setVisible(true);
    }

    private JPanel createViewAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        List<TradingAccount> accounts = Database.getTradingAccountsForUser(this.userName);
        for (TradingAccount account : accounts) {
            listModel.addElement("Account Number: " + account.getAccountNumber() + " | Balance: " + account.getBalance());
        }
        accountList.setModel(listModel);
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
                if (Database.createTradingAccount(userName, initialBalance)) {
                    JOptionPane.showMessageDialog(null, "New trading account created!");
                    refreshAccountList();
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to create a new trading account. Please try again.");
                }
            }
        });

        return panel;
    }
    private void refreshAccountList() {
        List<TradingAccount> accounts = Database.getTradingAccountsForUser(this.userName);
        listModel.clear();
        for (TradingAccount account : accounts) {
            listModel.addElement("Account Number: " + account.getAccountNumber() + " | Balance: " + account.getBalance());
        }
    }

    private JPanel createLogoutPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        panel.add(logoutButton);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                loginPage.run();
            }
        });

        return panel;
    }
}
