package main.Utils;

/**
 * @Description: used to calculate profit/loss in an account
 */
public class Profit {
    protected double profit;
    public Profit(){
        this.profit=0;
    }
    public void add(double profit) {
        this.profit+=profit;
    }
    public void subtract(double loss) {
        this.profit-=loss;
    }
    public double getProfit() {
        return this.profit;
    }
}
