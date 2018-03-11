package dataProc;

import dataGet.ExcelDataGet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataGet.MySQLDemo;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.log4j.Logger;
public class Corrcoef {
    static Logger logger = Logger.getLogger(Corrcoef.class.getName());
    Map<String,Double> rating_map = new HashMap<String, Double>();
    List<Double> rating_map_list = new ArrayList<Double>();

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
        ExcelDataGet obj = new ExcelDataGet();
        double[][] corr = new double[obj.getStockNum()][obj.getStockNum()];
        //int cpNum = 246;
        int stockNum = 4;
        //Excel路径：
        File file = new File("E:/Projects/成都华迪生产实习/屈松/源代码/银行个股本季度历史行情数据.xls");
        obj.readExcel(file);
        //obj.print();
        Corrcoef[] corrcoef = new Corrcoef[stockNum];
        for (int i = 0;i < stockNum;i++){  //重点注意：需要在此为对象数组的元素分配空间，否则会报错
            corrcoef[i] = new Corrcoef();
        }
        for (int i = 0;i < stockNum;i++){
            for (int j = 0;j < cpNum;j++){
                corrcoef[i].rating_map_list.add(cp[i][j]);
            }
        }
        for (int i = 0;i < stockNum;i++){
            for (int j =0;j < stockNum;j++){
                if (i==j){
                    corr[i][j] = 0;
                }else {
                    corr[i][j] = corrcoef[i].getCorrcoef_bydim(corrcoef[j]);
                }
                //System.out.print(corr[i][j]+"   ");
                logger.info("stock"+(i+1)+" and stock"+(j+1)+"的相关系数为:"+corrcoef[i].getCorrcoef_bydim(corrcoef[j]));
            }
            //System.out.println();
        }

        //求出收盘价相关系数最大的两支股票stockA和stockB
        int maxCpCorrR = 1;     //系数最大的行数
        int maxCpCorrC = 1;     //系数最大的列数
        double maxCpCorr = corr[maxCpCorrR][maxCpCorrC];
        for (int i = 0;i < stockNum;i++){
            for (int j = 0;j<stockNum;j++){
                if (corr[maxCpCorrR][maxCpCorrC] < corr[i][j]){
                    maxCpCorrR = i;
                    maxCpCorrC =j;
                    maxCpCorr = corr[maxCpCorrR][maxCpCorrC];
                }
            }
        }

        double stockA[] = cp[maxCpCorrC];
        double stockB[] = cp[maxCpCorrR];


        //对收盘价序列进行对数处理
        for (int i = 0;i<cpNum;i++){
            stockA[i] = Math.log(stockA[i]);
            stockB[i] = Math.log(stockB[i]);
        }
        for (int i = 0;i<cpNum;i++){
            System.out.print("stockA cp " + (i+1) + " is :"+stockA[i]+",   ");
        }
        System.out.println();
        for (int i = 0;i<cpNum;i++){
            System.out.print("stockB cp " + (i+1) + " is :"+stockB[i]+",   ");
        }
        double X[] = new double[cpNum];
        double Y[] = new double[cpNum];     //存放随机变量y的cpNum个观测值

        double X1[] = new double[cpNum];
        double Y1[] = new double[cpNum];

        for (int i = 0;i<cpNum;i++){
            X[i] = stockA[i];
            Y[i] = stockB[i];
            if (i != cpNum-1){
                X1[i] = X[i+1] - X[i];
                Y1[i] = Y[i+1] - Y[i];
                //System.out.println(Y1[i]);
            }else {
                X1[i] = X[i];
                Y1[i] = Y[i];
            }
        }

        //进行多元线性回归
        int m = 3;      //自变量个数
        int n = cpNum;      //观测数据的维数

        double[] K = new double[m+1];       //返回回归系数 a
        double[][] Xt = new double[m][n];   //每一列存放m个自变量的观测值
        double[] dt = new double[4];        //dt[0]返回偏差平方和q，dt[1]平均标准偏差s，dt[2]复相关系数r，dt[3]回归平方和u
        double[] v = new double[m];     //返回m个自变量的偏自相关系数

        int i;
        Corrcoef.sqt2(Xt,Y,m,n,K,dt,v);
        for (i = 0;i<m+1;i++)
            System.out.println(K[i]);
        for (i = 0;i <= 3;i++){
            System.out.println("a(" + i + ")=" + K[i]);
        }
        System.out.println("偏差平方和q="+dt[0]+"     平均标准偏差s="+dt[1]+"     复相关系数r=" + dt[2]);
        for (i = 0;i <= 2;i++){
            System.out.println("v(" + i + ")=" + v[i]);
        }
        System.out.println("回归平方和u=" + dt[3]);

