package main.Stocks;


/**
 * @Description: This class represents a stock and implements the IStock interface. This is the basic element for the entire project used by the MarketStock and CustomerStock classes.
 * @Params: symbol, a company name, and a current price.
    *   Constructor:
        *   Stock(String name, String companyName, double currentPrice, double lastClosingPrice, double highPrice, double lowPrice, double dividend) -
            ### initializes the name, companyName, currentPrice, lastClosingPrice, highPrice, lowPrice, and dividend.
    * getName()
 *  getCompanyName() - returns the company name
 *  getCurrentPrice() - returns the current price
 *  setCurrentPrice(double currentPrice) - sets the current price
 *  updatePrice(double newPrice) - updates the current price
 *  getLastClosingPrice() - returns the last closing price
 *  setLastClosingPrice(double lastClosingPrice) - sets the last closing price
 *  getHighPrice() - returns the high price
 *  setHighPrice(double highPrice) - sets the high price
 *  getLowPrice() - returns the low price
 *  setLowPrice(double lowPrice) - sets the low price
 *  getDividend() - returns the dividend
 *  getPriceBoughtAt() - returns the price bought at
 *  setPriceBoughtAt(double priceBoughtAt) - sets the price bought at
 *  getChangePercent() - returns the change percent
 *  getChangeAmount() - returns the change amount
 *  getChangePercentSinceBought() - returns the change percent since bought
 *  getChangeAmountSinceBought() - returns the change amount since bought
 */

public class Stock {
    private String name;
    private String companyName;
    private double currentPrice;
    private double lastClosingPrice;
    private double highPrice;
    private double lowPrice;
    private final double dividend;
    private double priceBoughtAt;

    public Stock(String name, String companyName, double currentPrice, double lastClosingPrice, double highPrice, double lowPrice, double dividend) {
        this.name = name;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
        this.lastClosingPrice =  lastClosingPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.dividend = dividend;
        this.priceBoughtAt = 0;
    }
    public Stock(String name, String companyName, double currentPrice, double lastClosingPrice, double highPrice, double lowPrice, double dividend,double priceBoughtAt) {
        this(name, companyName, currentPrice, lastClosingPrice, highPrice, lowPrice, dividend);
        this.priceBoughtAt = priceBoughtAt;
    }

    public String getName() {
        return name;
    }

    public String getCompanyName() {
        return companyName;
    }


    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void updatePrice(double newPrice) {
        setCurrentPrice(newPrice);
    }

    public double getLastClosingPrice() {
        return lastClosingPrice;
    }

    public void setLastClosingPrice(double lastClosingPrice) {
        this.lastClosingPrice = lastClosingPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public double getDividend() {
        return dividend;
    }

    public double getPriceBoughtAt() {
        return priceBoughtAt;
    }

    public void setPriceBoughtAt(double priceBoughtAt) {
        this.priceBoughtAt = priceBoughtAt;
    }

    public double getChangePercent() {
        return (currentPrice - lastClosingPrice) / lastClosingPrice;
    }

    public double getChangeAmount() {
        return currentPrice - lastClosingPrice;
    }

    public double getChangePercentSinceBought() {
        return (currentPrice - priceBoughtAt) / priceBoughtAt;
    }

    public double getChangeAmountSinceBought() {
        return currentPrice - priceBoughtAt;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "name='" + name + '\'' +
                ", companyName='" + companyName + '\'' +
                ", currentPrice=" + currentPrice +
                ", lastClosingPrice=" + lastClosingPrice +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                ", dividend=" + dividend +
                '}';
    }
}

