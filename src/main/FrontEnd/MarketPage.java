package main.FrontEnd;

import main.Accounts.TradingAccount;
import main.Database.Database;
import main.PortfolioManager.Trading;
import main.Stocks.*;

import javax.swing.*;
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
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private Market market;
    private int accountNumber;
    private TradingAccount tradingAccount;
    private Trading trading;

    public MarketPage(int accountNumber) {
        this.accountNumber = accountNumber;
        this.tradingAccount = Database.getTradingAccount(accountNumber);
        this.marketPanel = createMarketPanel();
    }
    public void run() {
        frame = new JFrame("Stock Market");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);

        frame.add(this.marketPanel, BorderLayout.CENTER);
        JPanel topPanel = buyStockPanel(this.accountNumber, this.tradingAccount, this.trading);
        frame.add(topPanel, BorderLayout.NORTH);
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

        JPanel stockListPanel = new JPanel();
        stockListPanel.setLayout(new BoxLayout(stockListPanel, BoxLayout.Y_AXIS));
            for (MarketStock stock : marketStocks) {
                JPanel stockPanel = createMarketStockPanel(stock);
                stockListPanel.add(stockPanel);
            }

        JScrollPane scrollPane = new JScrollPane(stockListPanel);
        return scrollPane;
    }

        private JPanel createMarketStockPanel(MarketStock stock) {
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String stockName = stock.getStockName();
        Stock stock_orgin=Database.getStock(stockName);

        JLabel stockLabel = new JLabel(stockName+" | Quantity: " + stock.getQuantity() + " | Price: " + stock_orgin.getCurrentPrice());
        stockPanel.add(stockLabel);

        return stockPanel;
    }

    private JPanel buyStockPanel(int accountNumber, TradingAccount tradingAccount, Trading trading){
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


        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = stockName.getText();
                    int quantity = Integer.parseInt(stockQuantity.getText());
                    Boolean result = trading.buyStock(accountNumber, name, quantity);
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
                    Boolean result = trading.sellStock(accountNumber, name, quantity);
                    if(result){
                        JOptionPane.showMessageDialog(null, "You have successfully sold " + quantity + " " + name + " stocks");
                        refreshMarketPanel();
                    }
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(null, "Invalid quantity");
                }
            }
        });

        return panel;
    }

    private void refreshMarketPanel() {
//        JPanel newViewAccountsPanel = createMarketPanel();
//        marketPanel.removeAll();
//        marketPanel.add(newViewAccountsPanel, BorderLayout.CENTER);
//        marketPanel.revalidate();
//        marketPanel.repaint();
        frame.dispose();
        new MarketPage(accountNumber).run();
    }

}