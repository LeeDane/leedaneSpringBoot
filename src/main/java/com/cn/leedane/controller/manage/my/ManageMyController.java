package com.cn.leedane.controller.manage.my;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.service.manage.ManageMyService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 商城全局搜索统一入口接口controller
 * @author LeeDane
 * 2019年11月12日 下午6:30:40
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.myManage)
public class ManageMyController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ManageMyService<IDBean> manageMyService;

	/**
	 * 绑定电子邮箱
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/my/email/bind", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> bindEmail(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);

		message.putAll(manageMyService.bindEmail(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}

	/**
	 * 绑定手机
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/my/phone/bind", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> phoneGetCode(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);

		message.putAll(manageMyService.bindPhone(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}

	/**
	 * 第三方授权解绑
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/my/third/unbind", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> thirdUnBind(@RequestParam("id") long id, HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();

		message.putAll(manageMyService.thirdUnBind(id, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}

	/**
	 * 保存标签
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/my/tags/save", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> saveTags(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);

		message.putAll(manageMyService.saveTags(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
