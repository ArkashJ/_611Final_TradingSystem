//package main.Utils;
//
//import main.Stocks.CustomerStocks;
//import main.Stocks.Stock;
//
//import java.util.HashMap;
//
///**
// * @Description: used to calculate profit/loss in an account
// */
//public class Profit {
//
//    protected HashMap<Stock, Double> profit;
//    public Profit() {
//        this.profit = new HashMap<>();
//    }
//    public void buyStock(Stock stock,int num) {
//        if(num<=0) {
//            throw new RuntimeException("nums must be larger than 0");
//        }
//        else{
//            if(profit.containsKey(stock)) {
//                profit.put(stock, profit.get(stock)-stock.getCurrentPrice()*num);
//            }
//            else {
//                profit.put(stock, -stock.getCurrentPrice()*num);
//            }
//        }
////    }
//    public void sellStock(Stock stock, int num) {
//        if(num<=0) {
//            throw new RuntimeException("nums must be larger than 0");
//        }
//        else{
//            if(profit.containsKey(stock)) {
//                profit.put(stock, profit.get(stock)+stock.getCurrentPrice()*num);
//            }
//            else {
//                throw new RuntimeException("You don't have this stock");
//            }
//        }
//    }
//
//    public double get_unrealizedProfit_forOneStock(Stock stock,CustomerStocks cs) {
//        if(profit.containsKey(stock)) {
//            int whole_num=cs.getStockNum(stock);
//            return profit.get(stock)+stock.getCurrentPrice()*whole_num;
//        }
//        else {
//            throw new RuntimeException("You don't have this stock");
//        }
//    }
//    public double get_unrealizedProfit(CustomerStocks cs) {
//        double sum=0;
//        for(Stock stock:profit.keySet()) {
//            sum+=profit.get(stock);
//        }
//        for(Stock stock:cs.getStocks().keySet()) {
//            sum+=stock.getCurrentPrice()*cs.getStockNum(stock);
//        }
//        return sum;
//    }
//
//    public double get_realizedProfit() {
//        double sum=0;
//        for(Stock stock:profit.keySet()) {
//            sum+=profit.get(stock);
//        }
//        return sum;
//    }
//}
