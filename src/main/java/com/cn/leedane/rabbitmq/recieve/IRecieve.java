package com.cn.leedane.rabbitmq.recieve;

/**
 * 接收信息的接口
 * @author LeeDane
`* 2016年8月5日 下午3:24:46
 * Version 1.0
 */
public interface IRecieve {
	
	/**
	 * 是否不管处理是否错误直接销毁（默认是false）
	 * @return
	 */
	public boolean errorDestroy();
	
	/**
	 * 获取队列的名称
	 * @return
	 */
	public String getQueueName();
	
	/**
	 * 队列中对象的类名
	 * @return
	 */
	public Class<?> getQueueClass();
	
	/**
	 * 执行队列的操作
	 * @param obj
	 * @return 返回该操作是否已经处理成功
	 */
	public boolean excute(Object obj);

}
