package com.cn.leedane.mall.taobao.api;

import com.cn.leedane.mall.taobao.CommUtil;
import com.cn.leedane.utils.StringUtil;
import com.taobao.api.ApiException;
import com.taobao.api.internal.util.WebUtils;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取access_token
 * @author LeeDane
 * 2019年12月10日 14:07
 * Version 1.0
 */
public class AccessTokenUtilApi {

    public static JSONObject getAccessToken(String code, String state) throws ApiException {
        if(StringUtil.isNull(code)){
            return null;
        }

        String url="https://oauth.taobao.com/token";
        Map<String,String> props=new HashMap<String,String>();
        props.put("grant_type","authorization_code");
        /*测试时，需把test参数换成自己应用对应的值*/
        props.put("code",code);
        props.put("client_id", CommUtil.appkey);
        props.put("client_secret", CommUtil.secret);
        props.put("redirect_uri", CommUtil.redirectUrl);
        props.put("view","web");
        props.put("state", state);
        String s="";
        try{
            WebUtils.setIgnoreSSLCheck(true);
            s= WebUtils.doPost(url, props, 8000, 8000);
            System.out.println(s);
            return JSONObject.fromObject(s);
        }catch(IOException e){
            e.printStackTrace();
            throw new ApiException("淘宝联盟获取access_token失败");
        }
    }
}
