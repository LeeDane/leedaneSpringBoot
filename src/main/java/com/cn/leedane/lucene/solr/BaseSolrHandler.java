package com.cn.leedane.lucene.solr;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.cn.leedane.model.IDBean;

public interface BaseSolrHandler <T extends IDBean>{
	static final String BASE_SOLR_URL = "http://localhost:8899/solr/";
	static final int DEFAULT_MAX_RETRIES = 5;  /** solr最大重试次数 */  
	static final int DEFAULT_CONNECTION_TIMEOUT = 30000;  /** 设置solr连接超时时间 */  
	static final int DEFAULT_SO_TIMEOUT = 60000; /** 设置solr查询超时时间 */ 
	static final int DEFAULT_MAX_CONNECTIONS_PERHOST = 100; /** solr最大连接数 */  
	static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100; /** solr所有最大连接数 */ 
	static final boolean FOLLOW_REDIRECTS = false; /** solr是否followRedirects */
	static final boolean ALLOW_COMPRESSION = true; /** solr是否允许压缩 */
	
	
	/**
	 * 获取corename,跟solrhome/{modelName}/core.properties的name={}保持一致
	 * @return
	 */
	String corename();
	
	/**
	 * 添加一个对象
	 * @param object
	 * @return
	 * @throws IOException
	 * @throws SolrServerException
	 */
	boolean addBean(T bean);
	
	/**
	 * 添加多个对象
	 * @param beans
	 * @return
	 * @throws IOException
	 * @throws SolrServerException
	 */
	boolean addBeans(List<T> beans);
	
	
	/**
	 * 查询
	 * @param query
	 * @return
	 * @throws SolrServerException
	 */
	QueryResponse query(SolrQuery query) throws SolrServerException ;
	
	/**
	 * 通过ID删除
	 * @param id
	 * @return
	 */
	boolean deleteBean(String id);
	
	/**
	 * 通过ID批量删除
	 * @param id
	 * @return
	 */
	boolean deleteBeans(List<String> ids);
	
	/**
	 * 更新一个对象
	 * @param object
	 * @return
	 * @throws IOException
	 * @throws SolrServerException
	 */
	boolean updateBean(T bean);
	
	/**
	 * 更新多个对象
	 * @param beans
	 * @return
	 * @throws IOException
	 * @throws SolrServerException
	 */
	boolean updateBeans(List<T> beans);
	
	/**
	 * 对该对象的solr进行优化
	 * @return
	 */
	boolean optimize();
	
}
