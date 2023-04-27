package main.Stocks;

import main.Database.Database;

public class CustomerStock {
    private String stockName;
    private int stockNumber;
    private double stockBoughtPrice;

    public CustomerStock(String stockName, int stockNumber, double stockBoughtPrice) {
        this.stockName = stockName;
        this.stockNumber = stockNumber;
        this.stockBoughtPrice = stockBoughtPrice;
    }

    public String getStockName() {
        return stockName;
    }

    public int getStockNumber() {
        return stockNumber;
    }

    public double getStockBoughtPrice() {
        return stockBoughtPrice;
    }

    public double getProfit() {
        Stock stock = Database.getStock(stockName);
        double currentPrice = stock.getCurrentPrice();
        double profit = (currentPrice - stockBoughtPrice) * stockNumber;
        return profit;
    }
}
