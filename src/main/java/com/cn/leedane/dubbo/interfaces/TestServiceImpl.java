package com.cn.leedane.dubbo.interfaces;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cn.leedane.model.UserBean;


@Service("testService")
public class TestServiceImpl implements  TestService{

	@Override
	public UserBean sayHello() {
		System.out.println("反反复复");
		UserBean user = new UserBean();
		user.setAccount(UUID.randomUUID().toString());
		return user;
	}
	
}
