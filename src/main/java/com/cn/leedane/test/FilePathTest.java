package com.cn.leedane.test;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.mapper.FilePathMapper;
import com.cn.leedane.mapper.OperateLogMapper;
import com.cn.leedane.model.FilePathBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.FilePathService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UserService;

/**
 * 文件相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:10:34
 * Version 1.0
 */
public class FilePathTest extends BaseTest {
	@Autowired
	private OperateLogMapper operateLogMapper;
	
	@Autowired
	private UserService<UserBean> userService;
	
	@Autowired
	private FilePathMapper filePathMapper; 
	@Test
	public void testupload(){
		doTestupload();
	}
	
	/**
	 * 测试上传
	 */
	public void doTestupload() {
		if(filePathMapper != null){
			List<FilePathBean> filePathBeans = filePathMapper.getBeans("select * from t_file_path where is_upload_qiniu = 0 ");

			if(filePathBeans != null && filePathBeans.size() > 0){			
				
				for(FilePathBean filePathBean: filePathBeans){					
					try {
					    //更新状态标记为已经上传
					    filePathBean.setUploadQiniu(ConstantsUtil.STATUS_NORMAL);
					    filePathBean.setQiniuPath(ConstantsUtil.QINIU_SERVER_URL + filePathBean.getPath());
					    filePathMapper.update(filePathBean);
					    System.out.println("上传七牛云存储服务器成功,文件本地路径：" + filePathBean.getPath());
					}catch (Exception e) {
						e.printStackTrace();
						System.out.println("上传七牛云存储服务器失败,文件本地路径：" + filePathBean.getPath());
						continue;
					}
				}
			}else{
				System.out.println("没有要上传到七牛云存储服务器的文件");
			}
		}else{
			System.out.println("filePathService为空");
		}
		
	}
}
