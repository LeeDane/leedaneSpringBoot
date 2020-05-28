package com.cn.leedane.service.manage;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.letter.FutureLetterBean;
import com.cn.leedane.utils.LayuiTableResponseModel;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.quartz.SchedulerException;
import org.springframework.transaction.annotation.Transactional;

/**
 * 未来的信件接口service
 * @author LeeDane
 * 201
 * 2020年5月23日 上午10:30:40
 * version 1.0
 */
@Transactional
public interface FutureLetterService<T extends FutureLetterBean> {

    /**
     * 添加信件
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel add(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws SchedulerException;


    /**
     * 删除信件
     * @param letterId
     * @param jo
     * @param user
     * @param request
     * @return
     * @throws SchedulerException
     */
    public ResponseModel delete(long letterId, JSONObject jo, UserBean user, HttpRequestInfoBean request) throws SchedulerException;

    /**
     * 信件列表
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public LayuiTableResponseModel list(JSONObject jo, UserBean user, HttpRequestInfoBean request);

}
