package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.cn.leedane.mybatis.SqlProvider;

/**
 * 基本mapper
 * @author LeeDane
 * 2016年7月13日 上午9:12:58
 * Version 1.0
 */
public interface BaseMapper<T> {
	
	@InsertProvider(type = SqlProvider.class, method = "saveClass")
    @Options(useGeneratedKeys=true)
	public int saveClass(Class<?> clazz);
	
	@InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(useGeneratedKeys=true)
	public int save(T bean);
	
	@InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(useGeneratedKeys=true)
    public int insert(T bean);

    @DeleteProvider(type = SqlProvider.class, method = "delete")
    public int delete(T bean);
    
    @DeleteProvider(type = SqlProvider.class, method = "deleteById")
    public int deleteById(Class<?> clazz, int id);
    
    /**
     * 批量删除
     * @param clazz
     * @param ids
     * @return
     */
    @DeleteProvider(type = SqlProvider.class, method = "deleteByIds")
    public int deleteByIds(Class<?> clazz, int ...ids);
    
    /**
     * 批量删除
     * @param clazz
     * @param filed
     * @param ids
     * @return
     */
    @DeleteProvider(type = SqlProvider.class, method = "deleteByField")
    public int deleteByField(Class<?> clazz, String filed, Object ...values);
    
    @DeleteProvider(type = SqlProvider.class, method = "deleteSql")
    public int deleteSql(Class<?> clazz, String sql, Object ... params);

    @UpdateProvider(type = SqlProvider.class, method = "update")
    public int update(T bean);
    
    @UpdateProvider(type = SqlProvider.class, method = "updateSql")
    public int updateSql(Class<?> clazz, String sql, Object ... params);

    /*@SelectProvider(type = SqlProvider.class, method = "findFirst")
    public T findFirst(T bean);*/
    
    @SelectProvider(type = SqlProvider.class, method = "findById")
    public T findById(Class<?> clazz, int id);
    
    @SelectProvider(type = SqlProvider.class, method = "getBeans")
    public List<T> getBeans(String sql, Object ...params);
    
    @SelectProvider(type = SqlProvider.class, method = "executeSQL")
    public List<Map<String, Object>> executeSQL(String sql, Object ...params);
    
    /**
	 * 判断是否已经存在
	 * @param tableName
	 * @param tableId
	 * @param userId
	 * @return
	 */
    @SelectProvider(type = SqlProvider.class, method = "exists")
	public List<Map<String, Object>> exists(Class<?> clazz, String tableName, int tableId, int userId);
	
	/**
	 * 判断记录在数据中是否存在
	 * @param tableName
	 * @param tableId
	 * @return
	 */
    @SelectProvider(type = SqlProvider.class, method = "recordExists")
	public List<Map<String, Object>> recordExists(String tableName, int tableId);
    
    /**
	 * 根据实体表的名称和ID获取其创建人ID
	 * @param commentId
	 * @return
	 */
    @SelectProvider(type = SqlProvider.class, method = "getObjectCreateUserId")
	public List<Map<String, Object>> getObjectCreateUserId(String tableName, int tableId);
    
    /**
	 * 获取对应的用户的总数
	 * @param userId
	 * @return
	 */
    @SelectProvider(type = SqlProvider.class, method = "getTotalByUser")
	public List<Map<String, Object>> getTotalByUser(String tableName, int userId);
    
    /**
	 * 获取总数
	 * @param tableName  表名
	 * @param where where后面语句，参数需直接填写在字符串中
	 * @return
	 */
    @SelectProvider(type = SqlProvider.class, method = "getTotal")
    public List<Map<String, Object>> getTotal(String tableName, String where);
}
