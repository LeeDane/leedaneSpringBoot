package com.cn.leedane.rabbitmq.recieve;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;

import com.cn.leedane.mapper.clock.ClockScoreMapper;
import com.cn.leedane.message.JpushCustomMessage;
import com.cn.leedane.message.notification.CustomMessage;
import com.cn.leedane.model.clock.ClockScoreQueueBean;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.EnumUtil;

/**
 * 任务积分处理消费者
 * @author LeeDane
 * 2018年11月5日 下午4:52:19
 * version 1.0
 */
@Component
public class ClockScoreRecieve implements IRecieve{

//	@Autowired
//	private ClockScoreMapper scoreMapper;

	public final static String QUEUE_NAME = "clock_score_rabbitmq";
	@Override
	public String getQueueName() {
		return QUEUE_NAME;
	}

	@Override
	public Class<?> getQueueClass() {
		return ClockScoreQueueBean.class;
	}

	@Override
	public boolean excute(Object obj) {
		boolean success = false;
		try{
			ClockScoreQueueBean clockScoreQueueBean = (ClockScoreQueueBean)obj;
			int operateType = clockScoreQueueBean.getOperateType();
			ClockScoreMapper scoreMapper = (ClockScoreMapper) SpringUtil.getBean("clockScoreMapper");
			if(operateType == EnumUtil.ClockScoreOperateType.新增.value){
				//先判断连续打卡的次数
				
				//获得总的积分，
				success =  scoreMapper.save(clockScoreQueueBean.getClockScoreBean()) > 0;
				
				//更新积分
				
				//通知用户
				if(success){
					Map<String, Object> mp = new HashMap<String, Object>();
					mp.put("user_id", clockScoreQueueBean.getClockScoreBean().getCreateUserId());
					mp.put("user_name", null);
					mp.put("clock_title", "任务打卡成功，获得"+ clockScoreQueueBean.getClockScoreBean().getScore() +"积分，请查收！");
					mp.put("clock_id", clockScoreQueueBean.getClockScoreBean().getId());
					CustomMessage customMessage = new JpushCustomMessage();
					customMessage.sendToAlias("leedane_user_"+ clockScoreQueueBean.getClockScoreBean().getCreateUserId(),  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.任务打卡送积分.value);
				}
				
			}
			System.out.println("operateType="+ operateType);
			/*VisitorMapper visitorMapper = (VisitorMapper) SpringUtil.getBean("visitorMapper");
			visitorMapper.deleteSql(VisitorBean.class, " where table_name = ? and table_id= ?", visitorBean.getTableName(), visitorBean.getTableId());
			success = true;*/
		}catch(Exception e){
			e.printStackTrace();
		}
		return success;
	}
	
	@Override
	public boolean errorDestroy() {
		return false;
	}
}
