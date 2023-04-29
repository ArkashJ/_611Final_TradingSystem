package main.Stocks;

/*
    * This class is a factory used to create a stock object
    @Description: This class is a SINGLETON factory used to create a stock object
    * Methods:
        * createStock(String name, String CompanyName, double stockPrice, double lastClosingPrice, double highPrice, double lowPrice, double dividend) - creates a stock object
        * copyStock(Stock) - copies a stock object
        * copyStock_withPriceBoughtAt(Stock, double priceBoughtAt) - copies a stock object and sets the price bought at
 */

public class StockFactory {

    private StockFactory() {   
    }

    private static StockFactory stockFactory = new StockFactory();
    // making a static stock factory
    public static Stock createStock(String name, String CompanyName, double stockPrice, double lastClosingPrice, double highPrice, double lowPrice, double dividend) {
        return new Stock(name, CompanyName, stockPrice, lastClosingPrice, highPrice, lowPrice, dividend);
    }
    public static Stock copyStock(Stock stock) {
        return new Stock(stock.getName(), stock.getCompanyName(), stock.getCurrentPrice(), stock.getLastClosingPrice(), stock.getHighPrice(), stock.getLowPrice(), stock.getDividend());
    }
    public static Stock copyStock_withPriceBoughtAt(Stock stock, double priceBoughtAt) {
        return new Stock(stock.getName(), stock.getCompanyName(), stock.getCurrentPrice(), stock.getLastClosingPrice(), stock.getHighPrice(), stock.getLowPrice(), stock.getDividend(), priceBoughtAt);
    }
}
