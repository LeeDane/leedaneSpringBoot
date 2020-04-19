package com.cn.leedane.controller.manage.security;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.ManageBlackBean;
import com.cn.leedane.service.manage.ManageBlackService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.LayuiTableResponseModel;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.ResponseModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 我的黑名单口接口controller
 * @author LeeDane
 * 2020年3月30日 下午7:30:40
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.myManage)
public class ManageBlackController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ManageBlackService<ManageBlackBean> manageBlackService;

	/**
	 * 我的黑名单列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/security/blacks", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public LayuiTableResponseModel blacks(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return manageBlackService.blacks(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

	/**
	 * 添加黑名单
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/security/black", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel addBlack(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		return manageBlackService.add(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

	/**
	 * 黑名单权限授权
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/security/black/{id}/authorization", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel authorization(@PathVariable("userId") long id, HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		return manageBlackService.authorization(id, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

	/**
	 * 移除出黑名单
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/security/black/{id}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public ResponseModel delete(@PathVariable("id") long id, HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		return manageBlackService.delete(id, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}
}
