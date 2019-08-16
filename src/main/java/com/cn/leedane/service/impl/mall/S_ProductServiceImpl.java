package com.cn.leedane.service.impl.mall;

import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.handler.mall.S_ProductHandler;
import com.cn.leedane.lucene.solr.ProductSolrHandler;
import com.cn.leedane.mapper.mall.S_ProductMapper;
import com.cn.leedane.mapper.mall.S_StatisticsMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.echart.comm.*;
import com.cn.leedane.model.echart.line.LineOption;
import com.cn.leedane.model.mall.S_BigEventBean;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.model.mall.S_ProductBean;
import com.cn.leedane.model.mall.S_StatisticsBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.mall.MallRoleCheckService;
import com.cn.leedane.service.mall.S_BigEventService;
import com.cn.leedane.service.mall.S_ProductService;
import com.cn.leedane.taobao.api.AlimamaRecommend;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.SolrAddThread;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.MallProductStatisticsType;
import com.cn.leedane.utils.EnumUtil.ProductPlatformType;
import com.taobao.api.ApiException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 购物商品的service的实现类
 * @author LeeDane
 * 2017年11月7日 下午4:48:03
 * version 1.0
 */
@Service("S_ProductService")
public class S_ProductServiceImpl extends MallRoleCheckService implements S_ProductService<S_ProductBean>{
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private S_ProductHandler productHandler;
	
	@Autowired
	private S_ProductMapper productMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private S_BigEventService<S_BigEventBean> bigEventService;
	
	@Autowired
	private S_StatisticsMapper statisticsMapper;
	
	@Override
	public Map<String, Object> save(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		
		logger.info("S_ProductServiceImpl-->save():jo="+jo);
		SqlUtil sqlUtil = new SqlUtil();
		S_ProductBean productBean = (S_ProductBean) sqlUtil.getBean(jo, S_ProductBean.class);

		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(productBean.getTitle()) || StringUtil.isNull(productBean.getDetailSource())){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			return message.getMap();
		}
		
		String returnMsg = "商品已经发布成功！";
		productBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		Date createTime = new Date();
		String detail = MardownUtil.parseHtml(productBean.getDetailSource());
		//取180个作为摘要
		productBean.setDigest(JsoupUtil.getInstance().getDigest(detail, 0, 180));
		productBean.setDetail(detail);
		productBean.setCreateTime(createTime);
		productBean.setCreateUserId(user.getId());
		
		//需要从文中切主图
		if(StringUtil.isNull(productBean.getMainImgLinks())){
			Document h = Jsoup.parse(detail);
			Elements a = h.getElementsByTag("img");
			StringBuffer mainImgs = new StringBuffer();
			if(a != null && a.size() > 0){
				for(int i = 0; i < a.size(); i++){
					if(i < 5)
						mainImgs.append(a.get(i).attr("src") +";");
				}
				//删除最后一个";"
				productBean.setMainImgLinks(mainImgs.deleteCharAt(mainImgs.length() - 1).toString());
			}
		}
		
