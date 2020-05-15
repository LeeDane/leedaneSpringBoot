package com.cn.leedane.service.manage;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.apache.hadoop.fs.FSDataInputStream;
import org.quartz.SchedulerException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * 我的工具service
 * @author LeeDane
 * 2020年05月4日 11:44
 * Version 1.0
 */
@Transactional
public interface MyToolService<T extends IDBean> {

    /**
     * 移除抖音视频水印
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel removeWatermark(JSONObject jo, UserBean user, HttpRequestInfoBean request);


    /**
     * 添加事件提醒
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel addRemind(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws SchedulerException;

    /**
     * 删除事件提醒
     * @param remindId
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel deleteRemind(long remindId, JSONObject jo, UserBean user, HttpRequestInfoBean request) throws SchedulerException;

    /**
     * 事件提醒列表
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel reminds(JSONObject jo, UserBean user, HttpRequestInfoBean request);

}
