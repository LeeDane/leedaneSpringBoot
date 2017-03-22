package com.cn.leedane.service;

import org.springframework.transaction.annotation.Transactional;

@Transactional("txManager")
public class TimeConsumingTest implements ITimeConsumingTest{
	
	@Override
	 public void save(){  
		 System.out.println("测试类的test方法被调用");  
	}  

}
