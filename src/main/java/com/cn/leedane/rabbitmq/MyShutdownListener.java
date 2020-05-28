package com.cn.leedane.rabbitmq;

import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import org.apache.log4j.Logger;

/**
 * rabbitmq挂掉的监听
 * @author LeeDane
 * 2020年05月27日 16:14
 * Version 1.0
 */
public class MyShutdownListener implements ShutdownListener {
    private Logger logger = Logger.getLogger(getClass());
    @Override
    public void shutdownCompleted(ShutdownSignalException cause) {
        String hardError = "";
        String applInit = "";
        if (cause.isHardError()) {
            hardError = "connection";
        } else {
            hardError = "channel";
        }

        if (cause.isInitiatedByApplication()) {
            applInit = "application";
        } else {
            applInit = "broker";
        }
        logger.error("Connectivity to MQ has failed.  It was caused by " + applInit + " at the " + hardError
                + " level.  Reason received ", cause);
        //通知系统管理员
    }
}
