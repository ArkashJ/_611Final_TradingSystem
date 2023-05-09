package main.FrontEnd;

import main.Accounts.TradingAccount;
import main.Database.Database;
import main.PortfolioManager.BankManager;
import main.Stocks.Market;
import main.Stocks.MarketStock;
import main.Stocks.Stock;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


/**
 * @Description: This class is used to create a Manager Page
 * This class is part of the main.FrontEnd package and provides an interface for the manager/administrator.
 * The manager can view and manage accounts, update stock prices in the market, review account requests,
 * and view transaction logs. The interface also allows the manager to toggle the visibility of different
 * sections within the page.
 *
 * Dependencies: main.Accounts.TradingAccount, main.Database.Database, main.PortfolioManager.BankManager,
 * main.Stocks.Market, main.Stocks.MarketStock, main.Stocks.Stock
 */

public class ManagerPage {
    private JFrame frame;
    private String AdminName;
    private boolean accountsInfoVisible = true;
    private boolean marketStocksVisible = true;
    private boolean logVisible = true;
    private JScrollPane accountsInfoScrollPane;
    private JScrollPane marketStocksScrollPane;
    private JScrollPane logScrollPane;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public ManagerPage(String AdminName) {
        this.AdminName = AdminName;
    }

    public void run() {
        frame = new JFrame("Manager Page");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Add the Review Requests button back
        JButton reviewRequestsButton = new JButton("Review Requests");

        // Accounts info and profits panel
        accountsInfoScrollPane = createAccountsInfoScrollPane();
        mainPanel.add(accountsInfoScrollPane, BorderLayout.WEST);

        // Market stocks panel
        marketStocksScrollPane = createMarketStocksScrollPane();
        mainPanel.add(marketStocksScrollPane, BorderLayout.CENTER);

        // Log panel
        logScrollPane = createLogScrollPane();
        mainPanel.add(logScrollPane, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton toggleAccountsButton = new JButton("Toggle Accounts");
        buttonPanel.add(toggleAccountsButton);
        JButton toggleMarketButton = new JButton("Toggle Market");
        buttonPanel.add(toggleMarketButton);
        JButton toggleLogButton = new JButton("Toggle Log");
        buttonPanel.add(toggleLogButton);
        buttonPanel.add(reviewRequestsButton);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Add an ActionListener to toggle the visibility of each section
        toggleAccountsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accountsInfoVisible = !accountsInfoVisible;
                accountsInfoScrollPane.setVisible(accountsInfoVisible);
                refreshSize();
            }
        });

        toggleMarketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                marketStocksVisible = !marketStocksVisible;
                marketStocksScrollPane.setVisible(marketStocksVisible);
                refreshSize();
            }
        });

        toggleLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logVisible = !logVisible;
                logScrollPane.setVisible(logVisible);
                refreshSize();
            }
        });

        // Add the ActionListener for the Review Requests button back
        reviewRequestsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RequestReviewPage requestReviewPage = new RequestReviewPage();
                requestReviewPage.run();
            }
        });

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public void refreshSize() {
        frame.pack();
        frame.repaint();
    }

    private JScrollPane createAccountsInfoScrollPane() {
        JPanel accountsInfoPanel = new JPanel();
        accountsInfoPanel.setLayout(new BoxLayout(accountsInfoPanel, BoxLayout.Y_AXIS));

        String sql = "SELECT * FROM accounts WHERE account_type = 'TRADE';";
        Connection conn = Database.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet resultSet = pstmt.executeQuery();

            // Create the table model
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Name");
            model.addColumn("Account");
            model.addColumn("Balance");
            model.addColumn("Realized Profit");
            model.addColumn("Unrealized Profit");

            while (resultSet.next()) {
                String ownerName = resultSet.getString("user_name");
                int accountNumber = resultSet.getInt("account_number");
                double balance = resultSet.getDouble("balance");
                Map<String, Double> profits = BankManager.calculateProfits(accountNumber);

                // Add row to the table model
                model.addRow(new Object[] {
                        ownerName,
                        accountNumber,
                        balance,
                        profits.get("realized"),
                        profits.get("unrealized")
                });
            }

            // Create the table with font size 18
//            Font tableFont = new Font(Font.DIALOG, Font.PLAIN, 18);
            JTable table = new JTable(model);
//            table.setFont(tableFont);
            table.setShowVerticalLines(true);
            table.setShowHorizontalLines(true);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
            table.setDefaultRenderer(Object.class, centerRenderer);

            TableColumnModel columnModel = table.getColumnModel();

            // Adjust the width of each column based on the largest entry
            for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
                TableColumn column = columnModel.getColumn(columnIndex);
                int maxWidth = 0;
                TableCellRenderer renderer = column.getHeaderRenderer();
                if (renderer == null) {
                    renderer = table.getTableHeader().getDefaultRenderer();
                }
                Component headerComp = renderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, -1, columnIndex);
                maxWidth = Math.max(maxWidth, headerComp.getPreferredSize().width);

                for (int row = 0; row < table.getRowCount(); row++) {
                    TableCellRenderer cellRenderer = table.getCellRenderer(row, columnIndex);
                    Component comp = table.prepareRenderer(cellRenderer, row, columnIndex);
                    maxWidth = Math.max(maxWidth, comp.getPreferredSize().width);
                }

                column.setPreferredWidth(maxWidth + 10); // Adjusted column width with padding
            }

            // Add the table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(table);
            return scrollPane;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }




    private JScrollPane createMarketStocksScrollPane() {
        List<MarketStock> marketStocks = Market.getStocks();
        JPanel marketStocksPanel = new JPanel();
        marketStocksPanel.setLayout(new BoxLayout(marketStocksPanel, BoxLayout.Y_AXIS));

        for (MarketStock marketStock : marketStocks) {
            JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            String stockName = marketStock.getStockName();
            int quantity = marketStock.getQuantity();
            Stock stock = Database.getStock(stockName);
            double currentPrice = stock.getCurrentPrice();

            JLabel stockLabel = new JLabel(stockName + " | Quantity: " + quantity + " | Current Price: " + currentPrice);
            stockPanel.add(stockLabel);

            JTextField newPriceField = new JTextField(5);
            stockPanel.add(newPriceField);

            JButton setPriceButton = new JButton("Set Price");
            setPriceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    double newPrice;
                    try {
                        newPrice = Double.parseDouble(newPriceField.getText());
                        if(newPrice < 0) {
                            throw new NumberFormatException("price < 0");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid price entered.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (newPrice <= 0) {
                        JOptionPane.showMessageDialog(frame, "Invalid price. Please enter a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    BankManager.updateStockPrice(stockName, newPrice);
                    JOptionPane.showMessageDialog(frame, "Price updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                }
            });
            stockPanel.add(setPriceButton);
            marketStocksPanel.add(stockPanel);
        }

        JScrollPane scrollPane = new JScrollPane(marketStocksPanel);
        return scrollPane;
    }

    private JScrollPane createLogScrollPane() {
        // Create the table model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Account");
        model.addColumn("Stock");
        model.addColumn("Price");
        model.addColumn("Quantity");
        model.addColumn("Time");

        String sql = "SELECT * FROM log ORDER BY time DESC;";
        Connection conn = Database.getConnection();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                int accountNumber = resultSet.getInt("account_number");
                String stock = resultSet.getString("stock");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                String time = sdf.format(resultSet.getTimestamp("time"));

                // Add row to the table model
                model.addRow(new Object[]{
                        accountNumber,
                        stock,
                        price,
                        quantity,
                        time
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create the table
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Ensure horizontal scroll bar appears
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        // Adjust column width for "Time" column
        TableColumnModel columnModel = table.getColumnModel();
        TableColumn timeColumn = columnModel.getColumn(4);
        int preferredWidth = 200; // Set your preferred width for the "Time" column
        timeColumn.setPreferredWidth(preferredWidth);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        return scrollPane;
    }



    public void refresh() {
        frame.dispose();
        run();
    }
}