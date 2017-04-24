package com.cn.leedane.service;

import java.util.List;
import java.util.Map;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.LinkManageBean;
import com.cn.leedane.model.UserBean;

/**
 * App版本管理service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:28:51
 * Version 1.0
 */
public interface SqlBaseService <T extends IDBean>{
	/**
	 * 执行SQL对应字段的List<Map<String,Object>
	 * @param sql sql语句,参数直接写在语句中，存在SQL注入攻击de风险，慎用
	 * @param params ?对应的值
	 * @return
	 */
	public List<Map<String, Object>> executeSQL(String sql, Object ...params);
	
	/**
	 * 获取总数
	 * @param tableName  表名
	 * @param where where后面语句，参数需直接填写在字符串中
	 * @return
	 */
	public int getTotal(String tableName, String where);
	
	/**
	 * 获取当前用户的总数(评论/转发数。。。)
	 * @param userId
	 * @return
	 */
	public int getTotalByUser(String tableName, int userId);
	
	/**
	 * 获取我的全部的好友ID和备注
	 * @param uid
	 * @return
	 */
	public List<Map<String, Object>> getFromToFriends(int uid);
	
	/**
	 * 获取全部的好友对我的ID和备注
	 * @param uid
	 * @return
	 */
	public List<Map<String, Object>> getToFromFriends(int uid);
	
	/**
	 * 基础根据id找到一个实体对象
	 * 不存在延迟加载问题，不采用lazy机制，在内部缓存中进行数据查找，
	 * 如果没有发现数据則将越过二级缓存，直接调用SQL查询数据库。
	 * 如果没有数据就返回null，返回的是真正的实体类
	 * @param id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Object findById(Class clazz, int id);
	
	/**
	 * 基础的保存实体的方法
	 * @param t
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean saveClass(Class clazz);
	
	@SuppressWarnings("rawtypes")
	public boolean updateSql(Class clazz, String sql, Object...params);
	
	/**
	 * 获取所有的链接(包括正常状态和非正常状态)
	 * @param user
	 * @return
	 */
	public List<LinkManageBean> getAllLinks();
	
	/**
	 * 用户历史上是否有签到记录
	 * @param userId 用户ID
	 * @return
	 */
	public boolean hasHistorySign(int userId);
	
	/**
	 * 账号密码登录
	 * @param condition  用户的账号/邮箱
	 * @param password  用户的密码(密码将再次进行MD5加密)
	 * @return
	 */
	public UserBean loginUser(String condition , String password);
	
	/**
	 * 通过微信用户名找到leedane系统绑定的用户对象
	 * @param fromUserName
	 * @return
	 */
	public UserBean findUserBeanByWeixinName(String fromUserName);
	
}
