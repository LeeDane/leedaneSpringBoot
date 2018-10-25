package com.cn.leedane.utils.baby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cn.leedane.display.keyValueDisplay;
import com.cn.leedane.model.baby.BabyBean;
import com.cn.leedane.model.baby.BabyLifeBean;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.RelativeDateFormat;
import com.cn.leedane.utils.StringUtil;

/**
 * 宝宝相关的工具类
 * @author LeeDane
 * 2018年6月5日 下午6:10:54
 * version 1.0
 */
public class BabyUtil {

	/**
	 * 宝宝基本信息的key-value格式展示
	 * @return
	 */
	public static List<keyValueDisplay> getInfoDisplay(BabyBean baby){
		List<keyValueDisplay> displays = new ArrayList<keyValueDisplay>();
		if(baby != null){
			displays.add(new keyValueDisplay("姓名", baby.getName()));
			displays.add(new keyValueDisplay("昵称", baby.getNickname()));
			if(baby.isBorn()){
				displays.add(new keyValueDisplay("公历生日", baby.getGregorianBirthDay() != null ? DateUtil.DateToString(baby.getGregorianBirthDay(), "yyyy年MM月dd日"): null));
				displays.add(new keyValueDisplay("农历生日", baby.getLunarBirthDay() != null ? DateUtil.DateToString(baby.getLunarBirthDay(), "yyyy年MM月dd日"): null));
				displays.add(new keyValueDisplay("性别", baby.getSex()));
				displays.add(new keyValueDisplay("健康状态", baby.getHealthyState()+""));
			}
			displays.add(new keyValueDisplay("怀孕日期", baby.getPregnancyDate() != null ? DateUtil.DateToString(baby.getPregnancyDate(), "yyyy年MM月dd日"): null));
			displays.add(new keyValueDisplay("预产期", baby.getPreProduction() != null ? DateUtil.DateToString(baby.getPreProduction(), "yyyy年MM月dd日"): null));
			displays.add(new keyValueDisplay("个性签名", baby.getPersonalizedSignature()));
			displays.add(new keyValueDisplay("排行", "第"+baby.getSorting()+"胎"));
			displays.add(new keyValueDisplay("简介", baby.getIntroduction()));
		}
		return displays;
		
	}
	
	
	/**
	 * 宝宝生活方式的key-value格式展示
	 * @param life
	 * @param baby
	 * @return
	 */
	public static Map<String, Object> transform(BabyLifeBean life, BabyBean baby){
		Map<String, Object> bean = new HashMap<String, Object>();
		List<keyValueDisplay> displays = new ArrayList<keyValueDisplay>();
		if(life != null){
			switch (life.getLifeType()) {
			case 1: //吃喝
				displays.add(new keyValueDisplay("喂养时间", DateUtil.DateToString(life.getOccurTime(), "yyyy-MM-dd HH:mm")));
				displays.add(new keyValueDisplay("喂养方式", EnumUtil.getBabyEatType(life.getEatType())));
				if(life.getEatType() == 1){
					displays.add(new keyValueDisplay("左侧", life.getLeftCapacity() +"ml"));
					displays.add(new keyValueDisplay("右侧", life.getRightCapacity() +"ml"));
					displays.add(new keyValueDisplay("总量", (life.getLeftCapacity() + life.getRightCapacity()) +"ml"));
				}else{
					displays.add(new keyValueDisplay("总量", life.getCapacity() +"ml"));
					displays.add(new keyValueDisplay("温度", life.getTemperature() +"°C"));
				}
				displays.add(new keyValueDisplay("宝宝反应情况", EnumUtil.getBabyReaction(life.getReaction())));
				displays.add(new keyValueDisplay("其他描述", StringUtil.changeNotNull(life.getBabyDesc())));
				displays.add(new keyValueDisplay("地址", StringUtil.changeNotNull(life.getOccurPlace())));
				break;
			case 2: //睡觉
				displays.add(new keyValueDisplay("睡眠时间", DateUtil.DateToString(life.getOccurTime(), "yyyy-MM-dd HH:mm")));
				displays.add(new keyValueDisplay("睡醒时间", DateUtil.DateToString(life.getWakeUpTime(), "yyyy-MM-dd HH:mm")));
				
				String sleepLen = DateUtil.getDatePoor(life.getWakeUpTime(), life.getOccurTime());
				if(StringUtil.isNotNull(sleepLen))
					displays.add(new keyValueDisplay("睡眠时长", sleepLen));
				displays.add(new keyValueDisplay("宝宝反应情况", EnumUtil.getBabyReaction(life.getReaction())));
				displays.add(new keyValueDisplay("其他描述", StringUtil.changeNotNull(life.getBabyDesc())));
				displays.add(new keyValueDisplay("地址", StringUtil.changeNotNull(life.getOccurPlace())));
				break;
			case 3: //洗刷
				displays.add(new keyValueDisplay("洗刷时间", DateUtil.DateToString(life.getOccurTime(), "yyyy-MM-dd HH:mm")));
				displays.add(new keyValueDisplay("结束时间", DateUtil.DateToString(life.getWashEndTime(), "yyyy-MM-dd HH:mm")));
				displays.add(new keyValueDisplay("宝宝反应情况", EnumUtil.getBabyReaction(life.getReaction())));
				displays.add(new keyValueDisplay("其他描述", StringUtil.changeNotNull(life.getBabyDesc())));
				displays.add(new keyValueDisplay("地址", StringUtil.changeNotNull(life.getOccurPlace())));
				break;
			case 4: //臭臭
				displays.add(new keyValueDisplay("臭臭时间", DateUtil.DateToString(life.getOccurTime(), "yyyy-MM-dd HH:mm")));
				displays.add(new keyValueDisplay("宝宝反应情况", EnumUtil.getBabyReaction(life.getReaction())));
				displays.add(new keyValueDisplay("其他描述", StringUtil.changeNotNull(life.getBabyDesc())));
				displays.add(new keyValueDisplay("地址", StringUtil.changeNotNull(life.getOccurPlace())));
				break;
			default:
				break;
			}
		}
		bean.put("displays", displays);
		bean.put("time", RelativeDateFormat.format(life.getOccurTime()));
		bean.put("id", life.getId());
		bean.put("type", life.getLifeType());
		return bean;
	}
}
