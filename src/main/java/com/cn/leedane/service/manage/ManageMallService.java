package com.cn.leedane.service.manage;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 管理自己信息service接口类
 * @author LeeDane
 * 2019年11月12日 下午6:31:23
 * version 1.0
 */
@Transactional
public interface ManageMallService<T extends IDBean>{

	/**
	 * 推广位申请
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel promotionApply(JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 生成邀请码
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel buildReferrerCode(JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 绑定推荐人
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel bindReferrer(JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 推荐关系
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel referrerRelation(JSONObject json, UserBean user, HttpRequestInfoBean request);
}
