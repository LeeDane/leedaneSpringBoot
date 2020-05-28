package com.cn.leedane.utils;

import com.cn.leedane.task.spring.remind.BaseRemind;
import com.cn.leedane.task.spring.remind.RemindAnnotation;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件提醒的工具类
 * @author LeeDane
 * 2020年05月17日 14:51
 * Version 1.0
 */
public class RemindUtil {
    private Logger logger = Logger.getLogger(getClass());
    /**
     * 获取所有添加了@RemindAnnotation 接口的实现类
     * @return
     */
    public List<Map<String, Object>> getAllInterfaceClass(){
        //获取所有继承BaseRemind的类
        ClassUtil classUtil = new ClassUtil();
        List<Class> classes = classUtil.getAllClassByInterface(BaseRemind.class);
        logger.info("获取classes数量："+ classes.size());
        List<Map<String, Object>> types = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(classes)){
            for(Class clazz: classes)
                if(clazz.isAnnotationPresent(RemindAnnotation.class)){
                    // 获取 "类" 上的注解
                    RemindAnnotation annotation = (RemindAnnotation) clazz.getAnnotation(RemindAnnotation.class);
                    /*System.out.println("\"类\"上的注解值获取到第一个 ："
                            + annotation.name()+ "，第二个："+ annotation.value());*/
                    Map<String, Object> mp = new HashMap<>();
                    mp.put("name", annotation.name());
                    mp.put("value", annotation.value());
                    types.add(mp);
                }
        }
        return types;

    }
}
