package com.cn.leedane.controller.manage;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.service.manage.MyToolService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.ResponseModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 我的工具入口接口controller
 * @author LeeDane
 * 2019年11月12日 下午6:30:40
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.myManage)
public class MyToolController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private MyToolService myToolService;

	/**
	 * 抖音视频去水印
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/tool/douyin/remove/watermark", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel removeWatermark(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return myToolService.removeWatermark(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

}
