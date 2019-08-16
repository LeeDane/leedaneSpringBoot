package com.cn.leedane.thread.single;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cn.leedane.handler.AllReadHandler;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.*;
import org.apache.log4j.Logger;

import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.ISend;
import com.cn.leedane.rabbitmq.send.LogSend;

/**
 * 保存操作日志异步任务
 * @author LeeDane
 * 2017年6月22日 上午10:18:03
 * version 1.0
 */
public class OperateLogSaveThread implements Runnable{
	private Logger logger = Logger.getLogger(getClass());
	private UserBean mUser;
	private HttpRequestInfoBean mRequest;
	private Date mCreateTime;
	private String mSubject;
	private String mMethod;
	private int mStatus;
	private int mOperateType;

//	private String mIp;
//	private String mBrowserInfo;
	public OperateLogSaveThread(UserBean user, HttpRequestInfoBean request,
			Date createTime, String subject, String method, int status, int operateType) {
		this.mUser = user;
		this.mRequest = request;
		this.mCreateTime = createTime;
		this.mSubject = subject;
		this.mMethod = method;
		this.mStatus = status;
		this.mOperateType = operateType;
	}

	/*public OperateLogSaveThread(UserBean user, String ip, String browserInfo,
								Date createTime, String subject, String method, int status, int operateType) {
		this.mUser = user;
		this.mIp = ip;
		this.mBrowserInfo = browserInfo;
		this.mCreateTime = createTime;
		this.mSubject = subject;
		this.mMethod = method;
		this.mStatus = status;
		this.mOperateType = operateType;
	}*/

	@Override
	public void run() {
		OperateLogBean operateLogBean = new OperateLogBean();
		logger.info("OperateLogSaveThread--run()>saveOperateLog():subject="+mSubject+",method="+mMethod+",status="+mStatus+",operateType="+mOperateType);
		if(mRequest != null){
			operateLogBean.setIp(StringUtil.isNull(mRequest.getIp()) ? "未知来源IP" : mRequest.getIp());
			operateLogBean.setBrowser(StringUtil.isNull(mRequest.getBrowerInfo()) ? "未知来源浏览器信息" : mRequest.getBrowerInfo());
			operateLogBean.setLocation(StringUtil.isNull(mRequest.getLocation()) ? "未知区域" : mRequest.getLocation());
		}else{
			operateLogBean.setIp("未知来源IP");
			operateLogBean.setBrowser("未知来源浏览器信息");
			operateLogBean.setLocation("未知区域");
		}
		if(mUser != null){
			operateLogBean.setCreateUserId(mUser.getId());
			operateLogBean.setModifyUserId(mUser.getId());
			operateLogBean.setCreateTime(mCreateTime == null ? DateUtil.stringToDate(
					DateUtil.getSystemCurrentTime(DateUtil.DEFAULT_DATE_FORMAT),
					DateUtil.DEFAULT_DATE_FORMAT) : mCreateTime);
		}else{
			//未登录的用户，用系统管理员的账号创建私有状态方便查看
			operateLogBean.setCreateUserId(OptionUtil.adminUser.getId());
			operateLogBean.setModifyUserId(OptionUtil.adminUser.getId());
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

		if(mOperateType == EnumUtil.LogOperateType.网页端.value){
			AllReadHandler allReadHandler = (AllReadHandler)SpringUtil.getBean("allReadHandler");
			allReadHandler.addRead();
		}

	}

}
