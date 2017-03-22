package com.cn.leedane.lucene.solr;

import com.cn.leedane.model.BlogBean;
import com.cn.leedane.service.BlogService;
import com.cn.leedane.springboot.SpringUtil;

/**
 * 博客solr索引实现类
 * @author LeeDane
 * 2016年7月12日 下午3:36:14
 * Version 1.0
 */
public class BlogSolrIndexImpl implements SolrIndex{

	private BlogService<BlogBean> blogService;
	
	@SuppressWarnings("unchecked")
	public BlogSolrIndexImpl(){
		blogService = (BlogService<BlogBean>) SpringUtil.getBean("blogService");
	}
	@Override
	public void query(int start, int rows) {
		
	}

}
