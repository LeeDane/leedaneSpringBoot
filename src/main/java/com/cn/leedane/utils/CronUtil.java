package com.cn.leedane.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * cron表达式的工具类
 * @author LeeDane
 * 2020年05月24日 12:41
 * Version 1.0
 */
public class CronUtil {

    /**
     * 根据具体事件获取
     * @param date
     * @return
     */
    public static String time(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.SECOND) + " " +
                c.get(Calendar.MINUTE) + " " +
                c.get(Calendar.HOUR_OF_DAY) + " " +
                c.get(Calendar.DAY_OF_MONTH) + " " +
                (c.get(Calendar.MONTH) + 1) + " " +
                "? " + //周
                c.get(Calendar.YEAR);
    }
}
