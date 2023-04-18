package main.Stocks;

/**
 * @Description: This class represents trading between customers and the market, a singleton class
 * @Methods: trade: trade between customers and the market
 * @Params: cs, a customer's stocks; stock, a stock; num, the number of stocks; way, buy or sell
 */
public class Trading {
    // The trading is a singleton class
    private Trading() {}
    private static Trading trading = new Trading();
    
    public static boolean trade(CustomerStocks cs,Stock stock, int num, String way) {
        if(way.equals("buy")) {
            try {
                Market.sellStocks(stock, num);
                cs.buyStock(stock, num);
                return true;
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        else if(way.equals("sell")) {
            try {
                Market.buyStocks(stock, num);
                cs.sellStock(stock, num);
                return true;
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        else {
            throw new RuntimeException("way must be buy or sell");
        }
    }
}
