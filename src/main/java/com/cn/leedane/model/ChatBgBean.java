package com.cn.leedane.model;


/**
 * 聊天背景的实体bean
 * @author LeeDane
 * 2016年7月12日 上午10:40:36
 * Version 1.0
 */
//@Table(name="T_CHAT_BG")
public class ChatBgBean extends RecordTimeBean{

	private static final long serialVersionUID = 1L;
	
	private String path; //背景图像的路径
	
	private String chatBgDesc ;  //聊天背景的描述
	
	private int type; //聊天背景的类型，0：免费,1:收费, 2:全部
	
	private int score; //当是收费类型的时候需要扣除的积分(收费的时候这个字段必填)

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	//@Column(name="chat_bg_desc")
	public String getChatBgDesc() {
		return chatBgDesc;
	}

	public void setChatBgDesc(String chatBgDesc) {
		this.chatBgDesc = chatBgDesc;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
