package com.cn.leedane.service.manage;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.LayuiTableResponseModel;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理我的黑名单service接口类
 * @author LeeDane
 * 2020年4月19日 下午6:31:23
 * version 1.0
 */
@Transactional
public interface ManageBlackService<T extends IDBean>{
	/**
	 * 获取黑名单列表
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public LayuiTableResponseModel blacks(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 添加黑名单
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public ResponseModel add(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 删除我的黑名单
	 * @param blackId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel delete(long blackId, JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 黑名单授权
	 * @param blackId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel authorization(long blackId, JSONObject jo, UserBean user, HttpRequestInfoBean request);
}
