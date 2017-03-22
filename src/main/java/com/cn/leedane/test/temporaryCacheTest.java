package com.cn.leedane.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cn.leedane.cache.TemporaryCache;

public class temporaryCacheTest extends BaseTest {

	@Autowired
	private TemporaryCache temporaryCache;
	
	@Test
	public void addCache(){
		temporaryCache.addCache("lee", "Dane");
		System.out.println(temporaryCache.getCache("lee"));
		
	}
}
