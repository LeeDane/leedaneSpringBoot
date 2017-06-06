package com.cn.leedane.task.spring.scheduling;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.crawl.WangyiNews;
import com.cn.leedane.handler.BlogHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.lucene.solr.BlogSolrHandler;
import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.mapper.CrawlMapper;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.CrawlBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
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
public class WangyiNewsDeal implements BaseScheduling{
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
			@SuppressWarnings("unchecked")
			List<CrawlBean> beans = SqlUtil.convertMapsToBeans(CrawlBean.class, crawlMapper.findAllNotCrawl(0, EnumUtil.WebCrawlType.网易新闻.value));
			for(CrawlBean bean: beans){
				Pattern p=Pattern.compile("http://[a-z]+.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/*");//找网易新闻的子站
				Matcher m=p.matcher(bean.getUrl());
				if(m.find()){
					WangyiNews wangyi = new WangyiNews(bean.getUrl(),"","");
					try{
						wangyi.execute();
					}catch(IOException e){
						logger.error("处理网易新闻信息出现异常：deal()+url="+bean.getUrl() +e.toString());
						bean.setCrawl(true);
						//bean.setScore(wangyi.score());
						//将抓取标记为已经抓取
						crawlMapper.update(bean);
						continue;
					}
					
					if( wangyi != null && wangyi.getContent() != null && !wangyi.getContent().trim().equals("")&& wangyi.getTitle() != null && !wangyi.getTitle().trim().equals("")){
						
						//判断是否已经存在相同的信息
						List<Map<String, Object>> existsBlogs = blogMapper.executeSQL("select id from "+DataTableType.博客.value+" where origin_link != '' and origin_link = ? ", bean.getUrl());
						if(existsBlogs!= null && existsBlogs.size() > 0){
							bean.setCrawl(true);
							//bean.setScore(wangyi.score());
							//将抓取标记为已经抓取
							crawlMapper.update(bean);
							continue;
						}
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
							blog.setImgUrl(mainImgUrl);
							
						}
						blog.setOriginLink(bean.getUrl());
						//保存进博客表中
						boolean result = blogMapper.save(blog) > 0 ;
						
						//抓取成功
						if(result){
							BlogSolrHandler.getInstance().addBean(blog);
							bean.setCrawl(true);
							//bean.setScore(wangyi.score());
							//将抓取标记为已经抓取
							crawlMapper.update(bean);
						}
					}
				
				//不符合条件的也修改标记为已抓取
				}else{
					bean.setCrawl(true);
					//bean.setScore(wangyi.score());
					//将抓取标记为已经抓取
					crawlMapper.update(bean);
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("处理网易新闻信息出现异常：deal()");
		}
	}
}
