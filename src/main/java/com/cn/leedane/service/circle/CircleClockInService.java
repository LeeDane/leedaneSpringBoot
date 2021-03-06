package com.cn.leedane.service.circle;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * 圈子打卡的Service类
 * @author LeeDane
 * 2017年6月14日 下午4:29:06
 * version 1.0
 */
@Transactional
public interface CircleClockInService <T extends IDBean>{
	/**
	 * 用户指定时间对圈子是否打卡
	 * @param user
	 * @param circleId
	 * @param dateTime
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> isClockIn(UserBean user, long circleId, Date dateTime);
	
	/**
	 * 保存(打卡),当天已经打卡的直接返回false
	 * @param circleId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> saveClockIn(long circleId, JSONObject jo, UserBean user, HttpRequestInfoBean request);


	/**
	 * 获取打卡的分页记录
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
}
