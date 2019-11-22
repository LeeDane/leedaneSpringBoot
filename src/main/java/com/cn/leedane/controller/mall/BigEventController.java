package com.cn.leedane.controller.mall;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.mall.S_BigEventBean;
import com.cn.leedane.service.mall.S_BigEventService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 大事件接口controller
 * @author LeeDane
 * 2017年11月10日 下午1:50:48
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.mall)
public class BigEventController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private S_BigEventService<S_BigEventBean> bigEventService;
		
	/**
	 * 获取对象的主要评论列表(分页获取数据)
	 * @return
	 */
	@RequestMapping(value = "/product/{productId}/bigEvents", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(@PathVariable("productId") long productId , HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		message.putAll(bigEventService.paging(productId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
}
