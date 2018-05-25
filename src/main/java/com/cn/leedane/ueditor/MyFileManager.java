package com.cn.leedane.ueditor;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.MultiState;
import com.baidu.ueditor.define.State;
import com.cn.leedane.utils.ConstantsUtil;

/**
 * 重写百度富文本编辑器的FileManager类
 * @author LeeDane
 * 2016年12月1日 下午1:24:12
 * Version 1.0
 */
public class MyFileManager {
	private String dir = null;
	//private String rootPath = null;
	private String[] allowFiles = null;
	private int count = 0;

	public MyFileManager(Map<String, Object> conf){
	     //this.rootPath = ((String)conf.get("rootPath"));
	     //this.dir = (this.rootPath + (String)conf.get("dir"));
	     this.dir = UeditorFolder.getInstance().getFileFolder();
	     this.allowFiles = getAllowFiles(conf.get("allowFiles"));
	     this.count = ((Integer)conf.get("count")).intValue();
	}

	public State listFile(int index){
		File dir = new File(this.dir);
		State state = null;

		if (!dir.exists()) {
			return new BaseState(false, 302);
		}

		if (!dir.isDirectory()) {
			return new BaseState(false, 301);
		}

		Collection<?> list = FileUtils.listFiles(dir, this.allowFiles, true);

		if ((index < 0) || (index > list.size())) {
			state = new MultiState(true);
		} else {
			Object[] fileList = Arrays.copyOfRange(list.toArray(), index, index + this.count);
			state = getState(fileList);
		}

		state.putInfo("start", index);
		state.putInfo("total", list.size());

		return state;
	}

	private State getState(Object[] files){
		MultiState state = new MultiState(true);
		BaseState fileState = null;

		File file = null;

		for (Object obj : files) {
			if (obj == null) {
				break;
			}
			file = (File)obj;
			fileState = new BaseState(true);
			fileState.putInfo("url", ConstantsUtil.QINIU_SERVER_URL + file.getName());
			state.addState(fileState);
		}
 
		return state;
	}

	/*private String getPath(File file){
		String path = file.getAbsolutePath();

		return path.replace(this.rootPath, "/");
	}*/

	private String[] getAllowFiles(Object fileExt){
		String[] exts = null;
		String ext = null;

		if (fileExt == null) {
			return new String[0];
		}

		exts = (String[])fileExt;
		
		int i = 0; for (int len = exts.length; i < len; i++){
			ext = exts[i];
			exts[i] = ext.replace(".", "");
		}

		return exts;
	}
}
