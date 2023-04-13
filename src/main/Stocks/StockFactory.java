package main.Stocks;

/*
    * This class is a factory used to create a stock object
 */

public class StockFactory {
    public static Stock createStock(String stockType, String stockName, double stockPrice) {
        return new Stock(stockType, stockName, stockPrice);
    }
}
