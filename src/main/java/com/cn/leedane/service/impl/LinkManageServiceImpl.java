package com.cn.leedane.service.impl;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.mapper.LinkManageMapper;
import com.cn.leedane.model.LinkManageBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.service.LinkManageService;
import com.cn.leedane.service.OperateLogService;

/**
 * 链接权限管理service实现类
 * @author LeeDane
 * 2017年4月10日 下午4:49:07
 * version 1.0
 */
@Service("linkManageService")
@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class LinkManageServiceImpl implements LinkManageService<LinkManageBean> {
	
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private LinkManageMapper linkManageMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;


	@Override
	public List<LinkManageBean> getAllLinks() {
		logger.info("LinkManageServiceImpl-->getAllLinks()");
		return linkManageMapper.getAllLinks();
	}
}