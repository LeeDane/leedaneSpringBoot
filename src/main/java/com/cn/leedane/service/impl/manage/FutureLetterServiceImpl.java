package com.cn.leedane.service.impl.manage;

import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.handler.JobHandler;
import com.cn.leedane.handler.OptionHandler;
import com.cn.leedane.mapper.letter.FutureLetterMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.JobManageBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.letter.FutureLetterBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.manage.FutureLetterService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 未来的信件接口service实现类
 * @author LeeDane
 * 201
 * 2020年5月23日 上午10:30:40
 * version 1.0
 */
@Service
public class FutureLetterServiceImpl extends AdminRoleCheckService implements FutureLetterService<FutureLetterBean> {
    // 日志记录服务
    private static final Logger logger = LoggerFactory.getLogger(FutureLetterServiceImpl.class);

    @Autowired
    private OperateLogService<OperateLogBean> operateLogService;

    @Autowired
    private OptionHandler optionHandler;

    @Autowired
    private FutureLetterMapper futureLetterMapper;

    @Autowired
    private JobHandler jobHandler;

    /**
     * 到期job的名称前缀
     */
    public static String LETTER_EXPIRE_JOB_NAME_PREFIX = "letter_expire_name_";

    /**
     * 到期job的group前缀
     */
    public static String LETTER_EXPIRE_JOB_GROUP_PREFIX = "letter_expire_group_";

    /**
     * 信件过期发送短信前缀
     */
    public static String LETTER_EXPIRE_PREFIX = "letter_expire_send_";

    @Override
    public ResponseModel add(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws SchedulerException {
        logger.info("FutureLetterServiceImpl-->add():jo="+jo);
        SqlUtil sqlUtil = new SqlUtil();
        FutureLetterBean futureLetter = (FutureLetterBean) sqlUtil.getBean(jo, FutureLetterBean.class);
        ParameterUnspecificationUtil.checkObject(futureLetter.getEnd(), "到期日期 must not null.");
       /* if(DateUtil.leftDays(new Date(), futureLetter.getEnd()) < 1)
            throw new ParameterUnspecificationException("到期日期至少比当前日期晚一天");*/

        ParameterUnspecificationUtil.checkNullString(futureLetter.getContent(), "内容 must not null.");
        if(futureLetter.getWay().indexOf("短信") > -1)
            ParameterUnspecificationUtil.checkPhone(futureLetter.getPhone()+"", "请确定手机号码是否合法.");

        if(futureLetter.getWay().indexOf("邮件") > -1)
            ParameterUnspecificationUtil.checkEmail(futureLetter.getEmail()+"", "请确定电子邮箱是否合法.");
        ParameterUnspecificationUtil.checkNullString(futureLetter.getSign(), "签名 must not null.");
        //对内容进行加密
        String content = futureLetter.getContent();
        //6位验证码+ 结束日期年月+6位的随机码
        String pwd = StringUtil.build6ValidationCode()+ DateUtil.DateToString(futureLetter.getEnd(), "yyMM") + ShareCodeUtil.toSerialCode(user.getId());
        content = CommonUtil.sm4EncryptByKey(content, pwd);//对信件内容进行加密

        futureLetter.setCreateUserId(user.getId());
        futureLetter.setCreateTime(new Date());
        futureLetter.setModifyUserId(user.getId());
        futureLetter.setModifyTime(new Date());
        futureLetter.setStatus(ConstantsUtil.STATUS_SELF);//设置为私有的信件
        futureLetter.setPwd(pwd);
        futureLetter.setContent(content);
        boolean result = futureLetterMapper.save(futureLetter) > 0;
        operateLogService.saveOperateLog(user, request, new Date(), "发送未来的信件，用户ID为："+ user.getId()+ ", 信件ID="+ futureLetter.getId() +", 结果是："+ StringUtil.getSuccessOrNoStr(result), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
        if(result){
            JobManageBean jobManageBean = new JobManageBean();
            jobManageBean.setClassName("letterExpire");
            jobManageBean.setCronExpression(CronUtil.time(futureLetter.getEnd()));
            jobManageBean.setJobGroup(LETTER_EXPIRE_JOB_GROUP_PREFIX+ futureLetter.getCreateUserId());
            jobManageBean.setJobName(LETTER_EXPIRE_JOB_NAME_PREFIX + futureLetter.getId());
            jobManageBean.setJobParams("id="+ futureLetter.getId());
            //设置定时job
            jobHandler.start(jobManageBean);
            return new ResponseModel().ok().message("添加成功");
        }
        else
            return new ResponseModel().error().message("添加失败").code(EnumUtil.ResponseCode.操作失败.value);
    }

    @Override
    public ResponseModel delete(long letterId, JSONObject jsonObject, UserBean user, HttpRequestInfoBean request) throws SchedulerException {
        logger.info("FutureLetterServiceImpl-->delete():jo="+ jsonObject);
        FutureLetterBean futureLetter = futureLetterMapper.findById(FutureLetterBean.class, letterId);
        if(futureLetter == null){
            throw new NullPointerException("未来信件记录不存在");
        }
        checkAdmin(user, futureLetter.getCreateUserId());
        boolean result = futureLetterMapper.deleteById(FutureLetterBean.class, letterId) > 0;
        operateLogService.saveOperateLog(user, request, new Date(), "移除未来信件，subject为："+ futureLetter.getSubject() +", 结果是："+ StringUtil.getSuccessOrNoStr(result), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
        if(result){
            //删除任务
            jobHandler.stop(LETTER_EXPIRE_JOB_NAME_PREFIX+ futureLetter.getId(), LETTER_EXPIRE_JOB_GROUP_PREFIX+ user.getId());
            return new ResponseModel().ok().message("移除成功");
        }
        else
            return new ResponseModel().error().message("移除失败").code(EnumUtil.ResponseCode.操作失败.value);
    }

    @Override
    public LayuiTableResponseModel list(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
        logger.info("MyToolServiceImpl-->list():jo="+ jsonObject);
        int current = JsonUtil.getIntValue(jsonObject, "page", 0);
        int rows = JsonUtil.getIntValue(jsonObject, "limit", 10);
        int start = SqlUtil.getPageStart(current -1, rows, 0);
        List<Map<String, Object>> rs = futureLetterMapper.list(user.getId(), start, rows);
        if(rs !=null && rs.size() > 0){
            String statusText = null;
            Map<String, Object> map = null;
            for(int i = 0; i < rs.size(); i++){
                map = rs.get(i);
                int status = StringUtil.changeObjectToInt(map.get("status"));
                switch (status){
                    case ConstantsUtil.STATUS_NORMAL:
                        statusText = "正常";
                        break;
                    case ConstantsUtil.STATUS_SELF:
                        statusText = "未到期";
                        break;
                    case ConstantsUtil.STATUS_SHARE:
                        statusText = "已公开";
                        break;
                    default:
                        statusText = "未知状态";
                        break;
                }
                map.put("statusText", statusText);
                map.put("publica", StringUtil.changeObjectToBoolean(map.get("publica")) ? "公开": "私有");
            }
        }
        LayuiTableResponseModel responseModel = new LayuiTableResponseModel();
        responseModel.setData(rs).setCount(SqlUtil.getTotalByList(futureLetterMapper.getTotalByUser(EnumUtil.DataTableType.未来信件.value, user.getId()))).code(0);
        return responseModel;
    }

}
