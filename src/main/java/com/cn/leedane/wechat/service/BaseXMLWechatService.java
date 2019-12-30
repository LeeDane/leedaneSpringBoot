package com.cn.leedane.wechat.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public abstract class BaseXMLWechatService {
	
	HttpServletRequest request;
	Map<String,String> map;
	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 项目的基本路径
	 */
	//private String basePath;
	
	/**
	 * 返回信息的类型，将决定以什么样的格式返回给用户，所以需要根据实际控制好，默认是text
	 */
	//public String responseType = WeixinUtil.TYPE_TEXT;
	
	/**
	 * 发送的用户
	 */
	protected String FromUserName;  
	
	/**
	 * 目的的用户(就是请求返回的用户)
	 */
	protected String ToUserName;
	
	/**
	 * 信息的类型
	 */
	protected String MsgType;
	
	/**
	 * 信息的内容
	 */
	protected String Content;
	
	public BaseXMLWechatService() {
	}
	
	/**
	 * 通过请求传参数构造
	 * @param request
	 */
	public BaseXMLWechatService(HttpServletRequest request,Map<String,String> map) {
		this.request = request;
		this.map = map;
		try {
			init();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化请求参数
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void init() throws DocumentException, IOException {
		FromUserName = map.get("FromUserName");
		ToUserName = map.get("ToUserName");
		MsgType = map.get("MsgType");
		Content = map.get("Content");
		logger.info("FromUserName:"+FromUserName+",ToUserName:"+ToUserName);
	}
	
	/**
	 * 由子类自己实现的具体操作方法
	 * return
	 */
	protected abstract String execute();
	
	
	/**
	 * 返回的xml字符串
	 * @return
	 */
	public String responseXML(){
		return execute();
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public String getBasePath() {
		 /*this.basePath = request.getScheme()+"://"+request.getServerName()
				+":"+request.getServerPort()+request.getContextPath()+"/"; 
		 return basePath;*/
		return LeedanePropertiesConfig.getSystemUrl();
	}
}
