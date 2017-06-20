package com.cn.leedane.service.impl.circle;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.mapper.circle.CircleMapper;
import com.cn.leedane.mapper.circle.CirclePostMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.circle.CirclePostBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.circle.CirclePostService;

/**
 * 帖子service实现类
 * @author LeeDane
 * 2017年6月20日 下午6:09:27
 * version 1.0
 */
@Service("circlePostService")
public class CirclePostServiceImpl extends AdminRoleCheckService implements CirclePostService<CirclePostBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private CirclePostMapper circlePostMapper;
	
	@Autowired
	private CircleMapper circleMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
}
