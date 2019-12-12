package com.cn.leedane.mall.jingdong.api;

import com.cn.leedane.mall.jingdong.CommUtil;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.StringUtil;
import com.jd.open.api.sdk.JdException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * 获取access_token
 * @author LeeDane
 * 2019年12月10日 14:07
 * Version 1.0
 */
public class AccessTokenUtilApi {

    public static JSONObject getAccessToken(String code) throws JdException {
        if(StringUtil.isNull(code)){
            return null;
        }
        Connection.Response res = null;
        try {
            String url = "https://open-oauth.jd.com/oauth2/access_token?app_key="+ CommUtil.appkey +"&app_secret="+ CommUtil.secret +"&grant_type=authorization_code&code=" + code;
            res = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; BIDUBrowser 2.x)")
                    .timeout(8000)
                    .method(Connection.Method.GET)
                    .execute();
            JSONObject object = JSONObject.fromObject(res.body());
            //失败：{"code":304,"msg":"当前会话没有做过授权,请先授权","requestId":"7025c06a74cf49dfa4a2a6fc4e28ed62"}
            //成功：{"access_token":"0950116966e542018d299329701cb800njk2","expires_in":2592000,"refresh_token":"c25e43216a8847d6b5d25bc119c1876fzi1z","scope":"snsapi_base","open_id":"9YC7GJsgDhIhShxLQGgVauOxYjXIIocUzUWAGDvTUB4","uid":"1898013009","time":1575958459794,"token_type":"bearer","code":0}
            if(object.optInt("code") != 0){
                throw new JdException(object.optString("msg"));
            }
            return object;

        } catch (Exception e) {
            e.printStackTrace();
            throw new JdException("京东联盟获取access_token失败");
        }
    }
}
