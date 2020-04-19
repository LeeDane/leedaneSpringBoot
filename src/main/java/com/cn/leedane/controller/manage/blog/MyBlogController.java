package com.cn.leedane.controller.manage.blog;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.service.manage.ManageBlogService;
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
 * 我的博客入口接口controller
 * @author LeeDane
 * 2020年3月30日 下午7:30:40
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.myManage)
public class MyBlogController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ManageBlogService<BlogBean> manageBlogService;

	/**
	 * 我的博客列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/blog/list", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public LayuiTableResponseModel list(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return manageBlogService.list(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

	/**
	 * 我的博客草稿列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/blog/draft", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public LayuiTableResponseModel draft(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return manageBlogService.draft(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

	/**
	 * 我的博客删除
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/blog", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public ResponseModel delete(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request)){
			return message.getModel();
		}
		return manageBlogService.delete(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}
}
