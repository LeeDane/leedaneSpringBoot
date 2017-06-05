package com.cn.leedane.handler.circle;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.cn.leedane.model.UserBean;

/**
 * 圈子的处理类
 * @author LeeDane
 * 2017年5月31日 上午9:59:16
 * version 1.0
 */
@Component
public class CircleHandle {
	
	/**
	 * 获取该用户在该圈子角色的编码
	 * @param user
	 * @param circle
	 * @return
	 */
	public int getRoleCode(UserBean user, int circle){
		return 1;
	}

}
