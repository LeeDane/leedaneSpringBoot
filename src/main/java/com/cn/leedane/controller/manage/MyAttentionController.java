package com.cn.leedane.controller.manage;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.service.manage.ManageMyService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.LayuiTableResponseModel;
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
 * 我的关注入口接口controller
 * @author LeeDane
 * 2019年11月12日 下午6:30:40
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.myManage)
public class MyAttentionController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ManageMyService<IDBean> manageMyService;

	/**
	 * 我的关注历史
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/all/my/attention", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public LayuiTableResponseModel attentions(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request));

		return manageMyService.attentions(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

	/**
	 * 删除关注历史
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/all/my/attention", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public ResponseModel delete(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request)){
			return message.getModel();
		}
		return manageMyService.deleteAttention(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}
}
