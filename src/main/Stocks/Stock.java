package main.Stocks;


/**
 * @Description: This class represents a stock. It implements the IStock interface.
 * @Params: symbol, a company name, and a current price.
 */

public class Stock {
    private String name;
    private String companyName;
    private double currentPrice;

    public Stock(String name, String companyName, double currentPrice) {
        this.name = name;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    @Override
    public String toString() {
        return "Stock{" +
                "name='" + name + '\'' +
                ", companyName='" + companyName + '\'' +
                ", currentPrice=" + currentPrice +
                '}';
    }
}

