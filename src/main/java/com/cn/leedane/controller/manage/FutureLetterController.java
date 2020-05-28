package com.cn.leedane.controller.manage;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.service.manage.FutureLetterService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.ResponseModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 未来的信件接口controller
 *
 * @author LeeDane
 * 201
 * 2020年5月23日 上午10:30:40
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.myManage + "/future/letter")
public class FutureLetterController extends BaseController {

    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    private FutureLetterService futureLetterService;

    /**
     * 添加未来的信件
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public ResponseModel add(HttpServletRequest request) throws SchedulerException {
        ResponseMap message = new ResponseMap();
        if (!checkParams(message, request))
            return message.getModel();
        return futureLetterService.add(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
    }

    /**
     * 删除未来的信件
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/{letterId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
    public ResponseModel delete(@PathVariable("letterId") long letterId, HttpServletRequest request) throws SchedulerException {
        ResponseMap message = new ResponseMap();
        if (!checkParams(message, request))
            return message.getModel();
        return futureLetterService.delete(letterId, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
    }

    /**
     * 未来的信件列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public ResponseModel list(HttpServletRequest request) {
        ResponseMap message = new ResponseMap();
        if (!checkParams(message, request))
            return message.getModel();
        return futureLetterService.list(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
    }
}
