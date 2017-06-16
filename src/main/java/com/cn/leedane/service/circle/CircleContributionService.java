package com.cn.leedane.service.circle;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
/**
 * 圈子贡献值的Service类
 * @author LeeDane
 * 2017年6月14日 下午5:25:43
 * version 1.0
 */
@Transactional
public interface CircleContributionService<T extends IDBean>{
	
	/**
	 * 获取当前用户的总贡献值
	 * @param userId
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int getTotalScore(int userId);

	/**
	 * 分页获得贡献值历史列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 获取当前用户的总贡献值
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getTotalScore(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 减少分数
	 * @param reduceScore
	 * @param desc
	 * @param user
	 * @return
	 */
	public Map<String, Object> reduceScore(int reduceScore, String desc, UserBean user);
	
}
