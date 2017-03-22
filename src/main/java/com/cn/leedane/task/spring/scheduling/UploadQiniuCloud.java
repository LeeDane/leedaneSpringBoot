package com.cn.leedane.task.spring.scheduling;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.handler.CloudStoreHandler;
import com.cn.leedane.mapper.FilePathMapper;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.StringUtil;

/**
 * 上传文件到七牛云存储服务器
 * @author LeeDane
 * 2016年7月12日 下午3:25:39
 * Version 1.0
 */
@Component("uploadQiniuCloud")
public class UploadQiniuCloud {
	
	@Autowired
	private FilePathMapper filePathMapper; 
	
	@Autowired
	private CloudStoreHandler cloudStoreHandler;
	
	public void setCloudStoreHandler(CloudStoreHandler cloudStoreHandler) {
		this.cloudStoreHandler = cloudStoreHandler;
	}
	
	public void upload() {
		if(filePathMapper != null){
			try {
				/*Object object = systemCache.getCache("leedaneProperties"); 
				Map<String, Object> properties = new HashMap<String, Object>();
				if(object != null){
					properties = (Map<String, Object>) object;
				}
				
				long requestTimeOut = 30000;
				
				if(properties.containsKey("qiniuRequestTimeOut")){
					requestTimeOut = Long.parseLong(StringUtil.changeNotNull(properties.get("qiniuRequestTimeOut")));
				}*/
				
				List<Map<String, Object>> filePathBeans = filePathMapper.executeSQL("select * from "+DataTableType.文件.value+" where is_upload_qiniu = 0 and table_name <> '"+DataTableType.心情.value+"'");

				if(filePathBeans != null && filePathBeans.size() > 0){		
					List<Map<String, Object>> fileBeans = cloudStoreHandler.executeUpload(filePathBeans);
					if(fileBeans != null && fileBeans.size() >0){
						for(Map<String, Object> m: fileBeans){
							if(m.containsKey("id")){
								filePathMapper.updateSql(EnumUtil.getBeanClass(EnumUtil.getTableCNName(DataTableType.文件.value)), " set qiniu_path=? , is_upload_qiniu=?, modify_time=? where id=? ", ConstantsUtil.QINIU_SERVER_URL + StringUtil.changeNotNull(m.get("path")), ConstantsUtil.STATUS_NORMAL, new Date(), StringUtil.changeObjectToInt(m.get("id")));
							}
						}
					}
				}else{
					//uploadManager = null;
					//System.gc();
					System.out.println("没有要上传到七牛云存储服务器的文件");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}else{
			System.out.println("filePathService为空");
		}		
	}	
}
