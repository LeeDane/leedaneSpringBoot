package com.cn.leedane.controller.manage;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.service.manage.CloudDiskService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * 云盘接口api入口controller
 * @author LeeDane
 * 2020年03月11日 14:48
 * Version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.myManage+ "/clouddisk")
public class CloudDiskController extends BaseController {
    // 日志记录
    private static final Logger logger = LoggerFactory.getLogger(CloudDiskController.class);
    @Autowired
    private CloudDiskService cloudDiskService;

    /**
     * 添加文件夹
     * @param request
     * @return
     */
    @RequestMapping(value = "/create/folder", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public ResponseModel addFolder(HttpServletRequest request) throws Exception {
        ResponseMap message = new ResponseMap();
        if(!checkParams(message, request))
            return message.getModel();
        return cloudDiskService.mkdirFolder(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
    }

    @RequestMapping(value = "/create/file", method = RequestMethod.POST)
    public ResponseModel createFile(Model model, HttpServletRequest request, @RequestParam(value="file", required = false)
            CommonsMultipartFile[] multipartFiles){
        ResponseMap message = new ResponseMap();
        if(!checkParams(message, request))
            return message.getModel();
        return cloudDiskService.createFile(multipartFiles, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
    }

    @RequestMapping(value = "/create/local/file", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public ResponseModel createLocalFile(Model model, HttpServletRequest request){
        ResponseMap message = new ResponseMap();
        if(!checkParams(message, request))
            return message.getModel();
        return cloudDiskService.createLocalFile(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
    }

    @RequestMapping(value = "/content/file", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public ResponseModel readFileContent(Model model, HttpServletRequest request){
        ResponseMap message = new ResponseMap();
        if(!checkParams(message, request))
            return message.getModel();
        return cloudDiskService.readFileContent(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
    public ResponseModel deleteFile(Model model, HttpServletRequest request){
        ResponseMap message = new ResponseMap();
        if(!checkParams(message, request))
            return message.getModel();
        return cloudDiskService.deleteFile(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
    }

    @RequestMapping(value = "/delete/batch", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
    public ResponseModel deleteBatch(Model model, HttpServletRequest request){
        ResponseMap message = new ResponseMap();
        if(!checkParams(message, request))
            return message.getModel();
        return cloudDiskService.deleteBatch(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
    }


    @RequestMapping(value = "/rename", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public ResponseModel renameFile(Model model, HttpServletRequest request){
        ResponseMap message = new ResponseMap();
        if(!checkParams(message, request))
            return message.getModel();
        return cloudDiskService.renameFile(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
    }

    @RequestMapping(value = "/list/files", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public ResponseModel listFile(Model model, HttpServletRequest request){
        ResponseMap message = new ResponseMap();
        checkParams(message, request);
        return cloudDiskService.listFile(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
    }

    /**
     * 读取目录信息
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/read/catalog", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public ResponseModel readCatalog(Model model, HttpServletRequest request){
        ResponseMap message = new ResponseMap();
        if(!checkParams(message, request))
            return message.getModel();
        return cloudDiskService.readCatalog(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
    }

    /**
     * 读取目录信息
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/download/file", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public ResponseModel downloadFile(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseMap message = new ResponseMap();
        if(!checkParams(message, request))
            return message.getModel();
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        // 下载文件能正常显示中文
        String fileName = JsonUtil.getStringValue(getJsonFromMessage(message), "name");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        // 实现文件下载
        InputStream in = cloudDiskService.downloadFile(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)).getWrappedStream();
        if(in != null){
            OutputStream os = response.getOutputStream();
            int a = 0;
            while ((a = in.read()) != -1) {
                os.write(a);
            }
            in.close();
            os.close();
        }
        return new ResponseModel().ok().message("ok");
    }
}
