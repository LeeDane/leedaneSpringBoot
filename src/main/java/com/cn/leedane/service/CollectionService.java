package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
/**
 * 收藏夹的Service类
 * @author LeeDane
 * 2016年7月12日 上午11:32:01
 * Version 1.0
 */
@Transactional
public interface CollectionService<T extends IDBean>{

	/**
	 * 添加收藏(已经存在直接返回true)
	 * {'table_name':'t_mood', 'table_id':123}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> addCollect(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	

	/**
	 * 取消收藏
	 * {'cid':1, 'create_user_id':123}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deleteCollection(JSONObject jo, UserBean user, HttpRequestInfoBean request) ;



	/**
	 * 获取收藏列表
	 * {'uid':1,'table_name':'t_mood','table_id':123, 'method':'firstloadings'
	 * 'pageSize':5,'last_id':0,'first_id':0}
	 * 根据uid获取该用户的收藏列表，table_name为空表示获取全部收藏列表，table_name不为空，获取指定表收藏，table_id
	 * 为空，获取指定表下面的全部收藏列表，table_id不为空，获取指定的收藏
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> getLimit(JSONObject jo, UserBean user,
			HttpRequestInfoBean request);
}
