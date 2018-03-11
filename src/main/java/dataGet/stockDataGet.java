package dataGet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 *股票数据调用示例代码 － 聚合数据
 *在线接口文档：http://www.juhe.cn/docs/21
 **/

public class stockDataGet {
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    //配置您申请的KEY
    public static final String APPKEY ="d0a768c299b71fd005fd6594eb340554";
    public void getDataOf(int i){
        switch (i){
            case 1:getRequest1();break;
            case 2:getRequest2();break;
            case 3:getRequest3();break;
            case 4:getRequest4();break;
            case 5:getRequest5();break;
            case 6:getRequest6();break;
            case 7:getRequest7();break;
            default:break;
        }
    }
    //1.沪深股市
    public static void getRequest1(){
        String result =null;
        String url ="http://web.juhe.cn:8080/finance/stock/hs";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("gid","sz000001");//股票编号，上海股市以sh开头，深圳股市以sz开头如：sh601009

        params.put("key",APPKEY);//APP Key

        jsonProc(url, params);
    }

    //2.香港股市
    public static void getRequest2(){
        String result =null;
        String url ="http://web.juhe.cn:8080/finance/stock/hk";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("num","");//股票代码，如：00001 为“长江实业”股票代码
        params.put("key",APPKEY);//APP Key

        jsonProc(url, params);
    }

    private static void jsonProc(String url, Map params) {
        String result;
        try {
            result =net(url, params, "GET");
            JSONObject object = JSONObject.fromObject(result);
            if(object.getInt("error_code")==0){
                System.out.println(object.get("result"));
            }else{
                System.out.println(object.get("error_code")+":"+object.get("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //3.美国股市
    public static void getRequest3(){
        String result =null;
        String url ="http://web.juhe.cn:8080/finance/stock/usa";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("gid","");//股票代码，如：aapl 为“苹果公司”的股票代码
        params.put("key",APPKEY);//APP Key

        jsonProc(url, params);
    }

    //4.香港股市列表
    public static void getRequest4(){
        String result =null;
        String url ="http://web.juhe.cn:8080/finance/stock/hkall";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("key",APPKEY);//您申请的APPKEY
        params.put("page","");//第几页,每页20条数据,默认第1页

        jsonProc(url, params);
    }

    //5.美国股市列表
    public static void getRequest5(){
        String result =null;
        String url ="http://web.juhe.cn:8080/finance/stock/usaall";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("key",APPKEY);//您申请的APPKEY
        params.put("page","");//第几页,每页20条数据,默认第1页

        jsonProc(url, params);
    }

    //6.深圳股市列表
    public static void getRequest6(){
        String result =null;
        String url ="http://web.juhe.cn:8080/finance/stock/szall";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("key",APPKEY);//您申请的APPKEY
        params.put("page","");//第几页(每页20条数据),默认第1页

        jsonProc(url, params);
    }

    //7.沪股列表
    public static void getRequest7(){
        String result =null;
        String url ="http://web.juhe.cn:8080/finance/stock/shall";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("key",APPKEY);//您申请的APPKEY
        params.put("page","");//第几页,每页20条数据,默认第1页

        jsonProc(url, params);
    }



    public static void main(String[] args) {
        stockDataGet stock = new stockDataGet();
        stock.getDataOf(1);
    }

    /**
     *
     * @param strUrl 请求地址
     * @param params 请求参数
     * @param method 请求方法
     * @return  网络请求字符串
     * @throws Exception
     */
    public static String net(String strUrl, Map params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if(method==null || method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if(method==null || method.equals("GET")){
                conn.setRequestMethod("GET");
            }else{
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params!= null && method.equals("POST")) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlencode(params));
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    //将map型转为请求参数型
    public static String urlencode(Map<String,Object>data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
