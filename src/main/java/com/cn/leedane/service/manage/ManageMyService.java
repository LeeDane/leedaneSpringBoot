package com.cn.leedane.service.manage;

import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
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

}
