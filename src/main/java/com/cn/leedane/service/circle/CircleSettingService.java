package com.cn.leedane.service.circle;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

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
	public Map<String, Object> update(int circleId, int settingId, JSONObject json, UserBean user, HttpServletRequest request);
	
}
