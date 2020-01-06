package com.cn.leedane.controller;

import com.cn.leedane.handler.CitysHandle;
import com.cn.leedane.handler.CloudStoreHandler;
import com.cn.leedane.handler.ZXingCodeHandler;
import com.cn.leedane.mapper.util.CityMapper;
import com.cn.leedane.mapper.util.CountyMapper;
import com.cn.leedane.mapper.util.ProvinceMapper;
import com.cn.leedane.model.EmailBean;
import com.cn.leedane.model.FilePathBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.util.CityBean;
import com.cn.leedane.model.util.ProvinceBean;
import com.cn.leedane.notice.NoticeException;
import com.cn.leedane.notice.model.SMS;
import com.cn.leedane.notice.send.INoticeFactory;
import com.cn.leedane.notice.send.NoticeFactory;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.EmailSend;
import com.cn.leedane.rabbitmq.send.ISend;
import com.cn.leedane.service.AppVersionService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.EmailType;
import com.cn.leedane.wechat.util.HttpRequestUtil;
import com.google.zxing.WriterException;
import com.qiniu.util.Auth;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.tl)
public class ToolController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private AppVersionService<FilePathBean> appVersionService;

	@Autowired
	private CitysHandle citysHandle;

	@Autowired
	private ProvinceMapper provinceMapper;

	@Autowired
	private CityMapper cityMapper;
	@Autowired
	private CountyMapper countyMapper;
	/**
	 * 翻译
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/fanyi", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel fanyi(Model model, HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		
		checkRoleOrPermission(model, request);
		String content = JsonUtil.getStringValue(getJsonFromMessage(message), "content");
		String msg = HttpRequestUtil.sendAndRecieveFromYoudao(content);
		return new ResponseModel().ok().message(StringUtil.getYoudaoFanyiContent(msg));
	}
	
	/**
	 * 发送邮件通知的接口
	 * @return
	 */
	@RequestMapping(value = "/sendEmail", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel sendEmail(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		//{"id":1, "to_user_id": 2}
		if(!checkParams(message, request))
			return message.getModel();
		
		checkRoleOrPermission(model, request);
		JSONObject json = getJsonFromMessage(message);
		String toUserId = JsonUtil.getStringValue(json, "to_user_id");//接收邮件的用户的Id，必须
		String content = JsonUtil.getStringValue(json, "content"); //邮件的内容，必须
		String object = JsonUtil.getStringValue(json, "object"); //邮件的标题，必须
		if(StringUtil.isNull(toUserId) || StringUtil.isNull(content) || StringUtil.isNull(object))
			return new ResponseModel().error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value)).code(EnumUtil.ResponseCode.参数不存在或为空.value);
		
		UserBean toUser = userService.findById(StringUtil.changeObjectToInt(toUserId));
		if(toUser == null)
			return new ResponseModel().error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该用户不存在.value)).code(EnumUtil.ResponseCode.该用户不存在.value);

		if(StringUtil.isNull(toUser.getEmail()))
			return new ResponseModel().error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.对方还没有绑定电子邮箱.value)).code(EnumUtil.ResponseCode.对方还没有绑定电子邮箱.value);
		
		//String content = "用户："+user.getAccount() +"已经添加您为好友，请您尽快处理，谢谢！";
		//String object = "LeeDane好友添加请求确认";
		Set<UserBean> set = new HashSet<UserBean>();		
		set.add(toUser);
		EmailBean emailBean = new EmailBean();
		emailBean.setContent(content);
		emailBean.setCreateTime(new Date());
		emailBean.setFrom(getUserFromMessage(message));
		emailBean.setSubject(object);
		emailBean.setReplyTo(set);
		emailBean.setType(EmailType.新邮件.value); //新邮件
		ResponseModel responseModel = new ResponseModel();
		try {
			ISend send = new EmailSend(emailBean);
			SendMessage sendMessage = new SendMessage(send);
			sendMessage.sendMsg();//发送消息队列到消息队列
			responseModel.ok().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.邮件已经发送.value));

		} catch (Exception e) {
			e.printStackTrace();
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.邮件发送失败.value)+",失败原因是："+e.toString()).code(EnumUtil.ResponseCode.邮件发送失败.value);
		}
		return responseModel;
	}
	
	/**
	 * 发送信息
	 * @return
	 */
	@RequestMapping(value = "/sendMsg", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel sendMessage(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		
		checkRoleOrPermission(model, request);
		//type: 1为通知，2为邮件，3为私信，4为短信
		return userService.sendMessage(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 发送验证码(有效期120秒)
	 * @param phone  手机号码
	 * @param type  业务类型（0：验证码 ， 1：通知、）
	 * @param model
	 * @param request
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/sms/sendCode", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel sendCode(@RequestParam("phone") String phone, @RequestParam("type") String type, Model model, HttpServletRequest request) throws NoticeException {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		
		checkRoleOrPermission(model, request);
		//发送短信
		SMS sms = new SMS();
		sms.setType(type);
		sms.setExpire(60 * 10); //10分钟过期
		UserBean toUser = new UserBean();
		toUser.setMobilePhone(phone);
		sms.setToUser(toUser);
		INoticeFactory factory = new NoticeFactory();
		boolean success = factory.create(EnumUtil.NoticeType.短信).send(sms);
		if(success)
			return new ResponseModel().ok().message("发送成功，请留意手机查看");
		else
			return new ResponseModel().message("发送失败，请稍后重试。");
	}
	
	/**
	 * 获取七牛服务器的token凭证
	 * @return
	 */
	@RequestMapping(value = "/qiNiuToken", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel getQiNiuToken(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		
		checkRoleOrPermission(model, request);
		return new ResponseModel().ok().message(getToken());
	}
	
	/**
	 * 获取服务器上的公钥凭证
	 * @return
	 */
	@RequestMapping(value = "/publicKey", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel publicKey(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return new ResponseModel().ok().message(RSAKeyUtil.getInstance().getPublicKey());
	}

	/**
	 * 根据图片地址获取网络图片流
	 * @param imgUrl
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/networdImage", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel getNetwordImage(@RequestParam("url") String imgUrl, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		InputStream is = null;
		ByteArrayOutputStream out = null;
		try {			
			URL url = new URL(imgUrl);
			URLConnection uc = url.openConnection(); 
			is = uc.getInputStream(); 	    
			out = new ByteArrayOutputStream(); 
			int i=0;
			while((i = is.read())!=-1)   { 
				out.write(i); 
			}
			return new ResponseModel().ok().message(ConstantsUtil.BASE64_JPG_IMAGE_HEAD + new String(Base64Util.encode(out.toByteArray())));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return new ResponseModel().error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value)).code(EnumUtil.ResponseCode.服务器处理异常.value);
	}
	
	/**
     * 获取token 本地生成
     * 
     * @return
     */
    private String getToken() {
    	Auth auth = Auth.create(CloudStoreHandler.ACCESSKEY, CloudStoreHandler.SECRETKEY);
    	return auth.uploadToken(CloudStoreHandler.BUCKETNAME);
    }

	/**
	 * 根据图片地址获取网络图片流
	 * @param cnid
	 * @param request
	 * @return
	 * @throws WriterException
	 */
	@RequestMapping(value = "/loginQrCode", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel loginQrCode(@RequestParam("cnid") String cnid, HttpServletRequest request) throws WriterException{
		if(StringUtil.isNull(cnid))
			return new ResponseModel().error().message("连接id为空").code(EnumUtil.ResponseCode.参数不存在或为空.value);
		String bp = request.getScheme()+"://"+request.getServerName() +
				(request.getServerName().endsWith("com")? "" : ":"+request.getServerPort())
				+request.getContextPath()+"/";
		String bpath = bp +"dl?scan_login=" + cnid;
		return new ResponseModel().ok().message(ZXingCodeHandler.createQRCode(bpath, 200));
	}
	
	/**
	 * 获取最新的版本信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/newest", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getNewest(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		message.put("message", "获取最新版本失败");
		message.putAll(appVersionService.getNewest(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();	
	}

	/**
	 * 获取系统配置的详细信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/system/info", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel getSystemInfo(HttpServletRequest request) throws InterruptedException {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		//获得所有的环境变量
//       Map<String, String> env = System.getenv();
		//获得指定的环境变量
//       String path = System.getenv("path");
		return new ResponseModel().ok().message(CommonUtil.execMult("java -version"));
	}

	/**
	 * 获取目前所有的省数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/admin/util/province", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel utilProvince(Model model, HttpServletRequest request) throws InterruptedException {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();

		checkRoleOrPermission(model, request);

		provinceMapper.emptyTableData("t_util_province");//清空表数据
		citysHandle.parseProvince();
		return new ResponseModel().ok().message("本次已经处理完成");
	}

	/**
	 * 获取目前所有的市数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/admin/util/city", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel utilCity(Model model, HttpServletRequest request) throws InterruptedException {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();

		checkRoleOrPermission(model, request);

		cityMapper.emptyTableData("t_util_city");//清空表数据
		//读取所有的省
		List<ProvinceBean> provinces = provinceMapper.getProvinces();
		for(ProvinceBean province: provinces){
			try {
				citysHandle.parseCity(province.getCode());
				Thread.sleep(15000); //休息15秒
			} catch (IOException e) {
				e.printStackTrace();
				Thread.sleep(15000); //休息15秒后重试
				try {
					citysHandle.parseCity(province.getCode());
				} catch (IOException e1) {
					e1.printStackTrace();
					return new ResponseModel().ok().message("程序无法处理:"+ province.getCode());
				}
			}

		}
		return new ResponseModel().ok().message("本次已经处理完成");
	}

	/**
	 * 获取目前所有的县数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/admin/util/county", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel utilCounty(Model model, HttpServletRequest request) throws InterruptedException {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();

		checkRoleOrPermission(model, request);
		countyMapper.emptyTableData("t_util_county");//清空表数据
		//读取所有的省
		List<CityBean> citys = cityMapper.getCitys();
		for(CityBean city: citys){
			try {
				citysHandle.parseCounty(city.getCode());
				Thread.sleep(15000); //休息15秒
			} catch (IOException e) {
				e.printStackTrace();
				Thread.sleep(15000); //休息15秒后重试
				try {
					citysHandle.parseCounty(city.getCode());
				} catch (IOException e1) {
					e1.printStackTrace();
					return new ResponseModel().ok().message("程序无法处理:"+ city.getCode());
				}
			}

		}
		return new ResponseModel().ok().message("本次已经处理完成");
	}
}
