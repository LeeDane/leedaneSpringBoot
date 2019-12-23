package com.cn.leedane.mall.github;

import com.cn.leedane.exception.JdAccessTokenException;
import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import com.cn.leedane.utils.StringUtil;
import com.jd.open.api.sdk.internal.util.CodecUtil;
import com.jd.open.api.sdk.internal.util.HttpUtil;
import com.jd.open.api.sdk.request.JdRequest;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * github相关的公共工具
 * @author LeeDane
 * 2017年12月24日 下午8:46:17
 * version 1.0
 */
public class CommUtil {
    //client_id
    public static String client_id = "fd3353da72d40aaae486";

    //应用的secret
    public static String client_secret = "51b818868eada3e6004b14979813f031400d6fe4";

    //授权成功后的回调地址
    public static String getRedirectUrl(){
        if(LeedanePropertiesConfig.newInstance().isDebug()){
            return "http://127.0.0.1:8089/oauth2/github/verify";
        }
        return "http://onlyloveu.top/oauth2/github/verify";
    }

}