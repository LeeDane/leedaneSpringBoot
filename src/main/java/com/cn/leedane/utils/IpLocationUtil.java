package com.cn.leedane.utils;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.springboot.SpringUtil;
import com.squareup.okhttp.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP地址信息工具类
 * @author LeeDane
 * 2016年11月2日 下午3:10:05
 * Version 1.0
 */
public class IpLocationUtil {

    private Logger logger = Logger.getLogger(getClass());

    /**
     * 根据IP地址获取相应的位置信息
     * @param ip
     * @return
     */
    public String getLocaltion(String ip){
        String location  = null;
        //对127.0.0.1不做处理
        if(StringUtil.isNotNull(ip) && !"127.0.0.1".equalsIgnoreCase(ip) && !ip.startsWith("192.168")){
            SystemCache systemCache = (SystemCache) SpringUtil.getBean("systemCache");
            Object obj = systemCache.getCache(ip);
            if(obj == null || obj == ""){
                RedisUtil redisUtil = RedisUtil.getInstance();
                //根据Ip从redis中找
                location = redisUtil.getString(ip);
                if (StringUtil.isNotNull(location)) {
                    systemCache.addCache(ip, location);
                }else{
                    //从百度平台获取IP地址信息
                    location = getLocaltionFromWeb(ip);
                    if(StringUtil.isNotNull(location)){
                        redisUtil.addString(ip, location);
                        systemCache.addCache(ip, location);
                    }
                }
            }else{
                location = StringUtil.changeNotNull(obj);
            }
        }
        return StringUtil.isNull(location)? "未知": location;
    }


    /**
     * 通过网络请求获取
     * @param ip
     * @return
     */
    private String getLocaltionFromWeb(String ip){

        String address = null;

        OkHttpClient okHttpClient =new OkHttpClient();
        Request builder = new Request.Builder()
                .url("http://api.map.baidu.com/location/ip?ip="+ ip +"&ak=s7phdZqZn9sYZex6dF1lvCAakbVGNI33&coor=bd09ll")
                .build();
        Call call = okHttpClient.newCall( builder);
        /*call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful())
                {
                    String string = response.body().string();
                    logger.info(ip + "获取到位置信息："+ string);
                }
            }
        });*/

        //同步操作
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                String bodyStr = body.string();
                if(StringUtil.isNotNull(bodyStr)){
                    JSONObject jsonObject = JSONObject.fromObject(StringUtil.unicodeToString(bodyStr));
                    address = jsonObject.optJSONObject("content").optString("address");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }


}
