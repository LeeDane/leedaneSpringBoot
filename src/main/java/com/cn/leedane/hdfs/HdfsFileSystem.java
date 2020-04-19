package com.cn.leedane.hdfs;

import java.io.*;
import java.net.URI;

import com.cn.leedane.springboot.SpringUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author LeeDane
 * 2020年03月11日 21:32
 * Version 1.0
 */
public class HdfsFileSystem {
    private static final Logger log = LoggerFactory.getLogger(HdfsFileSystem.class);

    public static void copyFileToHDFSByName(Configuration conf , String hdfsUri, String localFileName, String remoteFileName) throws IOException {
        FileSystem fs = FileSystem.get(URI.create(hdfsUri), conf);
        fs.copyFromLocalFile(new Path(localFileName), new Path(remoteFileName));
        System.out.println("copy from local file:" + localFileName + " to HDFS file:" + remoteFileName +" done.");
        log.info("copy from local file:" + localFileName + " to HDFS file:" + remoteFileName +" done.");
        fs.close();
    }
    public static void copyFileToHDFSByFileObj(File localPath, String hdfsPath) throws IOException {
        InputStream in = null;
        if(null==localPath || null==hdfsPath || hdfsPath.isEmpty()){
            log.warn("copyFileToHDFSByFile: localpath and hdfspath are required");
            return;
        }
        try {
            /*Configuration conf = new Configuration();
            FileSystem fileSystem = FileSystem.get(URI.create(hdfsPath), conf);*/
            FileSystem fileSystem = (FileSystem) SpringUtil.getBean("fileSystem");
            FSDataOutputStream out = fileSystem.create(new Path(hdfsPath));
            in = new BufferedInputStream(new FileInputStream(localPath));
            IOUtils.copyBytes(in, out, 4096, false);
            out.hsync();
            out.close();
            //in.close();
        }
        finally {
            IOUtils.closeStream(in);
        }
        return;
    }
    /*
     * Download hdfs file in URI to local file
     */
    public static void downloadFromHDFS(Configuration conf , String uri ,String remoteFileName, String localFileName) throws IOException {
        Path path = new Path(remoteFileName);
        FileSystem fs = FileSystem.get(URI.create(uri), conf);
        fs.copyToLocalFile(path, new Path(localFileName));
        fs.close();
        System.out.println("downloading file from " + remoteFileName + " to " + localFileName +" succeed");
        log.info("downloading file from " + remoteFileName + " to " + localFileName +" succeed");
        return;
    }
    /*
     * Download hdfs file in URI to local file
     */
    public static void downloadFromHDFS(String uri ,String HDFSFileName, OutputStream localFileOutPut) throws IOException {
        Configuration config=new Configuration();
        FileSystem fs = FileSystem.get(URI.create(uri),config);
        InputStream is = fs.open(new Path(uri+"/"+HDFSFileName));
        IOUtils.copyBytes(is,localFileOutPut,4096, true);//close in and out stream via this API itself.
        System.out.println("downloading HDFS file " + HDFSFileName + " succeed");
        log.info("downloading HDFS file " + HDFSFileName + " succeed");
        fs.close();
        return;
    }
    /*
     * check whether the HDFS file exists in given URI
     */
    public static boolean  exists(String HDFSUri,String HDFSFileName) {
        Configuration conf = new Configuration();
        boolean fileExists=false;
        try {
            FileSystem fileSystem = FileSystem.get(URI.create(HDFSUri), conf);
            fileExists=fileSystem.exists(new Path(HDFSUri+"/"+HDFSFileName));
        } catch (IOException e){
            log.error("hdfs:exist() exception occurs. exception:" + e.getMessage());
            return fileExists;
        }

        System.out.println("HDFS URI:" +HDFSUri+", fileName:"+HDFSFileName +" exists ? "+fileExists);
        log.info("HDFS URI:" +HDFSUri+",fileName:"+HDFSFileName +" exists ? "+fileExists);
        return fileExists;
    }
}
