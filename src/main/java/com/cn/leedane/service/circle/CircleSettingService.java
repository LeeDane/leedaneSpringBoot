package com.cn.leedane.service.circle;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 圈子设置的Service类
 * @author LeeDane
 * 2017年7月6日 下午5:15:59
 * version 1.0
 */
@Transactional
public interface CircleSettingService <T extends IDBean>{

	/**
	 * 更新设置
	 * @param circleId
	 * @param settingId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> update(long circleId, long settingId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
}
