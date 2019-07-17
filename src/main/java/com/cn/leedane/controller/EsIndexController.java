package com.cn.leedane.controller;


import com.alibaba.dubbo.common.utils.StringUtils;
import com.cn.leedane.model.ResultBean;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: Administrator
 * @Date: 2018/8/21 07
 * @Description:
 */
//@Api(value = "Index", tags = "索引")
@RestController
@RequestMapping("es/index")
public class EsIndexController {

    private final String INDEX = "index";
    private final String TYPE = "type";
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TransportClient transportClient;

    public static final String PEOPLE_INDEX = "people";
    public static final String PEOPLE_TYPE_MAN = "man";
//    @ApiOperation(value = "结构化创建索引")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "index", value = "索引名", required = true, dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "type", value = "类型", required = true, dataType = "Integer", paramType = "query"),
//            @ApiImplicitParam(name = "fields", value = "结构化索引字段名,不定参数，传入的时候参数名为索引字段名，值为对应的数据类型")
//    })

    // 增加接口
    @RequestMapping(value = "/add/people/man" , method = RequestMethod.GET)
    public ResponseEntity add() {

        try {

            //删除所有记录
           /* DeleteIndexResponse deleteIndexResponse = transportClient.admin().indices().prepareDelete("index_t_blog").execute().actionGet();
            System.out.println(deleteIndexResponse.isAcknowledged());*/

            XContentBuilder content = XContentFactory.jsonBuilder().startObject()
                    .field("title", "我们的孩子吧的基督教的")
                    .field("digest", "我们的孩子吧的基督教的")
                    .field("content", "我们的孩子吧的基督教的")
                    .endObject();

            IndexResponse response = transportClient.prepareIndex("index_t_blog", "t_blog", "22556")
                    .setSource(content)
                    .get();
            return new ResponseEntity(response.getId(), HttpStatus.OK);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // 复合查询
    @RequestMapping(value = "/query/people/man" , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity query() {
        GetResponse getresponse = transportClient.prepareGet("index_t_blog", "t_blog", "22555").execute().actionGet();
        System.out.println("index:"+getresponse.getSourceAsString());

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        String title = "*孩子*";
       /* if (title != null) {
            boolQuery.should(QueryBuilders.wildcardQuery("title", title));
        }*/

       /* int gtAge = 24;
        int ltAge = 29;
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("age").from(gtAge);
        if (ltAge > 0) {
            rangeQuery.to(ltAge);
        }
        boolQuery.filter(rangeQuery);*/

       /* SearchRequestBuilder builder = transportClient.prepareSearch("index_t_blog").setTypes("t_blog")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQuery)
                .setFrom(0)
                .setSize(10);*/

        boolQuery.should(QueryBuilders.matchQuery("title", "我们"));
        SearchResponse searchResponse = transportClient.prepareSearch().setIndices("index_t_blog").setTypes("t_blog")
//                .setSearchType(SearchType.DEFAULT)
                .setQuery(boolQuery)
                .setFrom(0)
                .setSize(10)
                .get();

        logger.debug(boolQuery.toString());

     /*   SearchResponse response = builder.get();*/

        List result = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : searchResponse.getHits()) {
            result.add(hit.getSourceAsMap());
        }

        return new ResponseEntity(result, HttpStatus.OK);

    }


    @RequestMapping(value = "/create" , method = RequestMethod.GET)
    public ResponseEntity createIndex(@RequestParam Map<String,String> param){
        ResultBean resultBean = new ResultBean();
        String index = null;
        String type = null;
        List<String> fieldList = new ArrayList<String>();

        try {
            XContentBuilder content = XContentFactory.jsonBuilder().startObject()
                    .field("name", "leedane")
                    .field("age", 19)
                    .field("work", "软件工程师")
                    .endObject();
            String id = "1022";

            IndexResponse result = transportClient.prepareIndex("person1", "man",id).setSource(content).get();
            return new ResponseEntity(result.getId(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("错误：", e);
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        /*Set<Map.Entry<String, String>> set = param.entrySet();
        for (Map.Entry<String, String> entry: set) {
            String key = entry.getKey();
            if(key.trim().equals(INDEX)){
                index = entry.getValue();
            }else if(key.trim().equals(TYPE)){
                type = entry.getValue();
            }else{
                fieldList.add(key);
            }
        }


        if(StringUtils.isBlank(index) || StringUtils.isBlank(type)){
            resultBean.setSuccess(false);
            resultBean.setMsg("参数错误！");
            return resultBean;
        }*/
        /*try {
            XContentBuilder settings = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("number_of_shards",6)
                    .field("number_of_replicas",1)
                    .startObject("analysis").startObject("analyzer").startObject("ik")
                    .field("tokenizer","ik_max_word")
                    .endObject().endObject().endObject()
                    .endObject();
            XContentBuilder mapping = XContentFactory.jsonBuilder();
            mapping.startObject().field("dynamic","strict").startObject("properties");
            for (int i = 0,j = fieldList.size(); i < j; i++) {
                String field = fieldList.get(i);
                String fieldType = param.get(field);
                mapping.startObject(field).field("type",fieldType);
                if(fieldType.trim().equals("date")){
                    mapping.field("format","yyyy-MM-dd HH:mm:ss || yyyy-MM-dd ");
                }
                mapping.endObject();
            }
            mapping.endObject().endObject();
            CreateIndexRequest createIndexRequest = Requests.createIndexRequest(index).settings(settings).mapping(type,mapping);
            CreateIndexResponse response = transportClient.admin().indices().create(createIndexRequest).get();
            logger.info("建立索引映射成功：" + response.isAcknowledged());
            resultBean.setSuccess(true);
            resultBean.setMsg("创建索引成功！");
        } catch (Exception e) {
            resultBean.setSuccess(false);
            resultBean.setMsg("创建索引失败！");
            logger.error("创建索引失败！要创建的索引为{}，文档类型为{}，异常为：",index,type,e.getMessage(),e);
        }
        return resultBean;*/
    }

    /*@ApiOperation(value = "删除索引")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "index", value = "索引名", required = true, dataType = "String", paramType = "query"),
    })*/
    @RequestMapping(value = "/delete" , method = RequestMethod.GET)
    public ResultBean deleteIndex(String index){
        index = PEOPLE_INDEX;
        ResultBean resultBean = new ResultBean();
        try {
            DeleteIndexRequest deleteIndexRequest = Requests.deleteIndexRequest(index);
            DeleteIndexResponse response = transportClient.admin().indices().delete(deleteIndexRequest).get();
            logger.info("删除索引结果:{}",response.isAcknowledged());
            resultBean.setSuccess(response.isAcknowledged());
            resultBean.setMsg(response.isAcknowledged() ? "删除索引成功！" : "删除索引失败！");
        } catch (Exception e) {
            resultBean.setSuccess(false);
            resultBean.setMsg("创建索引失败！");
            logger.error("删除索引失败！要删除的索引为{}，异常为：",index,e.getMessage(),e);
        }
        return resultBean;
    }
}
