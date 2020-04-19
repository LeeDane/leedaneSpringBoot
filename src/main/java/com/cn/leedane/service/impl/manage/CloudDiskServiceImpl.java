package com.cn.leedane.service.impl.manage;

import com.cn.leedane.hdfs.HdfsFileSystem;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.manage.CloudDiskService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
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
 * 2020年03月11日 14:46
 * Version 1.0
 */

@Service
public class CloudDiskServiceImpl implements CloudDiskService<IDBean> {
    // 全局变量定义
    private static final int bufferSize = 1024 * 1024 * 64;

    // 日志记录服务
    private static final Logger logger = LoggerFactory.getLogger(CloudDiskServiceImpl.class);
    @Autowired
    private FileSystem fileSystem;

    /**
     * 对不同用户的根目录
     * @param user
     * @return
     */
    private String rootPath(UserBean user){
        return "/user"+ user.getId() + "/root";
    }

    @Override
    public ResponseModel mkdirFolder(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String path = JsonUtil.getStringValue(jo, "path");
        ParameterUnspecificationUtil.checkNullString(path, "path must not null.");
        boolean target = false;
        path = rootPath(user)+"/"+ path;
        if (existFile(path)) {
            return new ResponseModel().error().message("文件已经存在");
        }
        Path src = new Path(path);
        try {
            target = fileSystem.mkdirs(src);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        if(target)
            return new ResponseModel().ok().message("创建文件夹成功！");
        else
            return new ResponseModel().error().message("创建文件夹失败").code(EnumUtil.ResponseCode.服务器处理异常.value);
    }

    @Override
    public boolean existFile(String path) {
        boolean target = false;
        if (StringUtils.isEmpty(path)) {
            return target;
        }
        Path src = new Path(path);
        try {
            target = fileSystem.exists(src);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return target;
    }

    @Override
    public ResponseModel readCatalog(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String path = JsonUtil.getStringValue(jo, "path") ;
        ParameterUnspecificationUtil.checkNullString(path, "path must not null.");
        path = rootPath(user) + "/" + path;
        if (!existFile(path)){
            //对根目录下的
            path = path.substring(rootPath(user).length(), path.length()).replace("/", "");
            if(StringUtil.isNotNull(path))
                return new ResponseModel().error().message("文件不存在！").code(EnumUtil.ResponseCode.服务器处理异常.value);
            Map<String, Object> extra = new HashMap<>();
            extra.put("path", "/");
            return new ResponseModel().ok().message(new ArrayList<>()).extra(extra);
        }

        // 目标路径
        Path newPath = new Path(path);
        FileStatus[] statusList = null;
        try {
            statusList = fileSystem.listStatus(newPath);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        if(null != statusList){
            List<Map<String, Object>> list = new ArrayList<>();
            if (statusList.length > 0) {
                for (FileStatus fileStatus : statusList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("filePath", fileStatus.getPath());
                    map.put("fileStatus", fileStatus.toString());
                    map.put("name", fileStatus.getPath().getName());
                    map.put("isdir", fileStatus.isDirectory());
                    map.put("size", fileStatus.getLen());
                    map.put("time", DateUtil.formatStringTime(DateUtil.DateToString(new Date(fileStatus.getModificationTime()))));
//                map.put("path", filePath.toString());
                    list.add(map);
                }
            }
            Map<String, Object> extra = new HashMap<>();
            extra.put("path", newPath.toString().substring(rootPath(user).length(), newPath.toString().length()));
            return new ResponseModel().ok().message(list).extra(extra);
        }

        return new ResponseModel().error();
    }

    @Override
    public ResponseModel createFile(CommonsMultipartFile[] multipartFiles, JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String path = JsonUtil.getStringValue(jo, "path");
        path = path.replaceAll("&", "");//过滤掉无效的字符
        path = path.replaceAll(",", "");//过滤掉无效的字符
        path = path.replaceAll("=", "");//过滤掉无效的字符
        ParameterUnspecificationUtil.checkNullString(path, "path must not null.");
        path = rootPath(user) + "/" + path;
        if(multipartFiles == null)
            throw new NullPointerException("multipartFiles must not null");

        for(CommonsMultipartFile multipartFile: multipartFiles){
            if(multipartFile == null)
                throw new NullPointerException("multipartFile为空");
            String fileName = multipartFile.getFileItem().getName();
            Path newPath = new Path(path + "/" + fileName);
            // 打开一个输出流
            FSDataOutputStream outputStream;
            try {
                outputStream = fileSystem.create(newPath);
                outputStream.write(multipartFile.getBytes());
                outputStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return new ResponseModel().ok().message("创建文件成功！");
    }

    @Override
    public ResponseModel createLocalFile(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String path = JsonUtil.getStringValue(jo, "path");
        String fileName = JsonUtil.getStringValue(jo, "fileName");
        ParameterUnspecificationUtil.checkNullString(path, "path must not null.");
        ParameterUnspecificationUtil.checkNullString(fileName, "fileName must not null.");

        File file = new File("D://"+ fileName);

        Path newPath = new Path(path + "/" + fileName);
        // 打开一个输出流
        FSDataOutputStream outputStream;
        try {
            //outputStream = fileSystem.create(newPath);
            HdfsFileSystem.copyFileToHDFSByFileObj(file, path + "/" + fileName);
            // 使用Hadoop提供的IOUtils，将in的内容copy到out，设置buffSize大小，是否关闭流设置true
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return new ResponseModel().ok().message("上传本地文件成功！");
    }

    @Override
    public ResponseModel readFileContent(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String path = JsonUtil.getStringValue(jo, "path");
        ParameterUnspecificationUtil.checkNullString(path, "path must not null.");
        StringBuffer sb = new StringBuffer();
        if (!existFile(path))
            return new ResponseModel().error().message("文件不存在！").code(EnumUtil.ResponseCode.服务器处理异常.value);

        // 目标路径
        Path srcPath = new Path(path);
        FSDataInputStream inputStream = null;
        try {
            inputStream = fileSystem.open(srcPath);
            // 防止中文乱码
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String lineTxt = "";
            while ((lineTxt = reader.readLine()) != null) {
                sb.append(lineTxt);
            }
        }catch(Exception e){
            logger.error(e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return new ResponseModel().ok().message(sb.toString());
    }

    @Override
    public ResponseModel listFile(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String path =JsonUtil.getStringValue(jo, "path") ;
        ParameterUnspecificationUtil.checkNullString(path, "path must not null.");
        path = rootPath(user) + "/" + path;
        List<Map<String, Object>> returnList = new ArrayList<>();
        if (!existFile(path))
            return new ResponseModel().error().message("文件不存在！").code(EnumUtil.ResponseCode.服务器处理异常.value);

        // 目标路径
        Path srcPath = new Path(path);
        // 递归找到所有文件
        try{
            //FileStatus[] statuses = fileSystem.listStatus(srcPath);
            RemoteIterator<LocatedFileStatus> filesList = fileSystem.listFiles(srcPath, false);
            while (filesList.hasNext()) {
                LocatedFileStatus next = filesList.next();
                String fileName = next.getPath().getName();
                Path filePath = next.getPath();
                Map<String, Object> map = new HashMap<>();
                map.put("name", fileName);
                map.put("isdir", next.isDirectory());
                map.put("size", next.getLen());
                map.put("time", DateUtil.formatStringTime(DateUtil.DateToString(new Date(next.getModificationTime()))));
                map.put("path", filePath.toString());
                returnList.add(map);
            }
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        return new ResponseModel().ok().message(returnList);
    }

    @Override
    public ResponseModel renameFile(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String oldName = JsonUtil.getStringValue(jo, "oldName");
        String newName = JsonUtil.getStringValue(jo, "newName");
        ParameterUnspecificationUtil.checkNullString(oldName, "oldName must not null.");
        ParameterUnspecificationUtil.checkNullString(newName, "newName must not null.");
        oldName = rootPath(user)+ "/" + oldName;
        newName = rootPath(user)+ "/" + newName;
        boolean target = false;
        // 原文件目标路径
        Path oldPath = new Path(oldName);
        // 重命名目标路径
        Path newPath = new Path(newName);

        if (existFile(newPath.toString()))
            return new ResponseModel().error().message("文件已存在！").code(EnumUtil.ResponseCode.服务器处理异常.value);

        try{
            target = fileSystem.rename(oldPath, newPath);
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        if(target)
            return new ResponseModel().ok();
        else
            return new ResponseModel().error().message("请确认文件名称是否存在！");
    }

    @Override
    public ResponseModel deleteFile(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String path = JsonUtil.getStringValue(jo, "path");
        ParameterUnspecificationUtil.checkNullString(path, "path must not null.");
        boolean target = false;
        path = rootPath(user)+ "/"+ path;
        if (!existFile(path))
            return new ResponseModel().error().message("文件不存在！").code(EnumUtil.ResponseCode.服务器处理异常.value);

        Path srcPath = new Path(path);
        try{
            //deleteOnExit是jvm上的删除，在控制台还是能显示
//            target = fileSystem.deleteOnExit(srcPath);

            //这个是真正的物理删除，执行后在页面控制台就看不到了
            target = fileSystem.delete(srcPath, true);
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        if(target)
            return new ResponseModel().ok();
        else
            return new ResponseModel().error();
    }

    @Override
    public ResponseModel deleteBatch(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String path = JsonUtil.getStringValue(jo, "path");
        String root = JsonUtil.getStringValue(jo, "root");
        ParameterUnspecificationUtil.checkNullString(path, "path must not null.");
        String[] files = path.split(",");
        for(int i = 0; i < files.length; i++){
            String path0 = rootPath(user)+ "/"+ root + "/"+ files[i];
            try{
                //这个是真正的物理删除，执行后在页面控制台就看不到了
                fileSystem.delete(new Path(path0), true);
            }catch(Exception e){
                logger.error(e.getMessage());
            }
        }
        return new ResponseModel().ok();
    }

    @Override
    public ResponseModel uploadFile(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String path = JsonUtil.getStringValue(jo, "path");
        String uploadPath = JsonUtil.getStringValue(jo, "uploadPath");
        ParameterUnspecificationUtil.checkNullString(path, "path must not null.");
        ParameterUnspecificationUtil.checkNullString(uploadPath, "uploadPath must not null.");
        // 上传路径
        Path clientPath = new Path(path);
        // 目标路径
        Path serverPath = new Path(uploadPath);

        // 调用文件系统的文件复制方法，第一个参数是否删除原文件true为删除，默认为false
        try {
            fileSystem.copyFromLocalFile(false, clientPath, serverPath);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return new ResponseModel().ok().message("文件上传成功！");
    }

    @Override
    public FSDataInputStream downloadFile(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String path = rootPath(user) + "/" + JsonUtil.getStringValue(jo, "path");
        ParameterUnspecificationUtil.checkNullString(path, "path must not null.");
        // 目标路径
        Path serverPath = new Path(path);

        // 调用文件系统的文件复制方法，第一个参数是否删除原文件true为删除，默认为false
        try {
//            fileSystem.copyToLocalFile(false, clientPath, serverPath);
            return fileSystem.open(serverPath);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public ResponseModel copyFile(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String sourcePath = JsonUtil.getStringValue(jo, "sourcePath");
        String targetPath = JsonUtil.getStringValue(jo, "targetPath");
        ParameterUnspecificationUtil.checkNullString(sourcePath, "sourcePath must not null.");
        ParameterUnspecificationUtil.checkNullString(targetPath, "targetPath must not null.");
        // 原始文件路径
        Path oldPath = new Path(sourcePath);
        // 目标路径
        Path newPath = new Path(targetPath);

        FSDataInputStream inputStream = null;
        FSDataOutputStream outputStream = null;
        try {
            try{
                inputStream = fileSystem.open(oldPath);
                outputStream = fileSystem.create(newPath);
                IOUtils.copyBytes(inputStream, outputStream, bufferSize, false);
            }catch(Exception e){
                logger.error(e.getMessage());
            }
        } finally {
            try{
                inputStream.close();
                outputStream.close();
            }catch(Exception e){
                logger.error(e.getMessage());
            }

        }

        return new ResponseModel().ok().message("文件复制成功！");
    }

    @Override
    public ResponseModel openFileToBytes(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String path = JsonUtil.getStringValue(jo, "path");
        byte[] bytes= null;
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        if (!existFile(path)) {
            return null;
        }
        // 目标路径
        Path srcPath = new Path(path);
        try {
            FSDataInputStream inputStream = fileSystem.open(srcPath);
//            bytes = IOUtils.(inputStream);
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        return new ResponseModel().ok().message(bytes);
    }

    @Override
    public ResponseModel getFileBlockLocations(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
        String path = JsonUtil.getStringValue(jo, "path");
        BlockLocation[] blocks = null;
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        if (!existFile(path)) {
            return null;
        }
        // 目标路径
        Path srcPath = new Path(path);
        try{
            FileStatus fileStatus = fileSystem.getFileStatus(srcPath);
            blocks = fileSystem.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        return new ResponseModel().ok().message(blocks);

    }
}
