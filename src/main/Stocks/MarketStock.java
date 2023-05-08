package main.Stocks;

/*
    * @Description: This class makes a class for stocks in the market called by the market class to initialize a list of those stocks
    * Constructor:
        * MarketStock(String stockName, int quantity) - initializes the stockName and quantity of the stock
    * getStockName() - returns the stockName
    * setStockName(String stockName) - sets the stockName
    * getQuantity() - returns the quantity of the stock
    * setQuantity(int quantity) - sets the quantity of the stock
 */

public class MarketStock {
    private String stockName;
    private int quantity;

    public MarketStock(String stockName, int quantity) {
        this.stockName = stockName;
        this.quantity = quantity;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
