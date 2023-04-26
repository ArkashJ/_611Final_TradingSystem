package main.Stocks;

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
}
