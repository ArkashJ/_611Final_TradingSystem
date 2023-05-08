package main.FrontEnd;

import main.Accounts.TradingAccount;
import main.Database.Database;
import main.PortfolioManager.Trading;
import main.Stocks.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketPage {

    private JFrame frame;
    private JPanel marketPanel;

    private AccountPage accountPage;
    private StockPage stockPage;
    private UserLoginRegistration loginPage;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private Market market;
    private int accountNumber;
    private TradingAccount tradingAccount;

    public MarketPage(int accountNumber,AccountPage accountPage, StockPage stockPage, UserLoginRegistration loginPage) {
        this.accountNumber = accountNumber;
        this.tradingAccount = Database.getTradingAccount(accountNumber);
        this.marketPanel = createMarketPanel();
        this.accountPage = accountPage;
        this.stockPage = stockPage;
        this.loginPage = loginPage;
    }
    public void run() {
        frame = new JFrame("Stock Market");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);

        frame.add(this.marketPanel, BorderLayout.CENTER);
        JPanel topPanel = tradeStockPanel(this.accountNumber, this.tradingAccount);
        frame.add(topPanel, BorderLayout.NORTH);
        JPanel buttonPanel = createExitPanel();
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

    }

    private JPanel createMarketPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane marketStocksScrollPane = createStockListScrollPane();
        panel.add(marketStocksScrollPane);

        return panel;
    }

    private JScrollPane createStockListScrollPane() {
        List<MarketStock> marketStocks = Market.getStocks();
        Object data [][] = new Object[marketStocks.size()][3];

        JPanel stockListPanel = new JPanel();
        stockListPanel.setLayout(new BoxLayout(stockListPanel, BoxLayout.Y_AXIS));
            for (MarketStock stock : marketStocks) {
                Object [] items = createMarketStockPanel(stock);
                data[marketStocks.indexOf(stock)] = items;
            }

        Object[] columnNames = {"Stock Name", "Quantity", "Current Price ($)"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);


        // Create the table
        JTable table = new JTable(model);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        return scrollPane;
    }

    private Object [] createMarketStockPanel(MarketStock stock) {
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String stockName = stock.getStockName();
        Stock stock_orgin=Database.getStock(stockName);

        Object [] items = {stockName,stock.getQuantity(),stock_orgin.getCurrentPrice()};

        return items;
    }

    private JPanel tradeStockPanel(int accountNumber, TradingAccount tradingAccount){
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
        stockQuantity.setToolTipText("Quantity");

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(stockQuantityField, gbc);
        gbc.gridy = 1;
        panel.add(stockQuantity, gbc);


        JButton buyButton = new JButton("Buy");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(buyButton, gbc);

        JButton sellButton = new JButton("Sell");
        gbc.gridx = 1;
        panel.add(sellButton, gbc);


        JLabel searchLabel = new JLabel("Search");
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");

        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(searchLabel, gbc);
        gbc.gridy = 1;
        panel.add(searchField, gbc);
        gbc.gridy = 2;
        panel.add(searchButton, gbc);


        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = stockName.getText();
                    int quantity = Integer.parseInt(stockQuantity.getText());
                    Boolean result = Trading.buyStock(accountNumber, name, quantity);
                    if(result){
                        JOptionPane.showMessageDialog(null, "You have successfully bought " + quantity + " " + name + " stocks");
                        refreshMarketPanel();
                    }
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(null, "Invalid quantity");
                }
            }
        });

        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String name = stockName.getText();
                    int quantity = Integer.parseInt(stockQuantity.getText());
                    Boolean result = Trading.sellStock(accountNumber, name, quantity);
                    if(result){
                        JOptionPane.showMessageDialog(null, "You have successfully sold " + quantity + " " + name + " stocks");
                        refreshMarketPanel();
                    }
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(null, "Invalid quantity");
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText();
                refreshMarketPanel(keyword);
            }
        });

        return panel;
    }

    private JPanel createExitPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
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

        JButton stockButton = new JButton("View stocks in account");
        stockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stockPage.refresh();
                frame.dispose();
            }
        });

        buttonPanel.add(accountButton);
        buttonPanel.add(stockButton);
        buttonPanel.add(logoutButton);

        return buttonPanel;
    }

    private JScrollPane createStockListScrollPane(String keyword) {
        List<MarketStock> marketStocks = Market.getStocks(keyword);
        Object data [][] = new Object[marketStocks.size()][3];

        JPanel stockListPanel = new JPanel();
        stockListPanel.setLayout(new BoxLayout(stockListPanel, BoxLayout.Y_AXIS));
        for (MarketStock stock : marketStocks) {
            Object [] items = createMarketStockPanel(stock);
            data[marketStocks.indexOf(stock)] = items;
        }

        Object[] columnNames = {"Stock Name", "Quantity", "Current Price ($)"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);


        // Create the table
        JTable table = new JTable(model);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        return scrollPane;
    }
    private void refreshMarketPanel() {
        frame.dispose();
        new MarketPage(accountNumber, accountPage, stockPage, loginPage).run();
    }
    private void refreshMarketPanel(String keyword) {
        for (Component component : marketPanel.getComponents()) {
            if (BorderLayout.EAST.equals(((BorderLayout) marketPanel.getLayout()).getConstraints(component))) {
                marketPanel.remove(component);
                break;
            }
        }
        JScrollPane newStockListScrollPane = createStockListScrollPane(keyword);
        marketPanel.add(newStockListScrollPane, BorderLayout.EAST);
        marketPanel.revalidate();
        marketPanel.repaint();
    }
}