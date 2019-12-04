package com.cn.leedane.test;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cn.leedane.cache.SystemCache;

public class SystemCacheTest extends BaseTest {
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private SystemCache systemCache;
	
	@Test
	public void addCache(){
		systemCache.addCache("lee", "Dane");
		logger.info(systemCache.getCache("lee"));
		
	}
}
