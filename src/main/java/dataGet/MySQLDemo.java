package dataGet;
import java.sql.*;

public class MySQLDemo {
    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/stock";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "Code@n1ght";
    private double cp[][] = new double[4][246];
    private int cpNum;

    public int getCpNum() {
        return cpNum;
    }

    public double[][] getCp() {
        Connection conn = null;
        Statement stmt = null;
        try{
            // 注册 JDBC 驱动
            Class.forName("com.mysql.jdbc.Driver");

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = " SELECT close FROM his WHERE code = 600000";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库
            cpNum = 0;
            while(rs.next()){
                // 通过字段检索
                cp[0][cpNum++] = rs.getDouble("close");
            }
            sql = " SELECT close FROM his WHERE code = 600926";
            rs = stmt.executeQuery(sql);
            cpNum = 0;
            while(rs.next()){
                // 通过字段检索
                cp[1][cpNum++] = rs.getDouble("close");
            }

            sql = " SELECT close FROM his WHERE code = 601939";
            rs = stmt.executeQuery(sql);
            cpNum = 0;
            while(rs.next()){
                // 通过字段检索
                cp[2][cpNum++] = rs.getDouble("close");
            }

            sql = " SELECT close FROM his WHERE code = 600919";
            rs = stmt.executeQuery(sql);
            cpNum = 0;
            while(rs.next()){
                // 通过字段检索
                cp[3][cpNum++] = rs.getDouble("close");
            }

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }

        return cp;
    }
}
