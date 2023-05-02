package main.FrontEnd;

import main.Accounts.TradingAccount;
import main.Database.Database;
import main.PortfolioManager.Trading;
import main.Stocks.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        frame.setSize(screenSize.width/2, screenSize.height);

        // Get account information
        String ownerName = tradingAccount.getOwnerName();
        double balance = tradingAccount.getBalance();
        Map<String,Double> profit = tradingAccount.getProfitsForAccount();

        // Create account info panel
        JPanel accountInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel accountInfoLabel = new JLabel(
                "Name: " + ownerName
                        + " | Account: " + accountNumber
                        + " | Balance: " + balance
                        + " | Profit: " + profit.get("realized")
                        + " ( expect:  " + profit.get("unrealized") + " )"
                );
        accountInfoPanel.add(accountInfoLabel);

        // Create main panel with BorderLayout

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
//        JPanel exitButtonPanel = createExitPanel();
        frame.add(accountInfoPanel, BorderLayout.NORTH);
        frame.add(marketButtonPanel, BorderLayout.EAST);
        frame.add(stockListPanel, BorderLayout.CENTER);
//        frame.add(exitButtonPanel, BorderLayout.NORTH);
        frame.setVisible(true);
    }

    private JScrollPane createStockListScrollPane(boolean isUserStocks) {
        List<CustomerStock> userHoldings = tradingAccount.getHoldings();
        List<MarketStock> marketStocks = Market.getStocks();

        JPanel stockListPanel = new JPanel();
        stockListPanel.setLayout(new BoxLayout(stockListPanel, BoxLayout.Y_AXIS));

        if (isUserStocks) {
            HashMap<String,Integer> stock_numbers = new HashMap<>();
            for (CustomerStock holding : userHoldings) {
                String stockName = holding.getStockName();
                if(stock_numbers.containsKey(stockName)) {
                    stock_numbers.put(stockName, stock_numbers.get(stockName) + holding.getStockNumber());
                } else {
                    stock_numbers.put(stockName, holding.getStockNumber());
                }
            }
            for(Map.Entry<String, Integer> entry : stock_numbers.entrySet()) {
                String stockName = entry.getKey();
                int stockNumber = entry.getValue();
                CustomerStock holding = new CustomerStock(stockName, stockNumber, 0);
                JPanel stockPanel = createUserStockPanel(holding);
                stockListPanel.add(stockPanel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(stockListPanel);
        return scrollPane;
    }

    private JPanel createUserStockPanel(CustomerStock holding) {
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        String stockName = holding.getStockName();
        int stockNumber = holding.getStockNumber();
        double boughtPrice = holding.getStockBoughtPrice();
        double profit = tradingAccount.getProfitForStock(stockName);

        JLabel stockLabel = new JLabel(stockName + " | Quantity: " + stockNumber + " | Profit: " + profit);
        stockPanel.add(stockLabel);

        return stockPanel;
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


    public void refresh() {
        this.tradingAccount = Database.getTradingAccount(accountNumber);
        frame.dispose();
        run();
    }
}
