package com.cn.leedane.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.handler.CloudStoreHandler;
import com.cn.leedane.handler.ZXingCodeHandler;
import com.cn.leedane.model.EmailBean;
import com.cn.leedane.model.FilePathBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.EmailSend;
import com.cn.leedane.rabbitmq.send.ISend;
import com.cn.leedane.service.AppVersionService;
import com.cn.leedane.utils.Base64Util;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.RSAKeyUtil;
import com.cn.leedane.utils.EnumUtil.EmailType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.wechat.util.HttpRequestUtil;
import com.google.zxing.WriterException;
import com.qiniu.util.Auth;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.tl)
public class ToolController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private AppVersionService<FilePathBean> appVersionService;
	
	/**
	 * 翻译
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/fanyi", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> fanyi(HttpServletRequest request) throws IOException{
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		String content = JsonUtil.getStringValue(getJsonFromMessage(message), "content");
		String msg = HttpRequestUtil.sendAndRecieveFromYoudao(content);
		msg = StringUtil.getYoudaoFanyiContent(msg);
		message.put("isSuccess", true);
		message.put("message", msg);
		return message.getMap();
	}
	
	/**
	 * 发送邮件通知的接口
	 * @return
	 */
	@RequestMapping(value = "/sendEmail", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> sendEmail(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		//{"id":1, "to_user_id": 2}
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		JSONObject json = getJsonFromMessage(message);
		String toUserId = JsonUtil.getStringValue(json, "to_user_id");//接收邮件的用户的Id，必须
		String content = JsonUtil.getStringValue(json, "content"); //邮件的内容，必须
		String object = JsonUtil.getStringValue(json, "object"); //邮件的标题，必须
		if(StringUtil.isNull(toUserId) || StringUtil.isNull(content) || StringUtil.isNull(object)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			return message.getMap();
		}
		
		UserBean toUser = userService.findById(StringUtil.changeObjectToInt(toUserId));
		if(toUser == null){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.该用户不存在.value));
			message.put("responseCode", EnumUtil.ResponseCode.该用户不存在.value);
			return message.getMap();
		}
		if(StringUtil.isNull(toUser.getEmail())){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.对方还没有绑定电子邮箱.value));
			message.put("responseCode", EnumUtil.ResponseCode.对方还没有绑定电子邮箱.value);
			return message.getMap();
		}
		
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

		try {
			ISend send = new EmailSend(emailBean);
			SendMessage sendMessage = new SendMessage(send);
			sendMessage.sendMsg();//发送消息队列到消息队列
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.邮件已经发送.value));

		} catch (Exception e) {
			e.printStackTrace();
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.邮件发送失败.value)+",失败原因是："+e.toString());
			message.put("responseCode", EnumUtil.ResponseCode.邮件发送失败.value);
		}
		return message.getMap();
	}
	
	/**
	 * 发送信息
	 * @return
	 */
	@RequestMapping(value = "/sendMsg", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> sendMessage(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		//type: 1为通知，2为邮件，3为私信，4为短信
		message.putAll(userService.sendMessage(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取七牛服务器的token凭证
	 * @return
	 */
	@RequestMapping(value = "/qiNiuToken", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getQiNiuToken(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.put("isSuccess", true);
		message.put("message", getToken());
		return message.getMap();
	}
	
	/**
	 * 获取服务器上的公钥凭证
	 * @return
	 */
	@RequestMapping(value = "/publicKey", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> publicKey(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.put("isSuccess", true);
		message.put("message", RSAKeyUtil.getInstance().getPublicKey());
		return message.getMap();
	}
	
	/**
	 * 根据图片地址获取网络图片流
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/networdImage", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getNetwordImage(@RequestParam("url") String imgUrl, HttpServletRequest request){
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
			message.put("isSuccess", true);
			message.put("message", ConstantsUtil.BASE64_JPG_IMAGE_HEAD + new String(Base64Util.encode(out.toByteArray())));
			return message.getMap();
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
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
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
	 * @param request
	 * @param response
     * @throws WriterException 
	 */
	@RequestMapping(value = "/loginQrCode", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> loginQrCode(@RequestParam("cnid") String cnid, HttpServletRequest request) throws WriterException{
		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(cnid)){
			message.put("message", "连接id为空");
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			return message.getMap();
		}
		String bp = request.getScheme()+"://"+request.getServerName() +
				(request.getServerName().endsWith("com")? "" : ":"+request.getServerPort())
				+request.getContextPath()+"/";
		String bpath = bp +"dl?scan_login=" + cnid;
		message.put("message", ZXingCodeHandler.createQRCode(bpath, 200));
		message.put("isSuccess", true);
		return message.getMap();
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
		message.putAll(appVersionService.getNewest(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();	
	}
}
