package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;
/**
 * base64上传临时文件的Service类
 * @author LeeDane
 * 2016年7月12日 上午11:35:23
 * Version 1.0
 */
@Transactional
public interface TemporaryBase64Service<T extends IDBean>{
	
	/**
	 * 保存临时上传的base64字符串文件
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public boolean saveBase64Str(JSONObject jo, UserBean user, HttpRequestInfoBean request);
}
