package com.cn.leedane.notice.model;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.EnumUtil;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * 通知的消息
 * @author LeeDane
 * 2016年7月12日 下午2:22:12
 * Version 1.0
 */
public class SMS extends IDBean {
	
	/**
	 * 发送通知的用户
	 */
	private UserBean fromUser; 

	/**
	 * 需要通知的用户
	 */
	private UserBean toUser; 
	
	/**
	 * 消息的内容参数， key为模板的${} 的名称， value为对应的值
	 */
	private Map<String, Object> params;
	
	/**
	 * 通知的类型
	 */
	private String type;

	/**
	 * 短信过期时间(秒)， 默认是一个小时
	 */
	private int expire = 60 * 60;

	public UserBean getFromUser() {
		return fromUser;
	}

	public void setFromUser(UserBean fromUser) {
		this.fromUser = fromUser;
	}

	public UserBean getToUser() {
		return toUser;
	}

	public void setToUser(UserBean toUser) {
		this.toUser = toUser;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public EnumUtil.NoticeSMSType getType() {
		return EnumUtil.getNoticeSMSType(type);
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getExpire() {
		return expire > 0 ? expire : 60 * 10;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

	@Override
	public String toString() {
		return "内容："+ JSONObject.fromObject(params).toString()+",type："+type+", 发送用户："+ (fromUser != null ? fromUser.getId(): 0) +",目的用户："+(toUser != null ? toUser.getId(): 0)+",expire："+expire;
	}
}
