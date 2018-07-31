package com.cn.leedane.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Test;

import com.cn.leedane.mapper.BlogMapper;

/**
 * solr相关的测试类
 * 说明：solr继承Ik中文分词器配置连接：http://www.cnblogs.com/rainbowzc/p/3680029.html
 * @author LeeDane
 * 2016年7月12日 下午3:26:15
 * Version 1.0
 */
public class SolrTest extends BaseTest{
	private static Logger logger = Logger.getLogger(SolrTest.class);
	
	@Resource
	private BlogMapper blogMapper;
	
	//public static void main(String[] args) {
		//query();
		//index();
	//}
	
	@Test
	public void index() {
		/*try {
			 List<BlogBean> list = new ArrayList<BlogBean>();
			 
			 list.add(blogMapper.findById(BlogBean.class, 1510));
			 list.add(blogMapper.findById(BlogBean.class, 1511));
			 list.add(blogMapper.findById(BlogBean.class, 1512));
			 
			 List<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
			 SolrInputDocument document;
			 for(int i=0; i < list.size(); i++){
				 document = new SolrInputDocument();
				 document.addField("id", "b"+list.get(i).getId());
				 if(i == 0){
					 document.addField("myname", "我是小明，我是中华人民共和国子民，hello, every one,2238631566122153");
				 }else if(i == 1){
					 document.addField("myname", "hello,我是小红，我来自广东省,my name, 12");
				 }else{
					 document.addField("myname", "我是小理，yes I do !我来自湛江,23443");
				 }
				 
				 document.addField("mymanu", list.get(i).getTitle());
				 documents.add(document);
			 }
			 //SolrHandler.getInstance().getSolrInstance("blog").addField(bean);
			} catch (SolrServerException e) {
			    e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}*/

	}
	
	@Test
	public void delete(){
		String url = "http://localhost:8983/solr/collection1";
	
		HttpSolrServer server = new HttpSolrServer(url);
		List<String> ids = new ArrayList<String>();
		for(int i = 1; i < 9000; i++){
			ids.add("b"+i);
		}
		try {
			server.deleteById(ids);
			server.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static void query(){
		long start = System.currentTimeMillis();
		HttpSolrServer server = new HttpSolrServer("http://localhost:8983/solr");
		server.setMaxRetries(1);
	    server.setMaxRetries(1); // defaults to 0. > 1 not recommended.
	    server.setConnectionTimeout(500); // 5 seconds to establish TCP
	    //正常情况下，以下参数无须设置
	    //使用老版本solrj操作新版本的solr时，因为两个版本的javabin incompatible,所以需要设置Parser
	    server.setParser(new XMLResponseParser());
	    server.setSoTimeout(1000); // socket read timeout
	    server.setDefaultMaxConnectionsPerHost(100);
	    server.setMaxTotalConnections(100);
	    server.setFollowRedirects(false); // defaults to false
	    // allowCompression defaults to false.
	    // Server side must support gzip or deflate for this to have any effect.
	    server.setAllowCompression(true);

	    //使用ModifiableSolrParams传递参数
//			ModifiableSolrParams params = new ModifiableSolrParams();
//			// 192.168.230.128:8983/solr/select?q=video&fl=id,name,price&sort=price asc&start=0&rows=2&wt=json
//			// 设置参数，实现上面URL中的参数配置
//			// 查询关键词
//			params.set("q", "video");
//			// 返回信息
//			params.set("fl", "id,name,price,score");
//			// 排序
//			params.set("sort", "price asc");
//			// 分页,start=0就是从0开始,rows=5当前返回5条记录,第二页就是变化start这个值为5就可以了
//			params.set("start", 2);
//			params.set("rows", 2);
//			// 返回格式
//			params.set("wt", "javabin");
//			QueryResponse response = server.query(params);

	    //使用SolrQuery传递参数，SolrQuery的封装性更好
	    server.setRequestWriter(new BinaryRequestWriter());
	    SolrQuery query = new SolrQuery();
	    query.setQuery("search");
	    query.setFields("name", "manu");
	    //query.setSort("price", ORDER.asc);
	    query.setStart(0);
	    query.setRows(2);
//			query.setRequestHandler("/select");
	    QueryResponse response;
		try {
			response = server.query( query );
			// 搜索得到的结果数
			logger.info("Find:" + response.getResults().getNumFound());
		    // 输出结果
		    int iRow = 1;
		    for (SolrDocument doc : response.getResults()) {
		    	logger.info("----------" + iRow + "------------");
		      //logger.info("id: " + doc.getFieldValue("id").toString());
		    	logger.info("name: " + doc.getFieldValue("name").toString());
		      /*logger.info("price: "
		          + doc.getFieldValue("price").toString());
		      logger.info("score: " + doc.getFieldValue("score"));*/
		      iRow++;
		    }
		} catch (SolrServerException e) {
			e.printStackTrace();
		}  
		
		long end = System.currentTimeMillis();
		logger.info("总计耗时："+(end -start)+"毫秒");
	}

}
