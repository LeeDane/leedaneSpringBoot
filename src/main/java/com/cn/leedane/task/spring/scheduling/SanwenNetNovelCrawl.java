package com.cn.leedane.task.spring.scheduling;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.crawl.SanwenNetNovel;
import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.mapper.CrawlMapper;
import com.cn.leedane.model.CrawlBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.OptionUtil;

/**
 * 散文网短篇小说爬虫抓取数据任务
 * @author LeeDane
 * 2017年6月6日 上午10:53:21
 * version 1.0
 */
@Component("sanwenNetNovelCrawl")
public class SanwenNetNovelCrawl extends AbstractScheduling{

	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private CrawlMapper crawlMapper;
	
	@Autowired
	private BlogMapper blogMapper;
	
	@Autowired
	private UserService<UserBean> userService;
	
	public void setUserService(UserService<UserBean> userService) {
		this.userService = userService;
	}

	/**
	 * 执行爬取方法
	 */
	@Override
	public void execute() throws SchedulerException {
		//logger.info(DateUtil.getSystemCurrentTime("yyyy-MM-dd HH:mm:ss") + ":Sanwen:crawl()");
		try {
			SanwenNetNovel sanwenNet = new SanwenNetNovel();
			sanwenNet.setUrl("http://www.sanwen.net/novel/");
			sanwenNet.execute();
			List<String> homeUrls = sanwenNet.getHomeListsHref();
			for(String homeUrl : homeUrls){
				CrawlBean crawlBean = new CrawlBean();
				crawlBean.setUrl(homeUrl.trim());
				crawlBean.setSource("散文网短篇小说");
				crawlBean.setCreateTime(new Date());
				crawlBean.setCreateUserId(OptionUtil.adminUser.getId());
				logger.info(crawlMapper.save(crawlBean));
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("抓取散文网短篇小说信息出现异常：Crawl()", e);
		}
	}
}
