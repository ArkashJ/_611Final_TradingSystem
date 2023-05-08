package main.FrontEnd;

import main.Accounts.TradingAccount;
import main.Database.Database;
import main.PortfolioManager.Trading;
import main.Stocks.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**

- The StockPage class represents the user's stock management page in the trading application.
This page allows users to view their stock holdings, sell stocks, and navigate to the market
and account pages. The class uses the Java Swing library to create and manage the GUI components.

    - Key components of the class include:
- Constructor: Initializes the stock management page with account information.
- run(): Sets up the main JFrame for the stock management page and makes it visible.
- createStockListScrollPane(): Creates a JScrollPane for displaying the user's stock holdings.
- createExitPanel(): Creates a panel with buttons to logout and navigate to the account page.
- createSellStockPanel(): Creates a panel for selling stocks.
- checkAndNotifyOptionalAccountEligibility(): Checks if the user is eligible for an optional account.
- refresh(): Refreshes the stock management page to reflect any changes.

- The class interacts with the TradingAccount, Trading, MarketPage, and Database classes to manage
user stocks and perform stock-related actions.
*/

public class StockPage {
    private int accountNumber;
    private AccountPage accountPage;
    private UserLoginRegistration loginPage;
    private JFrame frame;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private TradingAccount tradingAccount;

    public StockPage(int accountNumber, AccountPage accountPage, UserLoginRegistration loginPage) {
        this.accountNumber = accountNumber;
        this.tradingAccount = Database.getTradingAccount(accountNumber);
        this.accountPage = accountPage;
        this.loginPage = loginPage;
    }

    public void run() {
        frame = new JFrame("Stock Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);

        // Get account information
        String ownerName = tradingAccount.getOwnerName();
        double balance = tradingAccount.getBalance();
        Map<String, Double> profit = tradingAccount.getProfitsForAccount();

        // Create account info panel
        JPanel accountInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel accountInfoLabel = new JLabel(
                "Name: " + ownerName
                        + " | Account: " + accountNumber
                        + " | Balance: " + balance
                        + " | Profit: " + profit.get("realized")
                        + " ( Unrealized:  " + profit.get("unrealized") + " )"
        );
        accountInfoLabel.setFont(accountInfoLabel.getFont().deriveFont(Font.ITALIC, 16f));
        accountInfoPanel.add(accountInfoLabel);
        checkAndNotifyOptionalAccountEligibility();

        JButton enterMarketButton = new JButton("Enter market");
        enterMarketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MarketPage marketPage = new MarketPage(accountNumber, accountPage, StockPage.this, loginPage);
                marketPage.run();
                frame.dispose();
            }
        });

        JPanel marketButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        marketButtonPanel.add(enterMarketButton);

        JScrollPane userStocksScrollPane = createStockListScrollPane(true);

        JPanel stockListPanel = new JPanel(new GridLayout(1, 2));
        stockListPanel.add(userStocksScrollPane);
        JPanel sellStockPanel = createSellStockPanel();
        frame.add(sellStockPanel, BorderLayout.SOUTH);
        frame.add(accountInfoPanel, BorderLayout.NORTH);
        frame.add(marketButtonPanel, BorderLayout.EAST);
        frame.add(stockListPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }


    private JScrollPane createStockListScrollPane(boolean isUserStocks) {
        List<CustomerStock> userHoldings = tradingAccount.getHoldings();

        JPanel stockListPanel = new JPanel();
        stockListPanel.setLayout(new BoxLayout(stockListPanel, BoxLayout.Y_AXIS));
        Object data[][] = new Object[userHoldings.size()][3];

        if (isUserStocks) {
            HashMap<String, Integer> stock_numbers = new HashMap<>();
            for (CustomerStock holding : userHoldings) {
                String stockName = holding.getStockName();
                if (stock_numbers.containsKey(stockName)) {
                    stock_numbers.put(stockName, stock_numbers.get(stockName) + holding.getStockNumber());
                } else {
                    stock_numbers.put(stockName, holding.getStockNumber());
                }
            }
            int i = 0;
            for (Map.Entry<String, Integer> entry : stock_numbers.entrySet()) {
                String stockName = entry.getKey();
                int stockNumber = entry.getValue();
                CustomerStock holding = new CustomerStock(stockName, stockNumber, 0);
                Object[] stock = {holding.getStockName(), holding.getStockNumber(), holding.getProfit()};
                data[i] = stock;
                i++;
            }
        }

        Object[] columnNames = {"Stock Name", "Quantity", "Price ($)"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);

        // Create the table
        JTable table = new JTable(model);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        // Set font size of the table
        Font tableFont = table.getFont().deriveFont(15f);
        table.setFont(tableFont);

        // Set font size of the column names
        Font columnNameFont = table.getTableHeader().getFont().deriveFont(18f);
        table.getTableHeader().setFont(columnNameFont);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        return scrollPane;
    }



    private Object[] createUserStockPanel(CustomerStock holding) {
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        String stockName = holding.getStockName();
        int stockNumber = holding.getStockNumber();
        double boughtPrice = holding.getStockBoughtPrice();
        double profit = tradingAccount.getProfitForStock(stockName);

        Object [] items = {stockName,stockNumber,profit};

        return items;
    }


    private JPanel createExitPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                loginPage.run();
            }
        });

        JButton accountButton = new JButton("Go to account page");
        accountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accountPage.run();
                frame.dispose();
            }
        });

        buttonPanel.add(accountButton);
        buttonPanel.add(logoutButton);

        return buttonPanel;
    }

    private JPanel createSellStockPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel stockNameField = new JLabel("Name");
        JTextField stockName = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(stockNameField, gbc);
        gbc.gridy = 1;
        panel.add(stockName, gbc);

        JLabel stockQuantityField = new JLabel("Quantity");
        JTextField stockQuantity = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(stockQuantityField, gbc);
        gbc.gridy = 1;
        panel.add(stockQuantity, gbc);

        JButton sellButton = new JButton("Sell");
        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String stockNameInput = stockName.getText();
                int stockQuantityInput;

                try {
                    stockQuantityInput = Integer.parseInt(stockQuantity.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid quantity input. Please enter a valid number.");
                    return;
                }

                boolean result = Trading.sellStock(tradingAccount.getAccountNumber(), stockNameInput, stockQuantityInput);

                if (result) {
                    JOptionPane.showMessageDialog(frame, "Stock sold successfully!");
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(frame, "Error selling stock. Please check stock name and quantity.");
                }
            }
        });

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        panel.add(sellButton, gbc);

        return panel;
    }

    private void checkAndNotifyOptionalAccountEligibility() {
        double profit = tradingAccount.getProfitsForAccount().get("realized");
        if (profit > 10000) {

            JOptionPane.showMessageDialog(frame, "Congratulations! Your profit is over 10,000. You are eligible to open an Optional account.");
        }
    }

    public void refresh() {
        this.tradingAccount = Database.getTradingAccount(accountNumber);
        frame.dispose();
        run();
    }
}
