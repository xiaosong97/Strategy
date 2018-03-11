package dataGet;

public class test {
    public static void main(String[] args){
        MySQLDemo ds = new MySQLDemo();
        double[][] cp = ds.getCp();
        int cpNum = ds.getCpNum();
        for (int i = 0;i<4;i++){
            System.out.println("stock"+(i+1)+" cp price is:");
            for (int j = 0;j<cpNum;j++)
                System.out.print(cp[i][j]+"     ");
            System.out.println();
        }

    }
}
