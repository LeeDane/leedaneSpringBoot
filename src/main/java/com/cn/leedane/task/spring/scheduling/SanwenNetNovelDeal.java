package com.cn.leedane.task.spring.scheduling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.crawl.SanwenNet;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.mapper.CrawlMapper;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.CrawlBean;
import com.cn.leedane.model.GalleryBean;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.BlogSolrAddThread;
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
 * 散文网短篇小说爬虫处理数据任务
 * @author LeeDane
 * 2017年6月6日 上午10:53:15
 * version 1.0
 */
@Component("sanwenNetNovelDeal")
public class SanwenNetNovelDeal implements BaseScheduling{

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
		//logger.info(DateUtil.getSystemCurrentTime("yyyy-MM-dd HH:mm:ss") + ":Sanwen:deal()");
		
		//@SuppressWarnings("unchecked")
		//List<CrawlBean> beans = SqlUtil.convertMapsToBeans(CrawlBean.class, crawlMapper.findAllNotCrawl(0, EnumUtil.WebCrawlType.散文网短篇小说.value));
		
		SqlUtil sqlUtil = new SqlUtil();
		List<Map<String, Object>> results = crawlMapper.findAllNotCrawl(0, EnumUtil.WebCrawlType.散文网短篇小说.value);
		if(CollectionUtil.isEmpty(results)){
			logger.error("处理散文网短篇小说信息--->没有数据");
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
		
		if(beans != null && beans.size()> 0){
			List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
			ExecutorService threadpool = Executors.newFixedThreadPool(beans.size() >2 ? 3: beans.size());
			SingleDealTask dealTask;
			for(CrawlBean bean: beans){
				dealTask = new SingleDealTask(bean);
				futures.add(threadpool.submit(dealTask));
			}
			
			threadpool.shutdown();
			List<String> errors = new ArrayList<String>();
			for(int i = 0; i < futures.size(); i++){
				try {
					if(!futures.get(i).get()){
						errors.add(beans.get(i).getUrl());
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			
			for(String url: errors){
				logger.error("处理散文网短篇小说信息出现异常的链接是："+url);
			}
		}
		logger.info("处理散文网短篇小说信息结束......");
	}
	
	/**
	 * 当个处理任务
	 * @author LeeDane
	 * 2016年3月18日 下午2:47:25
	 * Version 1.0
	 */
	class SingleDealTask implements Callable<Boolean>{
		private CrawlBean mCrawlBean;
		public SingleDealTask(CrawlBean crawlBean){
			this.mCrawlBean = crawlBean;
		}

		@Override
		public Boolean call() throws Exception {
			String url = mCrawlBean.getUrl();
			if(!StringUtil.isNull(url)){
				Pattern p=Pattern.compile("http://www.sanwen.net/subject/[0-9]+");
				Matcher m=p.matcher(url);
				if(m.find()){
					SanwenNet sanwenNet = new SanwenNet(url,"","");
					sanwenNet.execute();
					
					try{
						sanwenNet.execute();
					}catch(IOException e){
						logger.error("处理散文小说信息出现异常：deal()+url="+url +e.toString());
						mCrawlBean.setCrawl(true);
						//将抓取标记为已经抓取
						crawlMapper.update(mCrawlBean);
						return false;
					}
					
					String content = sanwenNet.getContent("#article .content",".adcontent");
					String title = sanwenNet.getTitle().trim();
					
					if(StringUtil.isNotNull(content) && StringUtil.isNotNull(title)){
						//判断是否已经存在相同的信息
						List<Map<String, Object>> existsBlogs = blogMapper.executeSQL("select id from "+DataTableType.博客.value+" where origin_link = ? ", url);
						if(CollectionUtil.isEmpty(existsBlogs)){
							BlogBean blog = new BlogBean();
							blog.setTitle(title);
							blog.setContent(content);
							blog.setCreateUserId(mCrawlBean.getCreateUserId());
							blog.setCreateTime(new Date());
							blog.setSource(EnumUtil.WebCrawlType.散文网短篇小说.value);
							blog.setFroms("爬虫抓取");
							blog.setCategory("我的日常");
							blog.setStatus(ConstantsUtil.STATUS_NORMAL);
							blog.setDigest(JsoupUtil.getInstance().getDigest(content, 0, 120));
							blog.setCanComment(true);
							blog.setCanTransmit(true);
							
							//获取主图
							String mainImgUrl = sanwenNet.getMainImg("#article .content img", 0);
							if( mainImgUrl != null && !mainImgUrl.equals("")){
								blog.setHasImg(true);
								
								//对base64位的src属性处理
								if(!StringUtil.isLink(mainImgUrl)){
									mainImgUrl = JsoupUtil.getInstance().base64ToLink(mainImgUrl, userHandler.getUserName(mCrawlBean.getCreateUserId()));
								}
								
								//由于图片做了限制，需要对图片进行先预处理
								GalleryBean bean = FileUtil.getNetWorkImgAttrs(null, mainImgUrl);
								blog.setImgUrl(bean.getPath());
							}
							blog.setOriginLink(url);
							//把抓取到的数据添加进博客表中
							boolean result = blogMapper.save(blog) > 0;
							
							//保存成功之后
							if(result){
								//异步修改solr索引
								new ThreadUtil().singleTask(new BlogSolrAddThread(blog));
								
								mCrawlBean.setCrawl(true);
								//将抓取标记为已经抓取
								crawlMapper.update(mCrawlBean);
								return true;
							}
						}
					}
				}	
				mCrawlBean.setCrawl(true);
				//将抓取标记为已经抓取
				crawlMapper.update(mCrawlBean);
			}
			return false;
		}
		
	}
}
