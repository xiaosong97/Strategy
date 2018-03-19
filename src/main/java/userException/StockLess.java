package userException;

public class StockLess extends Exception{
    public StockLess(){
        System.out.println("该板块内具有开牌超过150天的股票数过少");
    }
}
