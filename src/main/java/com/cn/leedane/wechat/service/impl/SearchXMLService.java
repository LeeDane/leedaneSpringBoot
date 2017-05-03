package com.cn.leedane.wechat.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cn.leedane.handler.MoodHandler;
import com.cn.leedane.handler.WechatHandler;
import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.mapper.MoodMapper;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.wechat.bean.News;
import com.cn.leedane.wechat.bean.NewsMessage;
import com.cn.leedane.wechat.service.BaseXMLWechatService;
import com.cn.leedane.wechat.util.MessageUtil;
import com.cn.leedane.wechat.util.WeixinUtil;

/**
 * 查询模式下的实现
 * @author LeeDane
 * 2016年4月7日 下午4:14:56
 * Version 1.0
 */
@Repository
public class SearchXMLService extends BaseXMLWechatService {

	@Autowired
	private WechatHandler wechatHandler;
	
	public void setWechatHandler(WechatHandler wechatHandler) {
		this.wechatHandler = wechatHandler;
	}
	
	@Autowired
	private MoodHandler moodHandler;
	
	public void setMoodHandler(MoodHandler moodHandler) {
		this.moodHandler = moodHandler;
	}
	
	public SearchXMLService(){
		
	}
	
	public SearchXMLService(HttpServletRequest request, Map<String, String> map) {
		super(request, map);
	}
	@Autowired
	private BlogMapper blogMapper;
	
	@Autowired
	private MoodMapper moodMapper;
	
	@Override
	protected String execute() {
		String r = "";
		if(!StringUtil.isNull(Content)){
			if(wechatHandler == null){
				wechatHandler = (WechatHandler) SpringUtil.getBean("wechatHandler");
			}
			
			if(blogMapper == null){
				blogMapper = (BlogMapper) SpringUtil.getBean("blogMapper");
			}
			
			if(moodHandler == null){
				moodHandler = (MoodHandler) SpringUtil.getBean("moodHandler");
			}
			
			if(moodMapper == null){
				moodMapper = (MoodMapper) SpringUtil.getBean("moodMapper");
			}
			r = initSearchNewsMessage(ToUserName, FromUserName,Content);
		}else{
			r = "进入官网查询模式";
		}
		return r;
	}

	/**
	 * 获得查询的新闻信息对象字符串
	 * @param ToUserName
	 * @param FromUserName
	 * @param search
	 * @return
	 */
	private String initSearchNewsMessage(String ToUserName,String FromUserName,String search) {		
		List<SearchBean> searchBeans= new ArrayList<SearchXMLService.SearchBean>();
		SearchBean searchBean;
		StringBuffer blogSql = new StringBuffer();
		blogSql.append("select b.id, b.img_url, b.title, b.has_img, b.tag, date_format(b.create_time,'%Y-%c-%d %H:%i:%s') create_time ");
		blogSql.append(" , b.digest, b.froms, b.create_user_id, u.account ");
		blogSql.append(" from "+DataTableType.博客.value+" b inner join "+DataTableType.用户.value+" u on b.create_user_id = u.id ");
		blogSql.append(" where b.status = ? and img_url != '' and ((b.content like '%"+search+"%') or (b.title like '%"+search+"%')) limit 10");
		List<Map<String, Object>> blogs = blogMapper.executeSQL(blogSql.toString(), ConstantsUtil.STATUS_NORMAL);
		
		if(blogs != null && blogs.size() >0){
			for(Map<String, Object> map: blogs){
				searchBean = new SearchBean();
				searchBean.setClickUrl(getBasePath() +"dt/"+StringUtil.changeObjectToInt(map.get("id")));
				searchBean.setCreateTime(DateUtil.DateToString(DateUtil.stringToDate(StringUtil.changeNotNull(map.get("create_time"))), "yyyyMMddHHmmss"));
				searchBean.setDescription(StringUtil.changeNotNull(map.get("account"))+":"+StringUtil.changeNotNull(map.get("digest")));
				searchBean.setPicUrl(StringUtil.changeNotNull(map.get("img_url")));
				searchBean.setTableName(DataTableType.博客.value);
				searchBean.setTitle(StringUtil.changeNotNull(map.get("title")));
				searchBeans.add(searchBean);
			}
		}
		
		StringBuffer moodSql = new StringBuffer();
		moodSql.append("select m.id, m.content, m.froms, m.uuid, m.create_user_id, date_format(m.create_time,'%Y-%c-%d %H:%i:%s') create_time, m.has_img,");
		moodSql.append(" m.read_number, m.zan_number, m.comment_number, m.transmit_number, m.share_number, u.account");
		moodSql.append(" from "+DataTableType.心情.value+" m inner join "+DataTableType.用户.value+" u on u.id = m.create_user_id where m.status = ? and ");
		moodSql.append(" m.content like '%"+search+"%' limit 10");
		List<Map<String, Object>> Moods = moodMapper.executeSQL(moodSql.toString(), ConstantsUtil.STATUS_NORMAL);
		
		if(Moods != null && Moods.size() >0){
			for(Map<String, Object> map: Moods){
				searchBean = new SearchBean();
				searchBean.setClickUrl(getBasePath() +"dt/"+StringUtil.changeObjectToInt(map.get("id")));
				searchBean.setCreateTime(DateUtil.DateToString(DateUtil.stringToDate(StringUtil.changeNotNull(map.get("create_time"))), "yyyyMMddHHmmss"));
				searchBean.setDescription(StringUtil.changeNotNull(map.get("account"))+":"+StringUtil.changeNotNull(map.get("content")));
				//获取图片
				searchBean.setPicUrl(moodHandler.getMoodImg("+DataTableType.心情.value+", StringUtil.changeNotNull(map.get("uuid")), "120x120"));
				searchBean.setTableName(DataTableType.心情.value);
				searchBean.setTitle(StringUtil.changeNotNull(map.get("account"))+":" +StringUtil.changeNotNull(map.get("content")));
				searchBeans.add(searchBean);
			}
		}
		return initNewsMessage(ToUserName,FromUserName,searchBeans);
	}
	
