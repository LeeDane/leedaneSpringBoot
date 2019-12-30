package com.cn.leedane.service.manage;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
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
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> promotionApply(JSONObject jo, UserBean user, HttpRequestInfoBean request);

}
