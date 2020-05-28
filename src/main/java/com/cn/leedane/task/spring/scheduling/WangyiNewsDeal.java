package com.cn.leedane.task.spring.scheduling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.crawl.WangyiNews;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.lucene.solr.BlogSolrHandler;
import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.mapper.CrawlMapper;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.CrawlBean;
import com.cn.leedane.model.GalleryBean;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.SolrAddThread;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.FileUtil;
import com.cn.leedane.utils.JsoupUtil;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 网易新闻爬虫处理数据任务
 * @author LeeDane
 * 2017年6月6日 上午10:52:07
 * version 1.0
 */
@Component("wangyiNewsDeal")
public class WangyiNewsDeal extends AbstractScheduling{
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private CrawlMapper crawlMapper;
	
	@Autowired
	private BlogMapper blogMapper;
	
	@Autowired
	private UserHandler userHandler;
	
	/**
	 * 执行处理方法
	 */
	@Override
	public void execute() throws SchedulerException {
		//logger.info(DateUtil.getSystemCurrentTime("yyyy-MM-dd HH:mm:ss") + ":Wangyi:deal()");
		try {
			SqlUtil sqlUtil = new SqlUtil();
			List<Map<String, Object>> results = crawlMapper.findAllNotCrawl(0, EnumUtil.WebCrawlType.网易新闻.value);
			if(CollectionUtil.isEmpty(results)){
				logger.error("处理网易新闻信息--->没有数据");
				return;
			}		
			List<CrawlBean> beans = new ArrayList<CrawlBean>();
			for(Map<String, Object> result: results){
				result.put("create_time", DateUtil.formatStringTime(StringUtil.changeNotNull(result.get("create_time"))));
				result.put("modify_time", DateUtil.formatStringTime(StringUtil.changeNotNull(result.get("modify_time"))));
				JSONObject json = JSONObject.fromObject(result);
				CrawlBean bean = (CrawlBean) sqlUtil.getBean(json, CrawlBean.class);
				beans.add(bean);
			}
			
			for(CrawlBean bean: beans){
				Pattern p=Pattern.compile("http://[a-z]+.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/*");//找网易新闻的子站
				Matcher m=p.matcher(bean.getUrl());
				if(m.find()){
					WangyiNews wangyi = new WangyiNews(bean.getUrl(),"","");
					try{
						wangyi.execute();
					}catch(IOException e){
						logger.error("处理网易新闻信息出现异常：deal()+url="+bean.getUrl(), e);
					}
					
					if( wangyi != null && StringUtil.isNotNull(wangyi.getContent()) && StringUtil.isNotNull(wangyi.getTitle())){
						//判断是否已经存在相同的信息
						List<Map<String, Object>> existsBlogs = blogMapper.executeSQL("select id from "+DataTableType.博客.value+" where origin_link != '' and origin_link = ? ", bean.getUrl());
						if(CollectionUtil.isEmpty(existsBlogs)){
							BlogBean blog = new BlogBean();
							blog.setTitle(wangyi.getTitle().trim());
							blog.setContent(wangyi.getContent());
							blog.setCreateUserId(bean.getCreateUserId());
							blog.setCreateTime(new Date());
							blog.setSource(EnumUtil.WebCrawlType.网易新闻.value);
							blog.setFroms("爬虫抓取");
							blog.setCategory("我的日常");
							blog.setStatus(ConstantsUtil.STATUS_NORMAL);
							blog.setDigest(JsoupUtil.getInstance().getDigest(wangyi.getContent(), 0, 120));
							blog.setCanComment(true);
							blog.setCanTransmit(true);
							String mainImgUrl = wangyi.getMainImg();
							if(StringUtil.isNotNull(mainImgUrl)){
								blog.setHasImg(true);
								
								//对base64位的src属性处理
								if(!StringUtil.isLink(mainImgUrl)){
									mainImgUrl = JsoupUtil.getInstance().base64ToLink(mainImgUrl, userHandler.getUserName(bean.getCreateUserId()));
								}
								
								//由于图片做了限制，需要对图片进行先预处理
								GalleryBean galleryBean = FileUtil.getNetWorkImgAttrs(null, mainImgUrl);
								blog.setImgUrl(galleryBean.getPath());								
							}
							blog.setOriginLink(bean.getUrl());
							//保存进博客表中
							boolean result = blogMapper.save(blog) > 0 ;
							
							//抓取成功
							if(result){
								//异步修改solr索引
								new ThreadUtil().singleTask(new SolrAddThread<BlogBean>(BlogSolrHandler.getInstance(), blog));
							}
						}
						
					}
				}
				
				//不管处理是否成功都标记为已抓取
				bean.setCrawl(true);
				//bean.setScore(wangyi.score());
				//将抓取标记为已经抓取
				crawlMapper.update(bean);
			}//for循环到此结束
		} catch (Exception e) {
			logger.error("处理网易新闻信息出现异常：deal()", e);
		}
	}
}
