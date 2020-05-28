package com.cn.leedane.rabbitmq;

import com.rabbitmq.client.BlockedListener;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * rabbitmq堵塞时候的监听
 * @author LeeDane
 * 2020年05月27日 16:11
 * Version 1.0
 */
public class MyBlockedListener implements BlockedListener {
    private Logger logger = Logger.getLogger(getClass());

    @Override
    public void handleBlocked(String reason) throws IOException {
        logger.info("handleBlocked, reason:" + reason);
    }

    @Override
    public void handleUnblocked() throws IOException {
        logger.info("handleUnblocked");
    }
}
