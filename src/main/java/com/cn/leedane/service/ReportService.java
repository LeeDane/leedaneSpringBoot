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
 * 举报的Service类
 * @author LeeDane
 * 2016年7月12日 上午11:34:48
 * Version 1.0
 */
@Transactional
public interface ReportService<T extends IDBean>{

	/**
	 * 添加举报(已经存在直接返回false)
	 * {'table_name':'t_mood', 'table_id':123, 'reason':'青色'}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> addReport(JSONObject jo, UserBean user, HttpServletRequest request);

	

	/**
	 * 取消举报
	 * {'table_name':'t_mood', 'table_id':234}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> cancel(JSONObject jo, UserBean user, HttpServletRequest request) ;



	/**
	 * 获取举报列表
	 * {'uid':1,'table_name':'t_mood','table_id':6, 'method':'firstloading',
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
