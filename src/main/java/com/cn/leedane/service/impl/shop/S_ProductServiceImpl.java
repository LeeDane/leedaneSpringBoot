package com.cn.leedane.service.impl.shop;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.handler.shop.S_ProductHandler;
import com.cn.leedane.lucene.solr.ProductSolrHandler;
import com.cn.leedane.mapper.shop.S_ProductMapper;
import com.cn.leedane.mapper.shop.S_StatisticsMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.shop.S_BigEventBean;
import com.cn.leedane.model.shop.S_ProductBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.shop.S_BigEventService;
import com.cn.leedane.service.shop.S_ProductService;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.SolrAddThread;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.JsoupUtil;
import com.cn.leedane.utils.MardownUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 购物商品的service的实现类
 * @author LeeDane
 * 2017年11月7日 下午4:48:03
 * version 1.0
 */
@Service("S_ProductService")
public class S_ProductServiceImpl extends AdminRoleCheckService implements S_ProductService<S_ProductBean>{
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
			HttpServletRequest request) {
		
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
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"发布新的商品:", productBean.getTitle() , "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "save()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return message.getMap();
	}

	@Override
	public Map<String, Object> statistics(int productId, JSONObject json, UserBean user,
			HttpServletRequest request) {
		
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
			end = DateUtil.DateToString(now, "yyyy-MM-dd");
		}
		
		//校验时间范围是否超过30天，超过将不做处理
		if(StringUtil.isNotNull(start) && StringUtil.isNotNull(end) && DateUtil.checkDateInRange(DateUtil.stringToDate(end, "yyyy-MM-dd HH:mm:ss"), DateUtil.stringToDate(start, "yyyy-MM-dd HH:mm:ss"), 30)){
			throw new ParameterUnspecificationException("时间范围超过30天，无法执行计算。");
		}
		
		statisticsMapper.getRange(start, end, productId);
		return message.getMap();
	}
}
