package com.cn.leedane.utils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.cn.leedane.mybatis.SqlProvider;
import com.cn.leedane.mybatis.table.TableFormat;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.impl.HumpToUnderLineFormat;

/**
 * sql工具类
 * @author leedane
 *
 */
public class SqlUtil {
	
	public static boolean getBooleanByList(List<?> list) {
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
    @SuppressWarnings("rawtypes")
	public static List convertMapsToBeans(Class<?> clazz, List<Map<String, Object>> maps) {
       return JSONArray.parseArray(JSONArray.toJSONString(maps), clazz);
    }
  
    
    /**
     * 组装实体bean(class没有实例化的情况)
     * @param params
     * @param clazz
     * @return
     */
    public Object getBean(JSONObject params, Class<?> clazz){
    	
		SqlProvider provider = new SqlProvider();
		TableFormat tableFormat = new HumpToUnderLineFormat();
		
		Field[] fields = provider.getFields(clazz);
		try {
			Object obj = clazz.newInstance();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				//特殊处理serialVersionUID
				if("serialVersionUID".equalsIgnoreCase(field.getName())){
					continue;
				}
				Column column = field.getAnnotation(Column.class);
				String columnName = "";
				if (column != null) {
					if (!column.required())
						continue;
					columnName = column.value().toUpperCase();
				}
				
				if (StringUtils.isEmpty(columnName)) {
					columnName = tableFormat.getColumnName(field.getName());
				}
				if(params.has(columnName.toLowerCase())){
					field.setAccessible(true);
					Class<?> typeClass = field.getType();
					if(typeClass == int.class || typeClass == Integer.class){
						field.set(obj, StringUtil.changeObjectToInt(params.get(columnName.toLowerCase())));
					}else if(typeClass == String.class){
						field.set(obj, StringUtil.changeNotNull((params.get(columnName.toLowerCase()))));
					}else if(typeClass == long.class || typeClass == Long.class){
						field.set(obj, StringUtil.changeObjectToLong((params.get(columnName.toLowerCase()))));
					}else if(typeClass == float.class || typeClass == Float.class){
						field.set(obj, StringUtil.changeObjectToFloat((params.get(columnName.toLowerCase()))));
					}else if(typeClass == double.class || typeClass == Double.class){
						field.set(obj, StringUtil.changeObjectToDouble((params.get(columnName.toLowerCase()))));
					}else if(typeClass == boolean.class || typeClass == Boolean.class){
						field.set(obj, StringUtil.changeObjectToBoolean((params.get(columnName.toLowerCase()))));
					}else if(typeClass == Date.class){
						field.set(obj, DateUtil.stringToDate(StringUtil.changeNotNull((params.get(columnName.toLowerCase())))));
					}else{
						new RuntimeException("无法创建实体bean,原因是未知的字段类型！");	
					}
				}
			}
			return obj;
		} catch (Exception e) {
			new RuntimeException("get select sql is exceptoin:" + e);
		}
		return null;
	}
    
