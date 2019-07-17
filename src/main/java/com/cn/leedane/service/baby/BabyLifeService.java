package com.cn.leedane.service.baby;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 宝宝生活方式的Service类
 * @author LeeDane
 * 2018年6月11日 下午6:44:53
 * version 1.0
 */
@Transactional
public interface BabyLifeService <T extends IDBean>{
	/**
	 * 添加新生活方式
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> add(int babyId, JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 编辑生活方式
	 * @param babyId
	 * @param lifeId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> update(int babyId, int lifeId, JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 删除宝宝生活方式
	 * @param babyId
	 * @param lifeId
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> delete(int babyId, int lifeId, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取宝宝的生活方式列表
	 * @param babyId
	 * @param startDate
	 * @param endDate
	 * @param keyWord
	 * @param lifeType
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> lifes(int babyId,
			String startDate, String endDate, String keyWord, int lifeType,
			UserBean user, HttpRequestInfoBean request);

}	
