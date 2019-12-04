package com.cn.leedane.rabbitmq.send;

import com.cn.leedane.model.ServerFileBean;
import com.cn.leedane.rabbitmq.recieve.DeleteServerFileRecieve;


/**
 * 删除服务器本地的临时文件的发送类
 * @author LeeDane
 * 2016年12月1日 下午12:16:11
 * Version 1.0
 */
public class DeleteServerFileSend implements ISend{
	
	private ServerFileBean serverFileBean;
	public DeleteServerFileSend(ServerFileBean serverFileBean){
		this.serverFileBean = serverFileBean;
	}

	@Override
	public String getQueueName() {
		return DeleteServerFileRecieve.QUEUE_NAME;
	}

	@Override
	public Object getQueueObject() {
		return serverFileBean;
	}

}
