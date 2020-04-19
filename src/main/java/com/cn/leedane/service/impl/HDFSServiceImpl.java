package com.cn.leedane.service.impl;

import com.cn.leedane.hdfs.GroupSort;
import com.cn.leedane.hdfs.GroupSortModel;
import com.cn.leedane.hdfs.HdfsFileSystem;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.service.HDFSService;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LeeDane
 * 2020年03月11日 14:46
 * Version 1.0
 */

@Service
public class HDFSServiceImpl implements HDFSService<IDBean> {

    // 全局变量定义
    private static final int bufferSize = 1024 * 1024 * 64;


    // 日志记录服务
    private static final Logger logger = LoggerFactory.getLogger(HDFSServiceImpl.class);

    @Autowired
    private FileSystem fileSystem;

    @Override
    public boolean mkdirFolder(String path) {
        // TODO Auto-generated method stub
        boolean target = false;
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        if (existFile(path)) {
            return true;
        }
        Path src = new Path(path);
        try {
            target = fileSystem.mkdirs(src);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }
        return target;
    }

    @Override
    public boolean existFile(String path) {
        // TODO Auto-generated method stub
        boolean target = false;

        if (StringUtils.isEmpty(path)) {
            return target;
        }
        Path src = new Path(path);
        try {
            target = fileSystem.exists(src);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }
        return target;
    }

    @Override
    public List<Map<String, Object>> readCatalog(String path) {
        // TODO Auto-generated method stub
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        if (!existFile(path)) {
            return null;
        }

        // 目标路径
        Path newPath = new Path(path);
        FileStatus[] statusList = null;
        try {
            statusList = fileSystem.listStatus(newPath);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }
        List<Map<String, Object>> list = new ArrayList<>();
        if (null != statusList && statusList.length > 0) {
            for (FileStatus fileStatus : statusList) {
                Map<String, Object> map = new HashMap<>();
                map.put("filePath", fileStatus.getPath());
                map.put("fileStatus", fileStatus.toString());
                list.add(map);
            }
            return list;
        } else {
            return null;
        }

    }

    @Override
    public void createFile(String path, MultipartFile file) {
        // TODO Auto-generated method stub
        if (StringUtils.isEmpty(path)) {
            return;
        }
        String fileName = file.getName();
        Path newPath = new Path(path + "/" + fileName);
        // 打开一个输出流
        FSDataOutputStream outputStream;
        try {
            outputStream = fileSystem.create(newPath);
            outputStream.write(file.getBytes());
            outputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }
    }

    @Override
    public void createLocalFile(String path, String fileName) {
        // TODO Auto-generated method stub
        if (StringUtils.isEmpty(path)) {
            return;
        }

        File file = new File("D://"+ fileName);

        Path newPath = new Path(path + "/" + fileName);
        // 打开一个输出流
        FSDataOutputStream outputStream;
        try {
            //outputStream = fileSystem.create(newPath);
            HdfsFileSystem.copyFileToHDFSByFileObj(file, path + "/" + fileName);
            // 使用Hadoop提供的IOUtils，将in的内容copy到out，设置buffSize大小，是否关闭流设置true
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }
    }

