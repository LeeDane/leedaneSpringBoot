package com.cn.leedane.ueditor;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.upload.StorageManager;
import com.cn.leedane.handler.CloudStoreHandler;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 重写百度富文本编辑器的Base64Uploader类
 * @author LeeDane
 * 2016年12月1日 下午3:04:49
 * Version 1.0
 */
public class MyBase64Uploader {
	public static State save(String content, Map<String, Object> conf){
		byte[] data = decode(content);

		long maxSize = ((Long)conf.get("maxSize")).longValue();

		if (!validSize(data, maxSize)) {
			return new BaseState(false, 1);
		}

		String suffix = FileType.getSuffix("JPG");

		String savePath = PathFormat.parse((String)conf.get("savePath"), 
				(String)conf.get("filename"));

		savePath = savePath + suffix;
		
		StringBuffer rootPath = new StringBuffer();
		rootPath.append(UeditorFolder.getInstance().getFileFolder());
		rootPath.append(File.separator);
		rootPath.append("ueditor_");
		rootPath.append(DateUtil.DateToString(new Date(), "yyyyMMdd"));
		rootPath.append("_");
		rootPath.append(StringUtil.getFileName(savePath));
		
		String physicalPath = rootPath.toString();

		State storageState = StorageManager.saveBinaryFile(data, physicalPath);
		
		//上传到七牛云存储服务器上
		String qiniuUrl = null;
		if (storageState.isSuccess()) {
			qiniuUrl = CloudStoreHandler.uploadFile(null, new File(physicalPath));
		}

		if (storageState.isSuccess()) {
			storageState.putInfo("url", qiniuUrl);
			storageState.putInfo("type", suffix);
			storageState.putInfo("original", "");
		}

		return storageState;
		}

		private static byte[] decode(String content) {
			return Base64.decodeBase64(content);
		}

		private static boolean validSize(byte[] data, long length) {
			return data.length <= length;
		}
}
