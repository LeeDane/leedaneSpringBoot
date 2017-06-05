package com.cn.leedane.model.circle;

import java.util.Date;

import com.cn.leedane.model.RecordTimeBean;

/**
 * 公告实体bean
 * @author LeeDane
 * 2017年5月30日 下午7:07:11
 * version 1.0
 */
public class AnnouncementBean extends RecordTimeBean{

	//只有管理员和圈主才能使用
	/**
	 * 
	 */
	private static final long serialVersionUID = 3896570329003234212L;

	/**
	 * 公告的内容
	 */
	private String content;
	
	/**
	 * 公告摘要
	 */
	private String digest;
	
	/**
	 * 公告主图片
	 */
	private String path;
	
	/**
	 * 公告开始展示的时间
	 */
	private Date startTime;
	
	/**
	 * 公告结束展示的时间
	 */
	private Date endTime;
}