		if(StringUtil.isNull(productBean.getMainImgLinks())){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.无法从详情中解析到主图.value));
			message.put("responseCode", EnumUtil.ResponseCode.无法从详情中解析到主图.value);
			return message.getMap();
		}
		//测试模拟的店铺ID
		productBean.setShopId(1);
		boolean result = productMapper.save(productBean) > 0;
		if(result){
			//先保存大事件
			Map<String, Object> bigEventMap = new HashMap<String, Object>();
			bigEventMap.put("product_id", productBean.getId());
			bigEventMap.put("text", "新品发布啦！");
			bigEventService.save(JSONObject.fromObject(bigEventMap), user, request);
			
			//加入索引
			new ThreadUtil().singleTask(new SolrAddThread<S_ProductBean>(ProductSolrHandler.getInstance(), productBean));
			
			message.put("isSuccess", true);
			message.put("message", returnMsg);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"发布新的商品:", productBean.getTitle() , "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "save()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
				
		return message.getMap();
	}

	@Override
	public Map<String, Object> statistics(int productId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		
		logger.info("S_ProductServiceImpl-->statistics():productId="+productId);
		S_ProductBean productBean = productHandler.getNormalProductBean(productId);
		if(productBean == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该商品不存在或已被删除.value));
		
		ResponseMap message = new ResponseMap();
		String start = JsonUtil.getStringValue(json, "start");
		String end = JsonUtil.getStringValue(json, "end");
		//验证时间
		if(StringUtil.isNull(start) && StringUtil.isNotNull(end))
			throw new ParameterUnspecificationException("请选择开始日期！");
		
		if(StringUtil.isNotNull(start) && StringUtil.isNull(end))
			throw new ParameterUnspecificationException("请选择结束日期！");
		
		Date now = new Date();
		if(StringUtil.isNull(start) && StringUtil.isNull(end)){
			start = DateUtil.DateToString(DateUtil.getDayBeforeOrAfter(now, -7, true), "yyyy-MM-dd");
			end = DateUtil.DateToString(DateUtil.getYestoday() , "yyyy-MM-dd");
		}
		
		Date startDate = DateUtil.stringToDate(start, "yyyy-MM-dd");
		Date endDate = DateUtil.stringToDate(end, "yyyy-MM-dd");
		
		//校验时间范围是否超过30天，超过将不做处理
		if(StringUtil.isNotNull(start) && StringUtil.isNotNull(end) && !DateUtil.checkDateInRange(startDate, endDate, 30)){
			throw new ParameterUnspecificationException("时间范围超过30天，无法执行计算。");
		}
		List<Date> listDate = DateUtil.findDates(startDate, endDate);
		List<S_StatisticsBean> statisticsBeans = statisticsMapper.getRange(start, end, productId, ConstantsUtil.STATUS_NORMAL);
		Map<String, S_StatisticsBean> mapStatisticsBean = new HashMap<String, S_StatisticsBean>();
		if(CollectionUtil.isNotEmpty(statisticsBeans)){
			for(S_StatisticsBean s: statisticsBeans)
				mapStatisticsBean.put(DateUtil.DateToString(s.getDate(), "yyyy-MM-dd"), s);
		}
		
		message.put("message", getLineOption(listDate, start, end, productId, mapStatisticsBean));
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	private LineOption getLineOption(List<Date> listDate, String start, String end, int productId, Map<String, S_StatisticsBean> mapStatisticsBean){
		
		List<String> legendData = new ArrayList<String>();
		legendData.add(EnumUtil.getMallProductStatisticsType(MallProductStatisticsType.心愿单.value));
		legendData.add(EnumUtil.getMallProductStatisticsType(MallProductStatisticsType.评论数.value));
		legendData.add(EnumUtil.getMallProductStatisticsType(MallProductStatisticsType.访问数.value));
		legendData.add(EnumUtil.getMallProductStatisticsType(MallProductStatisticsType.购买数.value));
		
		List<String> xAxisData = new ArrayList<String>();
		for(Date d: listDate){
			xAxisData.add(DateUtil.DateToString(d, "yyyy-MM-dd"));
		}
		
		List<Series> seriesData = new ArrayList<Series>();
		seriesData.add(getSeriesData(MallProductStatisticsType.心愿单.value, xAxisData, mapStatisticsBean));
		seriesData.add(getSeriesData(MallProductStatisticsType.评论数.value, xAxisData, mapStatisticsBean));
		seriesData.add(getSeriesData(MallProductStatisticsType.访问数.value, xAxisData, mapStatisticsBean));
		seriesData.add(getSeriesData(MallProductStatisticsType.购买数.value, xAxisData, mapStatisticsBean));
		
		LineOption option = new LineOption();
		//option.setTitle(new Title());
		
		Grid grid = new Grid();
		grid.setLeft("0%");
		grid.setRight("5%");
		grid.setBottom("0%");
		grid.setContainLabel(true);
		option.setGrid(grid);
		
		Tooltip tooltip = new Tooltip();
		tooltip.setTrigger("axis");
		option.setTooltip(tooltip);
		
		Legend legend = new Legend();
		legend.setData(legendData);
		option.setLegend(legend);
		
		//option.setToolbox(new Toolbox());
		
		XAxis xAxis = new XAxis();
		xAxis.setType("category");
		xAxis.setBoundaryGap(false);
		xAxis.setData(xAxisData);
		option.setxAxis(xAxis);
		
		YAxis yAxis = new YAxis();
		yAxis.setType("value");
		AxisLabel axisLabel = new AxisLabel();
		axisLabel.setFormatter("{value} 个");
		yAxis.setAxisLabel(axisLabel);
		option.setyAxis(yAxis);
		
		option.setSeries(seriesData);
		return option;
	}
	
	/**
	 * 获取单个模型的数据
	 * @param type
	 * @return
	 */
	private Series getSeriesData(int type, List<String> xAxisData, Map<String, S_StatisticsBean> mapStatisticsBean){
		Series series = new Series();
		series.setName(EnumUtil.getMallProductStatisticsType(type));
		series.setType("line");
		//series.setStack("总量");
		List<Integer> data = new ArrayList<Integer>();
		for(int i = 0; i < xAxisData.size(); i++){
			if(mapStatisticsBean.containsKey(xAxisData.get(i))){
				if(type == MallProductStatisticsType.心愿单.value)
					data.add(mapStatisticsBean.get(xAxisData.get(i)).getWishTotal());
				if(type == MallProductStatisticsType.评论数.value)
					data.add(mapStatisticsBean.get(xAxisData.get(i)).getCommentTotal());
				if(type == MallProductStatisticsType.访问数.value)
					data.add(mapStatisticsBean.get(xAxisData.get(i)).getVisitorTotal());
				if(type == MallProductStatisticsType.购买数.value)
					data.add(mapStatisticsBean.get(xAxisData.get(i)).getBuyTotal());
			}else{
				data.add(0);
			}
		}
		series.setData(data);
		return series;
	}
	
	@Override
	public Map<String, Object> recommend(int productId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		
		logger.info("S_ProductServiceImpl-->recommend():productId="+productId);
		S_ProductBean productBean = productHandler.getNormalProductBean(productId);
		if(productBean == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该商品不存在或已被删除.value));
		
		ResponseMap message = new ResponseMap();
		if(ProductPlatformType.淘宝.value.equals(productBean.getPlatform()) || ProductPlatformType.天猫.value.equals(productBean.getPlatform())){
			AlimamaRecommend recommend = new AlimamaRecommend();
			JSONObject recommendJson;
			try {
				recommendJson = recommend.doParse(Long.parseLong(productBean.getCode() +""));
				message.put("message", getTaoBaoRecommend(recommendJson));
				message.put("isSuccess", true);
			} catch (NumberFormatException e) {
				message.put("message", "获取淘宝/天猫推荐商品失败");
				e.printStackTrace();
			} catch (ApiException e) {
				message.put("message", "获取淘宝/天猫推荐商品失败");
				e.printStackTrace();
			}
		}
		return message.getMap();
	}

	/**
	 * 获取淘宝/天猫的推荐商品
	 * @param recommendJson
	 * @return
	 */
	private Object getTaoBaoRecommend(JSONObject recommendJson) {
		List<S_PlatformProductBean> taobaoItems = new ArrayList<S_PlatformProductBean>();
		if(recommendJson.getJSONObject("tbk_item_recommend_get_response").getJSONObject("results") != null){
			JSONArray items = recommendJson.getJSONObject("tbk_item_recommend_get_response").getJSONObject("results").getJSONArray("n_tbk_item");
			if(items != null && items.size() > 0){
				for(int i = 0; i < items.size(); i++){
					S_PlatformProductBean taobaoProductBean = new S_PlatformProductBean();
					taobaoProductBean.setAuctionId(items.getJSONObject(i).getLong("num_iid"));
					taobaoProductBean.setCashBack(0); //此处获取不到
					taobaoProductBean.setCashBackRatio(0); //此处获取不到
					taobaoProductBean.setImg(items.getJSONObject(i).getString("pict_url"));
					taobaoProductBean.setPlatform(EnumUtil.ProductPlatformType.淘宝.value);
					taobaoProductBean.setPrice(items.getJSONObject(i).getDouble("zk_final_price"));
					taobaoProductBean.setOldPrice(items.getJSONObject(i).getDouble("reserve_price"));
					taobaoProductBean.setTitle(items.getJSONObject(i).getString("title"));
					taobaoProductBean.setShopTitle(null);
					taobaoItems.add(taobaoProductBean);
				}
			}
		}
		return taobaoItems;
	}
}
