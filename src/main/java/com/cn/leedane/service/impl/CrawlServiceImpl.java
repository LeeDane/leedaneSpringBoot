package com.cn.leedane.service.impl;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.mapper.CrawlMapper;
import com.cn.leedane.model.CrawlBean;
import com.cn.leedane.service.CrawlService;
import com.cn.leedane.utils.SqlUtil;

/**
 * 爬虫的service实现类
 * @author LeeDane
 * 2016年7月12日 下午1:32:58
 * Version 1.0
 */
@Service("crawlService")
public class CrawlServiceImpl implements CrawlService<CrawlBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private CrawlMapper crawlMapper;

	@SuppressWarnings("unchecked")
	@Override
	public List<CrawlBean> findAllNotCrawl(int limit, String source) {
		return SqlUtil.convertMapsToBeans(CrawlBean.class, this.crawlMapper.findAllNotCrawl(limit, source));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CrawlBean> findAllHotNotCrawl(int limit) {
		return SqlUtil.convertMapsToBeans(CrawlBean.class, this.crawlMapper.findAllHotNotCrawl(limit));
	}

	@Override
	public boolean updateAllScore() {
		return false;
	}

	@Override
	public boolean save(CrawlBean t) {
		return crawlMapper.save(t) > 0 ;
	}

	@Override
	public boolean update(CrawlBean t) {
		return crawlMapper.update(t) > 0;
	}
}
