package com.cn.leedane.lucene.solr;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.cn.leedane.model.MoodBean;


/**
 * solr处理类
 * @author LeeDane
 * 2016年7月12日 下午3:36:22
 * Version 1.0
 */
public class MoodSolrHandler implements BaseSolrHandler<MoodBean> {
	public static MoodSolrHandler handler;
	
	public static HttpSolrServer server;
	
	private MoodSolrHandler() {
		buildSolrServerInstance();
	}
	
	@Override
	public String corename() {
		return "mood";
	}
	
	public synchronized static MoodSolrHandler getInstance(){
		if(handler == null){
			synchronized(MoodSolrHandler.class){
				if(handler == null)
					handler = new MoodSolrHandler();
			}
		}
		return handler;
	}
	
	public synchronized void buildSolrServerInstance(){
		if(server == null){
			synchronized(BaseSolrHandler.class){
				server = new HttpSolrServer(BaseSolrHandler.BASE_SOLR_URL +this.corename());
			    server.setMaxRetries(DEFAULT_MAX_RETRIES); // defaults to 0. > 1 not recommended.
			    server.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT); // 5 seconds to establish TCP
			    //正常情况下，以下参数无须设置
			    //使用老版本solrj操作新版本的solr时，因为两个版本的javabin incompatible,所以需要设置Parser
			    server.setParser(new XMLResponseParser());
			    server.setSoTimeout(DEFAULT_SO_TIMEOUT); // socket read timeout
			    server.setDefaultMaxConnectionsPerHost(DEFAULT_MAX_CONNECTIONS_PERHOST);
			    server.setMaxTotalConnections(DEFAULT_MAX_TOTAL_CONNECTIONS);
			    server.setFollowRedirects(FOLLOW_REDIRECTS); // defaults to false
			    // allowCompression defaults to false.
			    // Server side must support gzip or deflate for this to have any effect.
			    server.setAllowCompression(ALLOW_COMPRESSION);

			    //使用ModifiableSolrParams传递参数
//					ModifiableSolrParams params = new ModifiableSolrParams();
//					// 192.168.230.128:8983/solr/select?q=video&fl=id,name,price&sort=price asc&start=0&rows=2&wt=json
//					// 设置参数，实现上面URL中的参数配置
//					// 查询关键词
//					params.set("q", "video");
//					// 返回信息
//					params.set("fl", "id,name,price,score");
//					// 排序
//					params.set("sort", "price asc");
//					// 分页,start=0就是从0开始,rows=5当前返回5条记录,第二页就是变化start这个值为5就可以了
//					params.set("start", 2);
//					params.set("rows", 2);
//					// 返回格式
//					params.set("wt", "javabin");
//					QueryResponse response = server.query(params);

			    //使用SolrQuery传递参数，SolrQuery的封装性更好
			    server.setRequestWriter(new BinaryRequestWriter());
			}
		}
	}
	
	@Override
	public boolean addBean(MoodBean bean){
		try {
			server.addBean(bean);
			server.commit();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}catch (SolrServerException e) {
			e.printStackTrace();
		}  
		return false;
	}
	
	@Override
	public boolean addBeans(List<MoodBean> beans){
		try {
			server.addBeans(beans);
			server.commit();
			return true;
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return false;
	}
	
	/**
	 * 对原始solr查询的基本封装
	 * @param query
	 * @return
	 * @throws SolrServerException
	 */
	@Override
	public QueryResponse query(SolrQuery query) throws SolrServerException{
		return server.query(query);
	}
	
	@Override
	public boolean deleteBean(String id){
		try {
			server.deleteById(id);
			server.commit();
			return true;
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return false;
	}

	@Override
	public boolean deleteBeans(List<String> ids){
		try {
			server.deleteById(ids);
			server.commit();
			return true;
		} catch (SolrServerException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} 
		return false;
	}
	
	@Override
	public boolean updateBean(MoodBean bean) {
		return addBean(bean);
	}

	@Override
	public boolean updateBeans(List<MoodBean> beans) {
		return addBeans(beans);
	}

	@Override
	public boolean optimize() {
		try {
			//对索引进行优化
            server.optimize();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}catch (SolrServerException e) {
			e.printStackTrace();
		}  
		return false;
	}
}
