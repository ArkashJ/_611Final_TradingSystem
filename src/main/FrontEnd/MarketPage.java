package main.FrontEnd;

import main.Accounts.TradingAccount;
import main.Database.Database;
import main.Stocks.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketPage {

    private JFrame frame;
    //TODO: Create market table in the database
    private Market market;
    private int accountNumber;
    private TradingAccount tradingAccount;

    public void run() {
        frame = new JFrame("Stock Market");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel stockPanel = createMarketPanel();

        frame.add(stockPanel, BorderLayout.CENTER);
        JPanel topPanel = buyStockPanel(this.accountNumber, this.tradingAccount);
        frame.add(topPanel, BorderLayout.NORTH);
//        JPanel midPanel = createMarketPanel();
//        frame.add(midPanel, BorderLayout.WEST);
        frame.setVisible(true);
    }

    private JPanel createMarketPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane marketStocksScrollPane = createStockListScrollPane(true);
        panel.add(marketStocksScrollPane);

        return panel;
    }

    private JScrollPane createStockListScrollPane(boolean isUserStocks) {
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

    private JPanel buyStockPanel(int accountNumber, TradingAccount tradingAccount){
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
                String name = stockName.getText();
                if(Database.getStock(name) != null){
                    try{
                        int quantity = Integer.parseInt(stockQuantity.getText());
                        Stock bought = Database.getStock(name);
                        //TODO: Unable to insert stock into customer stocks
                        Database.insertStockIntoCustomerStocks(accountNumber, name,bought.getPriceBoughtAt(),quantity); //Add bought stock to customer database
                        tradingAccount.getCustomerStocks().add(name,quantity,bought.getPriceBoughtAt()); //Add bought stock to trading account
                        tradingAccount.setBalance(tradingAccount.getBalance()-(bought.getPriceBoughtAt()*quantity)); //Update balance in trading account

                        List<MarketStock> marketStocks = Market.getStocks();
                        for (MarketStock stock : marketStocks) { //Update quantity of stock in market
                            if(stock.getStockName().equals(name)){
                                stock.setQuantity(stock.getQuantity()-quantity);
                                if(stock.getQuantity()==0){
                                    Market.removeStock(name);
                                }
                            }
                        }
                        Database.setMarketStocks();
                        JOptionPane.showMessageDialog(null, "You have successfully bought "+quantity+" of "+name);
                    }
                    catch (NumberFormatException ex){
                        JOptionPane.showMessageDialog(null, "Please enter a valid quantity");
                    }

                }
                else{
                    JOptionPane.showMessageDialog(null, "Stock does not exist");
                }
            }
        });

        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = stockName.getText();
                if(Database.getCustomerStocks(accountNumber).getStock(name)!=null){
                    try{
                        int quantity = Integer.parseInt(stockQuantity.getText());
                        CustomerStock sold = Database.getCustomerStocks(accountNumber).getStock(name);
                        tradingAccount.getCustomerStocks().remove(name,quantity); //Remove sold stock from trading account
                        tradingAccount.setBalance(tradingAccount.getBalance()+(sold.getStockBoughtPrice()*quantity)); //Update balance in trading account
                        Market.addStock(name,quantity); //Add sold stock to market
                        Database.setMarketStocks(); //Update market stocks in database
                        JOptionPane.showMessageDialog(null, "You have successfully sold "+quantity+" of "+name);
                    }
                    catch (NumberFormatException ex){
                        JOptionPane.showMessageDialog(null, "Please enter a valid quantity");
                    }

                }
                else{
                    JOptionPane.showMessageDialog(null, "Stock does not exist");
                }
            }
        });

        return panel;
    }

}