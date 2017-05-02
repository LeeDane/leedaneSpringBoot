package com.cn.leedane.springboot;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * spring相关工具类
 * @author LeeDane
 * 2016年7月12日 上午10:31:30
 * Version 1.0
 */
@Configuration
public class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public static Object getBean(String beanName) {	
		/*if(applicationContext == null)
			//对main方法使用的时候加载所有的spring配置文件
			applicationContext = new ClassPathXmlApplicationContext("classpath*:config/spring-*.xml");*/
		return applicationContext.getBean(beanName);
	}

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		applicationContext = context;
	}
	
}
