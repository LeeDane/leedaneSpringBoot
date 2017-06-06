package com.cn.leedane.test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.crawl.SanwenNet;
import com.cn.leedane.crawl.Wangyi;
import com.cn.leedane.crawl.WangyiNews;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.CrawlMapper;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.CrawlBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.BlogService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 爬虫相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:10:02
 * Version 1.0
 */
public class CrawlTest extends BaseTest {
	@Resource
	private CrawlMapper crawlMapper;
	
	@Resource
	private BlogService<BlogBean> blogService;
	
	@Resource
	private UserService<UserBean> userService;
	
	@Autowired
	private UserHandler userHandler;
	
	/**
	 * 系统级别的缓存对象
	 */
	private SystemCache systemCache;

	/***
	 * 抓取网易新闻链接保存下来
	 * @throws IOException
	 */
	@Test
	public void crawlWangyiNews() throws Exception{
		
		//ExecutorService threadPool = Executors.newFixedThreadPool(5);
		WangyiNews wangyi = new WangyiNews();
		wangyi.setUrl("http://news.163.com/");
		wangyi.execute();
		List<String> homeUrls = wangyi.getHomeListsHref();
		for(String homeUrl : homeUrls){
			CrawlBean crawlBean = new CrawlBean();
			crawlBean.setUrl(homeUrl.trim());
			crawlBean.setSource("网易新闻");
			crawlBean.setCreateUserId(OptionUtil.adminUser.getId());
			crawlBean.setCreateTime(new Date());
			logger.info(crawlMapper.save(crawlBean));
		}
	}
	
	@Test
	public void dealWangyiNews() throws Exception {
		systemCache = (SystemCache) SpringUtil.getBean("systemCache");
		String adminId = (String) systemCache.getCache("admin-id");
		int aid = 1;
		if(!StringUtil.isNull(adminId)){
			aid = Integer.parseInt(adminId);
		}
		//获得用户
		UserBean user = userService.findById(aid);
		//ExecutorService threadPool = Executors.newFixedThreadPool(5);
		List<CrawlBean> beans = SqlUtil.convertMapsToBeans(CrawlBean.class, crawlMapper.findAllNotCrawl(0, EnumUtil.WebCrawlType.网易新闻.value));
		for(CrawlBean bean: beans){
			Pattern p=Pattern.compile("http://[a-z]+.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/*");//找网易新闻的子站
			Matcher m=p.matcher(bean.getUrl());
			if(m.find()){
				WangyiNews wangyi = new WangyiNews(bean.getUrl(),"","");
				wangyi.setSaveFile(true);
				wangyi.execute();
				//threadPool.execute(wangyi);
				if( wangyi != null && wangyi.getContent() != null && !wangyi.getContent().trim().equals("")&& wangyi.getTitle() != null && !wangyi.getTitle().trim().equals("")){
					
					BlogBean blog = new BlogBean();
					blog.setTitle(wangyi.getTitle());
					blog.setContent(wangyi.getContent());
					//optionService.loadById(1);
					blog.setCreateUserId(user.getId());
					//operateLogService.loadById(1);
					blog.setCreateTime(new Date());
					blog.setSource(EnumUtil.WebCrawlType.网易新闻.value);
					blog.setFroms("Android客户端");
					
					String mainImgUrl = wangyi.getMainImg();
					if( mainImgUrl != null && !mainImgUrl.equals("")){
						blog.setHasImg(true);
						blog.setImgUrl(mainImgUrl);
						blog.setOriginLink(bean.getUrl());
					}
					blog.setIndex(true);//是否检索标记为true
					
					
					Map<String,Object> result = blogService.addBlog(blog, user);
					if(Boolean.parseBoolean(String.valueOf(result.get("isSuccess"))) == true){
						bean.setCrawl(true);
						//将抓取标记为已经抓取
						crawlMapper.update(bean);
					}	
					
					//从数据库中把插入的数据再次加进索引中
					/*if(blog.getId() > 0){
						//List<Map<String, Object>> lists = blogService.loadById(blog.getId());
						//blogService.searchBlog("select * from "+DataTableType.博客.value+" where source = '散文网' and index = false ");
						Document document = dealListToDocument(blog);
						try {
							new LuceneUtil().simpleIndexOne(document);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}*/
					
				}
			}else{
				bean.setCrawl(true);
				//将抓取标记为已经抓取
				crawlMapper.update(bean);
			}
		}
	}
	@SuppressWarnings("unused")
	private Document dealListToDocument(BlogBean blog) {
		Document doc = new Document();
		//标题选择索引+不需要分词，可以存储
		Field f_id = new StringField("id", StringUtil.changeNotNull(blog.getId()), Store.YES);
		//标题选择索引+分词，可以存储
		Field f_title = new TextField("title", StringUtil.changeNotNull(blog.getTitle()), Store.YES);
		//文章内容一般很大，索引选择分词+索引，网上说一般不存储，可是不存储的话就显示不了这些内容
		Field f_content = new TextField("content", StringUtil.changeNotNull(blog.getContent()), Store.YES);
		//作者名应该保持完整索引，不需要分词。可以存储
		Field f_account = new StringField("account", StringUtil.changeNotNull(userHandler.getUserName(blog.getCreateUserId())), Store.YES);
		//来源应该保持完整索引，不需要分词。可以存储
		Field f_source = new StringField("source", StringUtil.changeNotNull(blog.getSource()), Store.YES);			
		//来自应该保持完整索引，不需要分词。可以存储
		Field f_froms = new StringField("froms", StringUtil.changeNotNull(blog.getFroms()), Store.YES);
		//来源的链接应该保持完整索引，不需要分词。可以存储
		Field f_origin_link = new StringField("origin_link", StringUtil.changeNotNull(blog.getOriginLink()), Store.YES);
		
		//时间选择索引，但是不分词，可以存储,日期转化成long类型
		Field f_createDate = new LongField("create_time", blog.getCreateTime().getTime(), Store.YES);
		
		doc.add(f_id);
		doc.add(f_title);
		doc.add(f_content);
		doc.add(f_account);
		doc.add(f_source);
		doc.add(f_froms);
		doc.add(f_origin_link);
		doc.add(f_createDate);
		
		return doc;
	}

