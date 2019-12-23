package com.cn.leedane.test;

import com.cn.leedane.model.UserBean;
import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import com.cn.leedane.utils.EmailUtil;
import com.sun.mail.util.MailSSLSocketFactory;
import org.junit.Test;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.fail;

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
		
		EmailUtil smt = EmailUtil.getInstance();
		smt.initData(user, set, content, subject);
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
		EmailUtil email = EmailUtil.getInstance();
		email.initData(null,userTos,content,"我是测试不同步的多账号");
		try {
			email.sendOne();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		EmailUtil emailUtil = EmailUtil.getInstance();
		Set<UserBean> set = new HashSet<>();
		UserBean userBean = new UserBean();
		userBean.setId(1);
		userBean.setChinaName("小轩");
		userBean.setEmail("825711424@qq.com");
		set.add(userBean);
		emailUtil.initData(userBean, set, "你好啊", LeedanePropertiesConfig.newInstance().getString("constant.websit.name") +"注册验证");
		try {
			emailUtil.sendMore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main1(String[] args) throws Exception {
		//创建一个配置文件并保存
		Properties properties = new Properties();

		properties.setProperty("mail.host","smtp.qq.com");

		properties.setProperty("mail.transport.protocol","smtp");

		properties.setProperty("mail.smtp.auth","true");


		//QQ存在一个特性设置SSL加密
		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.ssl.socketFactory", sf);

		//创建一个session对象
		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("642034701@qq.com","tzlbvpjkgvzlbbch");
			}
		});

		//开启debug模式
		session.setDebug(true);

		//获取连接对象
		Transport transport = session.getTransport();

		//连接服务器
		transport.connect("smtp.qq.com","642034701@qq.com","tzlbvpjkgvzlbbch");

		//创建邮件对象
		MimeMessage mimeMessage = new MimeMessage(session);

		//邮件发送人
		mimeMessage.setFrom(new InternetAddress("642034701@qq.com"));

		//邮件接收人
		mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress("825711424@qq.com"));

		//邮件标题
		mimeMessage.setSubject("Hello Mail");

		//邮件内容
		mimeMessage.setContent("我的想法是把代码放进一个循环里","text/html;charset=UTF-8");

		//发送邮件
		transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());

		//关闭连接
		transport.close();
	}
}
