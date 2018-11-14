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
public class SungoalAppController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	
	/**
	 * 国保APP测试请求的入口
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/AppServlet", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> appRequest(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		JSONObject json = getJsonFromMessage(message);
		System.out.println("请求参数："+ json.toString());
		int iftype = JsonUtil.getIntValue(json, "iftype");
		String responseStr = "";
		message.put("code", "0000");
		message.put("message", "处理成功");
		message.put("success", true);
		switch (iftype) {
		case 50: //登录
			responseStr = "[{\"id\":\"1\",\"time\":\"20150609101347\"},{\"id\":\"2\",\"time\":\"20150609101251\"},{\"id\":\"3\",\"time\":\"20150528104720\"}]";
			message.put("uptime", JSONArray.fromObject(responseStr));
			message.put("hasShuiYin", false);
			message.put("roles", JSONObject.fromObject("{\"is_gxfx\":true,\"is_dzda\":true,\"is_znjs\": true, \"is_ysjs\":true}"));
			break;
		case 1: //首页获取信息
			responseStr = "[{\"id\":\"wys\",\"models\":[{\"id\":\"wys_ry\",\"name\":\"人员\"},{\"id\":\"wys_wp\",\"name\":\"物品\"},{\"id\":\"wys_aj\",\"name\":\"案件\"},{\"id\":\"wys_dd\",\"name\":\"地点\"},{\"id\":\"wys_jg\",\"name\":\"机构\"}],\"name\":\"五要素\"}]";
			message.put("dimensions", JSONArray.fromObject(responseStr));
			break;
			
		case 3: //重点人活动首页列表
			responseStr = "[{\"hits\":1,\"model\":\"物品\",\"tables\":[{\"cname\":\"机动车\",\"ename\":\"ZHXX_JTXT.JG_JDC_JDCXX\",\"hits\":1}]},{\"hits\":30,\"model\":\"人员\",\"tables\":[{\"cname\":\"全省常住人口\",\"ename\":\"ZHXX_CZRK.V_JBXX\",\"hits\":1},{\"cname\":\"驾驶员\",\"ename\":\"ZHXX_JTXT.JG_JSY_JSYXX\",\"hits\":1},{\"cname\":\"驾驶员违法\",\"ename\":\"ZHXX_JTXT.JG_JSY_JGJSYWF\",\"hits\":1},{\"cname\":\"二代证\",\"ename\":\"ZHXX_CZRK.V_HIS_CARD_T\",\"hits\":1},{\"cname\":\"出国(境)人员申请信息\",\"ename\":\"ZHXX_CRJ.YW_CJ_HGTSQXXB\",\"hits\":3},{\"cname\":\"寄住人员\",\"ename\":\"ZHXX_ZZRK.V_ZA_RKGL_RHFL_RK_DJ\",\"hits\":1},{\"cname\":\"航空旅客\",\"ename\":\"ZHXX_HK.JC_RY_PASG\",\"hits\":20},{\"cname\":\"医疗保险人员基本信息\",\"ename\":\"ZHXX_RST.YILIAO_JBXX\",\"hits\":1},{\"cname\":\"养老保险人员基本信息\",\"ename\":\"ZHXX_RST.YANGLAO_JBXX\",\"hits\":1}]}]";
			message.put("statistics", JSONArray.fromObject(responseStr));
			break;
		case 4: //重点人活动简项列表
			responseStr = "{\"cname\":\"全省人口信息\",\"content\":[{\"carnum\":\"\",\"cartype\":\"\",\"columns\":[{\"name\":\"户口_公民身份号码\",\"value\":\"421181198710160017\"},{\"name\":\"户口_姓名\",\"value\":\"刘争亮\"},{\"name\":\"户口_性别代码\",\"value\":\"男性\"},{\"name\":\"户口_出生日期\",\"value\":\"1987-10-16\"},{\"name\":\"户口_民族代码\",\"value\":\"1\"},{\"name\":\"住址_地址名称\",\"value\":\"湖北省麻城市鼓楼高山咀村十二组牌坊岗垸45号\"},{\"name\":\"户口_婚姻状况代码\",\"value\":\"未婚\"},{\"name\":\"所属_行政区划代码\",\"value\":\"湖北省黄冈市麻城市\"},{\"name\":\"入库_日期时间\",\"value\":\"2015-05-15 03:03:37\"}],\"docid\":\"48513643\",\"ftime\":\"20150515030337\",\"idcard\":\"421181198710160017\",\"img\":\"\",\"label\":\"\",\"red\":false},{\"carnum\":\"\",\"cartype\":\"\",\"columns\":[{\"name\":\"户口_公民身份号码\",\"value\":\"421181198710160412\"},{\"name\":\"户口_姓名\",\"value\":\"戴鹏\"},{\"name\":\"户口_性别代码\",\"value\":\"男性\"},{\"name\":\"户口_出生日期\",\"value\":\"1987-10-16\"},{\"name\":\"户口_民族代码\",\"value\":\"1\"},{\"name\":\"住址_地址名称\",\"value\":\"湖北省麻城市龙池桥北正街11号(供销社9号)\"},{\"name\":\"户口_文化程度代码\",\"value\":\"大学本科教育\"},{\"name\":\"户口_婚姻状况代码\",\"value\":\"未婚\"},{\"name\":\"所属_行政区划代码\",\"value\":\"湖北省黄冈市麻城市\"},{\"name\":\"入库_日期时间\",\"value\":\"2015-05-14 22:23:18\"}],\"docid\":\"32540362\",\"ftime\":\"20150514222318\",\"idcard\":\"421181198710160412\",\"img\":\"\",\"label\":\"\",\"red\":false},{\"carnum\":\"\",\"cartype\":\"\",\"columns\":[],\"docid\":\"\",\"ftime\":\"\",\"idcard\":\"\",\"img\":\"\",\"label\":\"\",\"red\":true},{\"carnum\":\"\",\"cartype\":\"\",\"columns\":[{\"name\":\"户口_公民身份号码\",\"value\":\"421181198710160017\"},{\"name\":\"户口_姓名\",\"value\":\"刘争亮\"},{\"name\":\"户口_性别代码\",\"value\":\"男性\"},{\"name\":\"户口_出生日期\",\"value\":\"1987-10-16\"},{\"name\":\"户口_民族代码\",\"value\":\"1\"},{\"name\":\"住址_地址名称\",\"value\":\"湖北省麻城市鼓楼高山咀村十二组牌坊岗垸45号\"},{\"name\":\"户口_婚姻状况代码\",\"value\":\"未婚\"},{\"name\":\"所属_行政区划代码\",\"value\":\"湖北省黄冈市麻城市\"},{\"name\":\"入库_日期时间\",\"value\":\"2015-05-15 03:03:37\"}],\"docid\":\"48513643\",\"ftime\":\"20150515030337\",\"idcard\":\"421181198710160017\",\"img\":\"\",\"label\":\"\",\"red\":false},{\"carnum\":\"\",\"cartype\":\"\",\"columns\":[],\"docid\":\"\",\"ftime\":\"\",\"idcard\":\"\",\"img\":\"\",\"label\":\"\",\"red\":true}],\"ename\":\"TAB_VIEWS.V_QSRKXX_STGX\",\"hits\":18,\"timecost\":0.015}";
//			responseStr = "{\"cname\":\"航空旅客\",\"content\":[{\"carnum\":\"\",\"cartype\":\"\",\"columns\":[{\"name\":\"证件号码\",\"value\":\"21090519710206434x\"},{\"name\":\"旅客中文名\",\"value\":\"张淑华\"},{\"name\":\"旅客英文名\",\"value\":\"zhangshuhua\"},{\"name\":\"航空公司\",\"value\":\"中国国际航空公司\"},{\"name\":\"航班号\",\"value\":\"8933\"},{\"name\":\"座位号\",\"value\":\"20l\"},{\"name\":\"出发地\",\"value\":\"大连\"},{\"name\":\"目的地\",\"value\":\"深圳\"},{\"name\":\"航班日期\",\"value\":\"2014-12-18\"},{\"name\":\"国籍\",\"value\":\"中国\"},{\"name\":\"联系方式\",\"value\":\"02151069999\"},{\"name\":\"始发站起飞时间\",\"value\":\"715\"},{\"name\":\"经停站1到达时间\",\"value\":\"905\"},{\"name\":\"旅客名\",\"value\":\"\"},{\"name\":\"旅客姓\",\"value\":\"\"},{\"name\":\"公民身份号码\",\"value\":\"\"},{\"name\":\"经停站1\",\"value\":\"南京\"},{\"name\":\"经停站1起飞时间\",\"value\":\"955\"},{\"name\":\"经停站2到达时间\",\"value\":\"1210\"},{\"name\":\"证件类型\",\"value\":\"身份证\"},{\"name\":\"航班类型\",\"value\":\"国内\"},{\"name\":\"行李牌\",\"value\":\"\"},{\"name\":\"登机牌号\",\"value\":\"bn074\"},{\"name\":\"航班序号\",\"value\":\"4421599\"},{\"name\":\"始发站\",\"value\":\"大连\"},{\"name\":\"起飞-到达1\",\"value\":\"\"},{\"name\":\"起飞-到达2\",\"value\":\"\"},{\"name\":\"经停站2\",\"value\":\"szx\"},{\"name\":\"经停站2起飞时间\",\"value\":\"\"},{\"name\":\"经停站3到达时间\",\"value\":\"\"},{\"name\":\"起飞-到达3\",\"value\":\"\"},{\"name\":\"终点站\",\"value\":\"\"},{\"name\":\"旅客序号\",\"value\":\"546714959\"},{\"name\":\"订座记录BPNR\",\"value\":\"njkzky\"},{\"name\":\"订座记录CPNR\",\"value\":\"\"},{\"name\":\"舱位代码\",\"value\":\"\"},{\"name\":\"更新时间\",\"value\":\"2014-12-18 08:14:22\"}],\"docid\":\"124098830\",\"ftime\":\"20141218000000\",\"idCard\":\"21090519710206434x\",\"img\":\"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\",\"label\":\"\"},{\"carnum\":\"\",\"cartype\":\"\",\"columns\":[{\"name\":\"证件号码\",\"value\":\"21090519710206434x\"},{\"name\":\"旅客中文名\",\"value\":\"张淑华\"},{\"name\":\"旅客英文名\",\"value\":\"zhangshuhua\"},{\"name\":\"航空公司\",\"value\":\"中国国际航空公司\"},{\"name\":\"航班号\",\"value\":\"8926\"},{\"name\":\"座位号\",\"value\":\"16b\"},{\"name\":\"出发地\",\"value\":\"xiy\"},{\"name\":\"目的地\",\"value\":\"大连\"},{\"name\":\"航班日期\",\"value\":\"2014-11-16\"},{\"name\":\"国籍\",\"value\":\"中国\"},{\"name\":\"联系方式\",\"value\":\"1551001\"},{\"name\":\"始发站起飞时间\",\"value\":\"1520\"},{\"name\":\"经停站1到达时间\",\"value\":\"1650\"},{\"name\":\"旅客名\",\"value\":\"\"},{\"name\":\"旅客姓\",\"value\":\"\"},{\"name\":\"公民身份号码\",\"value\":\"\"},{\"name\":\"经停站1\",\"value\":\"xiy\"},{\"name\":\"经停站1起飞时间\",\"value\":\"1815\"},{\"name\":\"经停站2到达时间\",\"value\":\"2020\"},{\"name\":\"证件类型\",\"value\":\"身份证\"},{\"name\":\"航班类型\",\"value\":\"国内\"},{\"name\":\"行李牌\",\"value\":\"\"},{\"name\":\"登机牌号\",\"value\":\"bn006\"},{\"name\":\"航班序号\",\"value\":\"4407281\"},{\"name\":\"始发站\",\"value\":\"成都\"},{\"name\":\"起飞-到达1\",\"value\":\"\"},{\"name\":\"起飞-到达2\",\"value\":\"\"},{\"name\":\"经停站2\",\"value\":\"dlc\"},{\"name\":\"经停站2起飞时间\",\"value\":\"\"},{\"name\":\"经停站3到达时间\",\"value\":\"\"},{\"name\":\"起飞-到达3\",\"value\":\"\"},{\"name\":\"终点站\",\"value\":\"\"},{\"name\":\"旅客序号\",\"value\":\"545226277\"},{\"name\":\"订座记录BPNR\",\"value\":\"ney763\"},{\"name\":\"订座记录CPNR\",\"value\":\"\"},{\"name\":\"舱位代码\",\"value\":\"\"},{\"name\":\"更新时间\",\"value\":\"2014-11-16 16:52:45\"}],\"docid\":\"122900876\",\"ftime\":\"20141116000000\",\"idCard\":\"21090519710206434x\",\"img\":\"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\",\"label\":\"\"},{\"carnum\":\"\",\"cartype\":\"\",\"columns\":[{\"name\":\"证件号码\",\"value\":\"21090519710206434x\"},{\"name\":\"旅客中文名\",\"value\":\"张淑华\"},{\"name\":\"旅客英文名\",\"value\":\"zhangshuhua\"},{\"name\":\"航空公司\",\"value\":\"中国国际航空公司\"},{\"name\":\"航班号\",\"value\":\"8925\"},{\"name\":\"座位号\",\"value\":\"28j\"},{\"name\":\"出发地\",\"value\":\"大连\"},{\"name\":\"目的地\",\"value\":\"xiy\"},{\"name\":\"航班日期\",\"value\":\"2014-11-14\"},{\"name\":\"国籍\",\"value\":\"中国\"},{\"name\":\"联系方式\",\"value\":\"1551001\"},{\"name\":\"始发站起飞时间\",\"value\":\"910\"},{\"name\":\"经停站1到达时间\",\"value\":\"1125\"},{\"name\":\"旅客名\",\"value\":\"\"},{\"name\":\"旅客姓\",\"value\":\"\"},{\"name\":\"公民身份号码\",\"value\":\"\"},{\"name\":\"经停站1\",\"value\":\"xiy\"},{\"name\":\"经停站1起飞时间\",\"value\":\"1230\"},{\"name\":\"经停站2到达时间\",\"value\":\"1405\"},{\"name\":\"证件类型\",\"value\":\"身份证\"},{\"name\":\"航班类型\",\"value\":\"国内\"},{\"name\":\"行李牌\",\"value\":\"\"},{\"name\":\"登机牌号\",\"value\":\"bn037\"},{\"name\":\"航班序号\",\"value\":\"4406477\"},{\"name\":\"始发站\",\"value\":\"大连\"},{\"name\":\"起飞-到达1\",\"value\":\"\"},{\"name\":\"起飞-到达2\",\"value\":\"\"},{\"name\":\"经停站2\",\"value\":\"ctu\"},{\"name\":\"经停站2起飞时间\",\"value\":\"\"},{\"name\":\"经停站3到达时间\",\"value\":\"\"},{\"name\":\"起飞-到达3\",\"value\":\"\"},{\"name\":\"终点站\",\"value\":\"\"},{\"name\":\"旅客序号\",\"value\":\"545072380\"},{\"name\":\"订座记录BPNR\",\"value\":\"ney763\"},{\"name\":\"订座记录CPNR\",\"value\":\"\"},{\"name\":\"舱位代码\",\"value\":\"\"},{\"name\":\"更新时间\",\"value\":\"2014-11-14 09:40:55\"}],\"docid\":\"122845283\",\"ftime\":\"20141114000000\",\"idCard\":\"21090519710206434x\",\"img\":\"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\",\"label\":\"\"},{\"carnum\":\"\",\"cartype\":\"\",\"columns\":[{\"name\":\"证件号码\",\"value\":\"21090519710206434x\"},{\"name\":\"旅客中文名\",\"value\":\"张淑华\"},{\"name\":\"旅客英文名\",\"value\":\"zhangshuhua\"},{\"name\":\"航空公司\",\"value\":\"四川航空公司\"},{\"name\":\"航班号\",\"value\":\"8943\"},{\"name\":\"座位号\",\"value\":\"3b\"},{\"name\":\"出发地\",\"value\":\"杭州\"},{\"name\":\"目的地\",\"value\":\"大连\"},{\"name\":\"航班日期\",\"value\":\"2013-09-09\"},{\"name\":\"国籍\",\"value\":\"中国\"},{\"name\":\"联系方式\",\"value\":\"\"},{\"name\":\"始发站起飞时间\",\"value\":\"1650\"},{\"name\":\"经停站1到达时间\",\"value\":\"1835\"},{\"name\":\"旅客名\",\"value\":\"\"},{\"name\":\"旅客姓\",\"value\":\"\"},{\"name\":\"公民身份号码\",\"value\":\"\"},{\"name\":\"经停站1\",\"value\":\"大连\"},{\"name\":\"经停站1起飞时间\",\"value\":\"1925\"},{\"name\":\"经停站2到达时间\",\"value\":\"2055\"},{\"name\":\"证件类型\",\"value\":\"身份证\"},{\"name\":\"航班类型\",\"value\":\"国内\"},{\"name\":\"行李牌\",\"value\":\"\"},{\"name\":\"登机牌号\",\"value\":\"bn021\"},{\"name\":\"航班序号\",\"value\":\"4198874\"},{\"name\":\"始发站\",\"value\":\"杭州\"},{\"name\":\"起飞-到达1\",\"value\":\"\"},{\"name\":\"起飞-到达2\",\"value\":\"\"},{\"name\":\"经停站2\",\"value\":\"hrb\"},{\"name\":\"经停站2起飞时间\",\"value\":\"\"},{\"name\":\"经停站3到达时间\",\"value\":\"\"},{\"name\":\"起飞-到达3\",\"value\":\"\"},{\"name\":\"终点站\",\"value\":\"\"},{\"name\":\"旅客序号\",\"value\":\"516380381\"},{\"name\":\"订座记录BPNR\",\"value\":\"mlc6s6\"},{\"name\":\"订座记录CPNR\",\"value\":\"hq3eh3\"},{\"name\":\"舱位代码\",\"value\":\"\"},{\"name\":\"更新时间\",\"value\":\"2013-09-09 17:50:00\"}],\"docid\":\"2895206\",\"ftime\":\"20130909000000\",\"idCard\":\"21090519710206434x\",\"img\":\"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\",\"label\":\"\"},{\"carnum\":\"\",\"cartype\":\"\",\"columns\":[{\"name\":\"证件号码\",\"value\":\"21090519710206434x\"},{\"name\":\"旅客中文名\",\"value\":\"张淑华\"},{\"name\":\"旅客英文名\",\"value\":\"zhangshuhua\"},{\"name\":\"航空公司\",\"value\":\"中国国际航空公司\"},{\"name\":\"航班号\",\"value\":\"8955\"},{\"name\":\"座位号\",\"value\":\"36c\"},{\"name\":\"出发地\",\"value\":\"大连\"},{\"name\":\"目的地\",\"value\":\"杭州\"},{\"name\":\"航班日期\",\"value\":\"2013-09-06\"},{\"name\":\"国籍\",\"value\":\"中国\"},{\"name\":\"联系方式\",\"value\":\"\"},{\"name\":\"始发站起飞时间\",\"value\":\"1340\"},{\"name\":\"经停站1到达时间\",\"value\":\"1520\"},{\"name\":\"旅客名\",\"value\":\"\"},{\"name\":\"旅客姓\",\"value\":\"\"},{\"name\":\"公民身份号码\",\"value\":\"\"},{\"name\":\"经停站1\",\"value\":\"杭州\"},{\"name\":\"经停站1起飞时间\",\"value\":\"1640\"},{\"name\":\"经停站2到达时间\",\"value\":\"1745\"},{\"name\":\"证件类型\",\"value\":\"身份证\"},{\"name\":\"航班类型\",\"value\":\"国内\"},{\"name\":\"行李牌\",\"value\":\"\"},{\"name\":\"登机牌号\",\"value\":\"bn116\"},{\"name\":\"航班序号\",\"value\":\"4197358\"},{\"name\":\"始发站\",\"value\":\"大连\"},{\"name\":\"起飞-到达1\",\"value\":\"\"},{\"name\":\"起飞-到达2\",\"value\":\"\"},{\"name\":\"经停站2\",\"value\":\"xmn\"},{\"name\":\"经停站2起飞时间\",\"value\":\"\"},{\"name\":\"经停站3到达时间\",\"value\":\"\"},{\"name\":\"起飞-到达3\",\"value\":\"\"},{\"name\":\"终点站\",\"value\":\"\"},{\"name\":\"旅客序号\",\"value\":\"516161328\"},{\"name\":\"订座记录BPNR\",\"value\":\"nh1e5r\"},{\"name\":\"订座记录CPNR\",\"value\":\"\"},{\"name\":\"舱位代码\",\"value\":\"\"},{\"name\":\"更新时间\",\"value\":\"2013-09-06 15:39:38\"}],\"docid\":\"2552045\",\"ftime\":\"20130906000000\",\"idCard\":\"21090519710206434x\",\"img\":\"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\",\"label\":\"\"}],\"ename\":\"ZHXX_HK.JC_RY_PASG\",\"hits\":20,\"timecost\":0.216}";
			message.put("lists", JSONObject.fromObject(responseStr));
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
		case 501: //消息提醒的删除
			responseStr = "";
			message.put("data", "");
			break;
		default:
			break;
		}
		System.out.println(message);
		
		message.remove("responseCode");
		message.remove("json");
		message.remove("isSuccess");
		
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
