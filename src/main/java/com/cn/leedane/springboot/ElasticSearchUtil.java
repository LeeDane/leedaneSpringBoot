package com.cn.leedane.springboot;

import com.cn.leedane.controller.RoleController;
import com.cn.leedane.exception.MustLoginException;
import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.mapper.SqlSearchMapper;
import com.cn.leedane.model.ElasticSearchRequestBean;
import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * ElasticSearch相关的工具类
 * 从6.0版本开始，，一个ElasticSearch索引中，只有1个类型
 * @author LeeDane
 * 2019年7月4日 上午18:30:09
 * Version 1.0
 */

@Component
public class ElasticSearchUtil {

	private Logger logger = Logger.getLogger(getClass());

	//private static ElasticSearchUtil mLuceneUtil;

	@Autowired
	private volatile TransportClient transportClient;

	/*public synchronized TransportClient getTransportClient() {
		if(null == transportClient)
			transportClient = (TransportClient) SpringUtil.getBean("transportClient");

		return transportClient;
	}*/

	@Autowired
	private SqlSearchMapper sqlSearchMapper;

	public SqlSearchMapper getSqlSearchMapper() {
		if(null == sqlSearchMapper)
			sqlSearchMapper = (SqlSearchMapper) SpringUtil.getBean("sqlSearchMapper");
		return sqlSearchMapper;
	}

	/*private ElasticSearchUtil(){

	}*/
	
	/*public static synchronized ElasticSearchUtil getInstance(){
		if(null == mLuceneUtil){
			synchronized (ElasticSearchUtil.class) {
				if (mLuceneUtil == null)
					mLuceneUtil = new ElasticSearchUtil();
			}
		}
		return mLuceneUtil;
	}*/

	/**
	 *
	 * @param table
	 * @param id
	 * @param fields
	 * @return
	 */
	/**
	 * 添加索引
	 * @param table 表名称
	 * @param id 主键ID
	 * @param fields 相应的字段
	 * @param mapping 相应的mapping（只有在索引不存在的情况下才操作）
	 * @return
	 */
	public boolean add(String table, long id, HashMap<String, Object> fields, XContentBuilder mapping){
		try {

			//索引不存在就重新创建
			if(!indexExists(table) && mapping != null){
				CreateIndexRequestBuilder prepareCreate =transportClient.admin().indices().prepareCreate(getDefaultIndexName(table));
				prepareCreate.addMapping(table, mapping).execute().actionGet();
			}

			IndexResponse response = transportClient.prepareIndex(getDefaultIndexName(table), table, id +"")
					.setSource(fields)
					.get();
			logger.info("添加索引数据的结构："+ fields.toString());
			boolean success = (response.getResult() == DocWriteResponse.Result.CREATED || response.getResult() == DocWriteResponse.Result.UPDATED );
			if(success){
				//更新数据库的es_index字段
				return SqlUtil.getBooleanByList(getSqlSearchMapper().executeSQL("update "+ table +" set es_index = ? where id = ?", true, id));
			}
		} catch (Exception e) {
			logger.error("添加索引失败：", e);
		}
		return false;
	}

	/**
	 * 添加索引
	 * @param table 表名称
	 * @param id  主键ID
	 * @param fields 相应需要更新的字段
	 * @return
	 */
	public boolean update(String table, int id, XContentBuilder fields){
		try {
			UpdateResponse response = transportClient.prepareUpdate(getDefaultIndexName(table), table, id +"")
					.setDoc(fields)
					.get();
			return response.status() == RestStatus.CREATED;
		} catch (Exception e) {
			logger.error("修改索引失败：", e);
			return false;
		}
	}

	/**
	 * 判断index是否存在
	 * @param table
	 * @return
	 */
	public boolean indexExists(String table){
		IndicesExistsRequest request = new IndicesExistsRequest(getDefaultIndexName(table));
		IndicesExistsResponse response = transportClient.admin().indices().exists(request).actionGet();
		if (response.isExists()) {
			return true;
		}
		return false;
	}

	/**
	 * 获取文档相当于读取数据库的一行数据
	 * @param table 表名称
	 * @param id 主键ID
	 * @return
	 */
	public String getIndex(String table, int id){
		GetResponse getresponse = transportClient.prepareGet(getDefaultIndexName(table), table, id + "").execute().actionGet();
		return getresponse.getSourceAsString();
	}

	/**
	 * 相当于删除一行数据
	 * @param table 表名称
	 * @param id 主键ID
	 * @return
	 */
	public boolean delete(String table, long id){
		try {
			DeleteResponse deleteresponse = transportClient.prepareDelete(getDefaultIndexName(table), table,id + "").execute().actionGet();
			System.out.println(deleteresponse.getVersion());
			return deleteresponse.status() == RestStatus.OK;
		} catch (Exception e) {
			logger.error("删除索引数据失败：", e);
			return false;
		}
	}

