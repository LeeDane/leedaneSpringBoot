package com.cn.leedane.service;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TimeConsumingTest implements ITimeConsumingTest{
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	 public void save(){  
		logger.info("测试类的test方法被调用");  
	}  

}