    /**
     * 组装更新的实体bean(class没有实例化的情况)
     * @param params
     * @param clazz
     * @return
     */
    public Object getUpdateBean(JSONObject params, Class<?> clazz){
    	
		SqlProvider provider = new SqlProvider();
		TableFormat tableFormat = new HumpToUnderLineFormat();
		
		Field[] fields = provider.getFields(clazz);
		try {
			Object obj = clazz.newInstance();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				//特殊处理serialVersionUID
				if(field.getName().equalsIgnoreCase("serialVersionUID") 
						|| field.getName().equalsIgnoreCase("id")
						|| field.getName().equalsIgnoreCase("create_user_id")
						|| field.getName().equalsIgnoreCase("create_time")){
					continue;
				}
				Column column = field.getAnnotation(Column.class);
				String columnName = "";
				//判断是否获取的是注解的信息，是的话在实体后面添加上注解，便于还原回实体
				//boolean getAnnotation = false; 
				if (column != null) {
					if (!column.required())
						continue;
					columnName = column.value().toUpperCase();
					//getAnnotation = true;
				}
				
				if (StringUtils.isEmpty(columnName)) {
					columnName = tableFormat.getColumnName(field.getName());
				}
				if(params.has(columnName.toLowerCase())){
					field.setAccessible(true);
					Class<?> typeClass = field.getType();
					if(typeClass == int.class || typeClass == Integer.class){
						field.set(obj, StringUtil.changeObjectToInt(params.get(columnName.toLowerCase())));
					}else if(typeClass == String.class){
						field.set(obj, StringUtil.changeNotNull((params.get(columnName.toLowerCase()))));
					}else if(typeClass == long.class || typeClass == Long.class){
						field.set(obj, StringUtil.changeObjectToLong((params.get(columnName.toLowerCase()))));
					}else if(typeClass == float.class || typeClass == Float.class){
						field.set(obj, StringUtil.changeObjectToFloat((params.get(columnName.toLowerCase()))));
					}else if(typeClass == double.class || typeClass == Double.class){
						field.set(obj, StringUtil.changeObjectToDouble((params.get(columnName.toLowerCase()))));
					}else if(typeClass == boolean.class || typeClass == Boolean.class){
						field.set(obj, StringUtil.changeObjectToBoolean((params.get(columnName.toLowerCase()))));
					}else if(typeClass == Date.class){
						field.set(obj, DateUtil.stringToDate(StringUtil.changeNotNull((params.get(columnName.toLowerCase())))));
					}else{
						new RuntimeException("无法创建实体bean,原因是未知的字段类型！");	
					}
				}
			}
			return obj;
		} catch (Exception e) {
			new RuntimeException("get select sql is exceptoin:" + e);
		}
		return null;
	}
    
    /**
     * 组装更新的实体bean(class必须是实例化的情况下才使用)
     * @param params
     * @param obj
     * @return
     */
    public Object getUpdateBean(JSONObject params, Object obj){
    	
		SqlProvider provider = new SqlProvider();
		TableFormat tableFormat = new HumpToUnderLineFormat();
		Class<?> beanClass = obj.getClass();
		Field[] fields = provider.getFields(beanClass);
		try {
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				//特殊处理serialVersionUID
				if(field.getName().equalsIgnoreCase("serialVersionUID") 
						|| field.getName().equalsIgnoreCase("id")
						|| field.getName().equalsIgnoreCase("create_user_id")
						|| field.getName().equalsIgnoreCase("create_time")){
					continue;
				}
				Column column = field.getAnnotation(Column.class);
				String columnName = "";
				//判断是否获取的是注解的信息，是的话在实体后面添加上注解，便于还原回实体
				//boolean getAnnotation = false; 
				if (column != null) {
					if (!column.required())
						continue;
					columnName = column.value().toUpperCase();
					//getAnnotation = true;
				}
				
				if (StringUtils.isEmpty(columnName)) {
					columnName = tableFormat.getColumnName(field.getName());
				}
				if(params.has(columnName.toLowerCase())){
					field.setAccessible(true);
					Class<?> typeClass = field.getType();
					if(typeClass == int.class || typeClass == Integer.class){
						field.set(obj, StringUtil.changeObjectToInt(params.get(columnName.toLowerCase())));
					}else if(typeClass == String.class){
						field.set(obj, StringUtil.changeNotNull((params.get(columnName.toLowerCase()))));
					}else if(typeClass == long.class || typeClass == Long.class){
						field.set(obj, StringUtil.changeObjectToLong((params.get(columnName.toLowerCase()))));
					}else if(typeClass == float.class || typeClass == Float.class){
						field.set(obj, StringUtil.changeObjectToFloat((params.get(columnName.toLowerCase()))));
					}else if(typeClass == double.class || typeClass == Double.class){
						field.set(obj, StringUtil.changeObjectToDouble((params.get(columnName.toLowerCase()))));
					}else if(typeClass == boolean.class || typeClass == Boolean.class){
						field.set(obj, StringUtil.changeObjectToBoolean((params.get(columnName.toLowerCase()))));
					}else if(typeClass == Date.class){
						field.set(obj, DateUtil.stringToDate(StringUtil.changeNotNull((params.get(columnName.toLowerCase())))));
					}else{
						new RuntimeException("无法创建实体bean,原因是未知的字段类型！");	
					}
				}
			}
			return obj;
		} catch (Exception e) {
			new RuntimeException("get select sql is exceptoin:" + e);
		}
		return null;
	}
}
