package com.cn.leedane.event;

import com.cn.leedane.model.MoodBean;
import org.springframework.context.ApplicationEvent;

/**
 * 心情订阅事件
 * @author LeeDane
 * 2019年10月31日 16:45
 * Version 1.0
 */
public class MoodSubscribeEvent extends ApplicationEvent {

    public MoodSubscribeEvent(Object source) {
        super(source);
    }

    /**
    * 事件处理事项
    * @param moodBean
    */
    public void printMsg(MoodBean moodBean){
        System.out.println("心情的ID="+ moodBean.getId());
        System.out.println("监听到事件："+MoodSubscribeEvent.class);
    }
}
