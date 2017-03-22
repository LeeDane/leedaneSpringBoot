package com.cn.leedane.ueditor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.upload.StorageManager;
import com.cn.leedane.handler.CloudStoreHandler;
import com.cn.leedane.model.ServerFileBean;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.DeleteServerFileSend;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.StringUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * 重写百度富文本编辑器的BinaryUploader类
 * @author LeeDane
 * 2016年12月1日 上午11:17:12
 * Version 1.0
 */
public class MyBinaryUploader {
	public static final State save(HttpServletRequest request, Map<String, Object> conf){
		FileItemStream fileStream = null;
		boolean isAjaxUpload = request.getHeader("X_Requested_With") != null;
		if (!ServletFileUpload.isMultipartContent(request)) {
			return new BaseState(false, 5);
		}
		
		ServletFileUpload upload = new ServletFileUpload(
				new DiskFileItemFactory());

		if (isAjaxUpload) {
			upload.setHeaderEncoding("UTF-8");
		}
		try{
			FileItemIterator iterator = upload.getItemIterator(request);

			while (iterator.hasNext()) {
				fileStream = iterator.next();
				
				if (!fileStream.isFormField())
					break;
				fileStream = null;
			}

			if (fileStream == null) {
				return new BaseState(false, 7);
			}
 
			String savePath = (String)conf.get("savePath");
			String originFileName = fileStream.getName();
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0, 
					originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long)conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[])conf.get("allowFiles"))) {
				return new BaseState(false, 8);
			}
			
			savePath = PathFormat.parse(savePath, originFileName);
			
			StringBuffer rootPath = new StringBuffer();
			rootPath.append(UeditorFolder.getInstance().getFileFolder());
			rootPath.append(File.separator);
			rootPath.append("ueditor_");
			rootPath.append(DateUtil.DateToString(new Date(), "yyyyMMdd"));
			rootPath.append("_");
			rootPath.append(StringUtil.getFileName(savePath));
			
			final String physicalPath = rootPath.toString();

			InputStream is = fileStream.openStream();
			State storageState = StorageManager.saveFileByInputStream(is, 
					physicalPath, maxSize);
			is.close();
			
			//上传到七牛云存储服务器上
			String qiniuUrl = null;
			if (storageState.isSuccess()) {
				qiniuUrl = CloudStoreHandler.uploadFile(null, new File(physicalPath));
				/*new Thread(new Runnable() {
					
					@Override
					public void run() {
						ServerFileBean bean = new ServerFileBean();
						bean.setPath(physicalPath);
						DeleteServerFileSend send = new DeleteServerFileSend(bean);
						SendMessage sendMessage = new SendMessage(send);
						sendMessage.sendMsg();
						
					}
				}).start();*/
			}

			if (storageState.isSuccess() && StringUtil.isLink(qiniuUrl)) {
				storageState.putInfo("url", qiniuUrl/*PathFormat.format(savePath)*/);
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);
			}

			return storageState;
		} catch (FileUploadException e) {
			return new BaseState(false, 6);
		} catch (IOException localIOException) {
		}
		return new BaseState(false, 4);
	}

	private static boolean validType(String type, String[] allowTypes) {
		List list = Arrays.asList(allowTypes);
		
		return list.contains(type);
	}
}
