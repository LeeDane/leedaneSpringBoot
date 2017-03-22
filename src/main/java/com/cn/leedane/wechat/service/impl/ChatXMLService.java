package com.cn.leedane.wechat.service.impl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.cn.leedane.wechat.service.BaseXMLWechatService;
import com.cn.leedane.wechat.util.HttpRequestUtil;
import com.cn.leedane.wechat.util.WeixinUtil;

/**
 * 聊天的实现类
 * @author leedane
 *
 */
public class ChatXMLService extends BaseXMLWechatService{
	
	public ChatXMLService(HttpServletRequest request, Map<String, String> map) {
		super(request, map);
	}

	@Override
	protected String execute() {
		String r = null;
		synchronized (Content) {			
			try {
				r = HttpRequestUtil.sendAndRecieveFromTuLing(Content);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("图灵机器人返回的信息:"+r);
		JSONObject json = JSONObject.fromObject(r);
		return buildMessage(MsgType,ToUserName,FromUserName, json);
		
	}
	
	/**
	 * 对机器人返回的信息进行XML格式的封装
	 * @param MsgType
	 * @param ToUserName
	 * @param FromUserName
	 * @param json
	 * @return
	 */
	public String buildMessage(String MsgType,String ToUserName,String FromUserName,JSONObject json){
		//返回给微信用户内容的信息
		String wtext = "";
		if(WeixinUtil.TYPE_TEXT.equals(MsgType)){
			int code = json.getInt("code");
					
			if(code == 100000){
				wtext = json.getString("text");
				wtext = wtext.replaceAll("图灵", "");
			}else if(code == 302000){//新闻
				
				StringBuffer buffer = new StringBuffer();
				buffer.append(json.getString("text")+"\n");
				JSONArray lists = json.getJSONArray("list");
				if(lists.size() > 0){
					for(int i = 0; i < lists.size(); i++){
						JSONObject obj = lists.getJSONObject(i);
						buffer.append(obj.getString("article")+"\n");//标题 
						buffer.append(obj.getString("source") +"\n");//来源
						buffer.append(obj.getString("detailurl") +"\n");//详情地址
						buffer.append(obj.getString("icon") +"\n");//图标地址
						buffer.append("\n"); 	
					}
					
				}
				wtext = buffer.toString();
			}else if(code == 200000){//部分无法通过文字展示的功能，会以链接的形式返回
				wtext = json.getString("text") + ",以下是链接:\n\r" + json.getString("url") ;
			}else if(code == 305000){//列车信息
				StringBuffer buffer = new StringBuffer();
				buffer.append(json.getString("text")+"\n");
				JSONArray lists = json.getJSONArray("list");
				if(lists.size() > 0){
					for(int i = 0; i < lists.size(); i++){
						JSONObject obj = lists.getJSONObject(i);
						buffer.append(obj.getString("trainnum") + "\n");//车次
						buffer.append(obj.getString("start") + "   " + obj.getString("terminal")+"\n");//起始站和到达站
						buffer.append(obj.getString("starttime") + "   " + obj.getString("endtime") +"\n");//开车时间和到达时间
						buffer.append(obj.getString("detailurl") +"\n");//详情地址
						buffer.append(obj.getString("icon") +"\n");//图标地址
						buffer.append("\n");
					}
					
				}
				wtext = buffer.toString();
			}else if(code == 306000 ){//航班信息
				
				StringBuffer buffer = new StringBuffer();
				buffer.append(json.getString("text")+"\n");
				JSONArray lists = json.getJSONArray("list");
				if(lists.size() > 0){
					for(int i = 0; i < lists.size(); i++){
						JSONObject obj = lists.getJSONObject(i);
						buffer.append(obj.getString("flight") + "  " + obj.getString("route")+"\n");//航班和航班路线
						buffer.append(obj.getString("starttime") + "  " + obj.getString("endtime") +"\n");//起飞时间和到达时间
						buffer.append(obj.getString("state") +"\n");//航班状态
						buffer.append(obj.getString("detailurl") +"\n");//详情地址
						buffer.append(obj.getString("icon") +"\n");//图标地址
						buffer.append("\n"); 
					}
					
				}
				wtext = buffer.toString();
			}else if(code == 308000){//菜谱
				
				StringBuffer buffer = new StringBuffer();
				buffer.append(json.getString("text")+"\n");
				JSONArray lists = json.getJSONArray("list");
				if(lists.size() > 0){
					for(int i = 0; i < lists.size(); i++){
						JSONObject obj = lists.getJSONObject(i);
						buffer.append(obj.getString("name")+"\n");//名称 
						buffer.append(obj.getString("info") +"\n");//详情
						buffer.append(obj.getString("state") +"\n");//航班状态
						buffer.append(obj.getString("detailurl") +"\n");//详情地址
						buffer.append(obj.getString("icon") +"\n");//图标地址
						buffer.append("\n"); 
					}
					
				}
				wtext = buffer.toString();
				
			}else if(code == 40001){
				wtext = "key的长度错误(32位)";
			}else if(code == 40002){
				wtext = "请求内容为空 ";
			}else if(code == 40003){
				wtext = "key错误或帐号未激活";
			}else if(code == 40004){
				wtext = "当天请求次数已用完";
			}else if(code == 40005){
				wtext = "暂不支持该功能";
			}else if(code == 40006){
				wtext = "服务器升级中 ";
			}else if(code == 40007){
				wtext = "服务器数据格式异常";
			}else{
				wtext = "未知错误";
			}
		}	
		return wtext;
	}

}
