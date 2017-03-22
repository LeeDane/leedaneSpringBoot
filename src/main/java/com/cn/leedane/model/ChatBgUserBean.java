package com.cn.leedane.model;


/**
 * 聊天背景与用户关系的实体bean
 * @author LeeDane
 * 2016年7月12日 上午10:41:22
 * Version 1.0
 */
//@Table(name="T_CHAT_BG_USER")
public class ChatBgUserBean extends RecordTimeBean{

	private static final long serialVersionUID = 1L;
	
	private int chatBgTableId; //背景图像表的Id
	
	//@Column(name="chat_bg_table_id")
	public int getChatBgTableId() {
		return chatBgTableId;
	}

	public void setChatBgTableId(int chatBgTableId) {
		this.chatBgTableId = chatBgTableId;
	}
}