        /*for (int i = 0;i < m;i++){
            Xt[i][0] = 1;
            Xt[i][1] = X[i];
            Xt[i][2] = X1[i];
            Xt[i][3] = Y1[i];
        }*/
        /*System.out.println(Xt.length+":"+Y.length);
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(Y,Xt);
        double[] beta2 = regression.estimateRegressionParameters();*/
        //System.out.println(beta);
        //还需计算V和mspread

    }

    public Double getCorrcoef_bydim(Corrcoef u){
        if (this.rating_map_list.size()!=u.rating_map_list.size()){
            return null;
        }
        double sim = 0d;    //最后的皮尔逊相关度系数
        double common_item_len = this.rating_map_list.size();   //操作数的个数
        double this_sum =  0d;  //第一个相关数的和
        double u_sum = 0d;      //第二个相关数的和
        double this_sum_sq = 0d;    //第一个相关数的平方和
        double u_sum_sq = 0d;       //第二个相关数的平方和
        double p_sum = 0d;      //两个相关数乘积的和

        for (int i = 0;i<this.rating_map_list.size();i++){
            double this_grade = this.rating_map_list.get(i);
            double u_grade = u.rating_map_list.get(i);

            this_sum += this_grade;
            u_sum += u_grade;
            this_sum_sq +=Math.pow(this_grade,2);
            u_sum_sq += Math.pow(u_grade,2);
            p_sum +=this_grade*u_grade;
        }

        //logger.info("common_items_len:" + common_item_len);
        //logger.info("p_sum:"+p_sum);
        //logger.info("this_sum:"+this_sum);
        //logger.info("u_sum:"+u_sum);
        double num = common_item_len * p_sum - this_sum * u_sum;
        double den = Math.sqrt((common_item_len * this_sum_sq - Math.pow(this_sum,2)) * (common_item_len * u_sum_sq - Math.pow(u_sum,2)));
        //logger.info("" + num + ":" +den);
        sim = (den == 0) ? 1: num/den;
        return sim;
    }
    public static void sqt2(double[][] x,double[] y,int m,int n,double[] a,double[] dt,double[] v){
        int i,j,k,mm;
        double q,e,u,p,yy,s,r,pp;
        double[] b = new double[(m+1)*(m+1)];
        mm = m + 1;
        b[mm * mm - 1] = n;
        for (j = 0;j <= m - 1;j++){
            p = 0.0;
            for (i = 0;i < n - 1;i++){
                p = p + x[j][i];
            }
            b[m * mm + j] = p;
            b[j * mm + m] = p;
        }
        for (i = 0;i <= m - 1;i++){
            for (j = i;j <= m-1;j++){
                p = 0.0;
                for (k = 0;k <= n - 1;k++){
                    p = p + x[i][k] * x [j][k];
                }
                b[j * mm + i] = p;
                b[i * mm + j] = p;
            }
        }
        a[m] = 0.0;
        for (i = 0;i <= n - 1;i++){
            a[m] = a[m] + y[i];
        }
        for (i = 0;i <= m - 1;i++){
            a[i] = 0.0;
            for (j = 0;j <= n-1;j++){
                a[i] = a[i] +x[i][j] * y[j];
            }
        }
        chlk(b,mm,1,a);
        yy = 0.0;
        for (i = 0;i <= n-1;i++){
            yy = yy + y[i]/n;
        }
        q = e = u = 0.0;
        for (i = 0;i <= n - 1;i++){
            p = a[m];
            for (j = 0;j <= m-1;j++){
                p = p +a[j] * x[j][i];
            }
            q += (y[i] - p) * (y[i] - p);
            e += (y[i] - yy) * (y[i] - yy);
            u += (yy - p) * (yy - p);
        }
        s = Math.sqrt(q / n);
        r = Math.sqrt(1.0 - q / e);
        for (j = 0;j <= m - 1;j++){
            p = 0.0;
            for (i = 0;i <= n-1;i++){
                pp =a[m];
                for (k = 0;k <= m-1;k++){
                    if (k != j){
                        pp += a[k] * x[k][i];
                    }
                }
                p += (y[i] - pp) * (y[i] - pp);
            }
            v[j] = Math.sqrt(1.0 - q / p);
        }
        dt[0] = q;
        dt[1] = s;
        dt[2] = r;
        dt[3] = u;
    }
    private static int chlk(double[] a,int n,int m,double[] d){
        int i,j,k,u,v;
        if ((a[0] + 1.0 == 1.0) || (a[0] < 0.0)){
            System.out.println("fail\n");
            return (-2);
        }
        a[0] = Math.sqrt(a[0]);
        for (j = 1;j <= n-1;j++){
            a[j] = a[j] / a[0];
        }
        for (i = 1;i <= n-1;i++){
            u = i * n + i;
            for (j = 1;j <= i;j++){
                v = (j - 1) * n + i;
                a[u] = a[u] - a[v] * a[v];
            }
            if ((a[u]) + 1.0 == 1.0 || (a[u] < 0.0)){
                System.out.println("fail\n");
                return (-2);
            }
            a[u] = Math.sqrt(a[u]);
            if (i != (n-1)){
                for (j = i + 1;j <= n-1;j++){
                    v = i * n + j;
                    for (k = 1;k <= i;k++){
                        a[v] = a[v] - a[(k-1) * n + 1] * a[(k-1) * n + 1];
                    }
                    a[v] = a[v] / a[u];
                }
            }
        }
        for (j = 0;j <= m-1;j++){
            d[j] = d[j] / a[0];
            for (i = 1;i <= n-1;i++){
                u = i * n + i;
                v = i * m + j;
                for (k = 1;k <=i;k++){
                    d[v] = d[v] - a[(k-1) * n + i] * d[(k - 1) * m + j];
                }
                d[v] /= a[u];
            }
        }
        for (j = 0;j <= m - 1;j++){
            u = (n - 1) * m + j;
            d[u] = d[u] / a[n * n - 1];
            for (k = n - 1;k >= 1;k--){
                u = (k - 1) * m + j;
                for (i = k;i <= n-1;i++){
                    v = (k - 1) * n + i;
                    d[u] = d[u] - a[v] * d[i * m + j];
                }
                v = (k - 1) * n + k - 1;
                d[u] = d[u] / a[v];
            }
        }
    return (2);
    }
}
