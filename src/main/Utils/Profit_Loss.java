package main.Utils;
import main.Stocks.Stock;

public class Profit_Loss {
    private Stock stock;
    private double profitLoss;

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
