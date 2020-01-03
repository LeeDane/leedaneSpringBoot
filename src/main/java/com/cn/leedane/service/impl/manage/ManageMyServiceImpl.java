package com.cn.leedane.service.impl.manage;

import com.cn.leedane.exception.OperateException;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.MyTagsMapper;
import com.cn.leedane.mapper.Oauth2Mapper;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.notice.model.Email;
import com.cn.leedane.notice.send.INoticeFactory;
import com.cn.leedane.notice.send.NoticeFactory;
import com.cn.leedane.notice.send.SmsNotice;
import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.manage.ManageMyService;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.EsIndexAddThread;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import java.util.*;

/**
 * 我的管理信息的service的实现类
 * @author LeeDane
 * 2017年12月6日 下午7:52:25
 * version 1.0
 */
@Service("manageMyService")
public class ManageMyServiceImpl implements ManageMyService<IDBean> {
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private SmsNotice smsNotice;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserHandler userHandler;

	@Autowired
	private Oauth2Mapper oauth2Mapper;

	@Autowired
	private MyTagsMapper myTagsMapper;

	@Override
	public ResponseModel bindEmail(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception {
		logger.info("ManageMyServiceImpl-->bindEmail():jo="+jo);
		String email = JsonUtil.getStringValue(jo, "email");
		String password = JsonUtil.getStringValue(jo, "pwd");

		ParameterUnspecificationUtil.checkNullString(email, "email must not null.");
		ParameterUnspecificationUtil.checkNullString(password, "password must not null.");
		String oldEmail = user.getEmail();
		if(StringUtil.isNotNull(oldEmail) && oldEmail.equalsIgnoreCase(email))
			return new ResponseModel().error().message("与原先的邮箱一致，不需要重新绑定。");

		try {
			byte[] decodedData = RSACoder.decryptByPrivateKey(password, RSAKeyUtil.getInstance().getPrivateKey());
			password = new String(decodedData, "UTF-8");
			if(!user.getPassword().equalsIgnoreCase(MD5Util.compute(password)))
				return new ResponseModel().error().message("登录密码验证失败，请重试");

		} catch (BadPaddingException e1) {
			e1.printStackTrace();
			return new ResponseModel().error().message("该页面过期, 请刷新当前页面，重新操作！").code(EnumUtil.ResponseCode.RSA加密解密异常.value);
		}

		Date date = new Date();
		long start = date.getTime(); //开始时间

		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.MINUTE, 60); //60分钟过期
		long end = calendar2.getTimeInMillis(); //过期时间
		Map<String, Object> params = new HashMap<>();
		params.put("start", start);
		params.put("end", end);
		params.put("email", email);
		params.put("uid", user.getId());
		String cipherText = CommonUtil.sm4Encrypt(params);

		INoticeFactory factory = new NoticeFactory();
		Email emailBean = new Email();
		String content = "您需要跟LeeDane系统绑定该邮箱，如非本人操作，请勿理会或者联系<a href='"+ LeedanePropertiesConfig.getSystemUrl() +"/cc/35'>管理员</a>处理。</br>请点击链接<a href='"+ LeedanePropertiesConfig.getSystemUrl() +"/my/manage/my/email/bind/click?param="+ cipherText +"&start="+ start +"&end="+ end +"'>点击</a>以完成绑定工作， 该链接1个小时有效。";
		emailBean.setContent(content);
		//emailBean.setFrom(user);
		emailBean.setSubject("绑定电子邮箱通知");
		UserBean toUser = new UserBean();
		toUser.setEmail(email);
		Set<UserBean> replyTo = new HashSet<>();
		replyTo.add(toUser);
		emailBean.setReplyTo(replyTo);
		factory.create(EnumUtil.NoticeType.邮件).send(emailBean);

		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"想绑定新的电子邮箱，地址是：", email).toString(), "bindEmail()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
//		return message.getMap();
		return new ResponseModel().ok().message("已经发送，请注意查收！");
	}

	@Override
	public ResponseModel bindPhone(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception {
		logger.info("ManageMyServiceImpl-->bindPhone():jo="+jo);
		String phone = JsonUtil.getStringValue(jo, "phone");
		String code = JsonUtil.getStringValue(jo, "code");
		String password = JsonUtil.getStringValue(jo, "pwd");
		String type = JsonUtil.getStringValue(jo, "type");
		ParameterUnspecificationUtil.checkPhone(phone, "phone is null or not a phone.");
		ParameterUnspecificationUtil.checkNullString(code, "code is null or not a phone.");
		ParameterUnspecificationUtil.checkNullString(password, "password must not null.");
		ParameterUnspecificationUtil.checkNullString(type, "sms type must not null.");

		String oldPhone = user.getMobilePhone();
		if(StringUtil.isNotNull(oldPhone) && oldPhone.equalsIgnoreCase(phone))
			return new ResponseModel().error().message("与原先的手机号码一致，不需要重新绑定。");

		try {
			byte[] decodedData = RSACoder.decryptByPrivateKey(password, RSAKeyUtil.getInstance().getPrivateKey());
			password = new String(decodedData, "UTF-8");
			if(!user.getPassword().equalsIgnoreCase(MD5Util.compute(password)))
				return new ResponseModel().error().message("登录密码验证失败，请重试。");
		} catch (BadPaddingException e1) {
			e1.printStackTrace();
			return new ResponseModel().error().message("该页面过期, 请刷新当前页面，重新操作！").code(EnumUtil.ResponseCode.RSA加密解密异常.value);
		}

		if(!smsNotice.check(phone, code, type))
			return new ResponseModel().error().message("短信验证码校验失败，请检查是否正确或者过期。");

		//这里必须要重新获取一份UserBean对象，不然直接修改参数user会导致shiro里面的user有值，Java是值传递
		UserBean updateUser = userMapper.findById(UserBean.class, user.getId());
		updateUser.setMobilePhone(phone);
		boolean success = false;
		try{
			success = userMapper.update(updateUser) > 0;
		}catch (DuplicateKeyException e) {
			return new ResponseModel().error().error().message("绑定失败，手机号码已经被绑定！");
		}
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"想绑定新手机，号码是：", phone, ",结果是："+ StringUtil.getSuccessOrNoStr(success)).toString(), "bindPhone()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(success){
			//添加ES缓存
			new ThreadUtil().singleTask(new EsIndexAddThread<UserBean>(user));
			//把Redis缓存的信息删除掉
			userHandler.deleteUserDetail(user.getId());
			user = updateUser; //由于系统没有退出，需要重新赋值
			return new ResponseModel().ok().message("绑定成功！");
		}
		return new ResponseModel().message("绑定失败，请稍后重试。");
	}

	@Override
	public ResponseModel thirdUnBind(long oid, JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMyServiceImpl-->thirdUnBind():oid="+ oid +"&jo="+jo);
		ParameterUnspecificationUtil.checkLong(oid, "oid must not null.");

		Oauth2Bean oauth2Bean = oauth2Mapper.findById(Oauth2Bean.class, oid);
		ParameterUnspecificationUtil.checkObject(oauth2Bean, "oauth object must not null.");

		if(oauth2Bean.getCreateUserId() != user.getId())
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));

		String platform = oauth2Bean.getPlatform();
		boolean success = oauth2Mapper.delete(oauth2Bean) > 0;
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"解除绑定第三方平台：", platform , ",结果是："+ StringUtil.getSuccessOrNoStr(success)).toString(), "thirdUnBind()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(success)
			return new ResponseModel().ok().message("解除绑定成功！");

		return new ResponseModel().error().message("解除绑定失败，请稍后重试。");
	}

	@Override
	public ResponseModel saveTags(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMyServiceImpl-->saveTags():jo="+jo);
		ResponseMap message = new ResponseMap();

		String tags = JsonUtil.getStringValue(jo, "tags");
		ParameterUnspecificationUtil.checkNullString(tags, "tags must not null.");

		List<MyTagsBean> tagsBeans = new ArrayList<>();
		String[] tagArrsy = tags.split(",");
		if(tagArrsy.length > 10)
			throw new OperateException("目前一个人最多只能添加10个标签。");
		for(String tag: tagArrsy){
			MyTagsBean myTagsBean = new MyTagsBean();
			myTagsBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			myTagsBean.setCreateUserId(user.getId());
			myTagsBean.setCreateTime(new Date());
			myTagsBean.setModifyUserId(user.getId());
			myTagsBean.setModifyTime(new Date());
			myTagsBean.setTagName(tag);
			tagsBeans.add(myTagsBean);
		}
		myTagsMapper.deleteTags(user.getId());
		boolean success = myTagsMapper.batchSave(tagsBeans) == tagsBeans.size();
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"保存新的标签组合：", tags , ",结果是："+ StringUtil.getSuccessOrNoStr(success)).toString(), "saveTags()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(success){
			return new ResponseModel().ok().message("添加成功！");
		}
		return new ResponseModel().error().message("添加失败，请稍后重试。");
	}
}
