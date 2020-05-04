package com.cn.leedane.service.impl.manage;

import com.cn.leedane.handler.OptionHandler;
import com.cn.leedane.handler.tool.video.DouYinQushuiyin;
import com.cn.leedane.hdfs.HdfsFileSystem;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.manage.CloudDiskService;
import com.cn.leedane.service.manage.MyToolService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.FileUtil;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.util.*;

/**
 * 云盘service实现类
 * @author LeeDane
 * 2020年05月4日 11:46
 * Version 1.0
 */

@Service
public class MyToolServiceImpl implements MyToolService<IDBean> {
    // 全局变量定义
    private static final int bufferSize = 1024 * 1024 * 64;

    // 日志记录服务
    private static final Logger logger = LoggerFactory.getLogger(MyToolServiceImpl.class);

    @Autowired
    private CloudDiskService cloudDiskService;

    @Autowired
    private OptionHandler optionHandler;

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
}
