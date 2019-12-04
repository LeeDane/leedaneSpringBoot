package com.cn.leedane.service.clock;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 任务成员的Service类
 * @author LeeDane
 * 2018年10月8日 下午5:22:10
 * version 1.0
 */
@Transactional
public interface ClockMemberService <T extends IDBean>{
	/**
	 * 添加任务成员
	 * @param clockId
	 * @param memberId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public boolean add(long clockId, long memberId, JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 编辑任务成员
	 * @param clockId
	 * @param memberId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> update(long clockId, long memberId, JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 删除任务成员
	 * @param clockId
	 * @param memberId
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> delete(long clockId, long memberId, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取任务的成员列表
	 * @param clockId
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> members(long clockId, UserBean user, HttpRequestInfoBean request);
}	
