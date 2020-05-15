package com.cn.leedane.handler;

import com.cn.leedane.model.JobManageBean;
import com.cn.leedane.task.spring.QuartzJobFactory;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务的处理类
 * @author LeeDane
 * 2020年5月15日 下午11:24:12
 * Version 1.0
 */
@Component
public class JobHandler {

	@Autowired
	private Scheduler scheduler;

	/**
	 * 启动job，如果job存在，删除job再新建
	 * @param bean
	 * @return
	 * @throws SchedulerException
	 */
	public boolean start(JobManageBean bean) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(bean.getJobName(), bean.getJobGroup());
		//获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		//存在，删掉
		if (trigger != null) {
			try {
				scheduler.deleteJob(trigger.getJobKey());
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
		//按新的trigger重新设置job执行
		JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class)
				.withIdentity(bean.getJobName(), bean.getJobGroup()).build();
		jobDetail.getJobDataMap().put("scheduleJob", bean);

		//表达式调度构建器
		CronScheduleBuilder scheduleBuilder1 = CronScheduleBuilder.cronSchedule(bean.getCronExpression());

		//按新的cronExpression表达式构建一个新的trigger
		trigger = TriggerBuilder.newTrigger().withIdentity(bean.getJobName(), bean.getJobGroup()).withSchedule(scheduleBuilder1).build();
		scheduler.scheduleJob(jobDetail, trigger);
		return true;
	}

	/**
	 * 停止job
	 * @param jobName
	 * @param jobGroup
	 * @return
	 * @throws SchedulerException
	 */
	public boolean stop(String jobName, String jobGroup) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		//获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		//存在，删掉
		if (trigger != null) {
			try {
				scheduler.deleteJob(trigger.getJobKey());
				return true;
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
