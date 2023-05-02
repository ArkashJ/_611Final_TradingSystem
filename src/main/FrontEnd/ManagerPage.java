package main.FrontEnd;

import main.Accounts.TradingAccount;
import main.Database.Database;
import main.PortfolioManager.BankManager;
import main.Stocks.Market;
import main.Stocks.MarketStock;
import main.Stocks.Stock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ManagerPage {
    private JFrame frame;
    private String AdminName;
    private boolean accountsInfoVisible = true;
    private boolean marketStocksVisible = true;
    private boolean logVisible = true;
    private JScrollPane accountsInfoScrollPane;
    private JScrollPane marketStocksScrollPane;
    private JScrollPane logScrollPane;

    public ManagerPage(String AdminName) {
        this.AdminName = AdminName;
    }

    public void run() {
        frame = new JFrame("Manager Page");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1600, 600);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Accounts info and profits panel
        accountsInfoScrollPane = createAccountsInfoScrollPane();
        mainPanel.add(accountsInfoScrollPane, BorderLayout.WEST);

        // Market stocks panel
        marketStocksScrollPane = createMarketStocksScrollPane();
        mainPanel.add(marketStocksScrollPane, BorderLayout.CENTER);

        // Log panel
        logScrollPane = createLogScrollPane();
        mainPanel.add(logScrollPane, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton toggleAccountsButton = new JButton("Toggle Accounts");
        buttonPanel.add(toggleAccountsButton);
        JButton toggleMarketButton = new JButton("Toggle Market");
        buttonPanel.add(toggleMarketButton);
        JButton toggleLogButton = new JButton("Toggle Log");
        buttonPanel.add(toggleLogButton);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Add an ActionListener to toggle the visibility of each section
        toggleAccountsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accountsInfoVisible = !accountsInfoVisible;
                accountsInfoScrollPane.setVisible(accountsInfoVisible);
                refreshSize();
            }
        });

        toggleMarketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                marketStocksVisible = !marketStocksVisible;
                marketStocksScrollPane.setVisible(marketStocksVisible);
                refreshSize();
            }
        });

        toggleLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logVisible = !logVisible;
                logScrollPane.setVisible(logVisible);
                refreshSize();
            }
        });

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public void refreshSize() {
        frame.pack();
        frame.repaint();
    }

    private JScrollPane createAccountsInfoScrollPane() {
        JPanel accountsInfoPanel = new JPanel();
        accountsInfoPanel.setLayout(new BoxLayout(accountsInfoPanel, BoxLayout.Y_AXIS));

        String sql = "SELECT * FROM accounts WHERE account_type = 'TRADE';";
        Connection conn = Database.getConnection();
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String ownerName = resultSet.getString("user_name");
                int accountNumber = resultSet.getInt("account_number");
                double balance = resultSet.getDouble("balance");
                Map<String,Double> profits = BankManager.calculateProfits(accountNumber);
                JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel accountInfoLabel = new JLabel(
                        "Name: " + ownerName
                                + " | Account: " + accountNumber
                                + " | Balance: " + balance
                                + " | Profit: " + profits.get("realized")
                                + "( expect : " + profits.get("unrealized") + ")"
                );
                accountPanel.add(accountInfoLabel);
                accountsInfoPanel.add(accountPanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(accountsInfoPanel);
        return scrollPane;
    }

    private JScrollPane createMarketStocksScrollPane() {
        List<MarketStock> marketStocks = Market.getStocks();
        JPanel marketStocksPanel = new JPanel();
        marketStocksPanel.setLayout(new BoxLayout(marketStocksPanel, BoxLayout.Y_AXIS));

        for (MarketStock marketStock : marketStocks) {
            JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            String stockName = marketStock.getStockName();
            int quantity = marketStock.getQuantity();
            Stock stock = Database.getStock(stockName);
            double currentPrice = stock.getCurrentPrice();

            JLabel stockLabel = new JLabel(stockName + " | Quantity: " + quantity + " | Current Price: " + currentPrice);
            stockPanel.add(stockLabel);

            JTextField newPriceField = new JTextField(5);
            stockPanel.add(newPriceField);

            JButton setPriceButton = new JButton("Set Price");
            setPriceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    double newPrice;
                    try {
                        newPrice = Double.parseDouble(newPriceField.getText());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid price entered.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (newPrice <= 0) {
                        JOptionPane.showMessageDialog(frame, "Invalid price. Please enter a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    BankManager.updateStockPrice(stockName, newPrice);
                    JOptionPane.showMessageDialog(frame, "Price updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                }
            });
            stockPanel.add(setPriceButton);
            marketStocksPanel.add(stockPanel);
        }

        JScrollPane scrollPane = new JScrollPane(marketStocksPanel);
        return scrollPane;
    }

    private JScrollPane createLogScrollPane() {
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));

        String sql = "SELECT * FROM log ORDER BY time DESC;";
        Connection conn = Database.getConnection();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                int accountNumber = resultSet.getInt("account_number");
                String stock = resultSet.getString("stock");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                String time = sdf.format(resultSet.getTimestamp("time"));

                JPanel logEntryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel logEntryLabel = new JLabel(
                        "Account: " + accountNumber
                                + " | Stock: " + stock
                                + " | Price: " + price
                                + " | Quantity: " + quantity
                                + " | Time: " + time
                );
                logEntryPanel.add(logEntryLabel);
                logPanel.add(logEntryPanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(logPanel);
        return scrollPane;
    }

    public void refresh() {
        frame.dispose();
        run();
    }
}
