package com.cn.leedane.model.circle;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 圈子的设置实体bean
 * @author LeeDane
 * 2017年5月30日 下午5:10:55
 * version 1.0
 */
@Table(value="t_circle_setting")
public class CircleSettingBean extends RecordTimeBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3562775071804007807L;

	/**
	 * 圈子的ID
	 */
	@Column("circle_id")
	@Field
	private long circleId;
	
	/**
	 * 是否可以添加成员
	 */
	@Column("add_member")
	@Field
	private boolean addMember;
	
	/**
	 * 欢迎成员的信息(支持el表达式)，为空将不发送
	 */
	@Column("welcome_member")
	@Field
	private String welcomeMember;
	
	/**
	 * 此时问题的标题不能为空
	 */
	@Column("question_title")
	@Field
	private String questionTitle;
	
	/**
	 * 此时问题的答案不能为空
	 */
	@Column("question_answer")
	@Field
	private String questionAnswer;

	/**
	 * 公告
	 */
	@Column("announce")
	@Field
	private String announce;
	
	/**
	 * 是否需要审核圈子的帖子
	 */
	@Column("check_post")
	@Field
	private boolean checkPost;
	
	/**
	 * 背景颜色
	 */
	@Column("background_color")
	@Field
	private String backgroundColor;
	
	/**
	 * 限制成员的总数
	 */
	@Column("limit_number")
	@Field
	private int limitNumber;
	
	public String getWelcomeMember() {
		return welcomeMember;
	}

	public void setWelcomeMember(String welcomeMember) {
		this.welcomeMember = welcomeMember;
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

	public long getCircleId() {
		return circleId;
	}

	public void setCircleId(long circleId) {
		this.circleId = circleId;
	}

	public boolean isAddMember() {
		return addMember;
	}

	public void setAddMember(boolean addMember) {
		this.addMember = addMember;
	}

	public String getAnnounce() {
		return announce;
	}

	public void setAnnounce(String announce) {
		this.announce = announce;
	}

	public boolean isCheckPost() {
		return checkPost;
	}

	public void setCheckPost(boolean checkPost) {
		this.checkPost = checkPost;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getLimitNumber() {
		return limitNumber;
	}

	public void setLimitNumber(int limitNumber) {
		this.limitNumber = limitNumber;
	}
	
	
}
