package com.cn.leedane.Limit;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 对需要审核的打卡，提醒打卡管理员
 */
@Component
public class ClockInRemindAdmin  implements RequestLimitImplement{
    @Override
    public long time() {
        return 1000 * 60 * 10; //10分钟的现在
    }

    @Override
    public Map<String, Object> params() {
        return null;
    }

    @Override
    public void error(Map<String, Object> response) {

    }

    @Override
    public void success(Map<String, Object> response) {

    }


}
