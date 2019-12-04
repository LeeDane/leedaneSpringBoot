package com.cn.leedane.task.spring.scheduling;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.model.EmailBean;
import com.cn.leedane.model.FinancialBean;
import com.cn.leedane.model.FinancialReport;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.EmailSend;
import com.cn.leedane.rabbitmq.send.FinancialMonthReportSend;
import com.cn.leedane.rabbitmq.send.ISend;
import com.cn.leedane.service.FinancialService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil.EmailType;
import com.cn.leedane.utils.StringUtil;

/**
 * 处理记账月报任务
 * @author LeeDane
 * 2017年6月6日 上午10:53:42
 * version 1.0
 */
@Component("financialMonth")
public class FinancialMonth extends AbstractScheduling{
	private static Logger logger = Logger.getLogger(FinancialMonth.class);
	
	@Autowired
	private UserService<UserBean> userService;
	
	@Override
	public void execute() throws SchedulerException {
		
		long start = System.currentTimeMillis();
		
		//获取所有的用户列表
		List<UserBean> users = userService.getAllUsers(ConstantsUtil.STATUS_NORMAL);
		if(users != null && users.size() > 0){
			List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
			ExecutorService threadpool = Executors.newFixedThreadPool(users.size() >5 ? 5: users.size());
			SingleTask dealTask;
			for(UserBean bean: users){
				dealTask = new SingleTask(bean);
				futures.add(threadpool.submit(dealTask));
			}
			
			threadpool.shutdown();
			
			for(int i = 0; i < futures.size(); i++){
				try {
					if(!futures.get(i).get()){
						logger.error(users.get(i).getAccount() + "的周报发送失败");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		long end = System.currentTimeMillis();
		logger.info("执行记账周报总计耗时：" + (end - start) +"毫秒");
	}
}

	
/**
 * 计算单个用户的月报处理任务
 * @author LeeDane
 * 2016年11月2日 上午11:37:01
 * Version 1.0
 */
class SingleTask implements Callable<Boolean>{
	private UserBean mUser;
	
	public SingleTask(UserBean user){
		this.mUser = user;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean call() throws Exception {
		
		try{
			//找用户的记账记录
			FinancialService<FinancialBean> financialService = (FinancialService<FinancialBean>) SpringUtil.getBean("financialService");
			
			Date startTime = DateUtil.getLastMonthStart();
			Date endTime = DateUtil.getLastMonthEnd();
			List<FinancialBean> financialBeans = financialService.getByTimeRange(mUser.getId(), 
					ConstantsUtil.STATUS_NORMAL, startTime, endTime);
						
			FinancialReport report = new FinancialReport();
			report.setCreateTime(new Date());
			report.setCreateUserId(mUser.getId()); 
			report.setStartTime(startTime);
			report.setEndTime(endTime);
			report.setUser(mUser);
			
			if(CollectionUtil.isNotEmpty(financialBeans)){
				String detailLink = "http://onlylove.top/fn";
				float incomeTotal = 0;
				float spendTotal = 0;
				int incomeNum = 0;
				int spendNum = 0;
				for(FinancialBean financialBean:financialBeans){
					if(financialBean.getModel() == 1){//收入
						incomeTotal += financialBean.getMoney();
						incomeNum += 1;
					}else if(financialBean.getModel() == 2){//支出
						spendTotal += financialBean.getMoney();
						spendNum += 1;
					}
				}
				report.setIncome(incomeTotal);
				report.setSpend(spendTotal);
				StringBuffer buffer = new StringBuffer();
				buffer.append("尊敬的" +mUser.getAccount() + "用户， 您上个月收入");
				buffer.append(incomeNum +"笔，收入总金额是："+incomeTotal +"元,支出");
				buffer.append(spendNum +"笔，支出总金额是：" +spendTotal +"元。详情请查阅："+detailLink);
				report.setDesc(buffer.toString());
			}else {
				report.setDesc("尊敬的" +mUser.getAccount() + "用户， 您上个月没有任何收支记录！");
			}
			
			ISend send = new FinancialMonthReportSend(report);
			SendMessage sendMessage = new SendMessage(send);
			sendMessage.sendMsg();//发送日志到消息队列
			
			SystemCache systemCache = (SystemCache) SpringUtil.getBean("systemCache");
			int adminId = StringUtil.changeObjectToInt(systemCache.getCache("admin-id"));
			UserService<UserBean> userService = (UserService<UserBean>) SpringUtil.getBean("userService");
			UserBean adminUser = userService.findById(adminId);
			EmailBean emailBean = new EmailBean();
			emailBean.setContent(report.getDesc());
			emailBean.setCreateTime(new Date());
			emailBean.setFrom(adminUser);
			emailBean.setSubject(DateUtil.DateToString(startTime, "yyyy-MM-dd") +"到"+ DateUtil.DateToString(endTime, "yyyy-MM-dd") +"记账月报");
			Set<UserBean> set = new HashSet<UserBean>();
			set.add(mUser);
			emailBean.setReplyTo(set);
			emailBean.setType(EmailType.新邮件.value); //新邮件
			if(mUser.getId() == 1){
				ISend sendEmain = new EmailSend(emailBean);
				SendMessage sendEmailMessage = new SendMessage(sendEmain);
				sendEmailMessage.sendMsg();//发送日志到消息队列
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
}
