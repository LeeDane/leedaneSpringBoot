package com.cn.leedane.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = "kuniyasu/rest")
public class SungoalController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	
	/**
	 * 国保APP测试请求的入口
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/get", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> appRequest(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		JSONObject json = getJsonFromMessage(message);
		System.out.println("请求参数："+ json.toString());
		int method = JsonUtil.getIntValue(json, "method");
		String responseStr = "";
		message.put("code", "0000");
		message.put("message", "处理成功");
		switch (method) {
		case 1: //登录
			responseStr = "{\"userinfo\": {\"username\": \"潘桦\",\"department\": \"反恐部第一小组\",\"img\": \"\"},\"branch\": [{\"id\": \"0001011\",\"name\": \"硚口分局\"},{\"id\": \"0001012\",\"name\": \"江岸分局\"},{\"id\": \"0001013\",\"name\": \"硚口分局\"},{\"id\": \"0001014\",\"name\": \"江岸分局\"}],\"personType\": [{\"id\": \"0001024\",\"name\": \"集体利益\"},{\"id\": \"0001021\",\"name\": \"拆迁\"},{\"id\": \"0001022\",\"name\": \"邪教\"},{\"id\": \"0001023\",\"name\": \"涉恐\"}]}";
			message.put("data", JSONObject.fromObject(responseStr));
			break;
		case 2: //首页获取信息
			responseStr = "{\"msg\": {\"total\": 0,\"noread\": 10},\"zdrhd\": {\"total\": 18,\"noread\": 1},\"yjs\": {\"total\": 3,\"noread\": 1},\"lrlc\": {\"total\": 5,\"noread\": 1},\"wdgz\": {\"total\": 11,\"noread\": 0},\"zdbw\": {\"total\": 1,\"noread\": 0},\"dxbm\": {\"total\": 15,\"noread\": 0},\"gzmgjd\": {\"total\": 3,\"noread\": 0},\"zdgz\": {\"total\": 1,\"noread\": 0},\"gxzdr\": {\"total\": 15,\"noread\": 0},\"sjcj\": {\"total\": 3,\"noread\": 0},\"wdzl\": {\"total\": 1,\"noread\": 1},\"jstx\": {\"total\": 15,\"noread\": 15}}";
			message.put("data", JSONObject.fromObject(responseStr));
			break;
			
		case 31: //重点人活动首页列表
			responseStr = "[{\"id\": \"123124314\",\"desc\": \"退役志愿兵\",\"total\": \"3\",\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]}, {\"id\": \"123124314\",\"desc\": \"法轮功\",\"total\": \"3\",\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"},{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]}, {\"id\": \"123124314\",\"desc\": \"退役志愿兵\",\"total\": \"3\",\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]}, {\"id\": \"123124314\",\"desc\": \"退役志愿兵\",\"total\": \"3\",\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]},{\"id\": \"123124314\",\"desc\": \"退役志愿兵\",\"total\": \"3\",\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]},{\"id\": \"123124314\",\"desc\": \"退役志愿兵\",\"total\": \"3\",\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]},{\"id\": \"123124314\",\"desc\": \"退役志愿兵\",\"total\": \"3\",\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]},{\"id\": \"123124314\",\"desc\": \"退役志愿兵\",\"total\": \"3\",\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]},{\"id\": \"123124314\",\"desc\": \"退役志愿兵\",\"total\": \"3\",\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]},{\"id\": \"123124314\",\"desc\": \"退役志愿兵\",\"total\": \"3\",\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]},{\"id\": \"123124314\",\"desc\": \"退役志愿兵\",\"total\": \"3\",\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]},{\"id\": \"123124314\",\"desc\": \"退役志愿兵\",\"total\": \"3\",\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]},{\"id\": \"123124314\",\"desc\": \"退役志愿兵\",\"total\": \"3\",\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]}]";
			message.put("data", JSONArray.fromObject(responseStr));
			break;
		case 33: //重点人活动简项列表
			responseStr = "[{\"tag\": [{\"name\": \"在控\",\"color\": \"#00FF00\"}, {\"name\": \"重点关注\",\"color\": \"#FFFF00\"}, {\"name\": \"硚口分局\",\"color\": \"#0000FF\"}],\"img\": \"\",\"status\": \"未读\",\"data\": [{\"key\": \"姓名\",\"value\": \"李磊\"}, {\"key\": \"轨迹类型\",\"value\": \"火车\"}, {\"key\": \"身份证\",\"value\": \"44243119450782001 x\"}, {\"key\": \"活动地点\",\"value\": \"汉口火车站\"}, {\"key\": \"活动时间\",\"value\": \"2017-11-30 15: 10: 00\"}, {\"key\": \"轨迹动向\",\"value\": \"汉口 - 北京\"}]}, {\"tag\": [{\"name\": \"在控\",\"color\": \"#00FF00\"}, {\"name\": \"重点关注\",\"color\": \"#FFFF00\"}, {\"name\": \"硚口分局\",\"color\": \"#0000FF\"}],\"img\": \"\",\"status\": \"未读\",\"data\": [{\"key\": \"姓名\",\"value\": \"李磊\"}, {\"key\": \"轨迹类型\",\"value\": \"火车\"}, {\"key\": \"身份证\",\"value\": \"44243119450782001 x\"}, {\"key\": \"活动地点\",\"value\": \"汉口火车站\"}, {\"key\": \"活动时间\",\"value\": \"2017-11-30 15: 10: 00\"}, {\"key\": \"轨迹动向\",\"value\": \"汉口 - 北京\"}]}]";
			message.put("data", JSONArray.fromObject(responseStr));
			break;
		case 41: //预警数一级列表
			responseStr = "[{\"desc\": \"异动预警\",\"total\": 3,\"list\": [{\"id\": \"121234132\",\"desc\": \"法轮功\",\"total\": 1}, {\"id\": \"121234132\",\"desc\": \"退役志愿兵\",\"total\": 2}]}, {\"desc\": \"流动预警\",\"total\": 3,\"list\": [{\"id\": \"121234132\",\"desc\": \"法轮功\",\"total\": 1}, {\"id\": \"121234132\",\"desc\": \"退役志愿兵\",\"total\": 2}]}]";
			message.put("data", JSONArray.fromObject(responseStr));
			break;
		/*case 42: //预警数二级列表
			responseStr = "[{\"desc\": \"法轮功\",\"total\": 1}, {\"desc\": \"退役志愿兵\",\"total\": 2}]";
			message.put("data", JSONArray.fromObject(responseStr));
			break;*/
		case 43: //预警简项
			responseStr = "[{\"tag\": [{\"name\": \"在控\",\"color\": \"#2AD996\"}, {\"name\": \"重点关注\",\"color\": \"#FD955B\"}, {\"name\": \"硚口分局\",\"color\": \"#30ABFB\"}],\"img\": \"\",\"status\": \"未读\",\"data\": [{\"key\": \"姓名\",\"value\": \"李磊\"}, {\"key\": \"轨迹类型\",\"value\": \"火车\"}, {\"key\": \"身份证\",\"value\": \"44243119450782001 x\"}, {\"key\": \"活动地点\",\"value\": \"汉口火车站\"}, {\"key\": \"活动时间\",\"value\": \"2017-11-30 15: 10: 00\"}, {\"key\": \"轨迹动向\",\"value\": \"汉口 - 北京\"}]}, {\"tag\": [{\"name\": \"在控\",\"color\": \"#2AD996\"}, {\"name\": \"重点关注\",\"color\": \"#FD955B\"}, {\"name\": \"硚口分局\",\"color\": \"#30ABFB\"}],\"img\": \"\",\"status\": \"未读\",\"data\": [{\"key\": \"姓名\",\"value\": \"李磊1\"}, {\"key\": \"轨迹类型\",\"value\": \"火车\"}, {\"key\": \"身份证\",\"value\": \"44243119450782001 x\"}, {\"key\": \"活动地点\",\"value\": \"汉口火车站\"}, {\"key\": \"活动时间\",\"value\": \"2017-11-30 15: 10: 00\"}, {\"key\": \"轨迹动向\",\"value\": \"汉口 - 北京\"}]}, {\"tag\": [{\"name\": \"在控\",\"color\": \"#2AD996\"}, {\"name\": \"重点关注\",\"color\": \"#FD955B\"}, {\"name\": \"硚口分局\",\"color\": \"#30ABFB\"}],\"img\": \"\",\"status\": \"未读\",\"data\": [{\"key\": \"姓名\",\"value\": \"李磊2\"}, {\"key\": \"轨迹类型\",\"value\": \"火车\"}, {\"key\": \"身份证\",\"value\": \"44243119450782001 x\"}, {\"key\": \"活动地点\",\"value\": \"汉口火车站\"}, {\"key\": \"活动时间\",\"value\": \"2017-11-30 15: 10: 00\"}, {\"key\": \"轨迹动向\",\"value\": \"汉口 - 北京\"}]}, {\"tag\": [{\"name\": \"在控\",\"color\": \"#2AD996\"}, {\"name\": \"重点关注\",\"color\": \"#FD955B\"}, {\"name\": \"硚口分局\",\"color\": \"#30ABFB\"}],\"img\": \"\",\"status\": \"未读\",\"data\": [{\"key\": \"姓名\",\"value\": \"李磊3\"}, {\"key\": \"轨迹类型\",\"value\": \"火车\"}, {\"key\": \"身份证\",\"value\": \"44243119450782001 x\"}, {\"key\": \"活动地点\",\"value\": \"汉口火车站\"}, {\"key\": \"活动时间\",\"value\": \"2017-11-30 15: 10: 00\"}, {\"key\": \"轨迹动向\",\"value\": \"汉口 - 北京\"}]}, {\"tag\": [{\"name\": \"在控4\",\"color\": \"#2AD996\"}, {\"name\": \"重点关注4\",\"color\": \"#FD955B\"}, {\"name\": \"硚口分局4\",\"color\": \"#30ABFB\"}],\"img\": \"\",\"status\": \"未读\",\"data\": [{\"key\": \"姓名\",\"value\": \"李磊4\"}, {\"key\": \"轨迹类型\",\"value\": \"火车\"}, {\"key\": \"身份证\",\"value\": \"44243119450782001 x\"}, {\"key\": \"活动地点\",\"value\": \"汉口火车站\"}, {\"key\": \"活动时间\",\"value\": \"2017-11-30 15: 10: 00\"}, {\"key\": \"轨迹动向\",\"value\": \"汉口 - 北京\"}]}]";
			message.put("data", JSONArray.fromObject(responseStr));
			break;
			
		case 101: //数据采集获取tab列表
			responseStr = "{\"见面\": 1,\"收笔迹\": 14,\"摸查虚拟身份\": 12,\"银行账号\": 3,\"处置\": 12}";
			message.put("data", JSONObject.fromObject(responseStr));
			break;
		case 102: //数据采集，见面
			responseStr = "[{\"img\": \"\",\"data\":[{\"key\": \"姓名\", \"value\": \"李磊\"},{\"key\": \"性别\", \"value\": \"男\"},{\"key\": \"证件号码\", \"value\": \"453475775747466363\"},{\"key\": \"指令编号\", \"value\": \"ZL893973265354\"},{\"key\": \"见面地点\", \"value\": \"解放公园42号\", \"isAddress\": true, \"x\": \"22.46644\", \"y\": \"46.994994\"},{\"key\": \"见面开始时间\", \"value\": \"2017-11-30 14:00:00\"},{\"key\": \"见面结束时间\", \"value\": \"2017-11-30 16:00:00\"},{\"key\": \"接待情况\", \"value\": \"经济纠纷和恢复恢复将数据境界的彼方发\"},{\"key\": \"发挥作用\", \"value\": \"进度款开发是耗时发挥进度款开发是耗时发挥进度款开发是耗时发挥进度款开发是耗时发挥\"}]},{\"img\": \"\",\"data\":[{\"key\": \"姓名\", \"value\": \"李磊\"},{\"key\": \"性别\", \"value\": \"男\"},{\"key\": \"证件号码\", \"value\": \"453475775747466363\"},{\"key\": \"指令编号\", \"value\": \"ZL893973265354\"},{\"key\": \"见面地点\", \"value\": \"解放公园42号\", \"isAddress\": true, \"x\": \"22.46644\", \"y\": \"46.994994\"},{\"key\": \"见面开始时间\", \"value\": \"2017-11-30 14:00:00\"},{\"key\": \"见面结束时间\", \"value\": \"2017-11-30 16:00:00\"},{\"key\": \"接待情况\", \"value\": \"经济纠纷和恢复恢复将数据境界的彼方发\"},{\"key\": \"发挥作用\", \"value\": \"进度款开发是耗时发挥进度款开发是耗时发挥进度款开发是耗时发挥进度款开发是耗时发挥\"}]},{\"img\": \"\",\"data\":[{\"key\": \"姓名\", \"value\": \"李磊\"},{\"key\": \"性别\", \"value\": \"男\"},{\"key\": \"证件号码\", \"value\": \"453475775747466363\"},{\"key\": \"指令编号\", \"value\": \"ZL893973265354\"},{\"key\": \"见面地点\", \"value\": \"解放公园42号\", \"isAddress\": true, \"x\": \"22.46644\", \"y\": \"46.994994\"},{\"key\": \"见面开始时间\", \"value\": \"2017-11-30 14:00:00\"},{\"key\": \"见面结束时间\", \"value\": \"2017-11-30 16:00:00\"},{\"key\": \"接待情况\", \"value\": \"经济纠纷和恢复恢复将数据境界的彼方发\"},{\"key\": \"发挥作用\", \"value\": \"进度款开发是耗时发挥进度款开发是耗时发挥进度款开发是耗时发挥进度款开发是耗时发挥\"}]}]";
			message.put("data", JSONArray.fromObject(responseStr));
			break;
		case 151: //流入流出Destination
			responseStr = "{\"desc\": \"北京\",\"total\": 4,\"list\": [{\"id\": \"123412432\",\"desc\": \"法轮功\",\"total\": 2,\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"},{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]},{\"id\": \"123412432\",\"desc\": \"退伍志愿兵\",\"total\": 2,\"list\": [{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"},{\"id\": \"12412431243\",\"desc\": \"江厦分局\",\"total\": \"24\"}]}]}";
			message.put("data", JSONObject.fromObject(responseStr));
			break;
		case 152: //流入流出traffic
			responseStr = "[{\"desc\": \"大巴\",\"total\": 4},{\"desc\": \"航班\",\"total\": 6},{\"desc\": \"火车\",\"total\": 3}]";
			message.put("data", JSONArray.fromObject(responseStr));
			break;
		case 153: //流入流出city
			responseStr = "[{\"desc\": \"上海\",\"total\": 3,\"highlight\": false},{\"desc\": \"郑州\",\"total\": 2},{\"desc\": \"云南\",\"total\": 2,\"highlight\": false},{\"desc\": \"北京\",\"total\": 1,\"highlight\": true},{\"desc\": \"海南\",\"total\": 1}]";
			message.put("data", JSONArray.fromObject(responseStr));
			break;
		case 154: //流入流出group
			responseStr = "[{\"id\": \"1241243\",\"desc\": \"退役志愿兵\",\"total\": 5},{\"id\": \"1241243\",\"desc\": \"法轮功\",\"total\": 3},{\"id\": \"1241243\",\"desc\": \"出租车\",\"total\": 2},{\"id\": \"1241243\",\"desc\": \"拆迁户\",\"total\": 1}]";
			message.put("data", JSONArray.fromObject(responseStr));
			break;
		case 155: //流入流出branchy
			responseStr = "[{\"id\": \"1241243\",\"desc\": \"汉口分局\",\"total\": 3},{\"id\": \"1241243\",\"desc\": \"江夏分局\",\"total\": 2},{\"id\": \"1241243\",\"desc\": \"硚口分局\",\"total\": 2},{\"id\": \"1241243\",\"desc\": \"经开分局\",\"total\": 1}]";
			message.put("data", JSONArray.fromObject(responseStr));
			break;
			
		case 161: //消息提醒的类型列表
			responseStr = "[{\"id\": \"123412\",\"desc\": \"未读\",\"total\": \"18\",\"typeList\": [{\"id\": \"1111\",\"desc\": \"全部\",\"total\": \"18\"},{\"id\": \"2222\",\"desc\": \"异常聚集\",\"total\": \"6\"},{\"id\": \"2223\",\"desc\": \"异动预警\",\"total\": \"12\"},{\"id\": \"2224\",\"desc\": \"流动预警\",\"total\": \"2\"}]},{\"id\": \"12341243\",\"desc\": \"已读\",\"total\": \"1\",\"typeList\": [{\"id\": \"3333\",\"desc\": \"全部\",\"total\": \"18\"},{\"id\": \"4444\",\"desc\": \"异常聚集\",\"total\": \"2\"}]}]";
			message.put("data", JSONArray.fromObject(responseStr));
			break;
		case 162: //消息提醒的列表
			responseStr = "[{\"id\": \"000100\",\"zt\": {\"key\": \"状态\",\"value\": \"已读\",\"color\": \"blue\"},\"data\": [{\"key\": \"标题\",\"value\": \"<a href='http://www.baidu.com'>张红雨q</a> 4212134243124312\"}, {\"key\": \"创建时间\",\"value\": \"2017-12-06 04:29:02\"}, {\"key\": \"内容\",\"value\": \"法轮功（非管控对象)发现于......法轮功（非管控对象)发现于。。。。。法轮功（非管控对象)发现于。。。。。。法轮功（非管控对象)发现于。。。。。。法轮功（非管控对象)发现于。。。。。。。法轮功（非管控对象)发现于。。。。。。。法轮功（非管控对象)发现于\"}, {\"key\": \"状态\",\"value\": \"未处理\"}]}, {\"id\": \"000101\",\"zt\": {\"key\": \"状态\",\"value\": \"已读\",\"color\": \"blue\"},\"data\": [{\"key\": \"标题\",\"value\": \"张红雨 4212134243124312\"}, {\"key\": \"创建时间\",\"value\": \"2017-12-06 04:29:02\"}, {\"key\": \"内容\",\"value\": \"法轮功（非管控对象)发现于......\"}, {\"key\": \"状态\",\"value\": \"未处理\"}]}]";
			message.put("data", JSONArray.fromObject(responseStr));
			break;
		case 163: //消息提醒的标记已读
			responseStr = "";
			message.put("data", "");
			break;
		case 164: //消息提醒的删除
			responseStr = "";
			message.put("data", "");
			break;
		default:
			break;
		}
		System.out.println(message);
		
		message.remove("responseCode");
		message.remove("json");
		message.remove("success");
		
		HashMap<String, Object> map = message.getMap();
		System.out.println("-------"+ map);
		return map;
	}
	
	/**
	 * 登录
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/AppUserRest/login", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> login(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("AppUserRest_login");
		System.out.println(data);
		message.put("data", JSONObject.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 首页
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/HomePageRest/mainPage", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> mainPage(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("HomePageRest_mainPage");
		System.out.println(data);
		message.put("data", JSONObject.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 首页--主页单独获取指令和消息
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/HomePageRest/getMessageAndInstruct", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> HomePageRestGetMessageAndInstruct(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("HomePageRest_getMessageAndInstruct");
		System.out.println(data);
		message.put("data", JSONObject.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 重点人活动列表
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/managerKeyParts/personList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> personList(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("managerKeyParts_personList");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 重点人活动详情
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/managerKeyParts/personDetail", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> personDetail(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("managerKeyParts_personDetail");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 活动轨迹详情
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/ActiveWaring/activeDetails", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> activeDetails(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("ActiveWaring_activeDetails");
		System.out.println(data);
		message.put("data", JSONObject.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 预警概况
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/ActiveWaring/points", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> activeWaringPoints(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("ActiveWaring_points");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 预警概况列表
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/ActiveWaring/personsList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> activeWaringPersonsList(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("ActiveWaring_personsList");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 预警概况详情
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/ActiveWaring/waringDetails", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> activeWaringWaringDetails(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("ActiveWaring_waringDetails");
		System.out.println(data);
		message.put("data", JSONObject.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 获取流入/流出统计
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/inAndOutFlow/tabList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> inAndOutFlowTabList(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("inAndOutFlow_tabList");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 获取流入/流出列表
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/inAndOutFlow/flowList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> inAndOutFlowFlowList(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("inAndOutFlow_flowList");
		System.out.println(data);
		message.put("data", JSONObject.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 获取重点人类别
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/managerKeyPerson/personType", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> managerKeyPersonPersonType(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("managerKeyPerson_personType");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 获取重点人列表
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/managerKeyPerson/personList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> managerKeyPersonPersonList(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("managerKeyPerson_personList");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 获取重点人详情
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/managerKeyPerson/personDetail", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> managerKeyPersonPersonDetail(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("managerKeyPerson_personDetail");
		System.out.println(data);
		message.put("data", JSONObject.fromObject(data));
		return message.getMap();
	}
	
	
	/**
	 * 消息提醒类型列表
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/persons", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messagePersons(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.out.println("参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("Message_persons");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 消息提醒的列表
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/details", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageDetails(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("Message_details");
		System.out.println("消息："+data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 消息标记已读
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/updateZt", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageUpdateZt(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		message.put("data", "");
		return message.getMap();
	}
	
	/**
	 * 消息删除
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/deleteXx", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageDelete(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		message.put("data", "");
		return message.getMap();
	}
	
	/**
	 * 我的关注概况
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/follow/getFollowSurvey", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> followGetFollowSurvey(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("follow_getFollowSurvey");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 获取关注人员列表
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/follow/getFollowUserList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> followGetFollowUserList(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("follow_getFollowUserList");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 获取关注人员详情
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/follow/getFollowDetail", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> followGetFollowDetail(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("follow_getFollowDetail");
		System.out.println(data);
		message.put("data", JSONObject.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 获取数据采集类型
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/dataGather/typeList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> dataGatherTypeList(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("dataGather_typeList");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 获取数据采集列表
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/dataGather/dataList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> dataGatherDataList(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("dataGather_dataList");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 获取收集对象列表
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/dataGather/getCollectPersonList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> dataGatherGetCollectPersonList(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("dataGather_getCollectPersonList");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 获取指令列表
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/dataGather/getInstructionList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> dataGatherGetInstructionList(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("dataGather_getInstructionList");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 添加采集信息
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/dataGather/addDataGather", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> dataGatherAddDataGather(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		return message.getMap();
	}
	
	
	/**
	 * 指令统计
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/MyInstruct/persons", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> MyInstructPersons(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("MyInstruct_persons");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 指令列表
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/MyInstruct/personsList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> MyInstructPersonsList(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("MyInstruct_personsList");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 指令详情
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/MyInstruct/details", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> MyInstructDetails(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("MyInstruct_details");
		System.out.println(data);
		message.put("data", JSONObject.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 指令修改
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/MyInstruct/updateZl", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> MyInstructUpdateZl(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		return message.getMap();
	}
	
	/**
	 * 指令下发
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/MyInstruct/InstructInsert", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> MyInstructInstructInsert(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		return message.getMap();
	}
	
	/**
	 * 获取敏感日期列表
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/sensitive/getPointList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> sensitiveGetPointList(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("sensitive_getPointList");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 根据日期获取敏感节日信息
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/sensitive/getPointDay", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> sensitiveGetPointDay(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("sensitive_getPointDay");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 动向不明概况
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Dxbm/points", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> DxbmPoints(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("Dxbm_points");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 动向不明详情
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Dxbm/details", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> DxbmDetails(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("Dxbm_details");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 重点部位概况
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/KeySite/points", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> KeySitePoints(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("KeySite_points");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 重点部位详情
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/KeySite/zdbwDetails", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> KeySiteZdbwDetails(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("KeySite_zdbwDetails");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 重点部位活动
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/KeySite/zdbwhdDetails", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> KeySiteZdbwhdDetails(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("KeySite_zdbwhdDetails");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 重点部位类别
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/KeySite/getZdbwLx", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> KeySiteGetZdbwLx(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("KeySite_getZdbwLx");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	
	/**
	 * 获取用户信息
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/common/getTuserList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> commonGetTuserList(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("common_getTuserList");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}

	/**
	 * 支队分局派出所结构
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/common/getDepartmentTree", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> commonGetDepartmentTree(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("common_getDepartmentTree");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	
	/**
	 * 流程审批-待审批(我的指令)
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/getProcess", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageGetProcess(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("Message_getProcess");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 流程审批-历史流程
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/getHisProcess", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageGetHisProcess(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("Message_getHisProcess");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	
	
	/**
	 * 流程审批-新增指令
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/addXiaoXi", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageAddXiaoXi(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		return message.getMap();
	}
	
	/**
	 * 流程审批-显示反馈页面
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/showzhsOrzsld", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageShowzhsOrzsld(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("Message_showzhsOrzsld");
		System.out.println(data);
		message.put("data", JSONObject.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 流程审批-反馈指挥室（直属领导）
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/fkzhsOrzsld", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageFkzhsOrzsld(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		return message.getMap();
	}
	
	/**
	 * 流程审批-显示直属领导（指挥室）审批
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/showsp", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageShowsp(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("Message_showsp");
		System.out.println(data);
		message.put("data", JSONObject.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 流程审批-生成指令
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/qsxx", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageQsxx(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		return message.getMap();
	}
	
	/**
	 * 流程审批-单位列表
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/getJsdw", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageGetJsdw(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("Message_getJsdw");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 流程审批-获取单位人员
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/getryxx", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageGetryxx(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("Message_getryxx");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 流程审批-流程审批
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/getXiaoXiProcess", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> MessageGetXiaoXiProcess(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("Message_getXiaoXiProcess");
		System.out.println(data);
		message.put("data", JSONObject.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 流程审批-审批通过
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/sp", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageSp(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		return message.getMap();
	}
	
	/**
	 * 流程审批-审批不通过
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/nosp", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageNosp(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		return message.getMap();
	}
	
	/**
	 * 流程审批-指令-不处理消息
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/noqs", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageNoqs(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		return message.getMap();
	}
	
	/**
	 * 流程审批-获取采集信息
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/getcjxx", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageGetcjxx(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("Message_getcjxx");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 添加采集信息
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/cj", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> MessageCj(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		return message.getMap();
	}
	
	/**
	 * 首页预警
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/HomePageRest/mainPage2", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> mainPage2(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("HomePageRest_mainPage2");
		System.out.println(data);
		message.put("data", JSONObject.fromObject(data));
		return message.getMap();
	}
	
	/**
	 * 查找重点人员
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/Message/getZdryByXmOrSfz", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageGetZdryByXmOrSfz(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		System.err.println("请求参数："+ getJsonFromMessage(message));
		message.put("code", "0000");
		message.put("message", "处理成功");
		String data = getDataFromFile("Message_getZdryByXmOrSfz");
		System.out.println(data);
		message.put("data", JSONArray.fromObject(data));
		return message.getMap();
	}
	
	private String getDataFromFile(String fileName) throws IOException{
		File file = new File("C:\\guobao\\"+ fileName +".json");//定义一个file对象，用来初始化FileReader
        BufferedReader reader = null;
		if (file != null && file.exists() && file.isFile()) {
			InputStreamReader in = new InputStreamReader(new FileInputStream(file));
			reader = new BufferedReader(in);
			//取得配置文件中的json字符串
			StringBuffer buffer = new StringBuffer();
			String strs = null;
			while((strs = reader.readLine()) != null){
				if(strs != null && !strs.startsWith("#")){
					strs = URLDecoder.decode(strs, "UTF-8");
					buffer.append(strs);
				}
			}
			if(reader != null)
				reader.close();
			return buffer.toString();
		}
        return null;
	}
	
}
