package com.cn.leedane.test;

import java.util.Date;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.junit.Test;

import com.cn.leedane.utils.Base64ImageUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.model.RolesBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.UserRoleBean;
import com.cn.leedane.service.RolesService;
import com.cn.leedane.service.UserRoleService;
import com.cn.leedane.service.UserService;

/**
 * 用户相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:29:54
 * Version 1.0
 */
public class UserTest extends BaseTest {
	@Resource
	private UserService<UserBean> userService;
	
	@Resource
	private RolesService<RolesBean> rolesService;
	
	@Resource
	private UserRoleService<UserRoleBean> userRoleService;
	
	private Date[] dates ={DateUtil.stringToDate("2015-06-16 14:10:37"),DateUtil.stringToDate("2015-07-10 12:13:08")
			,DateUtil.stringToDate("2015-07-08 09:03:07"),DateUtil.stringToDate("2015-07-09 21:53:33")
			,DateUtil.stringToDate("2015-07-08 11:18:34"),DateUtil.stringToDate("2015-07-06 21:48:38")
			,DateUtil.stringToDate("2015-06-11 16:16:20"),DateUtil.stringToDate("2015-06-19 03:50:22")
			,DateUtil.stringToDate("2015-06-24 08:13:00"),DateUtil.stringToDate("2014-12-29 23:53:32")
			,DateUtil.stringToDate("2015-06-17 11:10:08"),DateUtil.stringToDate("2015-06-18 19:13:04")
			,DateUtil.stringToDate("2015-06-19 22:45:09"),DateUtil.stringToDate("2015-06-23 02:10:28")
			,DateUtil.stringToDate("2015-06-22 16:43:45"),DateUtil.stringToDate("2015-06-20 15:23:01")
			,DateUtil.stringToDate("2014-07-11 08:13:00"),DateUtil.stringToDate("2013-05-09 16:13:38")
			,DateUtil.stringToDate("2012-11-11 01:00:04"),DateUtil.stringToDate("2012-12-09 20:21:11")
			,DateUtil.stringToDate("2011-02-12 09:10:08"),DateUtil.stringToDate("2011-03-05 21:10:30")
			,DateUtil.stringToDate("2014-07-11 08:13:00"),DateUtil.stringToDate("2012-08-26 15:10:18")
			,DateUtil.stringToDate("2009-04-16 02:19:07"),DateUtil.stringToDate("2012-05-16 19:23:35")
			,DateUtil.stringToDate("2014-07-11 08:13:00"),DateUtil.stringToDate("2014-12-29 23:53:32")};
		

	@Test
	public void addUser() throws Exception{
		//先建两个角色
				
		/*RolesBean role1 = new RolesBean();
		role1.setName("管理员");
		role1.setCode("ADMIN");
		role1.setStatus(1);
		
		RolesBean role2 = new RolesBean();
		role2.setName("普通用户");
		role2.setCode("SIMPLE");
		role2.setStatus(1);
		
		rolesService.save(role1);
		rolesService.save(role2);*/
		
		
		UserBean use = null;
		
		int first = 1;
		int end = first + dates.length;  
		
		for(int i = first; i < end; i++){
			int age = (int) (Math.random()*50);
			use = new UserBean();
			use.setStatus(1);
			use.setAccount("Lee"+i);
			use.setChinaName("账号"+i);
			use.setEmail("424335535222"+i+"@qq.com");
			use.setPassword("d9b1d7db4cd6e70935368a1efb10e377"+i);
			use.setRegisterCode("2015051511403387b34fdbd72fdecd596f0c583dd483a0f"+i);
			use.setRegisterTime(dates[i-first]);
			use.setAge(age);
			use.setType(0);
			
			//use.getRoles().add(rolesService.findById(1));
			//use.getRoles().add(rolesService.findById(2));
			userService.save(use);
			
			/*UserRoleBean userRoleBean = new UserRoleBean();
			userRoleBean.setRole(role1);
			userRoleBean.setUser(use);
			userRoleService.save(userRoleBean);
			if( i%2== 0){
				UserRoleBean userRoleBean2 = new UserRoleBean();
				userRoleBean2.setRole(role2);
				userRoleBean2.setUser(use);
				userRoleService.save(userRoleBean2);
			}	*/
		}
	}	
	
	@Test
	public void deleteID(){
		UserBean b = userService.findById(3);
		try {
			userService.delete(b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//个人上传图片
	@Test
	public void uploadHeadBase64StrById(){
		
		String filePath = ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER+"yang.jpg";
		String base64;
		try {
			base64 = Base64ImageUtil.convertImageToBase64(filePath, null);
			String str = "{\"base64\": \""+ base64+ "\"}";
			JSONObject jo = JSONObject.fromObject(str);
			
			UserBean user = userService.findById(6);
			
			boolean sucess = userService.uploadHeadBase64StrById(jo, user, null);
			System.out.println("sucess:"+sucess);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getHeadBase64StrById(){
		try {
			
			String str = "{\"uid\":5, \"pic_size\":\"30x30\"}";
			JSONObject jo = JSONObject.fromObject(str);
			
			UserBean user = userService.findById(1);
			
			String base64Str = userService.getHeadBase64StrById(jo, user, null);
			System.out.println("base64Str:"+base64Str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
