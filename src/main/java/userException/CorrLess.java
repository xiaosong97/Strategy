package userException;

public class CorrLess extends Exception {
    public CorrLess(){
        System.out.println("未找到收盘价相关性超过0.9的股票对");
    }
}
