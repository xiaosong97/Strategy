package dataGet;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import garch.Class1;

public class test {
    public static void main(String[] args){
        int[] dims = {18,1};
        double[] Adata = { 1, 7, 13, 2, 8, 14, 3, 9, 15, 4, 10, 16, 5, 11, 17, 6, 12, 18};
        Object[] lhs = new Object[1];   //输出
        Object[] rhs = new Object[1];   //输入
        rhs[0] = MWNumericArray.newInstance(dims, Adata, MWClassID.DOUBLE);
        Class1 gar = null;
        try {
            gar = new Class1();
            gar.garch(lhs, rhs);
        } catch (MWException e) {
            e.printStackTrace();
        }
    }
}
