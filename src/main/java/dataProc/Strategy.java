package dataProc;

import Jama.Matrix;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import dataGet.DecisionData;
import dataGet.realPriceGet;
import mygarch.Class1;
import userException.CorrLess;
import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.Random;

public class Strategy {
    //定义两支选定的股票的实时价格
    private double priceX;
    private double priceY;
    private double mspreadT;
    private double[] spread;

    public void setConitVector(double conitVector) {
        this.conitVector = conitVector;
    }
    /**
     * 定义价差和标准差的关系，
     * 若flag=0，则位于平仓获利区间，

     * 若flag=1，则价差大于0.8倍的标准差，
     * 若flag=-1，则价差小于0.8倍的标准差
     */
    private ArrayList<Integer> flag = new ArrayList<Integer>();

    //定义板块信息
    private String segment;
    private double conitVector;
    private double[] V;

    public double getConitVector() {
        return conitVector;
    }
    private double[] mspread;
    private String NameA;
    private String NameB;
    private int cpNum;
    public void pro(){
        DecisionData decisionData = new DecisionData();
        decisionData.setSegment(segment);
        decisionData.setCp();
        //获取选中板块的所有股票的2017/3/10日-2018/3/9日期间每日收盘价
        double[][] cp = decisionData.getCp().getData();
        String[] codeList = decisionData.getTickerCodeList();
        String[] codeName = decisionData.getTickerNameList();
        StockNum st = new StockNum();
        st.checkStockNum(cp.length);
        int stockNum = cp.length;
        cpNum = cp[0].length;

        //计算相关系数矩阵
        Corrcoef corrcoef = new Corrcoef();
        corrcoef.setCp(cp);
        corrcoef.setCorr();
        double[][] corr = corrcoef.getCorr();

        //求出收盘价相关系数最大的两支股票stockA和stockB
        int maxCpCorrR = 0;     //系数最大的行数
        int maxCpCorrC = 0;     //系数最大的列数
        for (int i = 0;i < stockNum;i++){
            for (int j = 0;j<stockNum;j++){
                if (corr[maxCpCorrR][maxCpCorrC] < corr[i][j]){
                    maxCpCorrR = i;
                    maxCpCorrC =j;
                }
            }
        }
        if (corr[maxCpCorrC][maxCpCorrR] < 0.9){
            System.out.println("---------------------------");
            try {
                throw new CorrLess();
            } catch (CorrLess corrLess) {
                corrLess.printStackTrace();
            }
        }
        //得到相关系数最大的一对股票的收盘价序列
        double stockA[] = cp[maxCpCorrC];
        double stockB[] = cp[maxCpCorrR];
        System.out.println("相关系数最大的一对股票分别为:\n" + codeList[maxCpCorrC] + " " + codeName[maxCpCorrC]);
        System.out.println(codeList[maxCpCorrR] + " " + codeName[maxCpCorrR]);
        //printMaxRelativeStockInfo(stockA,stockB);

        double X[] = new double[cpNum];     //存放随机变量X的cpNum个观测值
        double Y[] = new double[cpNum];     //存放随机变量Y的cpNum个观测值
        //对收盘价序列进行对数处理
        for (int i = 0;i<cpNum;i++){
            X[i] = Math.log(stockA[i]);
            Y[i] = Math.log(stockB[i]);
        }
        //多元线性回归
        double[] K = multiRegress(X,Y);
        //计算协整向量
        this.setConitVector((-1-K[3])/(K[1]+K[2]));
        //System.out.println(cointVector);
        //计算价差序列
        spread = new double[cpNum];    //价差序列
        for (int i = 0;i<cpNum;i++){
            spread[i] = Y[i] + this.getConitVector() * X[i];
        }
        //当日价差
        realPriceGet rp = new realPriceGet();
        rp.dataGet(codeList[maxCpCorrR],codeList[maxCpCorrC]);
        priceX = Math.log(rp.getpB());
        priceY = Math.log(rp.getpA());
        double spreadT = priceY + this.getConitVector() * priceX;
        mspread = new double[cpNum];   //中心化处理价差序列
        double avgSpread = avg(spread);
        mspreadT = spreadT - avgSpread;
        for (int i = 0;i<cpNum;i++){
            mspread[i] = spread[i] - avgSpread;
        }

        //用GARCH(1，1)模型估计序列spreadt的条件方差方程
        V = new double[3]; //存放估计的garch模型参数，分别为：Constant，GARCH{1}，ARCH{1}
        int[] dims = {cpNum,1};
        Object[] lhs = new Object[1];   //输出
        Object[] rhs = new Object[1];   //输入
        rhs[0] = MWNumericArray.newInstance(dims, spread, MWClassID.DOUBLE);
        Class1 gar;
        try {
            gar = new Class1();
            gar.mygarch(lhs, rhs);
        } catch (MWException e) {
            e.printStackTrace();
        }
        Object lh = lhs[0];     //garch函数返回Object[] lhs，时变标准差结果在lhs[0]中
        String[] names = lh.toString().split("\n");//将lh转换为String后，以换行符为分隔符分割字符串

        for (int i = 0;i<3;i++){
            V[i] = Double.valueOf(names[i].toString()); //存储得到garch模型系数
        }
        //确定具体交易策略
        NameA = codeName[maxCpCorrR];
        NameB = codeName[maxCpCorrC];

    }

