package com.cn.leedane.test;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.cn.leedane.utils.EmailUtil;
import com.cn.leedane.model.UserBean;

/**
 * 邮件相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:10:18
 * Version 1.0
 */
public class EmailTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@Test
	public void sendRE(){
		//EmailBean email = new EmailBean();
		String content = "最近我过得很开心！<a href = 'http://localhost:8080/cnBuy/login.jsp'>点击百度</a>";
		//email.setContent("最近我过得很开心！<a href = 'http://localhost:8080/cnBuy/login.jsp'>点击百度</a>");
		UserBean user = new UserBean();
		user.setAccount("account1");
		user.setChinaName("账号1");
		user.setEmail("642034701@qq.com");
		user.setQq("642034701");
		user.setStr1("qq19901107");
		//email.setFrom(user);
		
		UserBean user2 = new UserBean();
		user2.setAccount("account2");
		user2.setChinaName("账号2");
		user2.setEmail("825711424@qq.com");
		
		UserBean user3 = new UserBean();
		user3.setAccount("account3");
		user3.setChinaName("账号3");
		user3.setEmail("825711424@qq.com");
			
		/*UserBean user3 = new UserBean();
		user3.setAccount("account3");
		user3.setChinaName("账号3");
		user3.setEmail("1576178870@qq.com");*/
		
		Set<UserBean> set = new HashSet<UserBean>();
		
		set.add(user2);
		set.add(user3);
		/*set.add(user3);*/
		
		//email.setReplyTo(set);
		//email.setSubject("我是主题/标题");
		String subject = "我是同步主题/标题";
		
		EmailUtil smt = EmailUtil.getInstance(user, set, content, subject);
		try {
			smt.sendMore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void sendEmail() {
		
		UserBean userTo1 = new UserBean();
		userTo1.setAccount("account2");
		userTo1.setChinaName("账号2");
		userTo1.setEmail("825711424@qq.com");
		
		UserBean userTo2 = new UserBean();
		userTo2.setAccount("account2");
		userTo2.setChinaName("账号2");
		userTo2.setEmail("825711424@qq.com");
		
		Set<UserBean> userTos = new HashSet<UserBean>();
		userTos.add(userTo1);
		userTos.add(userTo2);
		
		String content[] ={"欢迎您：账号2注册！<a href = 'http://localhost:8080/leedane/user/completeRegister.action?registerCode=abcd1'>点击完成注册</a>"
				+ "<p>请勿回复此邮件，有事联系客服QQ642034701</p>",
				"欢迎您：账号3注册！<a href = 'http://localhost:8080/leedane/user/completeRegister.action?registerCode=abcd2'>点击完成注册</a>"
				+ "<p>请勿回复此邮件，有事联系客服QQ642034701</p>"};
		EmailUtil email = EmailUtil.getInstance(null,userTos,content,"我是测试不同步的多账号");
		try {
			email.sendOne();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
