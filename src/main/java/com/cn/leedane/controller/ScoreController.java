package com.cn.leedane.controller;

import com.cn.leedane.model.ScoreBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.ScoreService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.sc)
public class ScoreController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private ScoreService<ScoreBean> scoreService;
	
	@Resource
	public void setScoreService(ScoreService<ScoreBean> scoreService) {
		this.scoreService = scoreService;
	}
	
	/**
	 * 分页获取积分历史列表
	 * @return
	 */
	@RequestMapping(value = "/scores", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel paging(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return scoreService.getLimit(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	
	/**
	 * 获得指定用户的总积分
	 * @return
	 */
	@RequestMapping(value = "/score/total", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel getTotalScore(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		
		checkRoleOrPermission(model, request);
		return scoreService.getTotalScore(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 减少积分
	 * @return
	 */
	@RequestMapping(value = "/score/reduce", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel reduceScore(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		JSONObject json = getJsonFromMessage(message);
		UserBean user = getUserFromMessage(message);
		int score = JsonUtil.getIntValue(json, "score", 0);
		String desc = JsonUtil.getStringValue(json, "desc");
		long tableId = JsonUtil.getLongValue(json, "tableId");
		String tableName = JsonUtil.getStringValue(json, "tableName");
		return scoreService.reduceScore(score, desc, tableName, tableId, user);
	}
}
