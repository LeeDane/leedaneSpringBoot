package com.cn.leedane.test;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.junit.Test;

import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.model.CommentBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.CommentService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UserService;

/**
 * 评论相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:09:35
 * Version 1.0
 */
public class CommentTest extends BaseTest {
	@Resource
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Resource
	private UserService<UserBean> userService;
	
	@Resource
	private CommentService<CommentBean> commentService;
	
	

	@Test
	public void publish() throws Exception{
		long start = System.currentTimeMillis();
		UserBean user = userService.findById(1);
		
		String str = "{\"table_name\":\""+DataTableType.心情.value+"\", \"table_id\":1104, \"content\":\"谢谢，你也是！\", \"cid\":0}";
		
		JSONObject jo = JSONObject.fromObject(str);
		try {
			System.out.println("isSuccess:" +commentService.add(jo, user, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("最终总耗时：" +(end - start) +"毫秒");
	}
	
	@Test
	public void getCommentByLimit(){
		UserBean user = userService.findById(1);
		
		String str = "{\"table_name\":\""+DataTableType.心情.value+"\", \"table_id\":1,\"pageSize\":5,\"first_id\": 2, \"last_id\":2, \"method\":\"firstloading\"}";
		JSONObject jo = JSONObject.fromObject(str);
		try {
			List<Map<String, Object>> ls = commentService.getCommentsByLimit(jo, user, null);
			System.out.println("总数"+ls.size());
			for(Map<String, Object> map: ls){
				System.out.println("id:" +map.get("id"));
				System.out.println("content:" +map.get("content"));
				System.out.println("count:" +map.get("count"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getOneCommentItemsByLimit(){
		UserBean user = userService.findById(1);
		
		String str = "{\"table_name\":\""+DataTableType.心情.value+"\", \"cid\":1, \"table_id\":1,\"first_id\": 2, \"last_id\":2, \"method\":\"firstloading\"}";
		JSONObject jo = JSONObject.fromObject(str);
		try {
			List<Map<String, Object>> ls = commentService.getOneCommentItemsByLimit(jo, user, null);
			System.out.println("总数"+ls.size());
			for(Map<String, Object> map: ls){
				System.out.println("id:" +map.get("id"));
				System.out.println("content:" +map.get("content"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getCommentsCount(){
		UserBean user = userService.findById(1);
		
		String str = "{\"table_name\":\""+DataTableType.心情.value+"\", \"table_id\":1}";
		JSONObject jo = JSONObject.fromObject(str);
		try {
			int count = commentService.getCountByObject(jo, user, null);
			System.out.println("总数"+count);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
