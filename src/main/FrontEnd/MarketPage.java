package main.FrontEnd;

import main.Database.Database;
import main.Stocks.CustomerStocks;
import main.Stocks.Market;
import main.Stocks.Stock;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MarketPage {

    private JFrame frame;

    public void run() {
        frame = new JFrame("Stock Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel stockPanel = createMarketPanel();

        frame.add(stockPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel createMarketPanel() {

        //TODO: Create market table in the database
        Market market = null;

        JPanel panel = new JPanel(new BorderLayout());
        JPanel stockListPanel = new JPanel();
        stockListPanel.setLayout(new BoxLayout(stockListPanel, BoxLayout.Y_AXIS));

        for (Map.Entry<Stock, Integer> stocks : market.getStocks().entrySet()) {
            JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel stockLabel = new JLabel("Company: " + stocks.getKey().getCompanyName()
                    + " | Product Name: " + stocks.getKey().getName()
                    + " | Current Price: " + stocks.getKey().getCurrentPrice()
                    + " | Last Closing Price: " + stocks.getKey().getLastClosingPrice()
            + " | Quantity: " + stocks.getValue());
            stockPanel.add(stockLabel);

            stockListPanel.add(stockPanel);
        }
        JScrollPane scrollPane = new JScrollPane(stockListPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
