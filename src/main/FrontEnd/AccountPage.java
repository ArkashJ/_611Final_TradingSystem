package main.FrontEnd;

import main.Database.Database;
import main.Accounts.TradingAccount;
import main.PortfolioManager.BankManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

/**
 * @Description: show all accounts of a user, and user can click into one account
 * 
 * This class is part of the main.FrontEnd package, and provides an interface for users to manage their accounts.
 * It is responsible for displaying a user's trading accounts and facilitating the creation of new accounts.
 * Additionally, it allows users to view their account details and navigate to the stock trading page.
 * Users who are eligible for opening an optional account, based on their realized profits, will be notified.
 *
 * Dependencies: main.Database.Database, main.Accounts.TradingAccount, main.PortfolioManager.BankManager
 */
public class AccountPage{
    private String userName;
    private JFrame frame;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private JPanel viewAccountsPanel;
    private JPanel createCreateAccountPanel;
    private UserLoginRegistration loginPage;

    private boolean isEiligibleForOption=false;

    public AccountPage(String userName,UserLoginRegistration us) {
        this.userName = userName;
        Map<String, Double> profits = BankManager.calculateProfits(userName);
        double realized_profit=profits.get("realized");
        if(realized_profit>=10000) {isEiligibleForOption=true;}
        this.loginPage=us;
        this.viewAccountsPanel = createViewAccountsPanel();
        this.createCreateAccountPanel = createCreateAccountPanel();
    }

    public void run() {
        frame = new JFrame("Account Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("<html><body><i><font size='5'>View Accounts</font></i></body></html>", this.viewAccountsPanel);
        tabbedPane.addTab("<html><body><i><font size='5'>Create Account</font></i></body></html>", this.createCreateAccountPanel);
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.add(createLogoutPanel(), BorderLayout.NORTH);
        if (isEiligibleForOption) {
            JOptionPane.showMessageDialog(frame,
                    "<html><body><font size='5'>Congratulations! Your total realized profit is over 10,000. You are eligible to open an Optional account.</font></body></html>");

        }
        frame.setVisible(true);
    }


    private JPanel createViewAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.PINK);

        List<TradingAccount> accounts = Database.getTradingAccountsForUser(this.userName);

        JPanel accountListPanel = new JPanel();
        accountListPanel.setLayout(new BoxLayout(accountListPanel, BoxLayout.Y_AXIS));

        for (TradingAccount account : accounts) {
            JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            accountPanel.setBackground(Color.PINK);

            JLabel accountLabel = new JLabel("Account Number: " + account.getAccountNumber() + " | Balance: " + account.getBalance());
            accountLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
            accountLabel.setForeground(Color.BLACK);
            accountPanel.add(accountLabel);

            JButton enterButton = new JButton("Enter");
            enterButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
            enterButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    StockPage stockPage = new StockPage(account.getAccountNumber(), AccountPage.this, loginPage);
                    stockPage.run();
                    frame.dispose();
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
        if(!isEiligibleForOption) {
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
                    if (Database.submitAccountRequest(userName, initialBalance, "TRADE")) {
                        JOptionPane.showMessageDialog(null, "Trading account creation request submitted!");
                        refreshCreateCreateAccountsPanel();
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to create a new trading account. Please try again.");
                    }
                }
            });
        }
        else {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);

            JLabel balanceLabel = new JLabel("Initial Balance:");
            JTextField balanceField = new JTextField(20);
            JButton createAccountButton = new JButton("Create Account");

            // Add balance label and field to the panel
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(balanceLabel, gbc);
            gbc.gridx = 1;
            panel.add(balanceField, gbc);

            // Add account type label and combo box to the panel
            JLabel accountTypeLabel = new JLabel("Account Type:");
            String[] accountTypes = {"Trade", "Options"};
            JComboBox<String> accountTypeComboBox = new JComboBox<>(accountTypes);

            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(accountTypeLabel, gbc);
            gbc.gridx = 1;
            panel.add(accountTypeComboBox, gbc);

            // Add create account button to the panel
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(createAccountButton, gbc);

            // Add action listener to the create account button
            createAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    double initialBalance = Double.parseDouble(balanceField.getText());
                    if(initialBalance < 0 ) {
                        JOptionPane.showMessageDialog(null, "Invalid balance. Please enter a balance greater than or equal to 0.");
                    }
                    String accountType = (String) accountTypeComboBox.getSelectedItem();
                    if (Database.submitAccountRequest(userName, initialBalance, accountType.toUpperCase())) {
                        JOptionPane.showMessageDialog(null, accountType + " account creation request submitted!");
                        refreshCreateCreateAccountsPanel();
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to create a new " + accountType + " account. Please try again.");
                    }
                }
            });
        }

        return panel;
    }

    private void refreshCreateCreateAccountsPanel() {
        JPanel newViewAccountsPanel = createCreateAccountPanel();
        this.createCreateAccountPanel.removeAll();
        this.createCreateAccountPanel.add(newViewAccountsPanel);
        this.createCreateAccountPanel.revalidate();
        this.createCreateAccountPanel.repaint();
    }

    private JPanel createLogoutPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        Font buttonFont = new Font(logoutButton.getFont().getName(), Font.BOLD, 15);
        logoutButton.setFont(buttonFont);
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
