package dataProc;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.E;

public class Corrcoef {

    private double[][] corr;
    private double[][] cp;
    private int stockNum;
    private int cpNum;


    public void setCp(double[][] cp) {
        this.cp = cp;
    }

    public void setCorr() {
        stockNum = cp.length;
        cpNum = cp[0].length;
        corr = new double[stockNum][stockNum];
        for (int i = 0;i < stockNum;i++){
            for (int j =0;j < stockNum;j++){
                if (i==j){
                    corr[i][j] = 0;
                }else {
                    corr[i][j] = getCorrcoef(cp[i],cp[j]);
                }
            }
        }
    }

    public double[][] getCorr() {
        return corr;
    }

    private double getCorrcoef(double[] x, double[] y) {
        double corr = 0;
        corr = cov(x,y)/(sig(x)*sig(y));
        return corr;
    }

    private double sig(double[] x) {
        double sig;
        double xAvg = mean(x);
        double temp = 0;
        for (int i=0;i<x.length;i++){
            temp = (x[i]-xAvg)*(x[i]-xAvg);
        }
        sig = temp/x.length;
        return sig;
    }

    private double cov(double[] x, double[] y) {
        double cov;
        double xAvg = mean(x);
        double yAvg = mean(y);
        double temp = 0;
        for (int i=0;i<x.length;i++){
            temp = (x[i]-xAvg)*(y[i]-yAvg);
        }
        cov = temp / (x.length - 1);
        return cov;
    }

    private double mean(double[] x) {
        double sum = 0;
        int len = x.length;
        for(int i=0; i<len; i++) {
            sum += x[i];
        }
        return sum / len;
    }


}