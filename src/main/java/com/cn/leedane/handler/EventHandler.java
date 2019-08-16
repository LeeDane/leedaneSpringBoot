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
public class EventHandler {

	@Autowired
	private EventMapper eventMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;

	/**
	 * 获取所有大事件对象
	 * @return
	 */
	public List<Map<String, Object>> all(){
		EventAllBean datas = null;
		String key = getAllEventKey();
		Object obj = systemCache.getCache(key);
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					datas =  (EventAllBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), EventAllBean.class);
					if(datas != null){
						systemCache.addCache(key, datas, true);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						List<Map<String, Object>> data = eventMapper.all();
						if(CollectionUtil.isNotEmpty(data)){
							try {
								datas = new EventAllBean();
								datas.setList(data);
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(datas));
								systemCache.addCache(key, datas, true);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				List<Map<String, Object>> data = eventMapper.all();
				if(CollectionUtil.isNotEmpty(data)){
					try {
						datas = new EventAllBean();
						datas.setList(data);
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(datas));
						systemCache.addCache(key, datas, true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			datas = (EventAllBean) obj;
		}
		return datas != null ? datas.getList(): new ArrayList<>();
	}
	
	
	/**
	 * 根据用户ID删除该用户的cache和redis缓存
	 * @return
	 */
	public boolean deleteAllEventCache(){
		String key = getAllEventKey();
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取所有的事件在redis的key
	 * @return
	 */
	public static String getAllEventKey(){
		return ConstantsUtil.All_EVENT_REDIS;
	}
}
