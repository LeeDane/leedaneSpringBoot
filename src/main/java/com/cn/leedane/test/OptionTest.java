package com.cn.leedane.test;

import javax.annotation.Resource;

import org.junit.Test;

import com.cn.leedane.mapper.OptionMapper;
import com.cn.leedane.model.OptionBean;
import com.cn.leedane.service.TimeConsumingTest;

/**
 * Option选项实体相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:27:54
 * Version 1.0
 */
public class OptionTest extends BaseTest{
	
	@Resource
	private OptionMapper optionMapper;

	@Test
	public void testSave() throws Exception {
		OptionBean bean = new OptionBean();
		bean.setId(1);
		bean.setOptionKey("hello");
		bean.setOptionValue("world");
		bean.setVersion(3);
		optionMapper.save(bean);

	}
	
	@Test
	public void testUpdate() throws Exception{
		
		OptionBean bean = optionMapper.findById(OptionBean.class, 1);
		bean.setVersion(3);
		optionMapper.update(bean);
	}
	
	@Test
	public void testDelete(){
		TimeConsumingTest test = new TimeConsumingTest();
		test.save();
	}
}
