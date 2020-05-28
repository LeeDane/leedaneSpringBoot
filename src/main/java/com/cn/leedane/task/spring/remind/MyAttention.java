package com.cn.leedane.task.spring.remind;

import com.cn.leedane.exception.ErrorException;
import com.cn.leedane.handler.OptionHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.ManageRemindMapper;
import com.cn.leedane.model.ManageRemindBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.notice.model.Notification;
import com.cn.leedane.notice.model.SMS;
import com.cn.leedane.notice.send.INoticeFactory;
import com.cn.leedane.notice.send.NoticeFactory;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.service.impl.manage.MyToolServiceImpl;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的关注的任务
 * @author LeeDane
 * 2020年5月17日 下午23:59:57
 * version 1.0
 */
@Component("myAttention")
@RemindAnnotation(name = "我的关注", value = "myAttention")
public class MyAttention extends BaseRemind {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ManageRemindMapper manageRemindMapper;

	@Autowired
	private UserHandler userHandler;

	@Autowired
	private OptionHandler optionHandler;

	/**
	 * 执行爬取方法
	 */
	@Override
	public void execute() throws SchedulerException {
		try {
			JSONObject params = getParams();
			long id = JsonUtil.getLongValue(params, "id");
			ManageRemindBean manageRemindBean = manageRemindMapper.findById(ManageRemindBean.class, id);
			logger.info("ManageRemindBean 的 id是："+ id);
			String content = manageRemindBean.getContent();
			String way = manageRemindBean.getWay();
			boolean success = false;
			if(way.indexOf("站内信") > -1){
				//发送站内通知
				success = NoticeUtil.notification(OptionUtil.adminUser.getId(), manageRemindBean.getCreateUserId(), DateUtil.getChineseDateFormat() +"好，您关注的《"+ content +"》事件，已经为您及时推送,请查收。");
			}

			if(way.indexOf("邮件") > -1){
				//发送邮件
				success = NoticeUtil.email(userHandler.getEmail(manageRemindBean.getCreateUserId()),
						"您的关注",
						DateUtil.getChineseDateFormat() +"好，您关注的《"+ content +"》事件，已经为您及时推送,请查收。",
						true,
						manageRemindBean.getCreateUserId(),
						"您关注的《"+ content +"》事件邮件发送异常");
			}

			if(way.indexOf("短信") > -1){
				//判断当天短信是否用完
				String smsKey = MyToolServiceImpl.SMS_REMIND_PREFIX + manageRemindBean.getCreateUserId();
				int limit = StringUtil.changeObjectToInt(optionHandler.getData("remindSmsLimit", true));
				//超过限制的短信数量
				if(StringUtil.changeObjectToInt(RedisUtil.getInstance().getString(smsKey)) >= limit){
					//发送站内通知
					Notification notification = new Notification();
					notification.setContent("您当天的短信已经超过限制的"+ limit +"条，将不再继续发送短信。明天自行恢复！当前消息名称："+ manageRemindBean.getName());
					notification.setFromUserId(OptionUtil.adminUser.getId());
					notification.setToUserId(manageRemindBean.getCreateUserId());
					notification.setType(EnumUtil.NotificationType.通知);
					INoticeFactory factory = new NoticeFactory();
					factory.create(EnumUtil.NoticeType.站内信).send(notification);
					throw new ErrorException("短信超过当天发送的限制");
				}

				//发送短信
				SMS sms = new SMS();
				sms.setType(manageRemindBean.getType());
				sms.setExpire(5); //5秒过期
				Map<String, Object> myParams = new HashMap<>();
				myParams.put("content", content);
				sms.setParams(myParams);
				UserBean toUser = new UserBean();
				toUser.setMobilePhone(userHandler.getUserMobilePhone(manageRemindBean.getCreateUserId()));
				toUser.setChinaName(userHandler.getChinaName(manageRemindBean.getCreateUserId()));
				sms.setToUser(toUser);
				INoticeFactory factory = new NoticeFactory();
				success = factory.create(EnumUtil.NoticeType.短信).send(sms);
				if(success){
					RedisUtil.getInstance().incr(smsKey);//自增1
					RedisUtil.getInstance().expire(smsKey, DateUtil.leftSeconds(new Date(), DateUtil.getTodayEnd()));//设置当天的过期时间
				}
			}

			logger.info("我的关注的任务执行"+ StringUtil.getSuccessOrNoStr(success));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("我的关注的任务出现异常：execute()", e);
		}
	}
}
