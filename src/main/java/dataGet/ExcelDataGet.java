package dataGet;

import java.io.*;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelDataGet {
    private String date;
    private String stockName;
    private int stockNum = 4;
    private int cpNum = 43;
    private double cp[][] = new double[stockNum][cpNum];    //存储stockNum支股票最近cpNum天的收盘价
    public int getStockNum(){
        return  stockNum;
    }
    public int getCpNum(){
        return cpNum;
    }

    public double[][] getCp() {
        return cp;
    }

    //读Excel的方法readExcel，该方法的入口参数为一个File对象
    public void readExcel(File file){
        try{
            //创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            //jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            //Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();

            for (int index = 0;index <sheet_size;index++){
                //每个页签创建一个Sheet对象
                Sheet sheet = wb.getSheet(index);
                //sheet.getRows()返回该页的总行数
                for (int i = 0;i<sheet.getRows();i++){
                    //sheet.getColumns()返回改业的总列数
                    for (int j = 0;j<sheet.getColumns();j++){

                        if (j==3){
                           cp[index][i] = Double.parseDouble(sheet.getCell(j,i).getContents());
                        }
                    }
                }
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (BiffException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void print(){
        for (int i = 0;i<stockNum;i++){
            for (int j =0;j<cpNum;j++){
                System.out.print(cp[i][j]+"     ");
            }
            System.out.println();
        }
    }
}
