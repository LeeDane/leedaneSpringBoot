package com.cn.leedane.controller.mall;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.service.mall.S_TaobaoService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import com.google.zxing.WriterException;
import com.taobao.api.ApiException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.Map;

/**
 * 淘宝接口controller(已经废弃，请参考MallSearchController/MallToolController使用）
 * @author LeeDane
 * 2017年12月6日 下午6:30:40
 * version 1.0
 */
@Deprecated
@RestController
@RequestMapping(value = ControllerBaseNameUtil.taobao)
public class TaobaoController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private S_TaobaoService<IDBean> taobaoService;
		
	/**
	 * 查询淘宝商品
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> search(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.putAll(taobaoService.search(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 构建淘宝的分享链接
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/build/share/link", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> buildShareLink(@PathParam("taobaoId") String taobaoId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(taobaoService.buildShare(taobaoId, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}


}
