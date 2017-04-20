package com.cn.leedane.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.ScoreBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.ScoreService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;

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
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(scoreService.getLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	
	/**
	 * 获得指定用户的总积分
	 * @return
	 */
	@RequestMapping(value = "/score/total", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getTotalScore(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(scoreService.getTotalScore(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 减少积分
	 * @return
	 */
	@RequestMapping(value = "/score/reduce", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> reduceScore(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		JSONObject json = getJsonFromMessage(message);
		UserBean user = getUserFromMessage(message);
		int score = JsonUtil.getIntValue(json, "score", 0);
		String desc = JsonUtil.getStringValue(json, "desc");
		int tableId = JsonUtil.getIntValue(json, "tableId");
		String tableName = JsonUtil.getStringValue(json, "tableName");
		message.putAll(scoreService.reduceScore(score, desc, tableName, tableId, user));
		return message.getMap();
	}
}
