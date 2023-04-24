package main.FrontEnd;

import main.Database.Database;
import main.Accounts.TradingAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @Description: show all accounts of a user, and user can click into one account
 */
public class AccountPage {
    private String userName;
    private JFrame frame;
    private JPanel viewAccountsPanel;
    private UserLoginRegistration loginPage;

    public AccountPage(String userName,UserLoginRegistration us) {
        this.userName = userName;
        this.loginPage=us;
    }

    public void run() {
        frame = new JFrame("Account Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();
        viewAccountsPanel = createViewAccountsPanel();
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

        JPanel accountListPanel = new JPanel();
        accountListPanel.setLayout(new BoxLayout(accountListPanel, BoxLayout.Y_AXIS));

        for (TradingAccount account : accounts) {
            JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JLabel accountLabel = new JLabel("Account Number: " + account.getAccountNumber() + " | Balance: " + account.getBalance());
            accountPanel.add(accountLabel);

            JButton enterButton = new JButton("Enter");
            enterButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    StockPage stockPage = new StockPage(account.getAccountNumber(), AccountPage.this);
                    stockPage.run();
                }
            });
            accountPanel.add(enterButton);

            accountListPanel.add(accountPanel);
        }

        JScrollPane scrollPane = new JScrollPane(accountListPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

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
                    refreshViewAccountsPanel();
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to create a new trading account. Please try again.");
                }
            }
        });

        return panel;
    }
//    private void refreshAccountList() {
//        List<TradingAccount> accounts = Database.getTradingAccountsForUser(this.userName);
//        listModel.clear();
//        for (TradingAccount account : accounts) {
//            listModel.addElement("Account Number: " + account.getAccountNumber() + " | Balance: " + account.getBalance());
//        }
//    }
    private void refreshViewAccountsPanel() {
        JPanel newViewAccountsPanel = createViewAccountsPanel();
        viewAccountsPanel.removeAll();
        viewAccountsPanel.add(newViewAccountsPanel, BorderLayout.CENTER);
        viewAccountsPanel.revalidate();
        viewAccountsPanel.repaint();
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
