package dataProc;

public class Regression {
    public Regression(){

    }

    /**
     * 模型：多元线性回归
     *简要说明：根据一个样本集，计算线性回归分析系数。线性回归分析的模型为：Y=k0+k1X1+k2X2+....+knXn,其中，X1,X2,....,Xn为因变量，Y为变量
     *     k0,k1,....,kn为回归系数，该函数就是要根据一组样本（Xi1,Xi2,...Xin,Yi）i=1,2,...,m[m个样本]，根据最小二乘法得原则，计算出
     *     最佳得回归分析系数k0,k1,....,kn,从而得到线性回归分析模型，该模型稍加扩展，就可以推广到非线性回归模型
     *输入参数:
     *    @param   X  自变量样本集,double[][]
     *    @param   Y  变量结果集,double[]
     *    @param  K  回归系数,double[]
     *    @param  n  回归变量个数,int
     *    @param  m  样本个数,int
     *输出参数：
     *    @return double result  0:失败，其他：成功
     */
    public static double LineRegression(double[][] X,double[] Y,double[] K,int n,int m){
        double result = 0;

        /**
         * 线性回归问题，最终转换为解一个对称线性方程组的求解问题
         *线性方程组的系数矩阵为n+1*n+1,常数矩阵为n+1*1
         */
        int XLen = n+1;
        int YLen = 1;
        int i,j,k;
        double[][] coeffX = new double[XLen][XLen];
        double[][] constY = new  double[XLen][1];
        double[][] resultK = new double[XLen][1];

        /**
         * 根据参数，计算索要求解方程组的系数矩阵、常数矩阵
         */
        double[][] temp = new double[m+1][n+1];
        for (i = 0;i < n+1;i++){
            temp[0][i] = 1;
        }
        for (i = 0;i < m+1;i++){
            temp[i][0] = 1;
        }
        for (i = i;i < m+1;i++){
            for (j = 1;j < n+1;j++){
                temp[i][j] = X[i-1][j-1];
            }
        }
        /**
         * 开始计算每一个系数
         */
        for (i = 0;i < n+1;i++){
            /**
             * coeffX的第i行和i列的系数，注意，是对称矩阵
             */
            for (j = i;j < n+1;j++){
                double col = 0;
                for (k = 1;k < m+1;k++){
                    col += (temp[k][i] * temp[k][j]);
                }
                coeffX[i][j] = col;
                coeffX[j][i] = col;
            }

            /**
             * constY的第i个元素
             */
            double conTemp = 0;
            for (k = 1;k < m+1;k++){
                conTemp += (Y[k-1] * temp[k][i]);
            }
            constY[i][0] = conTemp;
        }

        /**
         * 调用Sequation方法，解线性方程组
         */
        result = Sequation.guassEquation(coeffX,constY,resultK,XLen,1);
        if (result == 0){
            System.out.println("The regression is failed,please check the sample point \n");
            return result;
        }else {
            for (i = 0;i < n+1;i++){
                K[i] = resultK[i][0];
            }
        }
        return result;
    }
}
