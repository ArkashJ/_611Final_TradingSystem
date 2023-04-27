package main.FrontEnd;

import main.Database.Database;
import main.Stocks.CustomerStocks;
import main.Stocks.Market;
import main.Stocks.Stock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class MarketPage {

    private JFrame frame;
    //TODO: Create market table in the database
    private Market market = null;
    private int accountNumber;

    public void run() {
        frame = new JFrame("Stock Market");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel stockPanel = createMarketPanel();

        frame.add(stockPanel, BorderLayout.CENTER);
        GridLayout gridLayout = new GridLayout(1,1);
        JPanel bottomPanel = new JPanel(gridLayout);
        bottomPanel.add(buyStockPanel(this.accountNumber));
        bottomPanel.add(sellStockPanel(this.accountNumber));
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private JPanel createMarketPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        JPanel stockListPanel = new JPanel();
        stockListPanel.setLayout(new BoxLayout(stockListPanel, BoxLayout.Y_AXIS));

        //TODO: Uncomment below once market table is implemented
//        for (Map.Entry<Stock, Integer> stocks : this.market.getStocks().entrySet()) {
//            JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//            JLabel stockLabel = new JLabel("Company: " + stocks.getKey().getCompanyName()
//                    + " | Product Name: " + stocks.getKey().getName()
//                    + " | Current Price: " + stocks.getKey().getCurrentPrice()
//                    + " | Last Closing Price: " + stocks.getKey().getLastClosingPrice()
//            + " | Quantity: " + stocks.getValue());
//            stockPanel.add(stockLabel);
//
//            stockListPanel.add(stockPanel);
//        }
        JScrollPane scrollPane = new JScrollPane(stockListPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buyStockPanel(int accountNumber) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel buyStockField = new JLabel("Buy Stock:");
        JTextField buyStock = new JTextField(20);
        JButton buyButton = new JButton("Buy");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(buyStockField, gbc);
        gbc.gridx = 1;
        panel.add(buyStock, gbc);
        gbc.gridx = 2;
        panel.add(buyButton, gbc);

        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String buy = buyStock.getText();
            }
        });
        return panel;
    }

    private JPanel sellStockPanel(int accountNumber) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel sellStockField = new JLabel("Sell Stock:");
        JTextField sellStock = new JTextField(20);
        JButton sellButton = new JButton("Sell");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(sellStockField, gbc);
        gbc.gridx = 1;
        panel.add(sellStock, gbc);
        gbc.gridx = 2;
        panel.add(sellButton, gbc);

        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String buy = sellStock.getText();
            }
        });
        return panel;
    }

}
