package com.cn.leedane.lucene.solr;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.cn.leedane.model.UserBean;


/**
 * solr处理类
 * @author LeeDaneO
 * 2016年7月12日 下午3:36:22
 * Version 1.0
 */
public class UserSolrHandler implements BaseSolrHandler<UserBean> {
	private Logger logger = Logger.getLogger(getClass());
	
	public volatile static UserSolrHandler handler;
	
	public static HttpSolrServer server;
	
	private UserSolrHandler() {
		buildSolrServerInstance();
	}
	
	@Override
	public String corename() {
		return "user";
	}
	
	public synchronized static UserSolrHandler getInstance(){
		if(handler == null){
			synchronized(UserSolrHandler.class){
				if(handler == null)
					handler = new UserSolrHandler();
			}
		}
		return handler;
	}
	
	/**
	 * 构建HttpSolrServer实例
	 */
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
	public boolean addBean(UserBean bean){
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
	public boolean addBeans(List<UserBean> beans){
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
		logger.info(query.toString()); 
		/*HttpClient client = server.getHttpClient();
		
		// 实例化HTTP方法  
        HttpGet request = new HttpGet();  
        BufferedReader in = null; 
		try {
			request.setURI(new URI(BASE_SOLR_URL + "user/select?" + "q=*%3A*&wt=j&indent=true&_=1484710822264"));
			HttpResponse response = client.execute(request);
			
			in = new BufferedReader(new InputStreamReader(response.getEntity()  
                    .getContent()));  
            StringBuffer sb = new StringBuffer("");  
            String line = "";  
            String NL = System.getProperty("line.separator");  
            while ((line = in.readLine()) != null) {  
                sb.append(line + NL);  
            }  
            in.close();  
            logger.info("返回结果：" +sb.toString()); 
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		} finally{
			if (in != null) {  
                try {  
                    in.close();// 最后要关闭BufferedReader  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
		}*/
        
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
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return false;
	}
	
	@Override
	public boolean updateBean(UserBean bean) {
		return addBean(bean);
	}

	@Override
	public boolean updateBeans(List<UserBean> beans) {
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
