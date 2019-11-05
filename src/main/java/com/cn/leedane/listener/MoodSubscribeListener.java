package com.cn.leedane.listener;

import com.cn.leedane.event.MoodSubscribeEvent;
import com.cn.leedane.model.MoodBean;
import org.springframework.context.ApplicationListener;

/**
 * 心情订阅的监听
 * @author LeeDane
 * 2019年10月31日 16:43
 * Version 1.0
 */
public class MoodSubscribeListener implements ApplicationListener<MoodSubscribeEvent> {
    @Override
    public void onApplicationEvent(MoodSubscribeEvent moodSubscribeEvent) {
        System.out.println("MoodSubscribeListener---------------------------------");


        moodSubscribeEvent.printMsg((MoodBean) moodSubscribeEvent.getSource());
    }
}
