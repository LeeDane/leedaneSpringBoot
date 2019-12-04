package com.cn.leedane.rabbitmq.recieve;

import java.io.File;

import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.ServerFileBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 删除服务器本地的临时文件的消费者
 * @author LeeDane
 * 2016年12月1日 上午11:54:22
 * Version 1.0
 */
public class DeleteServerFileRecieve implements IRecieve{

	public final static String QUEUE_NAME = "delete_file_rabbitmq";
	@Override
	public String getQueueName() {
		return QUEUE_NAME;
	}

	@Override
	public Class<?> getQueueClass() {
		return ServerFileBean.class;
	}

	@Override
	public boolean excute(Object obj) {
		boolean success = false;
		ServerFileBean fileBean = (ServerFileBean)obj;
		try{
			if(fileBean != null && StringUtil.isNotNull(fileBean.getPath())){
				File file = new File(fileBean.getPath());
				if(file.exists())
					file.delete();//删除该临时文件
				
				@SuppressWarnings("unchecked")
				OperateLogService<OperateLogBean> operateLogService = (OperateLogService<OperateLogBean>) SpringUtil.getBean("operateLogService");
				//保存操作日志
				operateLogService.saveOperateLog(OptionUtil.adminUser, null, null, StringUtil.getStringBufferStr("删除临时文件："+fileBean.getPath(), StringUtil.getSuccessOrNoStr(success)).toString(), "DeleteServerFileRecieve excute()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
				success = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return success;
	}
	
	@Override
	public boolean errorDestroy() {
		return true;
	}
}
