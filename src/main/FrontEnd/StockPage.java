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
    private JFrame frame;

    private TradingAccount tradingAccount;

    public StockPage(int accountNumber, AccountPage accountPage) {
        this.accountNumber = accountNumber;
        this.tradingAccount = Database.getTradingAccount(accountNumber);
        this.accountPage = accountPage;
    }

    public void run() {
        frame = new JFrame("Stock Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

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
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(accountInfoPanel, BorderLayout.NORTH);

        JButton enterMarketButton = new JButton("Enter market");
        enterMarketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MarketPage marketPage = new MarketPage(accountNumber);
                marketPage.run();
            }
        });

        mainPanel.add(enterMarketButton, BorderLayout.SOUTH);

        JScrollPane userStocksScrollPane = createStockListScrollPane(true);
//        JScrollPane marketStocksScrollPane = createStockListScrollPane(false);

        JPanel stockListPanel = new JPanel(new GridLayout(1, 2));
        stockListPanel.add(userStocksScrollPane);
//        stockListPanel.add(marketStocksScrollPane);

        mainPanel.add(stockListPanel, BorderLayout.EAST);

        frame.add(mainPanel);
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
        } else {
//            for (MarketStock stock : marketStocks) {
//                // Market stock list, no profit display
//                JPanel stockPanel = createMarketStockPanel(stock);
//                stockListPanel.add(stockPanel);
//            }
        }

        JScrollPane scrollPane = new JScrollPane(stockListPanel);
        return scrollPane;
    }

    private JPanel createUserStockPanel(CustomerStock holding) {
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String stockName = holding.getStockName();
        int stockNumber = holding.getStockNumber();
        double boughtPrice = holding.getStockBoughtPrice();
        double profit = tradingAccount.getProfitForStock(stockName);

        JLabel stockLabel = new JLabel(stockName + " | Quantity: " + stockNumber + " | Profit: " + profit);
        stockPanel.add(stockLabel);

        JTextField sellQuantityField = new JTextField(5);
        stockPanel.add(sellQuantityField);

        JButton sellButton = new JButton("Sell");
        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int sellQuantity;
                try {
                    sellQuantity = Integer.parseInt(sellQuantityField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid quantity. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (sellQuantity <= 0) {
                    JOptionPane.showMessageDialog(frame, "Invalid quantity. Please enter a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = Trading.sellStock(accountNumber, stockName, sellQuantity);

                if (success) {
                    JOptionPane.showMessageDialog(frame, "Successfully sold " + sellQuantity + " of " + stockName, "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Refresh user stocks and market stocks panels
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to sell " + sellQuantity + " of " + stockName, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        stockPanel.add(sellButton);

        return stockPanel;
    }

    public void refresh() {
        frame.dispose();
        tradingAccount = Database.getTradingAccount(accountNumber);
        run();
    }

}
