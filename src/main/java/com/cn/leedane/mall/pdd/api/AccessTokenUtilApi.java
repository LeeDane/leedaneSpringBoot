package com.cn.leedane.mall.pdd.api;

import com.cn.leedane.mall.pdd.CommUtil;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.utils.StringUtil;
import com.jd.open.api.sdk.JdException;
import com.pdd.pop.sdk.http.PopAccessTokenClient;
import com.pdd.pop.sdk.http.token.AccessTokenResponse;
import com.taobao.api.ApiException;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * 获取access_token
 * @link {https://open.pinduoduo.com/#/document?url=https%253A%252F%252Fmai.pinduoduo.com%252Fautopage%252F83_static_3%252Findex.html}
 * @link {https://open.pinduoduo.com/#/document?url=https%253A%252F%252Fmai.pinduoduo.com%252Fautopage%252F73_static_4%252Findex.html}
 * @author LeeDane
 * 2019年12月10日 14:07
 * Version 1.0
 */
public class AccessTokenUtilApi {

    public static AccessTokenResponse getAccessToken(String code) throws PddException {
        if(StringUtil.isNull(code)){
            return null;
        }
        PopAccessTokenClient accessTokenClient = new PopAccessTokenClient(CommUtil.clientId, CommUtil.clientSecret);
        // 生成AccessToken
        try {
            return accessTokenClient.generate(code);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PddException("拼多多联盟获取access_token失败");
        }
    }
}
