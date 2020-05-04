package com.cn.leedane.service.manage;

import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.LayuiTableResponseModel;
import com.cn.leedane.utils.ResponseModel;
import com.suning.api.exception.SuningApiException;
import com.taobao.api.ApiException;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 管理自己信息service接口类
 * @author LeeDane
 * 2019年11月12日 下午6:31:23
 * version 1.0
 */
@Transactional
public interface ManageMyService<T extends IDBean>{
	/**
	 * 绑定电子邮箱
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public ResponseModel bindEmail(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception;

	/**
	 * 绑定手机号码
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public ResponseModel bindPhone(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception;

	/**
	 * 第三方授权解绑
	 * @param oid 关联ID
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel thirdUnBind(long oid, JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 保存标签
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel saveTags(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取登录历史
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public LayuiTableResponseModel loginHistorys(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 删除登录历史
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel deleteLoginHistory(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取我的关注记录
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public LayuiTableResponseModel attentions(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 删除我的关注
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel deleteAttention(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取我的收藏记录
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public LayuiTableResponseModel collections(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 删除我的收藏
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel deleteCollection(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/* 添加注销账号记录
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public ResponseModel addLogout(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/* 取消注销账号申请记录
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public ResponseModel cancelLogout(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/* 注销账号
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public ResponseModel destroyLogout(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/* 获取注销账号记录
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResponseModel getLogout(JSONObject jo, UserBean user, HttpRequestInfoBean request);
}
