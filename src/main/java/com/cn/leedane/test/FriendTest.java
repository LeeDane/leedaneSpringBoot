package com.cn.leedane.test;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.junit.Test;

import com.cn.leedane.model.FriendBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.FriendService;
import com.cn.leedane.service.UserService;

/**
 * 好友相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:10:47
 * Version 1.0
 */
public class FriendTest extends BaseTest {
	@Resource
	private FriendService<FriendBean> friendService;
	
	@Resource
	private UserService<UserBean> userService;
	@Test
	public void deleteFriends(){
		/*boolean delete = friendService.deleteFriends(3, 4, 2);
		System.out.println("detete:" +delete);*/
	}
	
	@Test
	public void agreeFriends(){
		String str = "{\"fid\":9, \"from_user_remark\":\"小羊\"}";
		JSONObject jsonObject = JSONObject.fromObject(str);
		UserBean user = userService.findById(1);
		friendService.addAgree(jsonObject, user, null);
	}
}
