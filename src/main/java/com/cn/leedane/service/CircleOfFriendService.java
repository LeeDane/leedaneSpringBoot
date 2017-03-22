package com.cn.leedane.service;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONObject;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
/**
 * 朋友圈的Service类
 * @author LeeDane
 * 2016年7月12日 上午11:31:45
 * Version 1.0
 */
@Transactional("txManager")
public interface CircleOfFriendService<T extends IDBean>{

	/**
	 * 获取朋友圈列表
	 * {'uid':1,'table_name':'t_mood','table_id':123, 'method':'firstloadings'
	 * 'pageSize':5,'last_id':0,'first_id':0}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getLimit(JSONObject jo, UserBean user, HttpServletRequest request);
}
