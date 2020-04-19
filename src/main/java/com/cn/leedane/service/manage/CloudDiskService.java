package com.cn.leedane.service.manage;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.apache.hadoop.fs.FSDataInputStream;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * 云盘service
 * @author LeeDane
 * 2020年03月11日 14:44
 * Version 1.0
 */
@Transactional
public interface CloudDiskService<T extends IDBean> {

    /**
     * 创建文件夹
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel mkdirFolder(JSONObject jo, UserBean user, HttpRequestInfoBean request);

    /**
     * 文件是否存在
     * @param path
     * @return
     */
    public boolean existFile(String path);

    /**
     * 读取目录信息
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel readCatalog(JSONObject jo, UserBean user, HttpRequestInfoBean request);

    /**
     * 创建文件
     * @param multipartFiles
     * @param jo
     * @param user
     * @param request
     */
    public ResponseModel createFile(CommonsMultipartFile[] multipartFiles, JSONObject jo, UserBean user, HttpRequestInfoBean request);

    /**
     * 创建本地文件
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel createLocalFile(JSONObject jo, UserBean user, HttpRequestInfoBean request);

    /**
     * 读取文件内容
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel readFileContent(JSONObject jo, UserBean user, HttpRequestInfoBean request);

    /**
     * 读完文件列表
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel listFile(JSONObject jo, UserBean user, HttpRequestInfoBean request);

    /**
     * 文件重命名
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel renameFile(JSONObject jo, UserBean user, HttpRequestInfoBean request);

    /**
     * 文件删除
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel deleteFile(JSONObject jo, UserBean user, HttpRequestInfoBean request);

    /**
     * 文件批量删除
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel deleteBatch(JSONObject jo, UserBean user, HttpRequestInfoBean request);

    /**
     * 文件上传
     * @param jo
     * @param user
     * @param request
     */
    public ResponseModel uploadFile(JSONObject jo, UserBean user, HttpRequestInfoBean request);

    /**
     * 文件下载
     * @param jo
     * @param user
     * @param request
     */
    public FSDataInputStream downloadFile(JSONObject jo, UserBean user, HttpRequestInfoBean request);

    /**
     * 文件复制
     * @param jo
     * @param user
     * @param request
     */
    public ResponseModel copyFile(JSONObject jo, UserBean user, HttpRequestInfoBean request);


    /**
     * 读取指定文件 返回字节数组
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel openFileToBytes(JSONObject jo, UserBean user, HttpRequestInfoBean request);

    /**
     * 获取指定文件 BlockLocation信息
     * @param jo
     * @param user
     * @param request
     * @return
     */
    public ResponseModel getFileBlockLocations(JSONObject jo, UserBean user, HttpRequestInfoBean request);
}
