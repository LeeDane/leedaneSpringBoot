package com.cn.leedane.service.impl.manage;

import com.cn.leedane.handler.mall.S_ProductHandler;
import com.cn.leedane.mall.jingdong.api.DetailBigFieldApi;
import com.cn.leedane.mall.model.ProductPromotionLinkBean;
import com.cn.leedane.mall.model.SearchProductRequest;
import com.cn.leedane.mall.model.SearchProductResult;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.mall.pdd.api.DetailSimpleApi;
import com.cn.leedane.mall.taobao.api.RecommendProductApi;
import com.cn.leedane.mall.taobao.api.SearchProductApi;
import com.cn.leedane.mall.taobao.other.AlimamaShareLink;
import com.cn.leedane.mapper.mall.S_ProductMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.mall.MallRoleCheckService;
import com.cn.leedane.service.mall.MallSearchService;
import com.cn.leedane.service.mall.S_TaobaoService;
import com.cn.leedane.service.manage.ManageMyService;
import com.cn.leedane.utils.*;
import com.suning.api.exception.SuningApiException;
import com.taobao.api.ApiException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	private EmailUtil emailUtil;
	@Override
	public Map<String, Object> bindEmail(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception {
		logger.info("ManageMyServiceImpl-->bindEmail():jo="+jo);
		ResponseMap message = new ResponseMap();

		String email = JsonUtil.getStringValue(jo, "email");
		String password = JsonUtil.getStringValue(jo, "pwd");

		ParameterUnspecificationUtil.checkNullString(email, "email must not null.");
		ParameterUnspecificationUtil.checkNullString(email, "password must not null.");
		String oldEmail = user.getEmail();
		if(StringUtil.isNotNull(oldEmail) && oldEmail.equalsIgnoreCase(email)){
			message.put("message", "与原先的邮箱一致，不需要重新绑定。");
			message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
			return message.getMap();
		}

		try {
			byte[] decodedData = RSACoder.decryptByPrivateKey(password, RSAKeyUtil.getInstance().getPrivateKey());
			password = new String(decodedData, "UTF-8");
			if(!user.getPassword().equalsIgnoreCase(MD5Util.compute(password))){
				message.put("message", "登录密码验证失败，请重试");
				message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
				return message.getMap();
			}
		} catch (BadPaddingException e1) {
			e1.printStackTrace();
			message.put("message", "该页面过期, 请刷新当前页面，重新操作！");
			message.put("responseCode", EnumUtil.ResponseCode.RSA加密解密异常.value);
			return message.getMap();
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
		String content = "您需要跟LeeDane系统绑定该邮箱，如非本人操作，请勿理会或者联系<a href='http://onlyloveu.top/cc/35'>管理员</a>处理。</br>请点击链接<a href='http://127.0.0.1:8089/my/manage/my/email/bind/click?param="+ cipherText +"&start="+ start +"&end="+ end +"'>点击</a>以完成绑定工作， 该链接1个小时有效。";
		emailUtil.initData(user, email, content, "绑定电子邮箱通知");
		emailUtil.sendMore();
		message.put("isSuccess", true);
		message.put("message", "已经发送，请注意查收！");
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		return message.getMap();
	}
}
