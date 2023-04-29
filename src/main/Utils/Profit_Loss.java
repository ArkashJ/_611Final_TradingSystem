package main.Utils;
import main.Stocks.Stock;

/**
 * @Description: This class is used to calculate the profit/loss of a stock
 * Profit_Loss() - constructor to initialize stock and profitLoss
 * getProfitLoss() - returns profitLoss
 * setProfitLoss() - sets profitLoss
 * calculateProfitLoss() - calculates the profit/loss of a stock
 */

public class Profit_Loss {
    private Stock stock;
    private double profitLoss;

    public Profit_Loss() {
        this.stock = null;
        this.profitLoss = 0;
    }

    public Profit_Loss(Stock stock, double profitLoss) {
        this.stock = stock;
        this.profitLoss = profitLoss;
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(double profitLoss) {
        this.profitLoss = profitLoss;
    }

    public void calculateProfitLoss(){

    }

}
