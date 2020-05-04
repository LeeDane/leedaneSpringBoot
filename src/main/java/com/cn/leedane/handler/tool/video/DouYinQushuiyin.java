package com.cn.leedane.handler.tool.video;

import com.cn.leedane.utils.CommonUtil;
import org.jsoup.Jsoup;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LeeDane
 * 2020年05月04日 09:09
 * Version 1.0
 */
public class DouYinQushuiyin {

    /**
     * 请求 html 源码
     * @param url
     * @return
     */
    public static String getHTMLSource(String url){
        InputStream openStream = null;
        BufferedReader buf = null;

        try {
            String line = null;
            URL theUrl= new URL(url);
            HttpURLConnection conn = (HttpURLConnection) theUrl.openConnection();


            Map<String, List<String>> map = conn.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet())
            {
                //如果发现有重定向了新的地址
                if ("Location".equals(key))
                {
                    //获取新地址
                    url = map.get(key).get(0);
                    break;
                }
            }
            theUrl= new URL(url);
            conn = (HttpURLConnection) theUrl.openConnection();

            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("contentType", "UTF-8");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept-Language", Locale.getDefault().toString());

            // 建立实际的连接
            conn.connect();

            buf = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));

            StringBuffer str = new StringBuffer();
            while((line = buf.readLine()) != null){
                str.append(line);
            }

            return str.toString();
        } catch (MalformedURLException e) {
            System.out.println("getHTMLSource has MalformedURLException.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("getHTMLSource has IOException.");
            e.printStackTrace();
        } finally{
            try {
                if(openStream!=null){
                    openStream.close();
                }
                if(buf!=null){
                    buf.close();
                }
            } catch (IOException e) {
                System.out.println("getHTMLSource close stream IOException.");
                e.printStackTrace();
            }
        }

        return null;

    }

    /**
     * 从抖音页面中匹配到链接中带playwm的地址
     * @param douyinHtml
     * @return
     */
    public static String parseVideoLink(String douyinHtml){
        Pattern pattern = Pattern.compile("https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(douyinHtml);
        while (matcher.find()) {
            String ma = matcher.group();
            if(ma.indexOf("playwm") > -1)
                return ma;
        }
        return null;
    }

    public static final InputStream byte2Input(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

}
