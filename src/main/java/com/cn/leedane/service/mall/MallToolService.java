package com.cn.leedane.service.mall;

import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ResponseModel;
import com.google.zxing.WriterException;
import com.jd.open.api.sdk.JdException;
import com.taobao.api.ApiException;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 商城工具service接口类
 * @author LeeDane
 * 2019年11月12日 下午6:31:23
 * version 1.0
 */
@Transactional
public interface MallToolService<T extends IDBean>{


	/**
	 * 长链接转化成短链接、淘宝口令、二维码等
	 * @param productId
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResponseModel transform(String productId, JSONObject jo, UserBean user, HttpRequestInfoBean request) throws WriterException, JdException, ApiException, PddException;


	/**
	 * 解析链接中的ID信息
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResponseModel parseUrlGetId(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws JdException, ApiException;
}