    @Override
    public String readFileContent(String path) {
        // TODO Auto-generated method stub
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        if (!existFile(path)) {
            return null;
        }
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
                // TODO Auto-generated catch block
                logger.error(e.getMessage());
            }
        }
        return sb.toString();
    }

    @Override
    public List<Map<String, String>> listFile(String path) {
        // TODO Auto-generated method stub
        List<Map<String, String>> returnList = new ArrayList<>();
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        if (!existFile(path)) {
            return null;
        }
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
                Map<String, String> map = new HashMap<>();
                map.put("fileName", fileName);
                map.put("filePath", filePath.toString());
                returnList.add(map);
            }
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        return returnList;
    }

    @Override
    public boolean renameFile(String oldName, String newName) {
        // TODO Auto-generated method stub
        boolean target = false;
        if (StringUtils.isEmpty(oldName) || StringUtils.isEmpty(newName)) {
            return false;
        }
        // 原文件目标路径
        Path oldPath = new Path(oldName);
        // 重命名目标路径
        Path newPath = new Path(newName);
        try{
            target = fileSystem.rename(oldPath, newPath);
        }catch(Exception e){
            logger.error(e.getMessage());
        }

        return target;
    }

    @Override
    public boolean deleteFile(String path) {
        // TODO Auto-generated method stub
        boolean target = false;
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        if (!existFile(path)) {
            return false;
        }
        Path srcPath = new Path(path);
        try{

            //deleteOnExit是jvm上的删除，在控制台还是能显示
//            target = fileSystem.deleteOnExit(srcPath);

            //这个是真正的物理删除，执行后在页面控制台就看不到了
            target = fileSystem.delete(srcPath, true);
        }catch(Exception e){
            logger.error(e.getMessage());
        }

        return target;

    }

    @Override
    public void uploadFile(String path, String uploadPath) {
        // TODO Auto-generated method stub
        if (StringUtils.isEmpty(path) || StringUtils.isEmpty(uploadPath)) {
            return;
        }
        // 上传路径
        Path clientPath = new Path(path);
        // 目标路径
        Path serverPath = new Path(uploadPath);

        // 调用文件系统的文件复制方法，第一个参数是否删除原文件true为删除，默认为false
        try {
            fileSystem.copyFromLocalFile(false, clientPath, serverPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }

    }

    @Override
    public void downloadFile(String path, String downloadPath) {
        // TODO Auto-generated method stub
        if (StringUtils.isEmpty(path) || StringUtils.isEmpty(downloadPath)) {
            return;
        }
        // 上传路径
        Path clientPath = new Path(path);
        // 目标路径
        Path serverPath = new Path(downloadPath);

        // 调用文件系统的文件复制方法，第一个参数是否删除原文件true为删除，默认为false
        try {
            fileSystem.copyToLocalFile(false, clientPath, serverPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }

    }

    @Override
    public void copyFile(String sourcePath, String targetPath) {
        // TODO Auto-generated method stub
        if (StringUtils.isEmpty(sourcePath) || StringUtils.isEmpty(targetPath)) {
            return;
        }
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

    }

    @Override
    public byte[] openFileToBytes(String path) {
        // TODO Auto-generated method stub
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
        return bytes;

    }

    @Override
    public BlockLocation[] getFileBlockLocations(String path) {
        // TODO Auto-generated method stub
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
        return blocks;

    }

    /**
     * 获取HDFS配置信息
     *
     * @return
     */
    public static Configuration getConfiguration() {
        System.setProperty("HADOOP_USER_NAME","hadoop");
        System.setProperty("user.name", "zkpk");
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://hadoop1:9000");
        configuration.set("mapred.job.tracker", "hdfs://hadoop1:9001");
        configuration.set("dfs.client.use.datanode.hostname", "true");
        // 运行在yarn的集群模式
        // configuration.set("mapreduce.framework.name", "yarn");
        // 这个配置是让main方法寻找该机器的mr环境
        // configuration.set("yarn.resourcemanmager.hostname", "node1");
        return configuration;
    }

    @Override
    public void doJob(String jobName, String inputPath, String outputPath) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = getConfiguration();
        Job job = Job.getInstance(conf, jobName);
        boolean target = false;
        // 目标路径
        Path targetPath = new Path(outputPath);
        if (existFile(outputPath)) {
            try{
                //这个是真正的物理删除，执行后在页面控制台就看不到了
                target = fileSystem.delete(targetPath, true);
            }catch(Exception e){
                logger.error(e.getMessage());
            }
        }

        job.setUser("hadoop");
        job.setJarByClass(GroupSort.class);

        // 设置reduce文件拆分个数
        // job.setNumReduceTasks(3);
        // 设置mapper信息
        job.setMapperClass(GroupSort.GroupSortMapper.class);
        job.setPartitionerClass(GroupSort.GroupSortPartitioner.class);
        job.setGroupingComparatorClass(GroupSort.GroupSortComparator.class);
        // 设置reduce信息
        job.setReducerClass(GroupSort.GroupSortReduce.class);

        // 设置Mapper的输出
        job.setMapOutputKeyClass(GroupSortModel.class);
        job.setMapOutputValueClass(IntWritable.class);

        // 设置mapper和reduce的输出格式，如果相同则只需设置一个
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 指定输入文件的位置
        FileInputFormat.addInputPath(job, new Path(inputPath));
        // 指定输入文件的位置
        FileOutputFormat.setOutputPath(job, targetPath);

        // 运行
        job.waitForCompletion(true);
    }

}
