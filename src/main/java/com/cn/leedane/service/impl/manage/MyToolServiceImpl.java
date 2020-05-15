package com.cn.leedane.service.impl.manage;

import com.cn.leedane.handler.JobHandler;
import com.cn.leedane.handler.OptionHandler;
import com.cn.leedane.handler.tool.video.DouYinQushuiyin;
import com.cn.leedane.mapper.ManageRemindMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.manage.CloudDiskService;
import com.cn.leedane.service.manage.MyToolService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.jsoup.Jsoup;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 云盘service实现类
 * @author LeeDane
 * 2020年05月4日 11:46
 * Version 1.0
 */

@Service
public class MyToolServiceImpl extends AdminRoleCheckService implements MyToolService<IDBean> {
    // 全局变量定义
    private static final int bufferSize = 1024 * 1024 * 64;

    // 日志记录服务
    private static final Logger logger = LoggerFactory.getLogger(MyToolServiceImpl.class);

    @Autowired
    private OperateLogService<OperateLogBean> operateLogService;

    @Autowired
    private CloudDiskService cloudDiskService;

    @Autowired
    private ManageRemindMapper manageRemindMapper;

    @Autowired
    private OptionHandler optionHandler;

    @Autowired
    private JobHandler jobHandler;

    @Override
    public ResponseModel removeWatermark(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String text = JsonUtil.getStringValue(jo, "text");
        ParameterUnspecificationUtil.checkNullString(text, "抖音链接地址文本 must not null.");

        //1.利用Jsoup抓取抖音链接
        String finalUrl = CommonUtil.parseLink(text);
        if(!StringUtil.isLink(finalUrl))
            return new ResponseModel().error().message("请输入合法的抖音链接地址");

        //抓取抖音网页
        String html = DouYinQushuiyin.getHTMLSource(finalUrl);

        if(StringUtil.isNull(html))
            return new ResponseModel().error().message("请输入合法的抖音链接地址");

        //解析获取视频的地址(带水印)
        String videoUrl = DouYinQushuiyin.parseVideoLink(html);

        if(!StringUtil.isLink(videoUrl))
            return new ResponseModel().error().message("抖音视频的链接不合法");

        //把带水印的视频地址转化成没有水印的地址
        videoUrl = videoUrl.replaceAll("playwm", "play");

        //5.将链接封装成流
        //注:由于抖音对请求头有限制,只能设置一个伪装手机浏览器请求头才可实现去水印下载
        Map<String, String> headers = new HashMap<>();
        headers.put("Connection", "keep-alive");
        headers.put("Host", "aweme.snssdk.com");
        headers.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/16D57 Version/12.0 Safari/604.1");

        //6.利用Joup获取视频对象,并作封装成一个输入流对象
        //maxBodySize表示下载数据的最大大小，不设置默认是1M，这里设置50 * 1024 * 102表示最大限制50M
        byte[] inBytes = new byte[0];
        try {
            inBytes = Jsoup.connect(videoUrl).header("Connection", "keep-alive")
                    .header("Host", "aweme.snssdk.com")
                    .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/16D57 Version/12.0 Safari/604.1").timeout(100000).maxBodySize(50 * 1024 * 1024).ignoreContentType(true).execute().bodyAsBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Long timetmp = new Date().getTime();
        InputStream in = DouYinQushuiyin.byte2Input(inBytes);
        try{
            //使用
            FileItem fileItem = FileUtil.createFileItem(in, "video_"+ DateUtil.getSystemCurrentTime("yyyyMMddHHmmss")+".mp4");
            CommonsMultipartFile xxCMF = new CommonsMultipartFile(fileItem);
            jo.put("path", optionHandler.getData("douyinremovewatermark", true)); //"/douyin/video"
            return cloudDiskService.createFile(new CommonsMultipartFile[]{xxCMF}, jo, user, request);
            //xxCMF就是转化后的CommonsMultipartFile文件
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(in  != null) {
                try {
                    in.close(); //关闭输入流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new ResponseModel().error().message("创建文件夹失败").code(EnumUtil.ResponseCode.服务器处理异常.value);
    }

    @Override
    public ResponseModel addRemind(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request) throws SchedulerException {
        logger.info("MyToolServiceImpl-->addRemind():jo="+ jsonObject);
        SqlUtil sqlUtil = new SqlUtil();
        ManageRemindBean remindBean = (ManageRemindBean) sqlUtil.getBean(jsonObject, ManageRemindBean.class);

        //检验参数
        ParameterUnspecificationUtil.checkNullString(remindBean.getName(), "name must not null.");
        ParameterUnspecificationUtil.checkNullString(remindBean.getType(), "type must not null.");
        ParameterUnspecificationUtil.checkNullString(remindBean.getCron(), "cron must not null.");
        ParameterUnspecificationUtil.checkNullString(remindBean.getWay(), "way must not null.");
        ParameterUnspecificationUtil.checkNullString(remindBean.getCycle(), "cycle must not null.");

        remindBean.setCreateUserId(user.getId());
        remindBean.setCreateTime(new Date());
        remindBean.setModifyUserId(user.getId());
        remindBean.setModifyTime(new Date());
        remindBean.setStatus(ConstantsUtil.STATUS_NORMAL);

        boolean result = manageRemindMapper.save(remindBean) > 0;
        operateLogService.saveOperateLog(user, request, new Date(), "添加事件，cron为："+ remindBean.getCron() +", 结果是："+ StringUtil.getSuccessOrNoStr(result), "addRemind()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
        if(result){
            String className = null;
            switch (remindBean.getType()){
                case "takeMedicine":
                    className = "takeMedicine";
                    break;
                case "againTakeMedicine":
                    className = "againTakeMedicine";
                    break;
            }
            JobManageBean jobManageBean = new JobManageBean();
            jobManageBean.setClassName(className);
            jobManageBean.setCronExpression(remindBean.getCron());
            jobManageBean.setJobGroup("remind_"+ user.getId());
            jobManageBean.setJobName("remind_job_"+ remindBean.getId());
            jobManageBean.setJobParams("id="+ remindBean.getId());
            //启动任务
            jobHandler.start(jobManageBean);
            return new ResponseModel().ok().message("添加成功");
        }else
            return new ResponseModel().error().message("添加失败").code(EnumUtil.ResponseCode.操作失败.value);
    }

    @Override
    public ResponseModel deleteRemind(long remindId, JSONObject jsonObject, UserBean user, HttpRequestInfoBean request) throws SchedulerException {
        logger.info("MyToolServiceImpl-->deleteRemind():remindId = "+ remindId +"&jo="+ jsonObject);
        ManageRemindBean remindBean = manageRemindMapper.findById(ManageRemindBean.class, remindId);
        if(remindBean == null){
            throw new NullPointerException("事件提醒记录不存在");
        }
        checkAdmin(user, remindBean.getCreateUserId());

        boolean result = manageRemindMapper.deleteById(ManageRemindBean.class, remindId) > 0;
        operateLogService.saveOperateLog(user, request, new Date(), "移除事件提醒，cron为："+ remindBean.getCron() +", 结果是："+ StringUtil.getSuccessOrNoStr(result), "deleteRemind()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
        if(result){
            //删除任务
            jobHandler.stop("remind_job_"+ remindBean.getId(), "remind_"+ user.getId());
            return new ResponseModel().ok().message("移除成功");
        }
        else
            return new ResponseModel().error().message("移除失败").code(EnumUtil.ResponseCode.操作失败.value);
    }

    @Override
    public LayuiTableResponseModel reminds(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
        logger.info("MyToolServiceImpl-->reminds():jo="+ jsonObject);
        int current = JsonUtil.getIntValue(jsonObject, "page", 0);
        int rows = JsonUtil.getIntValue(jsonObject, "limit", 10);
        int start = SqlUtil.getPageStart(current -1, rows, 0);
        List<Map<String, Object>> rs = manageRemindMapper.reminds(user.getId(), start, rows);
        if(rs !=null && rs.size() > 0){
            //为名字备注赋值
            for(int i = 0; i < rs.size(); i++){
            }
        }
        LayuiTableResponseModel responseModel = new LayuiTableResponseModel();
        responseModel.setData(rs).setCount(SqlUtil.getTotalByList(manageRemindMapper.getTotalByUser(EnumUtil.DataTableType.事件提醒.value, user.getId()))).code(0);
        return responseModel;
    }

}
