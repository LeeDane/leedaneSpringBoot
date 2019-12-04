package com.cn.leedane.mall.jingdong;

import com.cn.leedane.exception.JdAccessTokenException;
import com.cn.leedane.utils.StringUtil;
import com.jd.open.api.sdk.internal.util.CodecUtil;
import com.jd.open.api.sdk.internal.util.HttpUtil;
import com.jd.open.api.sdk.request.JdRequest;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 京东相关的公共工具
 * @author LeeDane
 * 2017年12月24日 下午8:46:17
 * version 1.0
 */
public class CommUtil {

    //京东图片的默认域名
    public static String imgBasePath = "https://img30.360buyimg.com/mall/";

    //京东api服务器的url
    public static String url = "https://api.jd.com/routerjson";

    /**
     * 京东京粉api服务器的url
     * @link{https://union.jd.com/helpcenter/13246-13247-46301}
     */
    public static String jinfenUrl = "https://router.jd.com/api";

    //京东app应用网站ID
    public static String websiteId = "860725442";

    //京东应用的appkey
    public static String appkey = "e96a02947bab021977b603ec95ddbd79";

    //京东应用的secret
    public static String secret = "8f7ae2ba1b124c7da383acd1bd8a7972";

    /**
     * 需要通过网页授权，所以第一次使用需要手动在页面进行授权
     */
    public static String code = "";//通过code才能获取access_token

    /**
     * 由于access_code每三天都会被刷新一次，所以会在三天前调用刷新token的方法，就需要这个
     */
    public static String refreshToken = "";//刷新的access_code

    private static String accessToken = "0950116966e542018d299329701cb800njk2";

    public static String getAccessToken() throws JdAccessTokenException {

        //如果token不为空，直接返回，目前无法验证是否过期，只有在其他地方调用异常的时候才会更新token
        if(StringUtil.isNotNull(accessToken)){
            return accessToken;
        }
        //code = "RIb4cz";
        Connection.Response res = null;
        try {
            res = Jsoup.connect("https://open-oauth.jd.com/oauth2/access_token?app_key=" + appkey + "&app_secret=" + secret + "&grant_type=authorization_code&code=" + code)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; BIDUBrowser 2.x)")
                    .timeout(8000)
                    .method(Connection.Method.GET)
                    .execute();
            JSONObject object = JSONObject.fromObject(res.body());
            if(object.has("access_token")){
                refreshToken = object.optString("refresh_token");
                return object.optString("access_token");
            }
            throw new JdAccessTokenException(object.optString("msg"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new JdAccessTokenException("京东授权网络连接异常");
        }
    }

    /**
     * 提供给其他地方调用出现异常后清掉token，目的是为了下次重新获取token
     */
    public static void clearAccessToken(){
        accessToken = null;
    }

    /**
     * 管理员可以手动更新token
     */
    public static void updateAccessToken(String access, String refresh){
        accessToken = access;
        refreshToken = refresh;
    }

    public static void main(String[] args) {
        try {
            String accessToken = getAccessToken();
            System.out.println("accessToken="+ accessToken);
        } catch (JdAccessTokenException e) {
            e.printStackTrace();
        }
    }


    public static String buildUrl(JdRequest request, String version) throws Exception {
        Map<String, String> sysParams = request.getSysParams();
        Map<String, String> pmap = new TreeMap();
        pmap.put("360buy_param_json", request.getAppJsonParams());
        sysParams.put("method", request.getApiMethod());
        sysParams.put("access_token", CommUtil.getAccessToken());
        sysParams.put("app_key", CommUtil.appkey);
        sysParams.put("v", "1.0");
        pmap.putAll(sysParams);
        String sign = sign(pmap, CommUtil.secret);
        sysParams.put("v", version);
        sysParams.put("sign", sign);
//		sysParams.put("timestamp", DateUtil.DateToString(new Date()));
        StringBuilder sb = new StringBuilder(CommUtil.url);
        sb.append("?");
        sb.append(HttpUtil.buildQuery(sysParams, "UTF-8"));
        sb.append("&360buy_param_json="+ pmap.get("360buy_param_json"));
        return sb.toString();
    }

    public static String buildUrl(JdRequest request) throws Exception {
        return buildUrl(request, "1.0");
    }

    private static String sign(Map<String, String> pmap, String appSecret) throws Exception {
        StringBuilder sb = new StringBuilder(appSecret);
        Iterator i$ = pmap.entrySet().iterator();

        while(i$.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)i$.next();
            String name = (String)entry.getKey();
            String value = (String)entry.getValue();
            if (com.jd.open.api.sdk.internal.util.StringUtil.areNotEmpty(new String[]{name, value})) {
                sb.append(name).append(value);
            }
        }
        sb.append(appSecret);
        String result = CodecUtil.md5(sb.toString());
        return result;
    }
}