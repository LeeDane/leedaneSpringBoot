package com.cn.leedane.handler;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.controller.RoleController;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.mapper.EventMapper;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.model.EventAllBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.UserSettingBean;
import com.cn.leedane.model.UserTokenBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.SerializationUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 大事件的处理类
 * @author LeeDane
 * 2019年7月22日 下午10:24:12
 * Version 1.0
 */
@Component
public class EventHandler extends BaseCacheHandler<EventAllBean> {

	@Autowired
	private EventMapper eventMapper;

	@Override
	protected EventAllBean getBean() {
		return new EventAllBean();
	}

	@Override
	public EventAllBean getT(Object... params) {
		List<Map<String, Object>> data = eventMapper.all();
		if(CollectionUtil.isNotEmpty(data)) {
			EventAllBean eventAllBean = new EventAllBean();
			eventAllBean.setList(data);
			return eventAllBean;
		}
		return null;
	}

	/**
	 * 获取所有大事件对象
	 * @return
	 */
	public List<Map<String, Object>> get(Object ... params) {
		Object obj  = super.get(params);
		if(obj != null){
			return ((EventAllBean) obj).getList();
		}
		return new ArrayList<>();
	}

	/**
	 * 获取所有的事件在redis的key
	 * @return
	 */
	@Override
	public String getKey(Object ... params) {
		return ConstantsUtil.All_EVENT_REDIS;
	}
}
