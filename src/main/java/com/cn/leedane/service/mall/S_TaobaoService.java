package com.cn.leedane.service.mall;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.google.zxing.WriterException;
import com.taobao.api.ApiException;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 淘宝相关service接口类(已经废弃，请参考MallSearchService使用）
 * @author LeeDane
 * 2017年12月6日 下午6:31:23
 * version 1.0
 */
@Deprecated
@Transactional
public interface S_TaobaoService <T extends IDBean>{
	/**
	 * 搜索商品
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  Map<String,Object> search(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 构建淘宝分享的链接
	 * @param taobaoId
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> buildShare(String taobaoId,UserBean user, HttpRequestInfoBean request);

	/**
	 *  长链接转化成短链接、淘宝口令、二维码等
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 * @throws ApiException
	 */
	/*@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  Map<String,Object> transform(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws ApiException, WriterException;*/

}
