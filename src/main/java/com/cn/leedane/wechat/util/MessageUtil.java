package com.cn.leedane.wechat.util;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.wechat.bean.ImageMessage;
import com.cn.leedane.wechat.bean.News;
import com.cn.leedane.wechat.bean.NewsMessage;
import com.cn.leedane.wechat.bean.TextMessage;
import com.thoughtworks.xstream.XStream;

public class MessageUtil {

	public static Map<String,String> xmlToMap(HttpServletRequest request) throws DocumentException, IOException{
		Map<String,String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		InputStream ins = request.getInputStream();
		
		Document doc = reader.read(ins);
		
		Element root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> list = root.elements();
		for(Element e : list){
			map.put(e.getName(), e.getText());
		}
		ins.close();
		
		return map;
	}
	
	public static String textMessageToXml(TextMessage msg){
		XStream stream = new XStream();
		stream.alias("xml", msg.getClass());
		return stream.toXML(msg);
	}
	
	public static String imageMessageToXml(ImageMessage msg){
		XStream stream = new XStream();
		stream.alias("xml", msg.getClass());
		return stream.toXML(msg);
	}
	
	/**
	 * 图文消息转为xml
	 * @param newsMessage
	 * @return
	 */
	public static String newsMessageToXml(NewsMessage newsMessage){
		XStream xstream = new XStream();
		xstream.alias("xml", newsMessage.getClass());
		xstream.alias("item", new News().getClass());
		return xstream.toXML(newsMessage);
	}
	
	/**
	 * 给客户端发送文本信息的xml格式数据
	 * @param ToUserName
	 * @param FromUserName
	 * @param Content
	 */
	public static String sendText(String ToUserName,String FromUserName,String Content) {
		System.out.println("Content:"+Content);
		//构建发送给客户端的xml格式数据
		TextMessage textMessage = new TextMessage();
		textMessage.setFromUserName(ToUserName);
		textMessage.setToUserName(FromUserName);
		textMessage.setMsgType("text");
		textMessage.setCreateTime(new Date().getTime());
		
		textMessage.setContent(Content);
		return MessageUtil.textMessageToXml(textMessage);

	}
	
	/**
	 * 博客图文消息的组装
	 * @param basePath  项目的路径
	 * @param ToUserName
	 * @param FromUserName
	 * @param beans
	 * @return
	 */
	public static String initBlogNewsMessage(String basePath, String ToUserName,String FromUserName,List<Map<String, Object>> beans){	
		List<News> newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		//有查询到记录
		if(beans.size() > 0){
			//小于10条记录的处理
			if(beans.size() < 10){
				for(Map<String, Object> bean: beans){
					News news = new News();
					news.setTitle(StringUtil.changeNotNull(bean.get("title")));
					news.setDescription(StringUtil.changeNotNull(bean.get("title")));
					news.setPicUrl(StringUtil.changeNotNull(bean.get("img_url")));
					news.setUrl(basePath +"dt/"+StringUtil.changeObjectToInt(bean.get("id")));
					
					newsList.add(news);
				}
			//大于10条记录，只去前面10条记录做处理
			}else{
				for(int i = 0; i < 10; i++){
					News news = new News();
					news.setTitle(StringUtil.changeNotNull(beans.get(i).get("title")));
					news.setDescription(StringUtil.changeNotNull(beans.get(i).get("title")));
					news.setPicUrl(StringUtil.changeNotNull(beans.get(i).get("img_url")));
					news.setUrl(basePath +"dt/"+StringUtil.changeObjectToInt(beans.get(i).get("id")));
					newsList.add(news);
				}
			}	
		}else{		
			//没有查询到记录改成发送文本信息
			return "找不到相应的记录";
		}
			
		newsMessage.setToUserName(FromUserName);
		newsMessage.setFromUserName(ToUserName);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType(WeixinUtil.TYPE_NEWS);
		newsMessage.setArticles(newsList);
		newsMessage.setArticleCount(newsList.size());		
		return MessageUtil.newsMessageToXml(newsMessage);
	}
	/**
	 * 登录绑定链接的消息的组装
	 * @param basePath  项目的路径
	 * @param ToUserName
	 * @param FromUserName
	 * @param currentType
	 * @return
	 */
	public static String initBingLoginMessage(String basePath, String ToUserName,String FromUserName, String currentType){	
		List<News> newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		News news = new News();
		news.setTitle("请先绑定leedane网站账号");
		news.setDescription("点击进入绑定登录，绑定成功后支持更多的操作，给您更好的体验。");
		news.setPicUrl("http://imgsrc.baidu.com/forum/pic/item/8cf76c63f6246b60d4f7bfafebf81a4c530fa26a.jpg");
		news.setUrl(basePath +"bw?FromUserName="+FromUserName+"&currentType="+currentType);
		
		newsList.add(news);
			
		newsMessage.setToUserName(FromUserName);
		newsMessage.setFromUserName(ToUserName);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType(WeixinUtil.TYPE_NEWS);
		newsMessage.setArticles(newsList);
		newsMessage.setArticleCount(newsList.size());		
		return MessageUtil.newsMessageToXml(newsMessage);
	}
}