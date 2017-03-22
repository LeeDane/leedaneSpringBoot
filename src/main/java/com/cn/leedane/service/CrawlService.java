package com.cn.leedane.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.CrawlBean;
import com.cn.leedane.model.IDBean;

/**
 * 爬虫service的接口类
 * @author LeeDane
 * 2016年7月12日 上午11:32:47
 * Version 1.0
 */
@Transactional("txManager")
public interface CrawlService <T extends IDBean>{
	
	/**
	 * 找出所有没有爬取的数据链接
	 * @param limit 现在的数量,等于0表示全部
	 * @param source 来源，空表示全部
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<CrawlBean> findAllNotCrawl(int limit, String source);
	
	/**
	 * 找出所有没有爬取的热门数据链接,根据score从大到小
	 * @param limit 现在的数量,等于0表示全部
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<CrawlBean> findAllHotNotCrawl(int limit);
	
	/**
	 * 更新所有的score(暂时未实现)
	 * @return
	 */
	public boolean updateAllScore();
	
	/**
	 * 基础的保存实体的方法
	 * @param t
	 * @return
	 */
	public boolean save(CrawlBean t);
	
	/**
	 * 基础更新实体的方法
	 * @param t
	 * @return
	 */
	public boolean update(CrawlBean t);
}
