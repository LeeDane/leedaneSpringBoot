package com.cn.leedane.service.circle;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
/**
 * 圈子贡献值的Service类
 * @author LeeDane
 * 2017年6月14日 下午5:25:43
 * version 1.0
 */
@Transactional
public interface CircleContributionService<T extends IDBean>{

	/**
	 * 分页获得贡献值历史列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 增加分数(并通知用户)
	 * @param addScore
	 * @param desc
	 * @return
	 */
	public Map<String, Object> addScore(int addScore, String desc, long circleId, UserBean user);
	
	/**
	 * 减少分数(并通知用户)
	 * @param reduceScore
	 * @param desc
	 * @param circleId
	 * @param user
	 * @return
	 */
	public Map<String, Object> reduceScore(int reduceScore, String desc, long circleId ,UserBean user);
	
}
