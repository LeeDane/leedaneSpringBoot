package com.cn.leedane.service.mall;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 购物商品大事件service接口类
 * @author LeeDane
 * 2017年11月10日 下午12:38:59
 * version 1.0
 */
@Transactional
public interface S_BigEventService <T extends IDBean>{
	/**
	 * 发布商品
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public ResponseModel save(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 分页获取大事件列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResponseModel paging(long productId, JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
}
