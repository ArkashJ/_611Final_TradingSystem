package main.Stocks;
import main.Database.Database;

/*
    * CustomerStock class is used to store the stocks that the customer has bought with the following features
    * Stores the stock name, number of stocks, and the price at which the stocks were bought.
    * Method to calculate the profit made by the customer on the stocks.
    * Method to set the number of stocks and the price at which the stocks were bought.
    * Methods:
        * getStockName() - returns the name of the stock.
        * getStockNumber() - returns the number of stocks.
        * getStockBoughtPrice() - returns the price at which the stocks were bought.
        * getProfit() - returns the profit made by the customer on the stocks.
        * setStockNumber() - sets the number of stocks.
        * setStockBoughtPrice() - sets the price at which the stocks were bought.
        * Constructor:
            * CustomerStock(String stockName, int stockNumber, double stockBoughtPrice) - initializes the stockName, stockNumber, and stockBoughtPrice.

 */

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

    public void setStockNumber(int stockNumber) {
        this.stockNumber = stockNumber;
    }

    public void setStockBoughtPrice(double stockBoughtPrice) {
        this.stockBoughtPrice = stockBoughtPrice;
    }
}
