package com.cn.leedane.mall.suning;

import com.cn.leedane.exception.JdAccessTokenException;
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
 * 苏宁相关的公共工具
 * @author LeeDane
 * 2017年12月24日 下午8:46:17
 * version 1.0
 */
public class CommUtil {

    //苏宁应用的服务器地址
    public static String serverUrl = "https://open.suning.com/api/http/sopRequest";

    //苏宁应用的appkey
    public static String appKey = "dbab0b2c3ec397f6cd967be3dfe97be3";

    //苏宁应用的secret
    public static String appSecret  = "919a671834c2fbe92fbb518a1929f6a1";

    //苏宁默认推广位ID
    public static String adBookId  = "269841";

}