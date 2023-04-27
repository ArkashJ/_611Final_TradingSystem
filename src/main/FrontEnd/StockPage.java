package main.FrontEnd;

import main.Database.Database;
import main.Accounts.TradingAccount;
import main.Stocks.CustomerStocks;
import main.Stocks.Stock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: show the customerStock of a user, and user can go to the Market to buy stocks.txt
 * stocks.txt with detail: name, currentprice, quantity, highest price, lowest price, profit/loss
 */
public class StockPage {
    private int accountNumber;
    private JFrame frame;
    private AccountPage accountPage;

    public StockPage(int accountNumber, AccountPage accountPage) {
        this.accountNumber = accountNumber;
        this.accountPage = accountPage;
    }

    public void run() {
        frame = new JFrame("Stock Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel stockPanel = createStockPanel();

        frame.add(stockPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel createStockPanel() {
        CustomerStocks customerStocks = Database.getCustomerStocks(this.accountNumber);

        JPanel panel = new JPanel(new BorderLayout());
        JPanel stockListPanel = new JPanel();
        stockListPanel.setLayout(new BoxLayout(stockListPanel, BoxLayout.Y_AXIS));

        JButton enterMarketButton = new JButton("Enter Trading Market");
//        enterMarketButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                StockPage stockPage = new StockPage(account.getAccountNumber(), AccountPage.this);
//                stockPage.run();
//            }
//        });

        panel.add(enterMarketButton, BorderLayout.CENTER);

        int size = customerStocks.getStockNames().size();
        if(size==0){
            JLabel noStockLabel = new JLabel("You do not own any stocks");
            panel.add(noStockLabel, BorderLayout.CENTER);
        }
        else{
            for (int i=0;i<size;i++){
                JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel stockLabel = new JLabel("Stock Name: " + customerStocks.getStockNames().get(i)
                        + " | Price Bought: " + customerStocks.getStock_BoughtPrices().get(i)
                + " | Quantity: " + customerStocks.getStockNumbers().get(i));
                stockPanel.add(stockLabel);

                stockListPanel.add(stockPanel);
            }
            JScrollPane scrollPane = new JScrollPane(stockListPanel);
            panel.add(scrollPane, BorderLayout.CENTER);

        }

        return panel;
    }

}
