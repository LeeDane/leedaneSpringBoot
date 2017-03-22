package com.cn.leedane.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cn.leedane.cache.SystemCache;

public class SystemCacheTest extends BaseTest {

	@Autowired
	private SystemCache systemCache;
	
	@Test
	public void addCache(){
		systemCache.addCache("lee", "Dane");
		System.out.println(systemCache.getCache("lee"));
		
	}
}
