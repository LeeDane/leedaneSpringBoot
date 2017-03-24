package com.cn.leedane.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.FilePathBean;
import com.cn.leedane.model.MoodBean;
import com.cn.leedane.model.TemporaryBase64Bean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.FilePathService;
import com.cn.leedane.service.MoodService;
import com.cn.leedane.service.TemporaryBase64Service;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping("/md")
public class MoodController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	//心情service
	@Autowired
	private MoodService<MoodBean> moodService;
	
	//上传临时base64service
	@Autowired
	private TemporaryBase64Service<TemporaryBase64Bean> temporaryBase64Service;
	
	//上传filePath表的service
	@Autowired
	private FilePathService<FilePathBean> filePathService;
	
	/**
	 * 发表心情(只是更新心情的草稿状态为正常状态)
	 * 适用于心情没有图片或者图片很小的情况下，其他情况下请使用uploadBase64()
	 * @return
	 */
	@RequestMapping(value= "/mood", method = RequestMethod.PUT)
	public Map<String, Object> send(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			JSONObject jsonObject = getJsonFromMessage(message);
			int status = JsonUtil.getIntValue(jsonObject, "status", ConstantsUtil.STATUS_NORMAL);
			message.putAll(moodService.updateMoodStatus(jsonObject, status, request, getUserFromMessage(message)));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 发表心情(为了用户可以后悔取消，这里只是先保存为草稿状态，需要用户再次发送请求调用send()方法保存为正常状态)
	 * @return
	 */
	@RequestMapping(value= "/sendDraft", method = RequestMethod.POST)
	public Map<String, Object> sendDraft(HttpServletRequest request, HttpServletResponse response){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(moodService.saveMood(getJsonFromMessage(message), getUserFromMessage(message), ConstantsUtil.STATUS_DRAFT, request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 发表文字心情
	 * @return
	 */
	@RequestMapping(value= "/sendWord", method = RequestMethod.POST)
	public Map<String, Object> sendWord(HttpServletRequest request, HttpServletResponse response){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			JSONObject jsObject = getJsonFromMessage(message);
			int status = JsonUtil.getIntValue(jsObject, "status", ConstantsUtil.STATUS_NORMAL);
			
			message.putAll(moodService.sendWord(jsObject, getUserFromMessage(message), status, request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 发表图片链接的心情
	 * @return
	 */
	@RequestMapping("/sendWordAndLink")
	public Map<String, Object> sendWordAndLink(HttpServletRequest request, HttpServletResponse response){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(moodService.sendWordAndLink(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 删除心情
	 * @return
	 */
	@RequestMapping(value= "/mood", method = RequestMethod.DELETE)
	public Map<String, Object> delete(HttpServletRequest request, HttpServletResponse response){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(moodService.deleteMood(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 获取指定符合条件的心情
	 * @return
	 */
	@RequestMapping(value = "/moods", method = RequestMethod.GET)
	//@RequiresRoles("ADMIN")
	//@RequiresPermissions("ADMIN_MANAGER")
	public Map<String, Object> getPagingMood(HttpServletRequest request, HttpServletResponse response){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			//checkAnyRoleAuthor("ADMIN");
			checkPermissionAuthor("ADMIN_MANAGER");
				
			message.putAll(moodService.getMoodByLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (UnauthorizedException e) {
			e.printStackTrace();
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.不能访问没有授权的链接.value));
			message.put("responseCode", EnumUtil.ResponseCode.不能访问没有授权的链接.value);
		}catch (Exception e) {
			e.printStackTrace();
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		
		return message.getMap();
	}
	
	/**
	 * 模拟断点续传发送base64字符串
	 * 目的：某些图片文件过大，一次性传会因为网络等复杂因素发生意外导致不成功，下次再传又要重新发送，很消耗客户端的流量
	 * 实现：客户端分批上传文件，一部分上传成功，返回true，客户端自己记录上传的路径，服务器端负责将未完成的base64位的字符串
	 * 	保存在T_TEMP_BASE64表中
	 * 	当客户端传递结束的标记，就根据响应的命名规范合并相应的数据成一个完整的base64字符串，生成相应的图片放在file文件夹下，保存记录在T_FILEPATH记录表中，状态为0(禁用)，合并成功后批量删除T_TEMP_BASE64对应的数据，
	 *  以最新的覆盖前面的值(考虑到网络问题，其中某次服务器保存了，客户端没有接收到正确的返回，客户端可以再次发送请求)
	 * 客户端：发送的请求包含{"start":0, "content":"base64strhhhfdjhsdjAM,ZKKASKN", "end": 10000,"uuid":"78778223hdyy8e", "order":1, "isEnd": false}
	 * @return
	 */
	@RequestMapping("/uploadBase64Str")
	public String uploadBase64Str(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			JSONObject json = getJsonFromMessage(message);
			UserBean user = getUserFromMessage(message);
					
			boolean isEnd = JsonUtil.getBooleanValue(json, "isEnd");
			
			//上传该base64字符串结束
			if(isEnd){
				filePathService.saveEachTemporaryBase64ToFilePath(json, user, request);
				message.put("isSuccess", true);
			//直接保存上传记录
			}else{
				message.put("isSuccess", temporaryBase64Service.saveBase64Str(json, user, request));
			}
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
	
	/**
	 * 应用于分开上传图片都成功后的发表心情
	 * 客户端：发送的请求格式{"content":"今天天气不错", "froms": "android客户端","uuid":"78778223hdyy8e", "hasImg":true}
	 * @return
	 */
	@RequestMapping("/sendDivideMood")
	public String sendDivideMood(HttpServletRequest request, HttpServletResponse response){
		long start = System.currentTimeMillis();
		Map<String, Object> message = new HashMap<String, Object>();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(moodService.saveDividedMood(getJsonFromMessage(message), getUserFromMessage(message), ConstantsUtil.STATUS_NORMAL, request));
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("发表心情总计耗时：" +(end - start) +"毫秒");
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
	
	/**
	 * 删除已经上传的base64文件和心情列表中相应的记录
	 * 用处：一般就是用户在文件上传过程中，自行取消发布的心情
	 * @return
	 */
	@RequestMapping("/deleteUploadBase64Str")
	public String deleteUploadBase64Str(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
	
	/**
	 * 下载图片源(查找t_filepath)
	 * {"uuid":"jhasdjdasd", "order": "1", "size":"default"}
	 * @return
	 */
	@RequestMapping("/downloadBase64Str")
	public String downloadBase64Str(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.put("isSuccess", true);
			message.put("message", filePathService.downloadBase64Str(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
	
	/**
	 * 获取单篇文章的图片列表(查t_mood表)
	 * {"uuid":"jhasdjdasd"}
	 * @return 图片地址列表
	 */
	/*public String getOneMoodImgs(){
		message.put("isSuccess", resIsSuccess);
		try {
			JSONObject jo = HttpUtils.getJsonObjectFromInputStream(params,request);
			if(jo.isEmpty()) {	
				return SUCCESS;
			}
			
			resmessage = filePathService.getOneMoodImgs(jo, user, request);
			resIsSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("isSuccess", resIsSuccess);
		message.put("message", resmessage);
        return SUCCESS;
	}*/
	
	/**
	 * 获取指定用户所有的发表成功的心情总数
	 * {"uid":1"},非必须，为空表示当前登录用户
	 * @return 图片地址列表
	 */
	@RequestMapping("/getCountByUser")
	public String getCountByUser(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.put("message", moodService.getCountByUser(getJsonFromMessage(message), getUserFromMessage(message), request));
			message.put("isSuccess", true);
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
	/**
	 * 获取心情的信息
	 * {'uid':1; 'mid':1},uid非必须，为空表示当前登录用户
	 * @return 返回心情的内容，图片地址（120x120大小的图像）
	 */
	@RequestMapping("/detail")
	public void detail(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return;
			}
			message.putAll(moodService.detail(getJsonFromMessage(message), getUserFromMessage(message), request, "120x120"));
			printWriter(message, response, start);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
	}
	/**
	 * 获取心情的图片
	 * {'table_uuid':'leedane4e2f2684-ac82-4186-97fa-d8807211ef92', 'table_name':'t_mood'},uuid必须
	 * @return 返回心情的内容，图片地址（120x120大小的图像）
	 */
	@RequestMapping("/detailImgs")
	public String detailImgs(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(moodService.detailImgs(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
	
	/**
	 * 获取心情的话题列表
	 * @return
	 */
	@RequestMapping("/topic")
	public String topic(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(moodService.getTopicByLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
}
