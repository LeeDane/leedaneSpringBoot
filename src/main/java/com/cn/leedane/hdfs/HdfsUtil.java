package com.cn.leedane.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * https://blog.csdn.net/zhouzhiwengang/article/details/94465850
 * @author LeeDane
 * 2020年03月09日 20:57
 * Version 1.0
 */
public class HdfsUtil {

    /**
     *
     * @Title: upload @Description: 文件上传 @param: @param config @param: @param
     * source @param: @param destination @return: void @throws
     */
    public static void upload(HdfsConfig config, String source, String destination) {

        try {
            // 获得FileSystem对象，指定使用root用户上传
            FileSystem fileSystem = FileSystem.get(new URI(getHdfsUrl(config)), new Configuration(),
                    config.getUsername());
            // 创建输入流，参数指定文件输出地址
            InputStream in = new FileInputStream(source);
            // 调用create方法指定文件上传，参数HDFS上传路径
            OutputStream out = fileSystem.create(new Path(destination));
            // 使用Hadoop提供的IOUtils，将in的内容copy到out，设置buffSize大小，是否关闭流设置true
            IOUtils.copyBytes(in, out, 4096, true);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *
     * @Title: download @Description: 文件上传 @param: @param config @param: @param
     * source @param: @param destination @return: void @throws
     */
    public static void download(HdfsConfig config, String source, String destination) {

        try {
            // 获得FileSystem对象，指定使用root用户上传
            FileSystem fileSystem = FileSystem.get(new URI(getHdfsUrl(config)), new Configuration(),
                    config.getUsername());
            // 调用open方法进行下载，参数HDFS路径
            InputStream in = fileSystem.open(new Path(source));
            // 创建输出流，参数指定文件输出地址
            OutputStream out = new FileOutputStream(destination);
            IOUtils.copyBytes(in, out, 4096, true);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *
     * @Title: delete @Description: 文件删除 @param: @param config @param: @param
     * target @param: @return @return: boolean @throws
     */
    public static boolean delete(HdfsConfig config, String target) {
        boolean flag = false;
        try {
            // 获得FileSystem对象，指定使用root用户上传
            FileSystem fileSystem = FileSystem.get(new URI(getHdfsUrl(config)), new Configuration(), config.getUsername());
            // 调用delete方法，删除指定的文件。参数:false:表示是否递归删除
            flag = fileSystem.delete(new Path(target), false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return flag;
    }

    public static boolean mkdir(HdfsConfig config, String directory) {
        boolean flag = false;
        try {
            // 获得FileSystem对象
            FileSystem 	fileSystem = FileSystem.get(new URI(getHdfsUrl(config)), new Configuration(), config.getUsername());
            // 调用mkdirs方法，在HDFS文件服务器上创建文件夹。
            flag = fileSystem.mkdirs(new Path(directory));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        };

        return flag;
    }

    private static String getHdfsUrl(HdfsConfig config) {
        StringBuilder builder = new StringBuilder();
        builder.append("hdfs://").append(config.getHostname()).append(":").append(config.getPort());
        return builder.toString();
    }

    public static void main(String[] args) {

        // TODO Auto-generated method stub
        HdfsConfig config = new HdfsConfig("106.12.130.136", "9000", "root");
        String destination = "D:\\data.sql"; //centos7 hdfs 文件存储地址
        String source = "/hadoop/data.sql"; // windows 文件
        HdfsUtil.upload(config, destination, source);

        HdfsUtil.download(config, source, destination);
    }
}
