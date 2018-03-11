package dataProc;

public class Sequation {
    public Sequation(){

    }
    /***********************************************************************
     *简要说明：全选主元高斯消元法
     *功能：解线性方程组
     *输入参数：
     *    @param  coeffA  系数矩阵 n×n,doule[][]
     *    @param  constB  常向量  线性方程组的右端 n×m，double[][]
     *    @param  resultX 返回线性方程组的解   n×m，double[][]
     *    @param  n  矩阵coeffA的阶数，int
     *    @param  m  矩阵const的列数，int
     *输出参数：
     *    @return double abs  矩阵coeffA的行列式,如果abs=0，比较复杂，本函数不作处理,认为没有希望得到的解当|coeffA|=0,可能有无穷多解
     *********************************************************************
     *
     */
    public static  double  guassEquation(double[][] coeffA ,double[][] constB, double[][] resultX , int n , int m )
    {
        int i,j,k,row,line;
        double temp,max,abs=1;
        /*
         *change用于记载系数矩阵列交换的信息
         */
        int[] change = new int[n] ;
        for(i=0;i<n;i++) change[i]=i ;
        /*
         *从矩阵的第一行开始
         *a、找主元
         *b、行列互换
         *c、线性变换
         */
        for(i=0;i<n-1;i++)
        {
            /*
             *找主元
             */
            row=i;line=i; max = Math.abs(coeffA[i][i]);
            for(j=i;j<n;j++)
            {
                for(k=i;k<n;k++)
                {
                    temp = Math.abs(coeffA[j][k]);
                    if(temp>max)
                    {
                        max = temp;
                        row = j;
                        line = k;

                    }

                }
            }
            /*
             *主元找到了为第row行，第line列，值为max
             *如果max＝0 ，表示行列式为0，返回0，退出
             */
            if(max==0)
            {
                return 0;
            }
            /*
             *第二步，行列互换，准备先行变换
             */
            if(row != i)
            {
                for(k=i;k<n;k++)
                {
                    temp = coeffA[i][k];
                    coeffA[i][k] = coeffA[row][k];
                    coeffA[row][k] = temp ;
                }
                for(k=0;k<m;k++)
                {
                    temp=constB[i][k];
                    constB[i][k]=constB[row][k];
                    constB[row][k]=temp;
                }
            }

            if(line != i)
            {
                for(j=0;j<n;j++)
                {
                    temp = coeffA[j][line];
                    coeffA[j][line]= coeffA[j][i];
                    coeffA[j][i]=temp;
                }
                /*
                 *记载变量位置的变化（列变换信息标识了变量位置的变化信息）
                 */
                k=change[i];
                change[i]=change[line];
                change[line]= k;
            }

            /*
             *开始线性变换,先对第i行归一化，然后对余行线性变换
             */
            abs *=coeffA[i][i];
            for(k=i+1;k<n;k++) coeffA[i][k]/=coeffA[i][i];
            for(k=0;k<m;k++) constB[i][k] /= coeffA[i][i];
            coeffA[i][i]=1;

            /*
             *余矩阵变换
             */
            for(j=i+1;j<n;j++)
            {
                for(k=i+1;k<n;k++) coeffA[j][k] -= coeffA[j][i]*coeffA[i][k];
                for(k=0;k<m;k++) constB[j][k] -= coeffA[j][i]*constB[i][k];
                coeffA[j][i] =0 ;

            }

        }
        abs *= coeffA[n-1][n-1];

        /*
         *回代消元
         */
        for(k=0;k<m;k++)
        {
            constB[n-1][k] /= coeffA[n-1][n-1];
            for(i=n-2;i>=0;i--)
                for(j=i+1;j<n;j++)
                    constB[i][k]-=coeffA[i][j]*constB[j][k];
        }

        /*
         *根据change，调整变量顺序，得最后解
         */
        for(i=0;i<n;i++)
        {
            for(j=0;j<m;j++)
            {
                resultX[change[i]][j]=constB[i][j];
            }
        }
        return abs ;
    }
}
