package dataGet;
import java.sql.*;
import java.util.ArrayList;

public class MySqlDataGet {
    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/stock";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "Code@n1ght";
    private ArrayList tickerCodeList = new ArrayList();
    private ArrayList tickerNameList = new ArrayList();
    private int stockNum;
    private int maxCpNum;      //行情数据最多的一支股票的行情数据条数
    private int minCpNum;      //行情数据最少的一支股票的行情数据条数
    private ArrayList cpNum = new ArrayList();

    public String[] getTickerCodeList() {
        String[] tick = new String[stockNum];
        for (int i = 0;i<stockNum;i++){
            tick[i] = (String) tickerCodeList.get(i);
        }
        return tick;
    }

    public String[] getTickerNameList() {
        String[] tick = new String[stockNum];
        for (int i = 0;i<stockNum;i++){
            tick[i] = (String) tickerNameList.get(i);
        }
        return tick;
    }

    private ArrayList[] cp;
    Connection conn = null;
    Statement stmt = null;
    public double[][] getCpData(String segment){
        buildConn();
        try {
            getTickerCode(segment);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            cp = new ArrayList[stockNum];
            cp = queryCp();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double[][] cpT = new double[stockNum][minCpNum];
        for (int i = 0;i<stockNum;i++){ //将所有查询到的股票行情数据调整为同样的条数
            int cpindex = cp[i].size()-1;
            int cpTindex = minCpNum-1;
            int num = 0;
            while (num < minCpNum){
                cpT[i][cpTindex] = (Double)cp[i].get(cpindex);
                cpindex--;
                cpTindex--;
                num++;
            }
        }
        return cpT;
    }

    private ArrayList[] queryCp() throws SQLException {
        ArrayList[] cp = new ArrayList[stockNum];
        int temp = 0;
        for (int i =0;i<stockNum;i++){
            //查询各支股票的行情数据的条数
            String sql0 = "select count(*) from `" + tickerCodeList.get(i) + "`;";
            ResultSet rs0 = stmt.executeQuery(sql0);
            while (rs0.next()){
                temp = rs0.getInt(1);
            }
            if (temp<150) {   //去除掉行情数据条数少于150条的股票
                tickerCodeList.remove(i);
                tickerNameList.remove(i);
                stockNum--;
            }
        }
        for (int i =0;i<stockNum;i++){
            String sql = "select * from `" + tickerCodeList.get(i) + "`;";
            ResultSet rs = stmt.executeQuery(sql);
            temp = 0;
            cp[i] = new ArrayList();
            while (rs.next()){
                cp[i].add(temp,rs.getDouble(4));
                temp++;
            }
            if (i == 0){
                minCpNum = temp;
                maxCpNum = temp;
            }else if (temp > maxCpNum){
                maxCpNum = temp;
            }else if (temp < minCpNum){
                minCpNum = temp;
            }
        }
        return cp;
    }

    private void getTickerCode(String segment) throws SQLException {
        String sql = "select * from code_segment where segment = '" + segment + " ';";
        ResultSet rs = stmt.executeQuery(sql);
        stockNum = 0;
        while (rs.next()){
            tickerCodeList.add(stockNum,rs.getString(1));
            tickerNameList.add(stockNum,rs.getString(3));
            stockNum++;
        }
    }

    public void buildConn(){
        try{
            // 注册 JDBC 驱动
            Class.forName("com.mysql.jdbc.Driver");

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}