    public String setSegment(String segment) {
        this.segment = segment;
        return null;
    }

    public void publishTradeInfo(int dayCusor) {
        System.out.println("--------------------");
        System.out.println("第"+dayCusor+"天");
        this.setConitVector(floor(-this.getConitVector()*10));
        int Anum = 1000;
        int Bnum = (int)this.getConitVector()*100;
        double v = var(spread);
        v = forecastV(v,dayCusor);
        v = sqrt(v);
       // System.out.println("价差为"+mspreadT+",0.8倍标准差为"+(0.8*v));
        //每日做1次是否建仓的判断
        String bulidMsg = "";
        if (mspreadT>0.8*v&&mspreadT<1.6*v){
            bulidMsg="买入"+Bnum+"股"+NameA+"的股票,卖空"+Anum+"股"+NameB+"的股票，建仓";
            flag.add(dayCusor,1);
        }else if (mspreadT<-0.8*v&&mspreadT>-1.6*v){
            bulidMsg = "卖空"+Anum+"股"+NameA+"的股票,买入"+Bnum+"股"+NameB+"的股票，建仓";
            flag.add(dayCusor,-1);
        }else {
            bulidMsg = "不建仓";
            flag.add(dayCusor,0);
            System.out.println(bulidMsg);
            if (dayCusor != 0){
                //非第一日则判断是否有仓可平，有则平仓，无则
                String cleanMsg = "当前无持有";
                int pNum = 0;
                int nNum = 0;
                int f;
                for(int i=0;i<flag.size()-1;i++){
                    f = flag.get(i);
                    if(f == 1){
                        pNum++;
                    }else if (f == 1){
                        nNum++;
                    }
                }
                if (pNum != 0){
                    cleanMsg="卖出"+(Bnum*pNum)+"股"+NameA+"的股票,买入"+(Anum*pNum)+"股"+NameB+"的股票，平仓";
                }
                if (nNum != 0){
                    cleanMsg = "买入"+(Anum*pNum)+"股"+NameA+"的股票,卖出"+(Bnum*pNum)+"股"+NameB+"的股票，平仓";
                }
                System.out.println(cleanMsg);
            }

        }
    }

    private double var(double[] spread) {
        double var = 0;
        double avgSpread = avg(spread);
        for (int i=0;i<spread.length;i++){
            double temp = (spread[i]-avgSpread);
            var += temp * temp;
        }
        return var;
    }

    private double forecastV(double v,int Cursor){
        //所用模型建立的最后一天的数据是18/03/09，今日为03/19,计算今日需要递推10次,
        // 从19-t+1天前开始模拟，需要递推10-t+1次
        Random random = new Random();
        double n = random.nextGaussian();
        double e = v * n * n;
        for (int i = 0;i < (Cursor+10);i++){
            v = V[0] + V[1] * e + V[2] * v;
            n = random.nextGaussian();
            e = v * n * n;
        }
        return v;
    }
    private static double[] multiRegress(double[] X, double[] Y) {
        //对价格序列做一阶差分
        double X1[] = diff(X);
        double Y1[] = diff(Y);

        //进行多元线性回归
        int m = 4;      //自变量个数
        int n = X.length;      //观测数据的维数

        double[] K;       //返回回归系数 a
        double[][] Xt = new double[n][m];   //每一列存放m个自变量的观测值

        for (int i = 0;i < n;i++){ //将多个自变量合并到一个多元变量Xt中，并在首列添加1，确保回归系数中有一个常数
            Xt[i][0] = 1;
            Xt[i][1] = X[i];
            Xt[i][2] = X1[i];
            Xt[i][3] = Y1[i];
        }

        K = regress(Y,Xt);
        return K;
    }

    private static double[] diff(double[] X) {
        double[] x = new double[X.length];
        for (int i = 0;i<x.length-1;i++){
            x[i] = X[i+1] - X[i];
        }
        return x;
    }

    private static void printMaxRelativeStockInfo(double[] stockA, double[] stockB) {
        System.out.println("The cp of stockA is:" );
        for (int i = 0;i<stockA.length;i++){
            System.out.print("第" + (i+1) + "天是 :"+stockA[i]+",   ");
        }
        System.out.println();
        System.out.println("The cp of stockB is:");
        for (int i = 0;i<stockB.length;i++){
            System.out.print("第" + (i+1) + "天是 :"+stockB[i]+",   ");
        }
        System.out.println();
    }

    private static double[] regress(double[] Y, double[][] Xt) {
        int n = Y.length;
        Matrix Xtt = new Matrix(Xt);
        Matrix Ytt = new Matrix(Y,n);
        Matrix Xttt = Xtt.transpose(); //转置
        Matrix temp = Xttt.times(Xtt); //矩阵乘法
        //temp.print(4,2);
        //int rank = Xtt.rank();
        temp = temp.inverse();         //求逆
        temp = temp.times(Xttt);
        Matrix B = temp.times(Ytt);
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


}
