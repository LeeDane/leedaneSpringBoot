package com.cn.leedane.task.spring.scheduling;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.crawl.SanwenNet;
import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.mapper.CrawlMapper;
import com.cn.leedane.model.CrawlBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.OptionUtil;

/**
 * 散文网爬虫抓取数据任务
 * @author LeeDane
 * 2017年6月6日 上午10:25:30
 * version 1.0
 */
@Component("sanwenNetCrawl")
public class SanwenNetCrawl extends AbstractScheduling{
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
			SanwenNet sanwenNet = new SanwenNet();
			sanwenNet.setUrl("http://www.sanwen.net/");
			sanwenNet.execute();
			List<String> homeUrls = sanwenNet.getHomeListsHref();
			for(String homeUrl : homeUrls){
				CrawlBean crawlBean = new CrawlBean();
				crawlBean.setUrl(homeUrl.trim());
				crawlBean.setSource("散文网");
				crawlBean.setCreateTime(new Date());
				crawlBean.setCreateUserId(OptionUtil.adminUser.getId());
				logger.info(crawlMapper.save(crawlBean));
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("抓取散文网信息出现异常：Crawl()");
		}
	}
}
