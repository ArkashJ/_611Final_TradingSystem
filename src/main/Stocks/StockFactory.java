package main.Stocks;

/*
    * This class is a factory used to create a stock object
    @Description: This class is a SINGLETON factory used to create a stock object
 */

public class StockFactory {

    private StockFactory() {   
    }

    private static StockFactory stockFactory = new StockFactory();
    // making a static stock factory
    public static Stock createStock(String stockType, String stockName, double stockPrice, double lastClosingPrice, double highPrice, double lowPrice, double dividend) {
        return new Stock(stockType, stockName, stockPrice, lastClosingPrice, highPrice, lowPrice, dividend);
    }
}
