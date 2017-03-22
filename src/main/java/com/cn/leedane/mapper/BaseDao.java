package com.cn.leedane.mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 基础的数据访问层mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:03:57
 * Version 1.0
 */
//相关配置请参考http://jingyan.baidu.com/article/455a9950872dc1a16627782f.html
public interface BaseDao<T extends Serializable> {

	/**
	 * 基础的保存实体的方法
	 * @param t
	 * @return
	 */
	//public boolean save(T t);
	
	/**
	 * 基础的保存，要是已经存在，就更新
	 * @param t
	 * @return
	 */
	//public boolean saveOrUpdate(T t);
	
	/**
	 * 基础更新实体的方法
	 * @param t
	 * @return
	 */
	//public boolean update(T t);
	
	/**
	 * 基础更新实体的方法
	 * @param sql
	 * @return
	 */
	//public boolean update(String sql);
	
	/**
	 * 基础更新实体的方法
	 * @param sql
	 * @param params
	 * @return
	 */
	//public boolean update(String sql, Object ... params);
	
	/**
	 * 基础更新SQL的方法
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	//public boolean updateSQL(String sql, Object ... obj);
	
	/**
	 * 根据多个id批量更新或者删除指定table
	 * @param sql sql语句，其他参数不能用?代替，必须写死，但是必须有id=?(其中的?要保留)
	 * @param ids  id整型数组
	 * @return
	 * @throws Exception
	 */
	
	//public boolean updateBatch(String sql, int[] ids);
	
	/**
	 * 基础删除实体的方法
	 * @param t
	 * @return
	 * @throws Exception
	 */
	//public boolean delete(T t);
	
	/**
	 * 基础根据表ID删除的方法
	 * @param tableName  表的名称
	 * @param id 表中的id字段的值
	 * @return
	 * @throws Exception
	 */
	//public boolean deleteById(String tableName, final int id);
	
	/**
	 * 根据id找到一个实体对象
	 * 存在延迟加载的问题，充分利用hibernate的内部缓存和二级缓存中的现有数据，
	 * 也许别人把数据库中的数据修改了，load如果在缓存中找到了数据，则不会再访问数据库，
	 * 而get则会返回最新数据。
	 * 如果没有得到数据，将抛出异常，返回的将是实体的代理类
	 * @param id
	 * @return
	 */
	//public T loadById(int id);
	
	/**
	 * 基础根据id找到一个实体对象
	 * 不存在延迟加载问题，不采用lazy机制，在内部缓存中进行数据查找，
	 * 如果没有发现数据則将越过二级缓存，直接调用SQL查询数据库。
	 * 如果没有数据就返回null，返回的是真正的实体类
	 * @param id
	 * @return
	 */
	//public T findById(int id);
	
	/**
	 * 基础根据id找到一个实体对象
	 * @param id
	 * @return
	 */
	//public void merge(T entity);
	
	/**
	 * 获取总数
	 * @param tableName  表名
	 * @param where where后面语句，参数需直接填写在字符串中
	 * @return
	 */
	//public int getTotal(String tableName, String where);
	
	/**
	 * 分页获取单表的全部字段的数据
	 * 注意：1、这个只获取单表
	 *      2、获取的是该表的全部字段
	 * @param tableName  表名
	 * @param where  where语句，参数需直接填写在字符串中
	 * @param pageSize  每页条数
	 * @param pageNo  第几页
	 * @return
	 */
	//public List<Map<String, Object>> getlimits(String tableName, String where, int pageSize, int pageNo);
	
	/**
	 * 获取指定表的全部数据的全部字段
	 *  注意：1、这个只获取单表
	 *        2、获取的是该表的全部字段
	 * @param tableName  表名
	 * @param where  where语句，参数需直接填写在字符串中
	 * @return
	 */
	//public List<Map<String, Object>> getAll(String tableName, String where);
	
	/**
	 * 执行HQL获得实体
	 * @param beanName  实体名称如：xxxBean
	 * @param where where语句，参数需直接填写在字符串中
	 * @return
	 */
	//public List<T> executeHQL(String beanName, String where);
	
	/**
	 * 执行SQL对应字段的List<Map<String,Object>
	 * @param sql sql语句,参数直接写在语句中，存在SQL注入攻击de风险，慎用
	 * @param params ?对应的值
	 * @return
	 */
	//public List<Map<String, Object>> executeSQL(String sql, Object ...params);

	/**
	 *  根据页数和页码获取分页信息
	 * @param sql sql语句(参数直接填写，存在SQL注入风险)
	 * @param pageSize  每页条数
	 * @param pageNo  第几页
	 * @return
	 */
	//public List<Map<String, Object>> getlimitsByPageSizeAndPageNo(String sql, int pageSize, int pageNo);
	
	/**
	 *  根据页数和页码获取分页信息
	 * @param sql sql语句(参数直接填写，存在SQL注入风险)
	 * @param pageSize  每页条数
	 * @param lastId  最后一条数据的id
	 * @return
	 */
	//public List<Map<String, Object>> getlimitsByPageSizeAndLastId(String sql, int pageSize, int lastId);
	
	/**
	 * 判断记录在数据中是否存在
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	//public boolean recordExists(String tableName, int tableId);
	
	/**
	 * 根据实体表的名称和ID获取其创建人ID
	 * @param commentId
	 * @return
	 */
	//public int getObjectCreateUserId(String tableName, int tableId);
	
}
