package com.cn.leedane.ueditor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import com.cn.leedane.handler.CloudStoreHandler;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 重写百度富文本编辑器的BinaryUploader类
 * @author LeeDane
 * 2016年12月1日 上午11:17:12
 * Version 1.0
 */
public class MyBinaryUploader {
	public static final State save(MultipartFile file, HttpServletRequest request, Map<String, Object> conf){
		if (!ServletFileUpload.isMultipartContent(request)) {
			return new BaseState(false, 5);
		}
		
		InputStream is;
		try {
			//上面的file是按文件名接收的
			String originFileName = file.getOriginalFilename();
			is = file.getInputStream();//按此可以得到流
			if (is == null) {
				return new BaseState(false, 7);
			}
			
			//也可以直接对file进行操作搜索			
			String savePath = (String)conf.get("savePath");
			
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0, 
					originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long)conf.get("maxSize")).longValue();

			if (!MyBinaryUploader.validType(suffix, (String[])conf.get("allowFiles"))) {
				return new BaseState(false, 8);
			}
			
			savePath = PathFormat.parse(savePath, originFileName);
			
			StringBuffer rootPathBuffer = new StringBuffer();
			rootPathBuffer.append(UeditorFolder.getInstance().getFileFolder());
			rootPathBuffer.append(File.separator);
			rootPathBuffer.append("ueditor_");
			rootPathBuffer.append(DateUtil.DateToString(new Date(), "yyyyMMdd"));
			rootPathBuffer.append("_");
			rootPathBuffer.append(StringUtil.getFileName(savePath));
			
			final String physicalPath = rootPathBuffer.toString();
			State storageState = MyStorageManager.saveFileByInputStream(is, 
					physicalPath, maxSize);
			//关闭流
			is.close();
			
			//上传到七牛云存储服务器上
			String qiniuUrl = null;
			if (storageState.isSuccess()) {
				qiniuUrl = CloudStoreHandler.uploadFile(null, new File(physicalPath));
			}

			if (storageState.isSuccess() && StringUtil.isLink(qiniuUrl)) {
				storageState.putInfo("url", qiniuUrl/*PathFormat.format(savePath)*/);
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);
			}
			return storageState;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return new BaseState(false, 4);
	}

	public static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);
		return list.contains(type);
	}
}
