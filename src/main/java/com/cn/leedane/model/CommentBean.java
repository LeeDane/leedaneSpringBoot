package com.cn.leedane.model;


/**
 * 评论实体类
 * @author LeeDane
 * 2016年7月12日 上午10:16:30
 * Version 1.0
 */
//@Table(name="T_COMMENT")
public class CommentBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	//评论的状态,1：正常，0:禁用，2、删除，3、审核中， 4、审核不通过
	
	/**
	 * 回复别人评论时，别人的评论的id，是直属父节点的ID,非必须
	 */
	private int pid;
	
	/**
	 * 所属的父节点的id（针对所有的子节点是必须的）
	 * 与pid的区别的pid指向的是上一级(是父节点)，
	 * 而cid一定是指向根节点的id
	 */
	//private int cid;
	
	/**
	 * 来自什么方式
	 */
	private String froms;
	/**
	 * 评论内容
	 */
	private String content;
	
	/**
	 * 评论等级，1：很差，2：一般，3：及格，4：良好，5：优秀
	 */
	private int commentLevel;
	
	/**
	 * 评论对象的类型(对象表名)必须
	 */
	private String tableName;
	
	/**
	 * 评论对象的表ID，必须
	 */
	private int tableId;
	
	//@Type(type="text")
	//@Column(name="content", nullable=false)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	//@Column(name="comment_level")
	public int getCommentLevel() {
		return commentLevel;
	}
	public void setCommentLevel(int commentLevel) {
		this.commentLevel = commentLevel;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	/*public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}*/
	//@Column(name="table_name", length= 15, nullable=false)
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	//@Column(name="table_id", nullable = false)
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	public String getFroms() {
		return froms;
	}
	public void setFroms(String froms) {
		this.froms = froms;
	}
	
	
	
}
