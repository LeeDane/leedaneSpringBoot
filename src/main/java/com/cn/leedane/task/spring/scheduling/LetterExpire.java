package com.cn.leedane.task.spring.scheduling;

import com.cn.leedane.exception.ErrorException;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.letter.FutureLetterMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.letter.FutureLetterBean;
import com.cn.leedane.notice.model.Notification;
import com.cn.leedane.notice.model.SMS;
import com.cn.leedane.notice.send.INoticeFactory;
import com.cn.leedane.notice.send.NoticeFactory;
import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.service.impl.manage.FutureLetterServiceImpl;
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
 * 处理信件过期的任务
 * @author LeeDane
 * 2020年5月24日 上午10:53:42
 * version 1.0
 */
@Component("letterExpire")
public class LetterExpire extends AbstractScheduling{
	private Logger logger = Logger.getLogger(getClass());
	@Autowired
	private FutureLetterMapper futureLetterMapper;

	@Autowired
	private UserHandler userHandler;
	
	@Override
	public void execute() throws SchedulerException {
		try {
			JSONObject params = getParams();
			long id = JsonUtil.getLongValue(params, "id");
			//获取信封对象
			FutureLetterBean futureLetter = futureLetterMapper.findById(FutureLetterBean.class, id);
			//判断是否过期拉
			if(DateUtil.leftSeconds(new Date(), futureLetter.getEnd()) < 0){
				logger.error("信件任务还没到期！！！ID = "+ id);
				return;
			}
			//解密内容
			String content = CommonUtil.sm4DecryptByKey(futureLetter.getContent(), futureLetter.getPwd());
			if(StringUtil.isNull(content)){
				logger.error("到期信件任务内容解密后为空！！！ID = "+ id);
				return;
			}
			String way = futureLetter.getWay();

			//共享的ID，设置共享查阅ID，以方便免登陆进行查阅
//			String shareId = "125453222kkfkf";

			String link = LeedanePropertiesConfig.newInstance().getSystemUrl() + "/my/manage/letter/share/"+ id;
			//消息发送的内容
			String msgContent = DateUtil.getChineseDateFormat() +"好，您有一封未来的信件，已经为您及时推送,请查收。<a href='"+ link +"'>链接</a>";

			//处理状态
			if(futureLetter.isPublica())
				futureLetter.setStatus(ConstantsUtil.STATUS_SHARE);
			else
				futureLetter.setStatus(ConstantsUtil.STATUS_NORMAL);

			//处理内容
			futureLetter.setContent(content);
			//处理修改时间
			futureLetter.setModifyTime(new Date());
			boolean success = futureLetterMapper.update(futureLetter) > 0;

			//操作失败将不继续执行
			if(!success){
				logger.error("到期信件任务更新信息失败！！！ID = "+ id);
				return;
			}
			;
			if(way.indexOf("站内信") > -1){
				//发送站内通知
				success = NoticeUtil.notification(OptionUtil.adminUser.getId(), futureLetter.getCreateUserId(), msgContent);
			}

			if(way.indexOf("邮件") > -1){
				//发送邮件
				success = NoticeUtil.email(userHandler.getEmail(futureLetter.getCreateUserId()),
						"一封未来的信件",
						msgContent,
						true,
						futureLetter.getCreateUserId(),
						userHandler.getUserName(futureLetter.getCreateUserId())+ "的未来一封邮件发送异常。");
			}

			if(way.indexOf("短信") > -1){
				//判断当天短信是否用完
				String smsKey = FutureLetterServiceImpl.LETTER_EXPIRE_PREFIX + futureLetter.getCreateUserId();
				int limit = 3;
				//超过限制的短信数量
				if(StringUtil.changeObjectToInt(RedisUtil.getInstance().getString(smsKey)) >= limit){
					//发送站内通知
					Notification notification = new Notification();
					notification.setContent("您当天的短信已经超过限制的"+ limit +"条，将不再继续发送短信。明天自行恢复！发送未来的一封信提醒");
					notification.setFromUserId(OptionUtil.adminUser.getId());
					notification.setToUserId(futureLetter.getCreateUserId());
					notification.setType(EnumUtil.NotificationType.通知);
					INoticeFactory factory = new NoticeFactory();
					factory.create(EnumUtil.NoticeType.站内信).send(notification);
					throw new ErrorException("短信超过当天发送的限制");
				}

				//发送短信
				SMS sms = new SMS();
				sms.setType("futureLetter"); //设置类型为未来的一封信的短信模板
				sms.setExpire(5); //5秒过期
				Map<String, Object> myParams = new HashMap<>();
				myParams.put("content", userHandler.getChinaName(futureLetter.getCreateUserId()));
				myParams.put("link", link);
				sms.setParams(myParams);
				UserBean toUser = new UserBean();
				toUser.setMobilePhone(userHandler.getUserMobilePhone(futureLetter.getCreateUserId()));
				toUser.setChinaName(userHandler.getChinaName(futureLetter.getCreateUserId()));
				sms.setToUser(toUser);
				INoticeFactory factory = new NoticeFactory();
				success = factory.create(EnumUtil.NoticeType.短信).send(sms);
				if(success){
					RedisUtil.getInstance().incr(smsKey);//自增1
					RedisUtil.getInstance().expire(smsKey, DateUtil.leftSeconds(new Date(), DateUtil.getTodayEnd()));//设置当天的过期时间
				}
			}
			logger.info("未来的信件到期任务执行"+ StringUtil.getSuccessOrNoStr(success));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("未来的信件到期任务出现异常：execute()", e);
		}
	}
	
}
