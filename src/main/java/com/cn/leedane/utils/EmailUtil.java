package com.cn.leedane.utils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.cn.leedane.model.UserBean;

/**
 * 邮件信息的公共类
 * @author LeeDane
 * 2016年7月12日 上午10:26:02
 * Version 1.0
 */
public class EmailUtil {
	
	private static EmailUtil mEmailUtil;

	/**
	 * 封装邮件的实体
	 */
	//private EmailBean email;
	
	/**
	 * 配置文件
	 */
	Properties props = new Properties();
	
	/**
	 * 是否立即发送
	 */
	//private boolean isSameTime = true;

	/**
	 * 发件来自哪个用户的信息
	 * 目前只支持QQ号码(64*******)
	 */
	private UserBean userFrom;
	
	/**
	 * 发送到哪个用户的信息,是个set集合
	 */
	private Set<UserBean> userTos;
	
	/**
	 * 邮件的内容
	 */
	private String content;
	
	/**
	 * 邮件的内容(多个)
	 */
	private String[] contents;
	
	/**
	 * 邮件的标题
	 */
	private String subject;
	
	private EmailUtil() {
	}
	
	/**
	 * 实例化对象
	 * @return
	 */
	public static synchronized EmailUtil getInstance(){
		if(mEmailUtil == null){
			mEmailUtil = new EmailUtil();
		}
		return mEmailUtil;
	}
	
	/**
	 * 实例化对象
	 * @return
	 */
	public static synchronized EmailUtil getInstance(UserBean userFrom, Set<UserBean> userTos, String[] contents, String subject){
		if(mEmailUtil == null){
			mEmailUtil = new EmailUtil(userFrom, userTos, contents, subject);
		}
		return mEmailUtil;
	}
	
	/**
	 * 实例化对象
	 * @return
	 */
	public static synchronized EmailUtil getInstance(UserBean userFrom, Set<UserBean> userTos, String content, String subject){
		if(mEmailUtil == null){
			mEmailUtil = new EmailUtil(userFrom, userTos, content, subject);
		}
		return mEmailUtil;
	}
	
	/**
	 * 构建邮件的实体,分别发送，所有的内容不一样，注意的是userTos的size()一样要等于content的length,
	 * 要是所有的内容都一样，推荐使用另外一个构造函数
	 * @param userFrom  邮件发送来自的用户信息
	 * @param userTos  邮件发送到对方的用户信息
	 * @param content  邮件的具体内容数组
	 * @param subject  邮件的标题
	 */
	private EmailUtil(UserBean userFrom, Set<UserBean> userTos, String[] contents, String subject) {
		if(userTos.size() != contents.length){
			throw new Error("userTos的size()和content的length值不相等");
		}
		//this.userFrom = userFrom;
		this.userTos = userTos;
		this.contents = contents;
		this.subject= subject;
		this.init();
	}
	
	/**
	 * 构建邮件的实体，同时发送，这样所有的内容将一样
	 * @param userFrom  邮件发送来自的用户信息
	 * @param userTos   邮件发送到对方的用户信息
	 * @param content   邮件的具体内容
	 * @param subject  邮件的标题
	 */
	private EmailUtil(UserBean userFrom, Set<UserBean> userTos, String content, String subject) {
		//this.userFrom = userFrom;
		this.userTos = userTos;
		this.content = content;
		this.subject = subject;
		this.init();
	}
	
	/**
	 * 初始化默认数据的方法
	 */
	private void init() {
		if(userFrom == null){
			userFrom = new UserBean();
			userFrom.setAccount(ConstantsUtil.DEFAULT_USER_FROM_ACCOUNT);
			userFrom.setChinaName(ConstantsUtil.DEFAULT_USER_FROM_CHINANAME);
			userFrom.setEmail(ConstantsUtil.DEFAULT_USER_FROM_EMAIL);
			userFrom.setQq(ConstantsUtil.DEFAULT_USER_FROM_QQ);
			//将额外的str1字段保存QQ密码
			userFrom.setStr1(ConstantsUtil.DEFAULT_USER_FROM_QQPSW);
		}
	}

	
	/**
	 * 批量实现同时发送邮件的方法(每个内容都一样)
	 * 一次性全部都一起发
	 * @throws Exception
	 */
	public void sendMore() throws Exception{	
		send(buildAllInternetAddress(), this.content);
	}
	
