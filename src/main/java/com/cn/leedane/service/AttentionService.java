package com.cn.leedane.service;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 关注的Service类
 * @author LeeDane
 * 2016年7月12日 上午11:30:02
 * Version 1.0
 */
@Transactional
public interface AttentionService<T extends IDBean>{

	/**
	 * 添加关注(已经存在直接返回false)
	 * {'table_name':'t_mood', 'table_id':123}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> addAttention(JSONObject jo, UserBean user, HttpServletRequest request) ;

	

	/**
	 * 取消关注
	 * {'aid':1, 'create_user_id':234}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deleteAttention(JSONObject jo, UserBean user, HttpServletRequest request) ;



	/**
	 * 获取关注列表
	 * {'uid':1,'table_name':'t_mood','table_id':6, 'method':'777firstloadings'
	 * 'pageSize':5,'last_id':0,'first_id':0}
	 * 根据uid获取该用户的关注列表，table_name为空表示获取全部关注列表，table_name不为空，获取指定表关注，table_id
	 * 为空，获取指定表下面的全部关注列表，table_id不为空，获取指定的关注
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> getLimit(JSONObject jo, UserBean user,
			HttpServletRequest request);
}
