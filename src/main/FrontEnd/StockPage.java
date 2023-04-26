//package main.FrontEnd;
//
//import main.Database.Database;
//import main.Accounts.TradingAccount;
//import main.Stocks.CustomerStocks;
//import main.Stocks.Stock;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @Description: show the customerStock of a user, and user can go to the Market to buy stocks.txt
// * stocks.txt with detail: name, currentprice, quantity, highest price, lowest price, profit/loss
// */
//public class StockPage {
//    private int accountNumber;
//    private JFrame frame;
//    private AccountPage accountPage;
//
//    public StockPage(int accountNumber, AccountPage accountPage) {
//        this.accountNumber = accountNumber;
//        this.accountPage = accountPage;
//    }
//
//    public void run() {
//        frame = new JFrame("Stock Management");
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frame.setSize(800, 600);
//
//        JPanel stockPanel = createStockPanel();
//
//        frame.add(stockPanel, BorderLayout.CENTER);
//
//        frame.setVisible(true);
//    }
//
//    private JPanel createStockPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        TradingAccount account = Database.getTraddingAccount(this.accountNumber);
//        CustomerStocks customerStocks = account.getCustomerStocks();
//        HashMap<Stock, Integer> stocksAndQuantity = customerStocks.getStocks();
//
//        DefaultListModel<String> listModel = new DefaultListModel<>();
//        for (Map.Entry<Stock, Integer> entry : stocksAndQuantity.entrySet()) {
//            Stock stock = entry.getKey();
//            int quantity = entry.getValue();
//            listModel.addElement(
//                    stock.getName()
//                            + " | Company: " + stock.getCompanyName()
//                            + " | Quantity: " + quantity
//                            + " | Current Price: " + stock.getCurrentPrice()
//                            + " | Highest Price: " + stock.getHighPrice()
//                            + " | Lowest Price: " + stock.getLowPrice()
//            );
//        }
//        JList<String> stockList = new JList<>(listModel);
//        panel.add(new JScrollPane(stockList), BorderLayout.CENTER);
//
//        return panel;
//    }
//
//}
