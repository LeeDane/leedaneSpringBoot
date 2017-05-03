package com.cn.leedane.handler;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.mapper.LinkManageMapper;
import com.cn.leedane.model.LinkManageBean;
import com.cn.leedane.model.LinkManagesBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 链接权限管理
 * @author LeeDane
 * 2017年4月10日 下午5:15:37
 * version 1.0
 */
@Component
public class LinkManageHandler {
	@Autowired
	private LinkManageMapper linkManageMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	/**
	 * 获取所有的链接
	 * @return
	 */
	public LinkManagesBean getAllLinks(){
		
		LinkManagesBean linkManageBeans = null;
		String key = getLinkManageKey();
	
		if(redisUtil.hasKey(key)){
			try {
				return (LinkManagesBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), LinkManagesBean.class);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}else{
			List<LinkManageBean> beans = linkManageMapper.getAllLinks(ConstantsUtil.STATUS_NORMAL);
			if(CollectionUtil.isNotEmpty(beans)){
				linkManageBeans = new LinkManagesBean();
				linkManageBeans.setLinkManageBean(beans);
				try {
					redisUtil.addSerialize(key, SerializeUtil.serializeObject(linkManageBeans));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return linkManageBeans;
	}
	
	/**
	 * 获取心情在redis的key
	 * @param blogId
	 * @return
	 */
	public static String getLinkManageKey(){
		return ConstantsUtil.LINK_MANAGE_REDIS;
	}
}
