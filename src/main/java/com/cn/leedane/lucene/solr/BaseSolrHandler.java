package com.cn.leedane.lucene.solr;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.cn.leedane.model.IDBean;

public abstract class BaseSolrHandler <T extends IDBean>{
	protected static final String BASE_SOLR_URL = "http://localhost:8899/solr/";
	
	/**
	 * 获取corename
	 * @return
	 */
	protected abstract String corename();
	
	/**
	 * 添加一个对象
	 * @param object
	 * @return
	 * @throws IOException
	 * @throws SolrServerException
	 */
	protected abstract boolean addBean(T bean);
	
	/**
	 * 添加多个对象
	 * @param beans
	 * @return
	 * @throws IOException
	 * @throws SolrServerException
	 */
	protected abstract boolean addBeans(List<T> beans);
	
	
	/**
	 * 查询
	 * @param query
	 * @return
	 * @throws SolrServerException
	 */
	protected abstract QueryResponse query(SolrQuery query) throws SolrServerException ;
	
	/**
	 * 通过ID删除
	 * @param id
	 * @return
	 */
	protected abstract boolean deleteBean(String id);
	
	/**
	 * 通过ID批量删除
	 * @param id
	 * @return
	 */
	protected abstract boolean deleteBeans(List<String> ids);
	
	/**
	 * 更新一个对象
	 * @param object
	 * @return
	 * @throws IOException
	 * @throws SolrServerException
	 */
	protected abstract boolean updateBean(T bean);
	
	/**
	 * 更新多个对象
	 * @param beans
	 * @return
	 * @throws IOException
	 * @throws SolrServerException
	 */
	protected abstract boolean updateBeans(List<T> beans);
	
}