	/**
	 * 批量实现单个发送邮件的方法(每个内容可以不一样)
	 * 一个一个地分开发
	 * @throws Exception
	 */
	public void sendOne() throws Exception{
		//取得所有的email地址
		InternetAddress[] tos = buildAllInternetAddress();
		synchronized (EmailUtil.class.getName()) {			
			for(int i = 0; i < tos.length; i++){				
				send(buildOneInternetAddress(tos,i), contents[i]);
			}		
		}
	}
	
	/**
	 * 解析指定索引的用户Email地址
	 * @param index 索引
	 * @return
	 * @throws AddressException
	 */
	private InternetAddress[] buildOneInternetAddress(InternetAddress[] tos, int index) {
		//不判断数组越界让程序自行处理
		
		//定义一个数组，长度为1
		InternetAddress[] to = new InternetAddress[1];		
		to[0] = tos[index];
		return to;
	}
	
	/**
	 * 解析所有的用户Email地址
	 * @return
	 * @throws AddressException
	 */
	private InternetAddress[] buildAllInternetAddress() throws AddressException {
		Set<String> addresses = new HashSet<String>();
		for(UserBean us : userTos){
			if(!StringUtil.isNull(us.getEmail())){
				addresses.add(us.getEmail());
			}else{
				throw new Error(us != null ? us.getChinaName() + "的Email为空": "有用户的Email为空");
			}
		}
		InternetAddress to[] = new InternetAddress[addresses.size()];
		int i = 0;
		for(Iterator<String> it = addresses.iterator();  it.hasNext();){
			to[i] = new InternetAddress(it.next().toString());
			i++;
		}
		return to;
	}
	
	/**
	 * 执行真正的发送方法
	 * @param to  到达放的地址数组
	 * @param c 发送的内容
	 * @throws Exception
	 */
	private void send(InternetAddress to[],String c) throws Exception{
		
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.auth", "true");
		Session session = Session.getInstance(props);
		session.setDebug(true);
		
		String fromName = userFrom.getChinaName();
		if(fromName == null || fromName ==""){
			fromName = userFrom.getAccount();
		}
		
		String fromUrl =userFrom.getEmail();
		MimeMessage msg = new MimeMessage(session);//
		msg.setFrom(new InternetAddress("\"" + MimeUtility.encodeText(fromName) + "\" <"+fromUrl+">"));
		msg.setSubject(this.subject);	 //邮件主题
				
		msg.setReplyTo(new Address[]{new InternetAddress(fromUrl)});
		//msg.setRecipients(RecipientType.TO,InternetAddress.parse(MimeUtility.encodeText("小轩") + " <642034701@qq.com>," + MimeUtility.encodeText("xiaoxuan") + " <825711424@qq.com>"));
		//msg.setRecipients(RecipientType.TO,InternetAddress.parse(MimeUtility.encodeText("小轩") + " <825711424@qq.com>" ));
		MimeMultipart msgMultipart = new MimeMultipart("mixed");
		msg.setContent(msgMultipart);		
		MimeBodyPart mimeBodyPart = new MimeBodyPart();	
		msgMultipart.addBodyPart(mimeBodyPart);
		
		MimeMultipart bodyMultipart = new MimeMultipart("related");
		mimeBodyPart.setContent(bodyMultipart);
		MimeBodyPart htmlPart = new MimeBodyPart();				
		bodyMultipart.addBodyPart(htmlPart);	
		htmlPart.setContent(c, "text/html;charset=gbk");//显示的内容	
		
		Transport transport = session.getTransport();
		transport.connect("smtp.qq.com",587, userFrom.getQq(), userFrom.getStr1());
		for(int i = 0 ;i < 1 ; i++){
			transport.sendMessage(msg, to);
		}	
		transport.close();
	}
}
