package com.cn.leedane.service.manage;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.LayuiTableResponseModel;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理我的博客service接口类
 * @author LeeDane
 * 2019年11月12日 下午6:31:23
 * version 1.0
 */
@Transactional
public interface ManageBlogService<T extends IDBean>{
	/**
	 * 获取博客列表
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public LayuiTableResponseModel list(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception;

	/**
	 * 获取草稿列表
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public LayuiTableResponseModel draft(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception;

	/**
	 * 删除我的博客
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public ResponseModel delete(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception;
}
