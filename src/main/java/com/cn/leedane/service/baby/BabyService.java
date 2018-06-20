package com.cn.leedane.service.baby;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 宝宝的Service类
 * @author LeeDane
 * 2018年6月6日 下午5:05:08
 * version 1.0
 */
@Transactional
public interface BabyService <T extends IDBean>{
	/**
	 * 添加新宝宝
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> add(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 编辑宝宝
	 * @param babyId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> update(int babyId, JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 删除宝宝
	 * @param babyId 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> delete(int babyId, UserBean user, HttpServletRequest request);

}	
