package com.cn.leedane.thread.single;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.ISend;
import com.cn.leedane.rabbitmq.send.LogSend;
import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.DateUtil;

/**
 * 保存操作日志异步任务
 * @author LeeDane
 * 2017年6月22日 上午10:18:03
 * version 1.0
 */
public class OperateLogSaveThread implements Runnable{
	private Logger logger = Logger.getLogger(getClass());
	private UserBean mUser;
	private HttpServletRequest mRequest;
	private Date mCreateTime;
	private String mSubject;
	private String mMethod;
	private int mStatus;
	private int mOperateType;
	public OperateLogSaveThread(UserBean user, HttpServletRequest request,
			Date createTime, String subject, String method, int status, int operateType) {
		this.mUser = user;
		this.mRequest = request;
		this.mCreateTime = createTime;
		this.mSubject = subject;
		this.mMethod = method;
		this.mStatus = status;
		this.mOperateType = operateType;
	}

	@Override
	public void run() {
		OperateLogBean operateLogBean = new OperateLogBean();
		logger.info("OperateLogSaveThread--run()>saveOperateLog():subject="+mSubject+",method="+mMethod+",status="+mStatus+",operateType="+mOperateType);
		if(mRequest != null){
			String browserInfo = CommonUtil.getBroswerInfo(mRequest);// 获取浏览器的类型
			String ip = CommonUtil.remoteAddr(mRequest); //获得IP地址
			operateLogBean.setIp(ip);
			operateLogBean.setBrowser(browserInfo);
		}
		
		if(mUser != null){
			operateLogBean.setCreateUserId(mUser.getId());
			operateLogBean.setModifyUserId(mUser.getId());
			operateLogBean.setCreateTime(mCreateTime == null ? DateUtil.stringToDate(
					DateUtil.getSystemCurrentTime(DateUtil.DEFAULT_DATE_FORMAT),
					DateUtil.DEFAULT_DATE_FORMAT) : mCreateTime);
		}
				
		operateLogBean.setCreateTime(mCreateTime == null ? DateUtil.stringToDate(
				DateUtil.getSystemCurrentTime(DateUtil.DEFAULT_DATE_FORMAT),
				DateUtil.DEFAULT_DATE_FORMAT) : mCreateTime);
		operateLogBean.setSubject(mSubject);
		operateLogBean.setStatus(mStatus);
		operateLogBean.setMethod(mMethod);
		operateLogBean.setOperateType(mOperateType);
		ISend send = new LogSend(operateLogBean);
		SendMessage sendMessage = new SendMessage(send);
		//logger.info("发送日志");
		sendMessage.sendMsg();//发送日志到消息队列
	}

}
