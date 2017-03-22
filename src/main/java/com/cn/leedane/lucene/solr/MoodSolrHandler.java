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
public class MoodSolrHandler extends BaseSolrHandler<MoodBean> {
public static MoodSolrHandler handler;
	
	public static HttpSolrServer server;
	
	private MoodSolrHandler() {
		buildSolrServerInstance();
	}
	
	@Override
	protected String corename() {
		return "mood";
	}
	
	public synchronized static MoodSolrHandler getInstance(){
		if(handler == null){
			synchronized(MoodSolrHandler.class){
				handler = new MoodSolrHandler();
			}
		}
		return handler;
	}
	
	public synchronized void buildSolrServerInstance(){
		if(server == null){
			synchronized(BaseSolrHandler.class){
				server = new HttpSolrServer(BaseSolrHandler.BASE_SOLR_URL +this.corename());
			    server.setMaxRetries(5); // defaults to 0. > 1 not recommended.
			    server.setConnectionTimeout(30000); // 5 seconds to establish TCP
			    //正常情况下，以下参数无须设置
			    //使用老版本solrj操作新版本的solr时，因为两个版本的javabin incompatible,所以需要设置Parser
			    server.setParser(new XMLResponseParser());
			    server.setSoTimeout(60000); // socket read timeout
			    server.setDefaultMaxConnectionsPerHost(100);
			    server.setMaxTotalConnections(100);
			    server.setFollowRedirects(false); // defaults to false
			    // allowCompression defaults to false.
			    // Server side must support gzip or deflate for this to have any effect.
			    server.setAllowCompression(true);

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
			//对索引进行优化
            server.optimize();
			server.commit();
			return true;
		} catch (IOException | SolrServerException e) {
			e.printStackTrace();
		}  
		return false;
	}
	
	@Override
	public boolean addBeans(List<MoodBean> beans){
		try {
			server.addBeans(beans);
			//对索引进行优化
            server.optimize();
			server.commit();
			return true;
		} catch (SolrServerException | IOException e) {
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
			//对索引进行优化
            server.optimize();
			server.commit();
			return true;
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}  
		return false;
	}

	@Override
	public boolean deleteBeans(List<String> ids){
		try {
			server.deleteById(ids);
			//对索引进行优化
            server.optimize();
			server.commit();
			return true;
		} catch (SolrServerException | IOException e) {
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
}
