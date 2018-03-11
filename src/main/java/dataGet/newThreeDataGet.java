package dataGet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 新三板实时股票信息调用
 *
 */
public class newThreeDataGet {
    public static final String DEF_CHATSET = "UTF-8";   //定义默认字符编码
    public static final int DEF_CONN_TIMEOUT = 30000;   //定义默认连接超时时间
    public static final int DEF_READ_TIMEOUT = 30000;   //定义默认读取超时时间
    public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    //配置申请拿到的KEY
    public static final String APPKEY = "8fd6260520261fd13ecc5d2476c01d81";
    public newThreeDataGet(){
        getRequest1();
    }
    //1.实时股票信息
    public static void  getRequest1(){
        String result = null;
        String url = "http://touchstone.api.juhe.cn/ajax/data/realtime";
        Map params = new HashMap(); //请求参数，首先搜索设定id的股票，然后再根据页数显示所有结果的对应页数的内容
            params.put("key",APPKEY);   //申请拿到的APPKEY
            params.put("pageIndex",""); //页数，如1，默认为1
            params.put("id","430032");    //证券代码，如430032，支持模糊查询，默认为全部
        try{
            result = net(url,params,"GET");
            JSONObject object = JSONObject.fromObject(result);
            //json 数据解析
            int error_code = object.getInt("error_code");
            String reason = object.getString("reason");
            JSONObject results = object.getJSONObject("result");
            JSONArray jsonArray = results.getJSONArray("result");
            int id[] = new int[jsonArray.size()];
            String stockName[] = new String[jsonArray.size()];
            String cp[] = new String[jsonArray.size()];
            String updateDate[] = new String[jsonArray.size()];
            for (int i = 0;i<jsonArray.size();i++){
                JSONObject data = jsonArray.getJSONObject(i);
                id[i] = data.getInt("_id");
                stockName[i] = data.getString("shortName");
                cp[i] = data.getString("ZRSP");
                updateDate[i] = data.getString("updateDate");
            }
            //System.out.println(error_code);
            //System.out.println(jsonArray.size());
            //System.out.println(object);
            if (error_code==0){
                for (int i = 0;i<jsonArray.size();i++){
                    System.out.println("id为"+id[i]+"的"+stockName[i]+"股票在"+updateDate[i]+"前一天的收盘价为"+cp[i]);
                }
            }else {
                System.out.println(error_code+":"+reason);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        newThreeDataGet dg = new newThreeDataGet();
    }
    /**
     * @param strUrl 请求地址
     * @param params 请求参数
     * @param method 请求方法
     * @return 网络请求字符串
     * @throws Exception
     */
    public static String net(String strUrl,Map params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try{
            StringBuffer sb = new StringBuffer();
            if (method==null||method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (method==null||method.equals("GET")){
                conn.setRequestMethod("GET");
            }else {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent",userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params!=null&&method.equals("POST")){
                try{
                    DataOutputStream out = new DataOutputStream((conn.getOutputStream()));
                    out.writeBytes(urlencode(params));
                }catch (Exception e){

                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is,DEF_CHATSET));
            String strRead = null;
            while((strRead = reader.readLine())!=null){
                sb.append(strRead);
            }
            rs = sb.toString();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (reader!=null){
                reader.close();
            }
            if (conn!=null){
                conn.disconnect();
            }
        }
        return rs;
    }
    //将map型转为请求参数型
    public static String urlencode(Map<String,Object>data){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()){
            try{
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }
        return  sb.toString();
    }
}
