package com.cn.leedane.handler.circle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.mapper.circle.CircleMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.utils.ConstantsUtil;

/**
 * 圈子的处理类
 * @author LeeDane
 * 2017年5月31日 上午9:59:16
 * version 1.0
 */
@Component
public class CircleHandle {
	@Autowired
	private CircleMapper circleMapper;
	
	/**
	 * 获取该用户在该圈子角色的编码
	 * @param user
	 * @param circle
	 * @return
	 */
	public int getRoleCode(UserBean user, int circle){
		return 1;
	}
	
	/**
	 * 获取该用户所有的圈子
	 * @param user
	 * @param circle
	 * @return
	 */
	public List<CircleBean> getAllCircles(UserBean user){
		return circleMapper.getAllCircles(user.getId(), ConstantsUtil.STATUS_NORMAL);
	}

}
