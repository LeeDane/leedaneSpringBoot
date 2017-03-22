package com.cn.leedane.rabbitmq.recieve;

import com.cn.leedane.model.EmailBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EmailUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 发送邮件消费者
 * @author LeeDane
 * 2016年8月5日 下午3:27:02
 * Version 1.0
 */
public class EmailRecieve implements IRecieve{


	public final static String QUEUE_NAME = "send_email_rabbitmq";
	@Override
	public String getQueueName() {
		return QUEUE_NAME;
	}

	@Override
	public Class<?> getQueueClass() {
		return EmailBean.class;
	}

	@Override
	public boolean excute(Object obj) {
		boolean success = false;
		EmailBean emailBean = (EmailBean)obj;
		try{
			if(emailBean != null){
				EmailUtil emailUtil = EmailUtil.getInstance(null, emailBean.getReplyTo(), emailBean.getContent(), emailBean.getSubject());
				emailUtil.sendMore();
			}
			success = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		OperateLogService<OperateLogBean> operateLogService = (OperateLogService<OperateLogBean>) SpringUtil.getBean("operateLogService");
		
		for(UserBean userBean: emailBean.getReplyTo()){
			//保存操作日志
			operateLogService.saveOperateLog(emailBean.getFrom(), null, null, StringUtil.getStringBufferStr(emailBean.getFrom().getAccount(),"给用户", userBean.getAccount(), "发送邮件,内容是：", emailBean.getSubject(), "，结果：",StringUtil.getSuccessOrNoStr(success)).toString(), "SendEmailRecieve excute()", ConstantsUtil.STATUS_NORMAL, 0);
		}
		return success;
	}
	
	@Override
	public boolean errorDestroy() {
		return true;
	}
}
