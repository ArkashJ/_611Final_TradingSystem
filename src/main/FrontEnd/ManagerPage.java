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
import java.util.List;
import java.util.Map;

public class ManagerPage {
    private JFrame frame;

    public ManagerPage() {}

    public void run() {
        frame = new JFrame("Manager Page");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1200, 600);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Accounts info and profits panel
        JScrollPane accountsInfoScrollPane = createAccountsInfoScrollPane();
        mainPanel.add(accountsInfoScrollPane, BorderLayout.WEST);

        // Market stocks panel
        JScrollPane marketStocksScrollPane = createMarketStocksScrollPane();
        mainPanel.add(marketStocksScrollPane, BorderLayout.CENTER);

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

    public void refresh() {
        frame.dispose();
        run();
    }
}
