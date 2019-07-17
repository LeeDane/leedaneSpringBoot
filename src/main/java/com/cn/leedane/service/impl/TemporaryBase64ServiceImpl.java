package com.cn.leedane.service.impl;

import com.cn.leedane.mapper.TemporaryBase64Mapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.TemporaryBase64Bean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.TemporaryBase64Service;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.JsonUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
/**
 * base64上传临时文件service的实现类
 * @author LeeDane
 * 2016年7月12日 下午2:11:18
 * Version 1.0
 */
@Service("temporaryBase64Service")
public class TemporaryBase64ServiceImpl implements TemporaryBase64Service<TemporaryBase64Bean>{
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private TemporaryBase64Mapper temporaryBase64Mapper;
	
	@Override
	public boolean saveBase64Str(JSONObject jo, UserBean user,
			HttpRequestInfoBean request){
		logger.info("TemporaryBase64ServiceImpl-->saveBase64Str():jo="+jo.toString());
		int start = JsonUtil.getIntValue(jo, "start");
		int end = JsonUtil.getIntValue(jo, "end");
		String uuid = JsonUtil.getStringValue(jo, "uuid");
		int order = JsonUtil.getIntValue(jo, "order");
		String content = JsonUtil.getStringValue(jo, "content");	
		TemporaryBase64Bean base64Bean = new TemporaryBase64Bean();
		base64Bean.setContent(content);
		base64Bean.setCreateTime(new Date());
		base64Bean.setCreateUserId(user.getId());
		base64Bean.setEnd(end);
		base64Bean.setTempOrder(order);
		base64Bean.setTableName(DataTableType.心情.value);
		base64Bean.setStart(ConstantsUtil.STATUS_NORMAL);
		base64Bean.setStart(start);
		base64Bean.setUuid(uuid);
		return temporaryBase64Mapper.save(base64Bean) > 0;
	}
}
