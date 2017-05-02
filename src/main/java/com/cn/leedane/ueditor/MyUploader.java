package com.cn.leedane.ueditor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.baidu.ueditor.define.State;

/**
 * 重写百度富文本编辑器的Uploader类
 * @author LeeDane
 * 2016年12月1日 上午11:06:31
 * Version 1.0
 */
public class MyUploader {
	private HttpServletRequest request = null;
	private Map<String, Object> conf = null;
	private MultipartFile file = null;
	public MyUploader(MultipartFile file, HttpServletRequest request, Map<String, Object> conf) {
		this.request = request;
		this.conf = conf;
		this.file = file;
	}
	
	public final State doExec() {
		String filedName = (String)this.conf.get("fieldName");
		State state = null;
		if ("true".equals(this.conf.get("isBase64")))
			state = MyBase64Uploader.save(this.request.getParameter(filedName), this.conf);
		else {
			state = MyBinaryUploader.save(file, this.request, this.conf);
		}
	return state;
	}
}
