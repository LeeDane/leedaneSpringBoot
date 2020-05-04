package com.cn.leedane.handler.tool.video;

import com.alibaba.fastjson.JSONObject;
import com.cn.leedane.utils.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LeeDane
 * 2020年05月04日 09:35
 * Version 1.0
 */
public class DouYinShareUtil {

    private DouYinShareUtil(){}

    private static final class GetInstance {
        static final DouYinShareUtil INSTANCE = new DouYinShareUtil();
        // 抖音字体编码
        static final JSONObject mapCode2Name = JSONObject.parseObject("{\"0xe602\":\"num_\",\"0xe605\":\"num_3\",\"0xe606\":\"num_4\",\"0xe603\":\"num_1\",\"0xe604\":\"num_2\",\"0xe618\":\"num_\",\"0xe619\":\"num_4\",\"0xe60a\":\"num_8\",\"0xe60b\":\"num_9\",\"0xe60e\":\"num_\",\"0xe60f\":\"num_5\",\"0xe60c\":\"num_4\",\"0xe60d\":\"num_1\",\"0xe612\":\"num_6\",\"0xe613\":\"num_8\",\"0xe610\":\"num_3\",\"0xe611\":\"num_2\",\"0xe616\":\"num_1\",\"0xe617\":\"num_3\",\"0xe614\":\"num_9\",\"0xe615\":\"num_7\",\"0xe609\":\"num_7\",\"0xe607\":\"num_5\",\"0xe608\":\"num_6\",\"0xe61b\":\"num_5\",\"0xe61c\":\"num_8\",\"0xe61a\":\"num_2\",\"0xe61f\":\"num_6\",\"0xe61d\":\"num_9\",\"0xe61e\":\"num_7\"}");

        // 对应抖音字体编码的数字
        static final JSONObject mapCode2Font = JSONObject.parseObject("{\"num_9\":8,\"num_5\":5,\"num_6\":6,\"num_\":1,\"num_7\":9,\"num_8\":7,\"num_1\":0,\"num_2\":3,\"num_3\":2,\"num_4\":4}");
    }

    public static DouYinShareUtil instance() {
        return GetInstance.INSTANCE;
    }

    /**
     * 根据分享链接爬取用户相关信息
     * @param shareUrl 抖音用户名片分享链接
     * */
    public JSONObject getDouyinInfo( String shareUrl ) {

        shareUrl = getHTMLSource(shareUrl);
//		System.out.println("html -> "+shareUrl);
        // 对抖音自定义的字体编码做处理
        shareUrl = shareUrl.replaceAll("&#", "hzsd");
        //		System.out.println("最后处理 -> "+html);

        JSONObject data = new JSONObject();
        try {

            Document doc = Jsoup.parse(shareUrl);

            // 头像
            Elements headUrl = doc.select("[class=avatar]");
            data.put("headUrl", headUrl.attr("src"));

            // 昵称
            Elements nickName = doc.select("[class=nickname]");
            data.put("nickName", nickName.get(0).text());

            // id
            Elements idEle = doc.select("[class=shortid]");
            String[] idArr = idEle.get(0).text().split(" ");
            String id = "";
            for (int i = 0; i < idArr.length; i++) {
                id = id+formatNum(idArr[i]);
            }
            data.put("id", id);

            // 个性签名
            Elements sign = doc.select("[class=signature]");
            data.put("sign", sign.get(0).text());


            // 关注信息
            Elements followInfo = doc.select("[class=follow-info]");

            // 关注数
            Elements focusBlock = followInfo.select("[class=focus block]");
            String[] focusArr = focusBlock.select("[class=num]").text().split(" ");
            String focusStr = "";
            for (int i = 0; i < focusArr.length; i++) {
                focusStr = focusStr+formatNum(focusArr[i]);
            }
            data.put("focus", focusStr);

            // 粉丝数
            Elements fansBlock = followInfo.select("[class=follower block]");
            String[] fansArr = fansBlock.select("[class=num]").text().split(" ");
            String fansStr = "";
            for (int i = 0; i < fansArr.length; i++) {
                fansStr = fansStr+formatNum(fansArr[i]);
            }
            data.put("fans", fansStr);

            // 点赞数
            Elements likedBlock = followInfo.select("[class=liked-num block]");
            String[] likedArr = likedBlock.select("[class=num]").text().split(" ");
            String likedStr = "";
            for (int i = 0; i < likedArr.length; i++) {
                likedStr = likedStr+formatNum(likedArr[i]);
            }
            data.put("liked", likedStr);

            // 作品数
            Elements works = doc.select("[class=user-tab active tab get-list]");
            String[] worksArr = works.select("[class=num]").text().split(" ");
            String worksStr = "";
            for (int i = 0; i < worksArr.length; i++) {
                worksStr = worksStr+formatNum(worksArr[i]);
            }
            data.put("works", worksStr);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("analyse douyin user info has error.");
        }
        return data;
    }

    /**
     * 请求 html 源码
     */
    private String getHTMLSource(String url){
        InputStream openStream = null;
        BufferedReader buf = null;

        try {
            String line = null;
            URL theUrl= new URL(url);
            HttpURLConnection conn = (HttpURLConnection) theUrl.openConnection();


            Map<String, List<String>> map = conn.getHeaderFields();
//			System.out.println("请求头："+map.toString());
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
     *
     * 判断是否包含中文。true：包含
     *
     */
    private boolean isChinese(String str){

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;

    }

    /**
     *
     * 抖音自定义字体对应的编码
     * 反编译成阿拉伯数字
     *
     */
    private String formatNum(String str) {
        if (isChinese(str)) {
            return "";
        }
        if ( str.length() < 8 || str.indexOf("hzsdxe6") < 0) {
            return str;
        }
        str = "0"+str.substring(4,str.length()-1);

        String resStr = GetInstance.mapCode2Font.getString(GetInstance.mapCode2Name.getString(str));
        if (StringUtil.isNull(resStr)) {
            return str;
        }
        return resStr;
    }
}
