package com.cn.leedane.mybatis;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.cn.leedane.exception.ErrorException;
import com.cn.leedane.mybatis.table.TableFormat;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;
import com.cn.leedane.mybatis.table.impl.HumpToUnderLineFormat;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.StringUtil;

public class SqlProvider {
	private TableFormat tableFormat = new HumpToUnderLineFormat();

	/**
	 * 保存数据
	 * @param bean
	 * @return
	 */
	public String saveClass(Class<?> bean) {
		String tableName = getTableName(bean);
		Field[] fields = getFields(bean);
		StringBuilder insertSql = new StringBuilder();
		List<String> insertParas = new ArrayList<String>();
		List<String> insertParaNames = new ArrayList<String>();
		insertSql.append("INSERT INTO ").append(tableName).append("(");
		try {
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				//特殊处理serialVersionUID
				if(field.getName().equalsIgnoreCase("serialVersionUID")){
					continue;
				}
				
				Column column = field.getAnnotation(Column.class);
				String columnName = "";
				if (column != null) {
					if (!column.required())
						continue;
					columnName = column.value();
				}
				if (StringUtils.isEmpty(columnName)) {
					columnName = tableFormat.getColumnName(field.getName());
				}
				field.setAccessible(true);
				Object object = field.get(bean);
				if (object != null) {
					insertParaNames.add(columnName);
					insertParas.add("#{" + field.getName() + "}");
				}
			}
		} catch (Exception e) {
			new RuntimeException("get insert sql is exceptoin:" + e);
		}
		for (int i = 0; i < insertParaNames.size(); i++) {
			insertSql.append(insertParaNames.get(i));
			if (i != insertParaNames.size() - 1)
				insertSql.append(",");
		}
		insertSql.append(")").append(" VALUES(");
		for (int i = 0; i < insertParas.size(); i++) {
			insertSql.append(insertParas.get(i));
			if (i != insertParas.size() - 1)
				insertSql.append(",");
		}
		insertSql.append(")");
		return insertSql.toString();
	}
	
	public String insert(Object bean) {
		Class<?> beanClass = bean.getClass();
		String tableName = getTableName(beanClass);
		Field[] fields = getFields(beanClass);
		StringBuilder insertSql = new StringBuilder();
		List<String> insertParas = new ArrayList<String>();
		List<String> insertParaNames = new ArrayList<String>();
		insertSql.append("INSERT INTO ").append(tableName).append("(");
		try {
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				//特殊处理serialVersionUID
				if(field.getName().equalsIgnoreCase("serialVersionUID")){
					continue;
				}
				
				Column column = field.getAnnotation(Column.class);
				String columnName = "";
				if (column != null) {
					if (!column.required())
						continue;
					columnName = column.value();
				}
				if (StringUtils.isEmpty(columnName)) {
					columnName = tableFormat.getColumnName(field.getName());
				}
				field.setAccessible(true);
				Object object = field.get(bean);
				if (object != null) {
					insertParaNames.add(columnName);
					insertParas.add("#{" + field.getName() + "}");
				}
			}
		} catch (Exception e) {
			new RuntimeException("get insert sql is exceptoin:" + e);
		}
		for (int i = 0; i < insertParaNames.size(); i++) {
			insertSql.append(insertParaNames.get(i));
			if (i != insertParaNames.size() - 1)
				insertSql.append(",");
		}
		insertSql.append(")").append(" VALUES(");
		for (int i = 0; i < insertParas.size(); i++) {
			insertSql.append(insertParas.get(i));
			if (i != insertParas.size() - 1)
				insertSql.append(",");
		}
		insertSql.append(")");
		return insertSql.toString();
	}

	public String update(Object bean) {
		Class<?> beanClass = bean.getClass();
		String tableName = getTableName(beanClass);
		Field[] fields = getFields(beanClass);
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" update ").append(tableName).append(" set ");
		try {
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				//特殊处理serialVersionUID
				if(field.getName().equalsIgnoreCase("serialVersionUID")){
					continue;
				}
				Column column = field.getAnnotation(Column.class);
				String columnName = "";
				if (column != null) {
					if (!column.required())
						continue;
					columnName = column.value();
				}
				if (StringUtils.isEmpty(columnName)) {
					columnName = tableFormat.getColumnName(field.getName());
				}
				field.setAccessible(true);
				Object beanValue = field.get(bean);
				//if (beanValue != null) {
					updateSql.append(columnName).append("=#{").append(field.getName()).append("}");
					if (i != fields.length - 1) {
						updateSql.append(",");
					}
				//}
			}
		} catch (Exception e) {
			new RuntimeException("get update sql is exceptoin:" + e);
		}
		updateSql.append(" where ").append(tableFormat.getId()+" =#{id}");
		return updateSql.toString();
	}
	
	public String updateSql(Class<?> clazz, String sql, Object ... params){
		String tableName = getTableName(clazz);
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" update ").append(tableName);
		if(StringUtil.isNotNull(sql)){
			updateSql.append(" ");
			updateSql.append(executeSQL(sql, params));
		}
		return updateSql.toString();
	}

	public String delete(Object bean) {
		Class<?> beanClass = bean.getClass();
		String tableName = getTableName(beanClass);
		Field[] fields = getFields(beanClass);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" delete from ").append(tableName).append(" where  ");
		try {
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				//特殊处理serialVersionUID
				if(field.getName().equalsIgnoreCase("serialVersionUID")){
					continue;
				}
				Column column = field.getAnnotation(Column.class);
				String columnName = "";
				if (column != null) {
					if (!column.required())
						continue;
					columnName = column.value();
				}
				if (StringUtils.isEmpty(columnName)) {
					columnName = tableFormat.getColumnName(field.getName());
				}
				field.setAccessible(true);
				Object beanValue = field.get(bean);
				if (beanValue != null) {
					deleteSql.append(columnName).append("=#{").append(field.getName()).append("}");
					if (i != fields.length - 1) {
						deleteSql.append(" and ");
					}
				}
			}
		} catch (Exception e) {
			new RuntimeException("get delete sql is exceptoin:" + e);
		}
		return deleteSql.toString();
	}
	
	public String deleteSql(Class<?> clazz, String sql, Object ... params){
		String tableName = getTableName(clazz);
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" delete from ").append(tableName);
		if(StringUtil.isNotNull(sql)){
			updateSql.append(" ");
			updateSql.append(executeSQL(sql, params));
		}
		return updateSql.toString();
	}
	
	public String deleteById(Class<?> clazz, int id) {
		String tableName = getTableName(clazz);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" delete from ").append(tableName).append(" where id="+id);
		return deleteSql.toString();
	}
	
	public String deleteByIds(Class<?> clazz, int ...ids) {
		String tableName = getTableName(clazz);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" delete from ").append(tableName).append(" where ");
		for(int i = 0 ; i < ids.length; i++){
			if(i == ids.length -1)
				deleteSql.append(" id="+ids[i] );
			else{
				deleteSql.append(" id="+ids[i]);
				deleteSql.append(" or ");
			}
		}
			
		return deleteSql.toString();
	}
	
	public String deleteByField(Class<?> clazz, String field, Object[] values) {
		String tableName = getTableName(clazz);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" delete from ").append(tableName).append(" where ");
		for(int i = 0 ; i < values.length; i++){
			if(i == values.length -1)
				deleteSql.append(field + "=" + values[i] );
			else{
				deleteSql.append(field + "=" + values[i]);
				deleteSql.append(" or ");
			}
		}
			
		return deleteSql.toString();
	}

	/*public String findFirst(Object bean) {
		Class<?> beanClass = bean.getClass();
		String tableName = getTableName(beanClass);
		Field[] fields = getFields(beanClass);
		StringBuilder selectSql = new StringBuilder();
		List<String> selectParaNames = new ArrayList<String>();
		List<String> selectParas = new ArrayList<String>();
		selectSql.append("select ");
		try {
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				//特殊处理serialVersionUID
				if(field.getName().equalsIgnoreCase("serialVersionUID")){
					continue;
				}
				Column column = field.getAnnotation(Column.class);
				String columnName = "";
				//判断是否获取的是注解的信息，是的话在实体后面添加上注解，便于还原回实体
				boolean getAnnotation = false; 
				if (column != null) {
					if (!column.required())
						continue;
					columnName = column.value().toUpperCase();
					getAnnotation = true;
				}
				
				if (StringUtils.isEmpty(columnName)) {
					columnName = tableFormat.getColumnName(field.getName());
				}
				field.setAccessible(true);
				//Object object = field.get(clazz);
				selectSql.append(columnName);
				if(getAnnotation){
					selectSql.append(" " + field.getName());
				}
				if (object != null) {
					selectParaNames.add(columnName);
					selectParas.add("#{" + field.getName() + "}");
				}
				if (i != fields.length - 1)
					selectSql.append(",");
			}
		} catch (Exception e) {
			new RuntimeException("get select sql is exceptoin:" + e);
		}
		selectSql.append(" from ").append(tableName).append(" where ");
		for (int i = 0; i < selectParaNames.size(); i++) {
			selectSql.append(selectParaNames.get(i)).append("=").append(selectParas.get(i));
			if (i != selectParaNames.size() - 1)
				selectSql.append(" and ");
		}
		return selectSql.toString();
	}*/
	
	/**
	 * 通过实体的ID寻找到该实体
	 * @param clazz
	 * @param id
	 * @return
	 */
	public String findById(Class<?> clazz, int id) {
		String tableName = getTableName(clazz);
		Field[] fields = getFields(clazz);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("select ");
		try {
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				//特殊处理serialVersionUID
				if(field.getName().equalsIgnoreCase("serialVersionUID")){
					continue;
				}
				Column column = field.getAnnotation(Column.class);
				String columnName = "";
				//判断是否获取的是注解的信息，是的话在实体后面添加上注解，便于还原回实体
				boolean getAnnotation = false; 
				if (column != null) {
					if (!column.required())
						continue;
					columnName = column.value().toUpperCase();
					getAnnotation = true;
				}
				
				if (StringUtils.isEmpty(columnName)) {
					columnName = tableFormat.getColumnName(field.getName());
				}
				field.setAccessible(true);
				selectSql.append(columnName);
				if(getAnnotation){
					selectSql.append(" " + field.getName());
				}
				if (i != fields.length - 1)
					selectSql.append(",");
			}
		} catch (Exception e) {
			new RuntimeException("get select sql is exceptoin:" + e);
		}
		selectSql.append(" from ").append(tableName).append(" where id="+id);
		return selectSql.toString();
	}
	
	/**
	 * 通过实体列表
	 * @param clazz
	 * @param id
	 * @return
	 */
	public String getBeans(String sql, Object ... params){
		return executeSQL(sql, params);
	}
	
	public String getObjectCreateUserId(String tableName, int tableId){
		return "select create_user_id from "+tableName +" where id="+tableId;
	}
	
	/**
	 * 通过实体列表
	 * @param clazz
	 * @param id
	 * @return
	 */
	public String getTotal(String tableName, String where){
		return "select count(*) ct from "+tableName +" " +StringUtil.changeNotNull(where);
	}
	
	public String getTotalByUser(String tableName, int userId){
		return "select count(*) ct from "+tableName +" where create_user_id="+userId;
	}
	
	public String exists(Class<?> clazz, String tableName, int tableId, int userId){
		String table = getTableName(clazz);
		return "select id from "+table+" where table_id = "+tableId+" and table_name = '"+ tableName +"' and create_user_id ="+userId;
	}
	
	public String recordExists(String tableName, int tableId){
		return "select id from "+tableName +" where id = "+tableId;
	}

	/**
	 * 执行最基本的sql语句
	 * @param clazz
	 * @param id
	 * @return
	 */
	public String executeSQL(String sql, Object ...params) {
		StringBuffer selectSql = new StringBuffer();
		
		int paramsIndex = 0;
		
		//替换掉“？”的参数
		for(int i = 0; i< sql.length(); i++){
			if(sql.charAt(i) == '?'){
				selectSql.append(buildParams(params[paramsIndex]));
				paramsIndex ++;
			}else{
				selectSql.append(sql.charAt(i));
			}
		}
		return selectSql.toString();
	}
	
	private Object buildParams(Object param){
		if (param instanceof Integer) {
		    return param;
		} else if (param instanceof String) {
			 return "'" +param+ "'";
		} else if (param instanceof Double) {
			 return param;
		} else if (param instanceof Float) {
			 return param;
		} else if (param instanceof Long) {
			 return param;
		} else if (param instanceof Boolean) {
		    boolean b = ((Boolean) param).booleanValue();
		    return b ? 1 : 0;
		} else if (param instanceof Date) {
		    return "str_to_date('"+DateUtil.DateToString((Date)param)+"', '%Y-%c-%d %H:%i:%s')";
		} 
		
		try {
			throw new ErrorException("BaseMapper 执行executeSQL() 传递的参数是不支持的类型");
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	private String getTableName(Class<?> beanClass) {
		String tableName = "";
		Table table = beanClass.getAnnotation(Table.class);
		if (table != null) {
			tableName = table.value();
		} else {
			tableName = tableFormat.getTableName(beanClass.getSimpleName());
		}
		
		if(!StringUtils.isEmpty(tableName) && tableName.endsWith("_BEAN")){
			tableName = tableName.substring(0, tableName.length() - "_BEAN".length());
		}
		return tableName;
	}
	
	/**
	 * 获取类clazz的所有Field，包括其父类的Field，如果重名，以子类Field为准。
	 * @param clazz
	 * @return Field数组
	 */
	public Field[] getFields(Class<?> beanClass) {
		ArrayList<Field> fieldList = new ArrayList<Field>();
		Field[] dFields = beanClass.getDeclaredFields();
		if (null != dFields && dFields.length > 0) {
			fieldList.addAll(Arrays.asList(dFields));
		}

		Class<?> superClass = beanClass.getSuperclass();
		if (superClass != Object.class) {
			Field[] superFields = getFields(superClass);
			if (null != superFields && superFields.length > 0) {
				for(Field field:superFields){
					if(!isContain(fieldList, field)){
						fieldList.add(field);
					}
				}
			}
		}
		Field[] result=new Field[fieldList.size()];
		fieldList.toArray(result);
		return result;
	}

	/**检测Field List中是否已经包含了目标field
	 * @param fieldList
	 * @param field 带检测field
	 * @return
	 */
	public static boolean isContain(ArrayList<Field> fieldList,Field field){
		for(Field temp:fieldList){
			if(temp.getName().equals(field.getName())){
				return true;
			}
		}
		return false;
	}
}
