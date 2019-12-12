package com.cn.leedane.controller.mall;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.service.mall.MallToolService;
import com.cn.leedane.service.mall.S_TaobaoService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import com.google.zxing.WriterException;
import com.jd.open.api.sdk.JdException;
import com.taobao.api.ApiException;
import jdk.nashorn.internal.runtime.regexp.RegExp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.Map;

/**
 * 商城工具接口controller
 * @author LeeDane
 * 2019年12月6日 下午6:30:40
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.mall_tool)
public class MallToolController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private MallToolService<IDBean> mallToolService;

	/**
	 * 链接转化
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/link/transform/{productId}", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> tranform(@PathVariable("productId") String productId, HttpServletRequest request) throws ApiException, WriterException, JdException, PddException {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		message.putAll(mallToolService.transform(productId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}


	/**
	 * 解析地址中的id字段（考虑到地址的长度和编码方式，这里通过post请求的方式）
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/parse/url", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> parseUrlGetId(HttpServletRequest request)throws JdException, ApiException {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		message.putAll(mallToolService.parseUrlGetId(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
