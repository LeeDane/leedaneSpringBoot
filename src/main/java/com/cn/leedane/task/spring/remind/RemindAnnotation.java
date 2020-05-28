package com.cn.leedane.task.spring.remind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事件提醒的注解
 * @author LeeDane
 * 2020年05月17日 14:45
 * Version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RemindAnnotation {

    /**
     * 中文的名称,用于展示该事件的中文名
     * @return
     */

    String name();

    /**
     * 英文的名称，一般是该类的容器名称
     * @return
     */
    String value() default "";
}
