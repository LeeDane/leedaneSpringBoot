package com.cn.leedane.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import com.google.gson.Gson;

/**
 * Created by LeeDnae on 2015/10/7.
 */
public class HttpConnectionUtil {

    private static Gson gson = new Gson();

    private HttpConnectionUtil(){

    }


    /**
     * 发送get请求
     * @param serverPath
     * @return
     * @throws IOException
     */
    public static String sendGetRequest(String serverPath) throws IOException {
        return sendGetRequest(serverPath, 5000, 5000);
    }

    /**
     * 发送get请求
     * @param serverPath
     * @param requestTimeOut
     * @param responseTimeOut
     * @return
     * @throws IOException
     */
    public static String sendGetRequest(String serverPath, int requestTimeOut, int responseTimeOut) throws IOException {
        InputStream is = getGetRequestInputStream(serverPath, requestTimeOut, responseTimeOut);
        if(is != null){
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            //存储单行文本的数据
            String line;
            //保存请求返回的所有文本数据
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine())!= null){
                buffer.append(line);
            }

            //关闭相关的流
            if(is != null) is.close();
            if(reader != null) reader.close();
            return buffer.toString();
        }
        return null;
    }

    /**
     * 发送get请求获取输入流对象
     * @param serverPath
     * @param requestTimeOut
     * @param responseTimeOut
     * @return
     * @throws IOException
     */
    public static InputStream getGetRequestInputStream(String serverPath, int requestTimeOut, int responseTimeOut) throws IOException {
        URL url = new URL(serverPath);
        //打开对服务器的连接
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        //设置连接超时时间
        urlConnection.setConnectTimeout(requestTimeOut);
        //设置读取超时时间
        urlConnection.setReadTimeout(responseTimeOut);

        //设置允许输入，输出
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        //设置请求模式为GET
        urlConnection.setRequestMethod("GET");
//        urlConnection.setRequestProperty("userid", "10");
//        urlConnection.setRequestProperty("token", "eyJ1c2VybmFtZSI6IueuoeeQhuWRmCIsInB3ZCI6IjU0YTJlYzVmNDQyMWZhMjRiZmE5YmY2NDYxZTY0OWEyIiwidGltZSI6IjIwMTcwMzI0MDAwMDAwIiwib3ZlcmR1ZSI6IjIwMTcwNDI0MDAwMDAwIn0=");
        //连接服务器
        urlConnection.connect();
        InputStream is = null;
        //响应成功
        if(urlConnection.getResponseCode() == 200){
            is = urlConnection.getInputStream();
        }

        //断开连接
       // if(urlConnection != null) urlConnection.disconnect();
        return is;
    }

    /**
     * 发送POST请求
     * @param serverPath
     * @param params
     * @param requestTimeOut
     * @param responseTimeOut
     * @return
     * @throws IOException
     */
    public static String sendPostRequest(String serverPath, Map<String, Object> params, int requestTimeOut, int responseTimeOut) throws IOException {
        InputStream is =  getPostRequestInputStream(serverPath, params, requestTimeOut, responseTimeOut);
        if(is != null){
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            //存储单行文本的数据
            String line;
            //保存请求返回的所有文本数据
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine())!= null){
                buffer.append(line);
            }
            //关闭相关的流
            if(is != null) is.close();
            if(reader != null) reader.close();
            return buffer.toString();
        }
        return null;
    }

    /**
     * 发送POST请求获得输入流
     * @param serverPath
     * @param params
     * @param requestTimeOut
     * @param responseTimeOut
     * @return
     * @throws IOException
     */
    public static InputStream getPostRequestInputStream(String serverPath, Map<String, Object> params, int requestTimeOut, int responseTimeOut) throws IOException {
        URL url = new URL(serverPath);
        //打开对服务器的连接
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        //设置连接超时时间
        urlConnection.setConnectTimeout(3000);
        //设置读取超时时间
        urlConnection.setReadTimeout(3000);

        //设置允许输入，输出
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        //设置头部信息
        urlConnection.setRequestProperty("headerdata", "leedaneheader");
        //一定要设置 Content-Type 要不然服务端接收不到参数
        urlConnection.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");

        //设置请求模式为GET
        urlConnection.setRequestMethod("POST");

        //post不能缓存用户数据，所有动态设置
        urlConnection.setUseCaches(false);

        urlConnection
                .setRequestProperty(
                        "User-Agent",
                        "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/7.0; SLCC2; "
                                + ".NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET4.0C; .NET4.0E)");
        /*urlConnection.setRequestProperty("Content-Length",
                String.valueOf(gson.toJson(httpRequest.getParams()).getBytes().length));*/

        //连接服务器
        urlConnection.connect();

        OutputStream os = urlConnection.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);

        //以utf-8处理数据的编码方式
        dos.writeBytes(URLEncoder.encode(gson.toJson(params), "UTF-8"));
        //刷新提交数据
        dos.flush();
        if(os != null) os.close();
        if(dos != null) dos.close();

        InputStream is = null;
        int code = urlConnection.getResponseCode();
        //响应成功
        if(code == 200){
            is = urlConnection.getInputStream();
        }
        //断开连接
        //if(urlConnection != null) urlConnection.disconnect();
        return is;
    }

    /**
     * 获取网络图片的InputStream流
     * @param imgUrl
     * @return
     */
    public static InputStream getInputStream(String imgUrl){
        if(StringUtil.isNull(imgUrl))
            return null;

        URL url = null;
        try {
            url = new URL(imgUrl);
            URLConnection uc = url.openConnection();
            uc.setConnectTimeout(60000);
            uc.setDoInput(true);
            uc.setDoInput(true);
            uc.setReadTimeout(30000);
            return uc.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
