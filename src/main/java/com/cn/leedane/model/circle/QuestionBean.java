package com.cn.leedane.model.circle;

import java.util.Date;
import java.util.List;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.circle.CircleUtil.SupportQuestionType;

/**
 * 圈子问题bean
 * @author LeeDane
 * 2017年6月1日 下午2:39:57
 * version 1.0
 */
public class QuestionBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7049789198794319706L;

	/**
	 * 试题的id(外键ExaminationBean的id)
	 */
	private int examinationId;
	
	/**
	 * 问题的类型(默认是单选题)
	 */
	private int type = SupportQuestionType.单选题.value;
	
	/**
	 * 是否是必须填写(默认是true)
	 */
	private boolean requested = true;
	
	/**
	 * 回答正确获取的积分(注意：当单题目的积分最终超过ExaminationBean总积分的时候，以此ExaminationBean为准)
	 */
	private int score;
	
	/**
	 * 回答问题的用户列表(为空表示全部圈内的用户)
	 */
	private List<UserBean> users;
	
	/**
	 * 开始时间(为空表示没有限制)
	 */
	private Date startTime;
	
	/**
	 * 结束时间(为空表示没有限制)
	 */
	private Date endTime;
	
	
}
