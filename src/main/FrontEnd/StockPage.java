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
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private TradingAccount tradingAccount;

    public StockPage(int accountNumber, AccountPage accountPage) {
        this.accountNumber = accountNumber;
        this.tradingAccount = Database.getTradingAccount(accountNumber);
        this.accountPage = accountPage;
    }

    public void run() {
        frame = new JFrame("Stock Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);

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
                MarketPage marketPage = new MarketPage(accountNumber);
                marketPage.run();
            }
        });

        JPanel marketButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        marketButtonPanel.add(enterMarketButton);

        JScrollPane userStocksScrollPane = createStockListScrollPane(true);

        JPanel stockListPanel = new JPanel(new GridLayout(1, 2));
        stockListPanel.add(userStocksScrollPane);

        frame.add(accountInfoPanel, BorderLayout.NORTH);
        frame.add(marketButtonPanel, BorderLayout.SOUTH);
        frame.add(stockListPanel, BorderLayout.CENTER);
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

}
