package main.FrontEnd;

import main.Accounts.TradingAccount;
import main.Database.Database;
import main.Stocks.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StockPage {
    private int accountNumber;
    private AccountPage accountPage;
    private JFrame frame;

    public StockPage(int accountNumber, AccountPage accountPage) {
        this.accountNumber = accountNumber;
        this.accountPage = accountPage;
    }

    public void run() {
        frame = new JFrame("Stock Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        // Get account information
        TradingAccount tradingAccount = Database.getTradingAccount(accountNumber);
        String ownerName = tradingAccount.getOwnerName();
        double balance = tradingAccount.getBalance();

        // Create account info panel
        JPanel accountInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel accountInfoLabel = new JLabel("Name: " + ownerName + " | Account: " + accountNumber + " | Balance: " + balance);
        accountInfoPanel.add(accountInfoLabel);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(accountInfoPanel, BorderLayout.NORTH);

        JScrollPane userStocksScrollPane = createStockListScrollPane(true);
        JScrollPane marketStocksScrollPane = createStockListScrollPane(false);

        JPanel stockListPanel = new JPanel(new GridLayout(1, 2));
        stockListPanel.add(userStocksScrollPane);
        stockListPanel.add(marketStocksScrollPane);

        mainPanel.add(stockListPanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JScrollPane createStockListScrollPane(boolean isUserStocks) {
        TradingAccount tradingAccount = Database.getTradingAccount(accountNumber);
        List<CustomerStock> userHoldings = tradingAccount.getHoldings();
        List<MarketStock> marketStocks = Market.getStocks();

        JPanel stockListPanel = new JPanel();
        stockListPanel.setLayout(new BoxLayout(stockListPanel, BoxLayout.Y_AXIS));

        if (isUserStocks) {
            for (CustomerStock holding : userHoldings) {
                // User's stock list, display profit
                JPanel stockPanel = createUserStockPanel(holding);
                stockListPanel.add(stockPanel);
            }
        } else {
            for (MarketStock stock : marketStocks) {
                // Market stock list, no profit display
                JPanel stockPanel = createMarketStockPanel(stock);
                stockListPanel.add(stockPanel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(stockListPanel);
        return scrollPane;
    }

    private JPanel createUserStockPanel(CustomerStock holding) {
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String stockName = holding.getStockName();
        int stockNumber = holding.getStockNumber();
        double boughtPrice = holding.getStockBoughtPrice();
        Stock stock = Database.getStock(stockName);
        double currentPrice = stock.getCurrentPrice();
        double profit = (currentPrice - boughtPrice) * stockNumber;

        JLabel stockLabel = new JLabel(stockName + " | Quantity: " + stockNumber + " | Profit: " + profit);
        stockPanel.add(stockLabel);

        JTextField sellQuantityField = new JTextField(5);
        stockPanel.add(sellQuantityField);

        JButton sellButton = new JButton("Sell");
        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement sell functionality
                // ...
            }
        });
        stockPanel.add(sellButton);

        return stockPanel;
    }

    private JPanel createMarketStockPanel(MarketStock stock) {
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String stockName = stock.getStockName();
        Stock stock_orgin=Database.getStock(stockName);

        JLabel stockLabel = new JLabel(stockName+" | Quantity: " + stock.getQuantity() + " | Price: " + stock_orgin.getCurrentPrice());
        stockPanel.add(stockLabel);

        JTextField buyQuantityField = new JTextField(5);
        stockPanel.add(buyQuantityField);

        JButton buyButton = new JButton("Buy");
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement buy functionality
                // ...
            }
        });
        stockPanel.add(buyButton);

        return stockPanel;
    }

}