	/**
	 * 图文消息的组装
	 * @param ToUserName
	 * @param FromUserName
	 * @return
	 */
	public String initNewsMessage(String ToUserName,String FromUserName,List<SearchBean> searchBeans){	
		List<News> newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		//有查询到记录
		if(searchBeans.size() > 0){
			
			//先对searchBeans进行时间大小排序取前面10个
			searchBeans = sortBeansByTimeLimit10(searchBeans);
			News news;
			for(SearchBean bean: searchBeans){
				news = new News();
				news.setTitle(StringUtil.changeNotNull(bean.getTitle()));
				news.setDescription(StringUtil.changeNotNull(bean.getTitle()));
				news.setPicUrl(StringUtil.changeNotNull(bean.getPicUrl()));
				if(bean.getTableName().equalsIgnoreCase(DataTableType.博客.value))
					news.setUrl(bean.getClickUrl());
				
				newsList.add(news);
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
	 * 将Bean进行时间排序,剩下最近时间的10个
	 * @param searchBeans
	 */
	private List<SearchBean> sortBeansByTimeLimit10(List<SearchBean> searchBeans) {
		if(searchBeans == null) return searchBeans;
		
		// 按排查时间倒序models
		Collections.sort(searchBeans, new Comparator<SearchBean>() {
			@Override
			public int compare(SearchBean ed1, SearchBean ed2) {
				String date1 = ed1.getCreateTime();
				String date2 = ed2.getCreateTime();
				if (StringUtil.isNull(date1))
					date1 = "0";
				if (StringUtil.isNull(date2))
					date2 = "0";
				long time1 = Long.parseLong(date1);
				long time2 = Long.parseLong(date2);
				return time1 == time2 ? 0 :  
	                (time1 > time2 ? -1 : 1); 
			}
		});
		
		if(searchBeans.size() >10)
			searchBeans = searchBeans.subList(0, 10);
		
		return searchBeans;
	}

	class SearchBean {
		private int id;
		private String tableName;
		private String title;//展示给用户看到的内容
		private String description;//展示给用户看到的内容
		private String createTime;
		private String picUrl;
		private String clickUrl;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getTableName() {
			return tableName;
		}
		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getPicUrl() {
			return picUrl;
		}
		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}
		public String getClickUrl() {
			return clickUrl;
		}
		public void setClickUrl(String clickUrl) {
			this.clickUrl = clickUrl;
		}
		
	}
	
	/*public static void main(String[] args) {
		SearchXMLService service = new SearchXMLService();
		service.Content = "他";
		service.FromUserName = "FromUserName";
		service.ToUserName = "ToUserName";
		System.out.println(service.execute());
	}*/
	
}
