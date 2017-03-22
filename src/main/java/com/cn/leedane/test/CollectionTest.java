package com.cn.leedane.test;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.junit.Test;

import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.model.CollectionBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.CollectionService;
import com.cn.leedane.service.UserService;

/**
 * 收藏夹相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:09:27
 * Version 1.0
 */
public class CollectionTest extends BaseTest {
	
	@Resource
	private CollectionService<CollectionBean> collectionService;

	@Resource
	private UserService<UserBean> userService;
	
	@Test
	public void add() throws Exception{
		String str = "{'table_name':'"+DataTableType.心情.value+"', 'table_id':1}";
		JSONObject jsonObject = JSONObject.fromObject(str);
		UserBean user = userService.findById(3);
		System.out.println(collectionService.addCollect(jsonObject, user, null));
	}
	
	@Test
	public void cancel() throws Exception{
		String str = "{'table_name':'"+DataTableType.心情.value+"', 'table_id':1}";
		JSONObject jsonObject = JSONObject.fromObject(str);
		UserBean user = userService.findById(1);
		System.out.println(collectionService.deleteCollection(jsonObject, user, null));
	}
	
	@Test
	public void getLimit() throws Exception{
		String str = " {'uid':2,'table_name':'','table_id':0, 'method':'lowloading','pageSize':5,'last_id':2,'first_id':0}";
		JSONObject jsonObject = JSONObject.fromObject(str);
		UserBean user = userService.findById(1);
		System.out.println(collectionService.getLimit(jsonObject, user, null).size());
	}
}
