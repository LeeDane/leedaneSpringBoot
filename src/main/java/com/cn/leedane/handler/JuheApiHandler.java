package com.cn.leedane.handler;

import com.cn.leedane.cache.JuheCache;
import com.cn.leedane.juheapi.HeadlinesApi;
import com.cn.leedane.juheapi.HistoryTodayApi;
import com.cn.leedane.juheapi.JokeApi;
import com.cn.leedane.juheapi.JuHeException;
import com.cn.leedane.model.KeyValueBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.SerializeUtil;
import com.cn.leedane.utils.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 聚合api处理类
 * @author LeeDane
 * 2016年7月12日 上午11:53:35
 * Version 1.0
 */
@Component
public class JuheApiHandler{
	/**
	 * 自动在15分钟后销毁掉
	 */
	@Autowired
	private JuheCache juheCache;

	/**
	 * 获取聚合api
	 * @param juheType
	 * @param number 数量，1以下获取全部
	 * @param desc 是否倒叙
	 * @return
	 * @throws JuHeException
	 */
	public JSONArray getData(EnumUtil.JuheApiType juheType, int number, boolean desc) throws JuHeException {
		String key = "";
		if(juheType == EnumUtil.JuheApiType.笑话精选)
			key = "juhejokes";
		else if(juheType == EnumUtil.JuheApiType.历史今天)
			key = "juhehistory";
		else if(juheType == EnumUtil.JuheApiType.头条新闻)
			key = "juheheadlines";

		Object obj = juheCache.getCache(key);
		KeyValueBean keyValueBean = null;
		if(obj == ""){
				keyValueBean = netData(juheType);
				if(keyValueBean != null){
					juheCache.addCache(key, keyValueBean);
				}
		}else{
			keyValueBean = (KeyValueBean)obj;
		}

		if(keyValueBean != null && StringUtil.isNotNull(keyValueBean.getValue()) ){
			JSONArray datas = JSONArray.fromObject(keyValueBean.getValue());
			if(datas != null && datas.size() > 0 && number > 0 && number < datas.size()){
				JSONArray newDatas = new JSONArray();
				if(desc){
					for(int i = datas.size() -1; i >= (datas.size() - number); i--){
						newDatas.add(datas.opt(i));
					}
				}else{
					for(int i = 0; i < number; i++){
						newDatas.add(i, datas.opt(i));
					}
				}

				return newDatas;
			}
			return datas;
		}
		return new JSONArray();
	}

	/**
	 * 从网络上读取数据放到KeyValueBean中
	 * @param juheType
	 * @return
	 * @throws JuHeException
	 */
	private KeyValueBean netData(EnumUtil.JuheApiType juheType) throws JuHeException {
		KeyValueBean keyValueBean = null;
		if(juheType == EnumUtil.JuheApiType.笑话精选){
			keyValueBean = new KeyValueBean();
			keyValueBean.setValue(JokeApi.getRequest().toString());
		}else if(juheType == EnumUtil.JuheApiType.历史今天){
			keyValueBean = new KeyValueBean();
			keyValueBean.setValue(HistoryTodayApi.getRequest().toString());
		}else if(juheType == EnumUtil.JuheApiType.头条新闻){
			keyValueBean = new KeyValueBean();
			keyValueBean.setValue(HeadlinesApi.getRequest().toString());
		}
		return keyValueBean;
	}

}