	/**
	 * 更新score
	 */
	@Test
	public void updateScore() throws Exception{
		List<CrawlBean> beans = SqlUtil.convertMapsToBeans(CrawlBean.class, crawlMapper.findAllNotCrawl(0, EnumUtil.WebCrawlType.网易新闻.value));
		for(CrawlBean bean: beans){
			Pattern p=Pattern.compile("http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/*");//找网易新闻的子站
			Matcher m=p.matcher(bean.getUrl());
			if(m.find()){
				Wangyi wangyi = new Wangyi(bean.getUrl(),"","");
				bean.setScore(wangyi.score());
				crawlMapper.update(bean);
			}
		}
	}
	
	/**
	 * 执行爬取散文网方法
	 */
	@Test
	public void crawlSanwenNet() throws Exception{
		try {
			//ExecutorService threadPool = Executors.newFixedThreadPool(5);
			SanwenNet sanwenNet = new SanwenNet();
			sanwenNet.setUrl("http://www.sanwen.net/");
			sanwenNet.execute();
			List<String> homeUrls = sanwenNet.getHomeListsHref();
			for(String homeUrl : homeUrls){
				CrawlBean crawlBean = new CrawlBean();
				//crawlBean.setCrawl(isCrawl);
				crawlBean.setUrl(homeUrl.trim());
				crawlBean.setSource("散文网");
				crawlBean.setCreateUserId(OptionUtil.adminUser.getId());
				crawlBean.setCreateTime(new Date());
				//crawlBean.setScore(wangyi.score());
				logger.info(crawlMapper.save(crawlBean));
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("抓取散文网信息出现异常：Crawl()");
		}
	}
	
	/**
	 * 执行处理散文网方法
	 */
	@Test
	public void dealSanwenNet() throws Exception{
		try {
			systemCache = (SystemCache) SpringUtil.getBean("systemCache");
			String adminId = (String) systemCache.getCache("admin-id");
			int aid = 1;
			if(!StringUtil.isNull(adminId)){
				aid = Integer.parseInt(adminId);
			}
			//获得用户
			UserBean user = userService.findById(aid);
			//ExecutorService threadPool = Executors.newFixedThreadPool(5);
			List<CrawlBean> beans = SqlUtil.convertMapsToBeans(CrawlBean.class, crawlMapper.findAllNotCrawl(0,EnumUtil.WebCrawlType.散文网.value));
			for(CrawlBean bean: beans){
				if(!StringUtil.isNull(bean.getUrl())){
					//http://www.sanwen.net/subject/3762456/
					Pattern p=Pattern.compile("http://www.sanwen.net/subject/[0-9]+");
					Matcher m=p.matcher(bean.getUrl());
					if(m.find()){
						SanwenNet sanwenNet = new SanwenNet(bean.getUrl(),"","");
						sanwenNet.setSaveFile(true);
						sanwenNet.execute();						
						if( sanwenNet != null){
							String content = sanwenNet.getContent("#article .content",".adcontent");
							String title = sanwenNet.getTitle();
							
							if(content != null && !content.trim().equals("") && title != null && !title.trim().equals("")){
								BlogBean blog = new BlogBean();
								blog.setTitle(title);
								blog.setContent(content);
								blog.setCreateUserId(user.getId());
								blog.setCreateTime(new Date());
								blog.setSource(EnumUtil.WebCrawlType.散文网.value);
								blog.setFroms("Android客户端");
								
								String mainImgUrl = sanwenNet.getMainImg("#article .content img",0);
								if( mainImgUrl != null && !mainImgUrl.equals("")){
									blog.setHasImg(true);
									blog.setImgUrl(mainImgUrl);
									blog.setOriginLink(bean.getUrl());
								}
									
								Map<String,Object> result = blogService.addBlog(blog, user);
								if(Boolean.parseBoolean(String.valueOf(result.get("isSuccess"))) == true){
									bean.setCrawl(true);
									//将抓取标记为已经抓取
									crawlMapper.update(bean);
								}
								
								//从数据库中把插入的数据再次加进索引中
								/*if(blog.getId() > 0){
									List<Map<String, Object>> lists = blogService.getOneBlog(blog.getId());
									Document document = dealListToDocument(lists);
									try {
										new LuceneUtil().simpleIndexOne(document);
									} catch (ParseException e) {
										e.printStackTrace();
									}
								}*/
							}	
						}
					}else{				
						bean.setCrawl(true);
						//将抓取标记为已经抓取
						crawlMapper.update(bean);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("处理散文网信息出现异常：deal()");
		}
	}
	/*public static void main(String[] args) {
		
		String u = "http://www.sanwen.net/subject/1";
		Pattern p=Pattern.compile("http://www.sanwen.net/subject/[0-9]+");
		Matcher m=p.matcher(u);
		if(m.find()){
			logger.info("true");
		}else
			logger.info("false");
	}*/
	
		
}
