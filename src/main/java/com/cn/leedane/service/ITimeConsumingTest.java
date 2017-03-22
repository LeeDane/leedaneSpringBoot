package com.cn.leedane.service;

import org.springframework.transaction.annotation.Transactional;

@Transactional("txManager")
public interface ITimeConsumingTest {
	
	 public void save();

}
