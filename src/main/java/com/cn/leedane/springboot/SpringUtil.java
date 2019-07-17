package com.cn.leedane.springboot;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * spring相关工具类
 * @author LeeDane
 * 2016年7月12日 上午10:31:30
 * Version 1.0
 */
@Component
public class SpringUtil{

	private static ApplicationContext applicationContext;

	public static Object getBean(String beanName) {	
		/*if(applicationContext == null)
			//对main方法使用的时候加载所有的spring配置文件
			applicationContext = new ClassPathXmlApplicationContext("classpath*:config/spring-*.xml");*/
		return getApplicationContext().getBean(beanName);
	}

	/*@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		if(SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = context;
        }
	}*/
	
	//获取applicationContext
    public static ApplicationContext getApplicationContext() {
        if(applicationContext == null)
            System.out.println("SpringUtil----getApplicationContext()是空的");
        return applicationContext;
    }

  //获取applicationContext
    public static void setApplicationContext2(ApplicationContext application) {
        applicationContext = application;
    }
}
