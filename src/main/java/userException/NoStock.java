package userException;

public class NoStock extends Exception {
    public NoStock(){
        System.out.println("该板块未找到开牌超过150天的股票");
    }
}
