package com.cn.leedane.mall.github.api;

import com.cn.leedane.mall.github.CommUtil;
import com.cn.leedane.mall.github.GithubException;
import com.cn.leedane.utils.HttpConnectionUtil;
import com.cn.leedane.utils.StringUtil;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取access_token
 * @author LeeDane
 * 2019年12月10日 14:07
 * Version 1.0
 */
public class AccessTokenUtilApi {

    public static JSONObject getAccessToken(String code) throws GithubException {
        if(StringUtil.isNull(code)){
            return null;
        }

        String url = "https://github.com/login/oauth/access_token?client_id="+ CommUtil.client_id+"&client_secret="+ CommUtil.client_secret+"&code="+code;
        /*String url = "https://github.com/login/oauth/access_token";
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", CommUtil.client_id);
        params.put("client_secret", CommUtil.client_secret);
        params.put("code", code);*/
        // 生成AccessToken
        try {
//            String response = HttpConnectionUtil.sendPostRequest(url, params, 15000, 15000);
            String response = HttpConnectionUtil.sendGetRequest(url, 30000, 30000);
            //access_token=7bccb2ecbf3ed39354d733413b6ec7ba50c895a6&scope=public_repo%2Cuser%3Aemail&token_type=bearer
            if(StringUtil.isNotNull(response)){
                //从字符串中解析access_token
                String pattern =  "(?<=[access_token=]){0,1}\\d+([^&]*)";
                // 创建 Pattern 对象
                Pattern r = Pattern.compile(pattern);
                // 现在创建 matcher 对象
                Matcher m = r.matcher(response);
                String accessToken = null;
                while(m.find()){
                    //这里为了防止地址栏有多个，只解析第一个即可
                    accessToken = m.group(0);
                    break;
                }

                if(StringUtil.isNotNull(accessToken)){
                    //获取用户信息
                    String response1 = HttpConnectionUtil.sendGetRequest("https://api.github.com/user?access_token="+ accessToken, 30000, 30000);
                    if(StringUtil.isNotNull(response1)){
                        return JSONObject.fromObject(response1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GithubException("gtihub获取access_token失败");
        }
        return null;
    }

    public class GithubUserInfo{

        //private
    }

   /* public static void main(String[] args) {
        String s = "7bccb2ecbf3ed39354d733413b6ec7ba50c895a6&scope=public_repo%2Cuser%3Aemail&token_type=bearer";
        String pattern =  "(?<=[access_token=]){0,1}\\d+([^&]*)";
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(s);
        while(m.find()){
            //这里为了防止地址栏有多个，只解析第一个即可
            System.out.println(m.group(0));
            break;
        }
    }*/
}
