package dataProc;

import userException.NoStock;
import userException.StockLess;

public class StockNum {
    public static void checkStockNum(int stockNum){
        if (stockNum == 0){
            System.out.println("---------------------------");
            try {
                throw new NoStock();
            } catch (NoStock noStock) {
                noStock.printStackTrace();
            }
        }else if (stockNum == 1){
            System.out.println("---------------------------");
            try {
                throw new StockLess();
            } catch (StockLess stockLess) {
                stockLess.printStackTrace();
            }
        }
    }
}
