package main.Stocks;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: This class represents a customer's stocks. like a bag
 * @Methods: getStock(Stock) if a stock name is given, return all stocks with the same name
 */

public class CustomerStocks {
    // stocks: key -> stock, value -> number of stocks
    protected HashMap<Stock, Integer> stocks;
    public HashMap<Stock, Integer> getStocks() {
        return stocks;
    }

    public void setStocks(HashMap<Stock, Integer> stocks) {
        this.stocks = stocks;
    }

    public CustomerStocks() {
        stocks = new HashMap<>();
    }

    public List<Stock> getStocks(String StockName) {
        List<Stock> stocks = new ArrayList<>();
        for (Stock stock : this.stocks.keySet()) {
            if (stock.getName().equals(StockName)) {
                stocks.add(stock);
            }
        }
        return stocks;
    }
    public List<Stock> getStocks(Stock stock_orgin) {
        return getStocks(stock_orgin.getName());
    }

    public void add(Stock stock, int num) {
        if (stocks.containsKey(stock)) {
            stocks.put(stock, stocks.get(stock) + num);
        } else {
            stocks.put(stock, num);
        }
    }

    public void remove(Stock stock, int num) {
        if (stocks.containsKey(stock)) {
            if (stocks.get(stock) > num) {
                stocks.put(stock, stocks.get(stock) - num);
            } else {
                stocks.remove(stock);
            }
        } else {
            throw new RuntimeException("You don't have this stock");
        }
    }


    public int getOriginalStockNum(Stock stock) {
        List<Stock> stocks = getStocks(stock);
        int num = 0;
        for (Stock stock1 : stocks) {
            num += getStockNum(stock1);
        }
        return num;
    }
    public int getStockNum(Stock stock) {
        if (stocks.containsKey(stock)) {
            return stocks.get(stock);
        } else {
            return 0;
        }
    }

    public void viewStocks() {
        System.out.println("Your stocks:");
        System.out.println("StockName\tCompanyName\tCurrentPrice\tNum");
        for (Stock stock : stocks.keySet()) {
            System.out.println(stock.getName()+"\t"+stock.getCompanyName()+"\t"+stock.getCurrentPrice()+"\t"+stocks.get(stock));
        }
    }
}
