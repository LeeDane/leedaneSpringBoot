package com.cn.leedane.test;

import java.util.Date;

import javax.annotation.Resource;

import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.utils.EnumUtil;
import org.junit.Test;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.OperateLogMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.task.spring.scheduling.UploadQiniuCloud;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.StringUtil;

/**
 * 操作日志相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:12:47
 * Version 1.0
 */
public class OperateLogTest extends BaseTest {
	@Resource
	private OperateLogMapper operateLogMapper;
	
	@Resource
	private UserHandler userHandler;
	@Resource
	private UploadQiniuCloud uploadQiniuCloud;
	/**
	 * 缓存
	 */
	private SystemCache systemCache;

	@Test
	public void addOperateLog() throws Exception{
		systemCache = (SystemCache) SpringUtil.getBean("systemCache");
		String adminId = (String) systemCache.getCache("admin-id");
		int aid = 1;
		if(!StringUtil.isNull(adminId)){
			aid = Integer.parseInt(adminId);
		}
		OperateLogBean operateLogBean = new OperateLogBean();
		 //处理服务器的IP
    	String ip = "127.0.0.1";
        operateLogBean.setIp(ip);
        operateLogBean.setStatus(1);
        operateLogBean.setCreateUserId(userHandler.getUserBean(aid).getId());
        operateLogBean.setCreateTime(new Date());
        operateLogBean.setBrowser("猎豹浏览器");
        operateLogBean.setMethod("单元测试");
        operateLogBean.setOperateType(EnumUtil.LogOperateType.内部接口.value);
        operateLogBean.setSubject("测试新增操作日志");
        operateLogMapper.save(operateLogBean);
		
		/*TimeConsumingTest test = new TimeConsumingTest();
		test.save();*/
	}
	
	@Test
	public void getlimits(){
		logger.info(operateLogMapper.getlimits(DataTableType.操作日志.value, " order by id desc ", 5, 1));
	}
	
	
}