	/**
	 * 通过用户IDs删除
	 * @param table
	 * @param field
	 * @param userId
	 * @return 返回删除成功的数量，默认是0
	 */
	public long deleteByUser(String table, String field, long userId) {

		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		String[] fields = field.split(",");
		if(fields.length > 0){
			for(String f: fields)
				queryBuilder.must(QueryBuilders.termQuery(f, userId));

			BulkByScrollResponse response =
					DeleteByQueryAction.INSTANCE.newRequestBuilder(transportClient)
							.filter(queryBuilder)
							.source(getDefaultIndexName(table))
							.get();
			return response.getDeleted();
		}
		return 0;
	}

	/**
	 * 删除索引及该索引所管辖的记录
	 * @param table 数据表名称
	 * @return
	 */
	public boolean deleteIndex(String table){
		try {
			//删除所有记录
			DeleteIndexResponse deleteIndexResponse = transportClient.admin().indices().prepareDelete(getDefaultIndexName(table)).execute().actionGet();
			System.out.println(deleteIndexResponse.isAcknowledged());
			return deleteIndexResponse.isAcknowledged();
		} catch (Exception e) {
			logger.error("删除索引失败：", e);
			return false;
		}
	}

	/**
	 * 获取到所有的索引
	 * @return
	 */
	public String[] getAllIndex(){
		ClusterStateResponse response = transportClient.admin().cluster().prepareState().execute().actionGet();
		//获取所有索引
		String[] indexs=response.getState().getMetaData().getConcreteAllIndices();
		for (String index : indexs) {
			System.out.println( index + " delete" );//
		}
		return indexs;
	}

