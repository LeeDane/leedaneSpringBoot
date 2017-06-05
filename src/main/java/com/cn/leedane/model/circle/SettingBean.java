package com.cn.leedane.model.circle;

import com.cn.leedane.model.RecordTimeBean;

/**
 * 圈子的设置实体bean
 * @author LeeDane
 * 2017年5月30日 下午5:10:55
 * version 1.0
 */
public class SettingBean extends RecordTimeBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3562775071804007807L;

	/**
	 * 是否可以添加成员
	 */
	private boolean addmember;
	
	/**
	 * 欢迎成员的信息(支持el表达式)，为空将不发送
	 */
	private String welcomeMember;
	
	/**
	 * 圈子的图标(可以访问的路径，如七牛等)
	 */
	private String path;
	
	/**
	 * 此时问题的标题不能为空
	 */
	private String questionTitle;
	
	/**
	 * 此时问题的答案不能为空
	 */
	private String questionAnswer;

	public boolean isAddmember() {
		return addmember;
	}

	public void setAddmember(boolean addmember) {
		this.addmember = addmember;
	}

	public String getWelcomeMember() {
		return welcomeMember;
	}

	public void setWelcomeMember(String welcomeMember) {
		this.welcomeMember = welcomeMember;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getQuestionTitle() {
		return questionTitle;
	}

	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}

	public String getQuestionAnswer() {
		return questionAnswer;
	}

	public void setQuestionAnswer(String questionAnswer) {
		this.questionAnswer = questionAnswer;
	}
}
