package com.cn.leedane.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.cn.leedane.model.FinancialBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * sql工具类
 * @author leedane
 *
 */
public class SqlUtil {
	public static boolean getBooleanByList(List<Map<String, Object>> list) {
		boolean result = false;
		if(!CollectionUtils.isEmpty(list)){
			result = true;
		}
		return result;
	}
	
	public static int getCreateUserIdByList(List<Map<String, Object>> list) {
		int createUserId = 0;
		if(!CollectionUtils.isEmpty(list)){
			createUserId = StringUtil.changeObjectToInt(list.get(0).get("create_user_id"));
		}
		return createUserId;
	}

	public static int getTotalByList(List<Map<String, Object>> list) {
		int total = 0;
		if(!CollectionUtils.isEmpty(list)){
			total = StringUtil.changeObjectToInt(list.get(0).get("ct"));
		}
		return total;
	}
	
	/**
	 * 获取分页的开始索引
	 * @param current
	 * @param pageSize
	 * @param total
	 * @return
	 */
	public static int getPageStart(int current, int pageSize, int total){
		pageSize = pageSize > 0? pageSize: ConstantsUtil.DEFAULT_PAGE_SIZE;
		//if(total > 0 && total % pageSize == 0)
			//current = current - 1;
			
		
		return current* pageSize;
	}
	
	/**
     * 将一个 Map 对象转化为一个 JavaBean
     * @param type
     * @param map
     * @return
     */
    public static List convertMapsToBeans(Class<?> clazz, List<Map<String, Object>> maps) {
       return JSONArray.parseArray(JSONArray.toJSONString(maps), clazz);
    }
}