	/**
	 * 获取查询结果
	 * @param elasticSearchRequestBean
	 * @return
	 */
	public SearchResponse getResults(ElasticSearchRequestBean elasticSearchRequestBean){
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

		BoolQueryBuilder boolQueryField = QueryBuilders.boolQuery();
		String[] searchFields = elasticSearchRequestBean.getSearchFields();
		String searchKey = elasticSearchRequestBean.getSearchKey();

		//判断是否是管理员
		Subject currentUser = SecurityUtils.getSubject();
		boolean isAdmin = currentUser.hasRole(RoleController.ADMIN_ROLE_CODE);
		//对操作日志查询，不需要关键字
		if(StringUtil.isNotNull(searchKey)) {
			for (int i = 0; searchFields.length > 0 && i < searchFields.length; i++) {
				switch (elasticSearchRequestBean.getAccurate()) {
					case 0: //模糊查询
						//管理员才能使用： 判断是否含有特殊字符，有特殊字符用
						if(isAdmin && StringUtil.isSpecialChar(searchKey)){
							boolQueryField.should(QueryBuilders.wildcardQuery(searchFields[i], searchKey));
						}else{
							boolQueryField.should(QueryBuilders.matchQuery(searchFields[i], searchKey)/*.prefixLength(4)*/);
						}
						break;
					case 1: //精确查询
						boolQueryField.should(QueryBuilders.termQuery(searchFields[i] + ".keyword", searchKey));
						break;
					case 2: //前缀查询
						boolQueryField.should(QueryBuilders.prefixQuery(searchFields[i], searchKey));
						break;
					default:
						break;
				}
			}
		}else{
			if(elasticSearchRequestBean.getSearchType() != ConstantsUtil.SEARCH_TYPE_OPERATE_LOG)
				throw new ParameterUnspecificationException("必须输入检索关键字");
		}

		//添加固定字段查询条件
		boolQuery.must(boolQueryField);
		BoolQueryBuilder boolQueryStatus = QueryBuilders.boolQuery();
		//所有用户可以查询共享状态的心情
		boolQueryStatus.should(QueryBuilders.termQuery("status", ConstantsUtil.STATUS_SHARE));
		if(null == elasticSearchRequestBean.getUser()) { //未登录用户只能查询正常状态下的信息{
			//操作日志查询只能是登录用户才可以
			if(elasticSearchRequestBean.getSearchType() == ConstantsUtil.SEARCH_TYPE_OPERATE_LOG)
				throw new MustLoginException("请先登录才能执行下一步操作");
		}else{
			//登录用户还可以查询正常状态的心情
			boolQueryStatus.should(QueryBuilders.termQuery("status", ConstantsUtil.STATUS_NORMAL));

			//判断是否是管理员，管理员可以查询所有的数据(无论状态)
			//非管理员，只能查询公开的或者自己的私有的
			if(!currentUser.hasRole(RoleController.ADMIN_ROLE_CODE)){
				//非日志可以看自己私有的
				if(elasticSearchRequestBean.getSearchType() != ConstantsUtil.SEARCH_TYPE_OPERATE_LOG){
					BoolQueryBuilder boolQuerySelf = QueryBuilders.boolQuery();
					boolQuerySelf.must(QueryBuilders.termQuery("status", ConstantsUtil.STATUS_SELF));
					boolQuerySelf.must(QueryBuilders.termQuery("create_user_id", elasticSearchRequestBean.getUser().getId()));
					boolQueryStatus.should(boolQuerySelf);
				}else{
					//非管理员，日志只能看自己正常状态的
					boolQueryStatus.must(QueryBuilders.termQuery("create_user_id", elasticSearchRequestBean.getUser().getId()));
				}
			}else{
				//管理员可以查看所有人私有的数据
				boolQueryStatus.should(QueryBuilders.termQuery("status", ConstantsUtil.STATUS_SELF));
			}
		}
		boolQuery.must(boolQueryStatus);
		//判断开始和结束日期
		if(StringUtil.isNotNull(elasticSearchRequestBean.getStartDate()) || StringUtil.isNotNull(elasticSearchRequestBean.getEndDate())){
			RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(elasticSearchRequestBean.getSearchType() == ConstantsUtil.SEARCH_TYPE_USER ? "register_time" : "modify_time");
			if(StringUtil.isNotNull(elasticSearchRequestBean.getStartDate()))
				rangeQueryBuilder.from(elasticSearchRequestBean.getStartDate() +" 00:00:00");

			if(StringUtil.isNotNull(elasticSearchRequestBean.getEndDate()))
				rangeQueryBuilder.to(elasticSearchRequestBean.getEndDate() +" 23:59:59");
			boolQuery.must(rangeQueryBuilder);
		}

		SearchRequestBuilder builder = transportClient.prepareSearch(getDefaultIndexName(elasticSearchRequestBean.getTable())).setTypes(elasticSearchRequestBean.getTable())
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(boolQuery)
				.setFrom(elasticSearchRequestBean.getStart());

		if(elasticSearchRequestBean.getShowFields() != null && elasticSearchRequestBean.getShowFields().length > 0)
			builder.setFetchSource(elasticSearchRequestBean.getShowFields(), null);


		//使用默认标签包裹
		//highlightBuilder.highlighterType("Unified");
		//设置高亮的字段名, 设置高亮的字段
		String[] highlightFields = elasticSearchRequestBean.getHighlightFields();
		if(highlightFields != null){
			//3.设置查询高亮显示--使用默认后缀
			HighlightBuilder highlightBuilder = new HighlightBuilder();
			String preTags = "<span style=\"color: #ff0000\">";
			String postTags = "</span>";
			//设置前缀-后缀
			highlightBuilder.preTags(preTags);
			highlightBuilder.postTags(postTags);
			for(int i= 0; i< highlightFields.length; i++){
				highlightBuilder.field(highlightFields[i]);
			}
			highlightBuilder.requireFieldMatch(false);
			//下面这两项,如果你要高亮如文字内容等有很多字的字段,必须配置,不然会导致高亮不全,文章内容缺失等
			highlightBuilder.fragmentSize(80000); //最大高亮分片数
			builder.highlighter(highlightBuilder);
		}


		//控制是否新增排序
		if(StringUtil.isNotNull(elasticSearchRequestBean.getSortField()))
			builder.addSort(elasticSearchRequestBean.getSortField(), elasticSearchRequestBean.getOrder());

		//获取数量小于0的情况下设置为0
		if(elasticSearchRequestBean.getNumber() < 0)
			//为0表示只获取统计数，不获取详细的文档数据
			builder.setSize(0);
		else
			builder.setSize(elasticSearchRequestBean.getNumber());
		logger.info(builder.toString());

		SearchResponse response = builder.get();

		/*List result = new ArrayList<Map<String, Object>>();
		for (SearchHit hit : response.getHits()) {
			Map<String, HighlightField> fieldMap = hit.getHighlightFields();
			result.add(hit.getSourceAsMap());
		}*/

		logger.info(boolQuery.toString());
		return response;
	}


    /**
     * 为了方便管理，这里统一可以获取系统默认的索引名称
	 * @param table
     * @return
     */
	public static String getDefaultIndexName(String table){
		if(StringUtil.isNotNull(table))
			return "index_"+ table;

		return LeedanePropertiesConfig.newInstance().getString("constant.es.default.index.name");
	}
}
