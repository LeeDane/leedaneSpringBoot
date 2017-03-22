package com.cn.leedane.service;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONObject;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
/**
 * 转发的Service类
 * @author LeeDane
 * 2016年7月12日 上午11:36:14
 * Version 1.0
 */
@Transactional("txManager")
public interface TransmitService<T extends IDBean>{

	/**
	 * 发表转发(对同一个对象可以多次转发)
	 * {'table_name':'t_mood', 'table_id':1, 'content':'转发内容'}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> add(JSONObject jo, UserBean user, HttpServletRequest request) throws Exception;

	

	/**
	 * 取消转发(由于对同一个对象可以多次转发)
	 * {'tid':1, "create_user_id":1}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deleteTransmit(JSONObject jo, UserBean user, HttpServletRequest request) ;



	/**
	 * 获取转发列表
	 * {'uid':1,'table_name':'t_mood','table_id':1, 'method':'firstloading'
	 * 'pageSize':5,'last_id':0,'first_id':0}
	 * 根据uid获取该用户的转发列表，table_name为空表示获取全部转发列表，table_name不为空，获取指定表转发，table_id
	 * 为空，获取指定表下面的全部转发列表，table_id不为空，获取指定的转发
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> getLimit(JSONObject jo, UserBean user,
			HttpServletRequest request);



	/**
	 * 获取当前用户的转发总数
	 * @param userId
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int getTotalTransmits(int userId);
	

	/**
	 * 更改转发编辑状态
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> updateTransmitStatus(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 获取总数
	 * @param tableName  表名
	 * @param where where后面语句，参数需直接填写在字符串中
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int getTotal(String tableName, String where);
}
