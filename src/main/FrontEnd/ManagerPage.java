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

    public ManagerPage(String AdminName) {
        this.AdminName = AdminName;
    }

//    public void run() {
//        frame = new JFrame("Manager Page");
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frame.setSize(1600, 600); // Increase width
//
//        // Create main panel with BorderLayout
//        JPanel mainPanel = new JPanel(new BorderLayout());
//
//        // Accounts info and profits panel
//        JScrollPane accountsInfoScrollPane = createAccountsInfoScrollPane();
//        mainPanel.add(accountsInfoScrollPane, BorderLayout.WEST);
//
//        // Market stocks panel
//        JScrollPane marketStocksScrollPane = createMarketStocksScrollPane();
//        mainPanel.add(marketStocksScrollPane, BorderLayout.CENTER);
//
//        // Log panel
//        JScrollPane logScrollPane = createLogScrollPane(); // Add log panel
//        mainPanel.add(logScrollPane, BorderLayout.EAST);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        JButton reviewRequestsButton = new JButton("Review Requests");
//        buttonPanel.add(reviewRequestsButton);
//        mainPanel.add(buttonPanel, BorderLayout.NORTH);
//
//        // Add an ActionListener to open the RequestReviewPage
//        reviewRequestsButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                RequestReviewPage requestReviewPage = new RequestReviewPage();
//                requestReviewPage.run();
//            }
//        });
//
//        frame.add(mainPanel);
//        frame.setVisible(true);
//    }
    private CardLayout cardLayout; // Add a CardLayout instance variable

    public void run() {
        frame = new JFrame("Manager Page");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1600, 600); // Increase width

        // Create main panel with CardLayout
        cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // Accounts info and profits panel
        JPanel accountsInfoPage = new JPanel(new BorderLayout());
        JScrollPane accountsInfoScrollPane = createAccountsInfoScrollPane();
        accountsInfoPage.add(accountsInfoScrollPane, BorderLayout.CENTER);
        mainPanel.add(accountsInfoPage, "AccountsInfo");

        // Market stocks panel
        JPanel marketStocksPage = new JPanel(new BorderLayout());
        JScrollPane marketStocksScrollPane = createMarketStocksScrollPane();
        marketStocksPage.add(marketStocksScrollPane, BorderLayout.CENTER);
        mainPanel.add(marketStocksPage, "MarketStocks");

        // Log panel
        JPanel logPage = new JPanel(new BorderLayout());
        JScrollPane logScrollPane = createLogScrollPane(); // Add log panel
        logPage.add(logScrollPane, BorderLayout.CENTER);
        mainPanel.add(logPage, "Log");

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton accountsInfoButton = new JButton("Accounts Info");
        JButton marketStocksButton = new JButton("Market Stocks");
        JButton logButton = new JButton("Log");

        buttonPanel.add(accountsInfoButton);
        buttonPanel.add(marketStocksButton);
        buttonPanel.add(logButton);

        accountsInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "AccountsInfo");
            }
        });

        marketStocksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "MarketStocks");
            }
        });

        logButton.addActionListener(new ActionListener() {
            private boolean logVisible = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (logVisible) {
                    cardLayout.show(mainPanel, "Log");
                } else {
                    cardLayout.show(mainPanel, "AccountsInfo");
                }
                logVisible = !logVisible;
            }
        });

        accountsInfoPage.add(buttonPanel, BorderLayout.NORTH);
        marketStocksPage.add(buttonPanel, BorderLayout.NORTH);
        logPage.add(buttonPanel, BorderLayout.NORTH);

        frame.add(mainPanel);
        frame.setVisible(true);
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
