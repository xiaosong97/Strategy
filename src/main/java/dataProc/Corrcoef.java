package dataProc;

import Jama.Matrix;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dataGet.ExcelDataGet;
import dataGet.MySQLDemo;
import garch.Class1;
public class Corrcoef {
    List<Double> rating_map_list = new ArrayList<Double>();

    public static void main(String[] args){
        /*
        MySQLDemo ds = new MySQLDemo();
        double[][] cp = ds.getCp();
        int cpNum = ds.getCpNum();
        int stockNum = 4;
        double[][] corr = new double[stockNum][stockNum];
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
                //logger.info("stock"+(i+1)+" and stock"+(j+1)+"的相关系数为:"+corrcoef[i].getCorrcoef_bydim(corrcoef[j]));
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

        double stockA[] = cp[maxCpCorrC];   //相关系数最大的一对股票的收盘价序列
        double stockB[] = cp[maxCpCorrR];

        //对收盘价序列进行对数处理
        for (int i = 0;i<cpNum;i++){
            stockA[i] = Math.log(stockA[i]);
            stockB[i] = Math.log(stockB[i]);
        }
        /*System.out.println("相关系数最大的一对股票的代码分别为:");
        for (int i = 0;i<cpNum;i++){
            System.out.print("stockA cp " + (i+1) + " is :"+stockA[i]+",   ");
        }
        System.out.println();
        for (int i = 0;i<cpNum;i++){
            System.out.print("stockB cp " + (i+1) + " is :"+stockB[i]+",   ");
        }
        System.out.println();

        double X[] = new double[cpNum];     //存放随机变量X的cpNum个观测值
        double Y[] = new double[cpNum];     //存放随机变量Y的cpNum个观测值

        double X1[] = new double[cpNum];    //变量X的一阶差分
        double Y1[] = new double[cpNum];    //变量Y的一阶差分

        for (int i = 0;i<cpNum;i++){
            X[i] = stockB[i];
            Y[i] = stockA[i];
        }
        for (int i = 0;i<cpNum;i++){
            if (i != cpNum-1){
                X1[i] = X[i+1] - X[i];
                Y1[i] = Y[i+1] - Y[i];
            }else {
                X1[i] = X[i];
                Y1[i] = Y[i];
            }
        }

        //进行多元线性回归
        int m = 4;      //自变量个数
        int n = cpNum;      //观测数据的维数

        double[] K = new double[m];       //返回回归系数 a
        double[][] Xt = new double[n][m];   //每一列存放m个自变量的观测值

        for (int i = 0;i < n;i++){ //将多个自变量合并到一个多元变量Xt中，并在首列添加1，确保回归系数中有一个常数
            Xt[i][0] = 1;
            Xt[i][1] = X[i];
            Xt[i][2] = X1[i];
            Xt[i][3] = Y1[i];
        }

        K = regress(Y,Xt);

        //计算协整向量
        double cointVector = (-1-K[3])/(K[1]+K[2]);
        //System.out.println(cointVector);
        //还需计算V和mspread
        double[] spread = new double[cpNum];    //价差
        for (int i = 0;i<cpNum;i++){
            spread[i] = Y[i] + cointVector * X[i];
        }*/
        int cpNum = 246;
        double[] spread = new double[cpNum];
        spreadInput(spread);
        double[] mspread = new double[cpNum];   //中心化处理价差
        double avgSpread = avg(spread);
        for (int i = 0;i<cpNum;i++){
            mspread[i] = spread[i] - avgSpread;
        }


        /*for(int i=0;i<cpNum;i++){
            spread[i] =
        }*/
        //用GARCH(1，1)模型估计序列spreadt的条件方差方程
        double[] V = new double[cpNum]; //存放时变标准差
        int[] dims = {cpNum,1};
        Object[] lhs = new Object[1];   //输出
        Object[] rhs = new Object[1];   //输入
        rhs[0] = MWNumericArray.newInstance(dims, spread, MWClassID.DOUBLE);
        Class1 gar = null;
        try {
            gar = new Class1();
            gar.garch(lhs, rhs);
        } catch (MWException e) {
            e.printStackTrace();
        }
        Object lh = lhs[0];     //garch函数返回Object[] lhs，时变标准差结果在lhs[0]中
        String[] names = lh.toString().split("\n");//将lh转换为String后，以换行符为分隔符分割字符串

        for (int i = 0;i<cpNum;i++){
            V[i] = Double.valueOf(names[i].toString()); //存储得到的时变标准差序列
        }

        int Anum=1000;
        int Bnum=400;
        for (int i =0;i<cpNum;i++){
            if (mspread[i]>0.8*V[i]){
                System.out.println("卖空"+Anum+"股stockA");
                System.out.println("买入"+Bnum+"股stockB");
            }
            if (mspread[i]<-0.8*V[i]){
                System.out.println("卖空"+Anum+"股stockA");
                System.out.println("买入"+Bnum+"股stockB");
            }
        }

        for (int i =1;i<cpNum;i++){
            if (mspread[i]>-0.8*V[i]&&mspread[i]<0.8*V[i]||mspread[i]<-2*0.8*V[i]||mspread[i]>2*0.8*V[i]){
                if (mspread[i-1]>0.8*V[i-1]){
                    System.out.println("卖空"+Anum+"股stockA");
                    System.out.println("买入"+Bnum+"股stockB");
                }
                if (mspread[i-1]<-0.8*V[i-1]){
                    System.out.println("卖空"+Anum+"股stockA");
                    System.out.println("买入"+Bnum+"股stockB");
                }
            }
        }
    }

    private static void spreadInput(double[] spread) {
        ExcelDataGet ex = new ExcelDataGet();
        File file = new File("E:/Projects/成都华迪生产实习/屈松/源代码/data.xls");
        ex.readExcel(file);
        for (int i=0;i<ex.getCpNum();i++){
            spread[i]=ex.getCp()[0][i];
        }
    }

    private static double[] regress(double[] Y, double[][] Xt) {
        int n = Y.length;
        Matrix Xtt = new Matrix(Xt);
        //Xtt.print(4,3);
        Matrix Ytt = new Matrix(Y,n);
        Matrix Xttt = Xtt.transpose(); //转置
        Matrix temp = Xttt.times(Xtt); //矩阵乘法
        temp = temp.inverse();         //求逆
        temp = temp.times(Xttt);
        Matrix B = temp.times(Ytt);
        // B.print(1,3);


        return B.getColumnPackedCopy();
    }

    private static double avg(double[] v) {
        double avg = 0;
        for (int i =0;i<v.length;i++){
            avg += v[i];
        }
        avg /= v.length;
        return avg;
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
}