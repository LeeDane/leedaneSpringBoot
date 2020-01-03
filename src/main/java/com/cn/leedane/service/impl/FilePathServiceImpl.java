package com.cn.leedane.service.impl;

import com.cn.leedane.handler.CloudStoreHandler;
import com.cn.leedane.mapper.FilePathMapper;
import com.cn.leedane.mapper.TemporaryBase64Mapper;
import com.cn.leedane.model.*;
import com.cn.leedane.service.FilePathService;
import com.cn.leedane.service.MoodService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 文件路径service实现类
 * @author LeeDane
 * 2016年7月12日 下午1:37:39
 * Version 1.0
 */
@Service("filePathService")
public class FilePathServiceImpl implements FilePathService<FilePathBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private FilePathMapper filePathMapper;
	
	@Autowired
	private MoodService<MoodBean> moodService;
	
	@Autowired
	private TemporaryBase64Mapper temporaryBase64Mapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private CloudStoreHandler cloudStoreHandler;
	
	@Value("${constant.qiniu.server.url}")
    public String QINIU_SERVER_URL;
	
	@Override
	public boolean saveEachTemporaryBase64ToFilePath(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FilePathServiceImpl-->saveEachTemporaryBase64ToFilePath():jo="+jo.toString());
		String uuid = JsonUtil.getStringValue(jo, "uuid");
		int order = JsonUtil.getIntValue(jo, "order");
		List<Map<String, Object>> contents = temporaryBase64Mapper.executeSQL("select content from T_TEMP_BASE64 where uuid = ? temp_order = ? order by start asc", uuid, order);
		if(contents != null && contents.size() > 0){
			StringBuffer bufferContent = new StringBuffer();
			for(Map<String, Object> map: contents){
				bufferContent.append(map.get("content").toString());
			}
			boolean isSaveEach = saveEachFile(order, bufferContent.toString(), user, uuid, DataTableType.心情.value);
			if(isSaveEach){
				//删除temporaryBase64表中相关的数据
				temporaryBase64Mapper.deleteByUuidAndOrder(uuid, order, DataTableType.心情.value);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 保存每一个filePath对象
	 * @param order
	 * @param base64
	 * @param user
	 * @param uuid
	 * @throws Exception
	 */
	@Override
	public boolean saveEachFile(int order, String base64, UserBean user, String uuid, String tableName, String sourcePath){
		logger.info("FilePathServiceImpl-->saveEachFile():order=" +order +", base64=" +base64 + ",uuid="+uuid);
		//String account = user.getAccount();
		List<Map<String, Object>> filePaths = new ArrayList<Map<String,Object>>();
		Map<String, Object> maps = new HashMap<String, Object>();

		FilePathBean filePathBean;
		File f;
		//保存原图
		if(!StringUtil.isNull(sourcePath)){
			filePathBean = new FilePathBean();
			filePathBean.setCreateTime(new Date());
			filePathBean.setCreateUserId(user.getId());
			filePathBean.setPicOrder(order);
			filePathBean.setPath(sourcePath);
			filePathBean.setPicSize(ConstantsUtil.DEFAULT_PIC_SIZE);//source
			f = new File(ConstantsUtil.getDefaultSaveFileFolder() +"file" +File.separator+sourcePath);
			int[] defaultWidthAndHeight = Base64ImageUtil.getImgWidthAndHeight(f);
			filePathBean.setWidth(defaultWidthAndHeight[0]);
			filePathBean.setHeight(defaultWidthAndHeight[1]);
			filePathBean.setLenght(f.length());
			filePathBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			filePathBean.setTableName(tableName);
			filePathBean.setTableUuid(uuid);
			filePathMapper.save(filePathBean);
			maps = new HashMap<String, Object>();
			maps.put("id", filePathBean.getId());
			maps.put("path", filePathBean.getPath());
			filePaths.add(maps);
		}
		
		
		//保存默认
		/*filePathBean = new FilePathBean();	
		String fileName = Base64ImageUtil.saveBase64ToImage(base64, account, Base64ImageUtil.buildFolderPath(FileType.FILE.value));
		if(StringUtil.isNotNull(fileName)){
			filePathBean.setBase64(base64);
			filePathBean.setCreateTime(new Date());
			filePathBean.setCreateUser(user);
			filePathBean.setOrder(order);
			filePathBean.setPath(fileName);
			filePathBean.setSize("default");
			f = new File(ConstantsUtil.getDefaultSaveFileFolder() +"file" +File.separator+fileName);
			int[] defaultWidthAndHeight = Base64ImageUtil.getImgWidthAndHeight(f);
			filePathBean.setWidth(defaultWidthAndHeight[0]);
			filePathBean.setHeight(defaultWidthAndHeight[1]);
			filePathBean.setLenght(f.length());
			filePathBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			filePathBean.setTableName(tableName);
			filePathBean.setTableUuid(uuid);
			filePathMapper.save(filePathBean);
			maps = new HashMap<String, Object>();
			maps.put("id", filePathBean.getId());
			maps.put("path", filePathBean.getPath());
			filePaths.add(maps);
		}*/
		
		
		//保存30x30
		/*filePathBean = new FilePathBean();					
		try {
			fileName = Base64ImageUtil.base64ImgTo30x30(base64, account);
			if(StringUtil.isNotNull(fileName)){
				f = new File(ConstantsUtil.getDefaultSaveFileFolder() +"file" +File.separator+fileName);
				filePathBean.setCreateTime(new Date());
				filePathBean.setCreateUser(user);
				filePathBean.setOrder(order);
				filePathBean.setPath(fileName);
				filePathBean.setSize("30x30");
				filePathBean.setWidth(30);
				filePathBean.setHeight(30);
				filePathBean.setLenght(f.length());
				filePathBean.setStatus(ConstantsUtil.STATUS_NORMAL);
				filePathBean.setTableName(tableName);
				filePathBean.setTableUuid(uuid);
				filePathMapper.save(filePathBean);
				maps = new HashMap<String, Object>();
				maps.put("id", filePathBean.getId());
				maps.put("path", filePathBean.getPath());
				filePaths.add(maps);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		
		
		//保存60x60
		/*filePathBean = new FilePathBean();					
		try {
			fileName = Base64ImageUtil.base64ImgTo60x60(base64, account);
			if(StringUtil.isNotNull(fileName)){
				f = new File(ConstantsUtil.getDefaultSaveFileFolder() +"file" +File.separator+fileName);
				filePathBean.setCreateTime(new Date());
				filePathBean.setCreateUser(user);
				filePathBean.setOrder(order);
				filePathBean.setPath(fileName);
				filePathBean.setSize("60x60");
				filePathBean.setWidth(60);
				filePathBean.setHeight(60);
				filePathBean.setLenght(f.length());
				filePathBean.setStatus(ConstantsUtil.STATUS_NORMAL);
				filePathBean.setTableName(tableName);
				filePathBean.setTableUuid(uuid);
				filePathMapper.save(filePathBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		
		
		//保存80x80
		/*filePathBean = new FilePathBean();					
		try {
			fileName = Base64ImageUtil.base64ImgTo80x80(base64, account);
			if(StringUtil.isNotNull(fileName)){
				f = new File(ConstantsUtil.getDefaultSaveFileFolder() +"file" +File.separator+fileName);
				filePathBean.setCreateTime(new Date());
				filePathBean.setCreateUser(user);
				filePathBean.setOrder(order);
				filePathBean.setPath(fileName);
				filePathBean.setSize("80x80");
				filePathBean.setWidth(80);
				filePathBean.setHeight(80);
				filePathBean.setLenght(f.length());
				filePathBean.setStatus(ConstantsUtil.STATUS_NORMAL);
				filePathBean.setTableName(tableName);
				filePathBean.setTableUuid(uuid);
				filePathMapper.save(filePathBean);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		
		//保存100x100
		/*filePathBean = new FilePathBean();					
		try {
			fileName = Base64ImageUtil.base64ImgTo100x100(base64, account);
			if(StringUtil.isNotNull(fileName)){
				f = new File(ConstantsUtil.getDefaultSaveFileFolder() +"file" +File.separator+fileName);
				filePathBean.setCreateTime(new Date());
				filePathBean.setCreateUser(user);
				filePathBean.setOrder(order);
				filePathBean.setPath(fileName);
				filePathBean.setSize("100x100");
				filePathBean.setWidth(100);
				filePathBean.setHeight(100);
				filePathBean.setLenght(f.length());
				filePathBean.setStatus(ConstantsUtil.STATUS_NORMAL);
				filePathBean.setTableName(tableName);
				filePathBean.setTableUuid(uuid);
				filePathMapper.save(filePathBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		
		
		//保存120x120
		/*filePathBean = new FilePathBean();					
		try {
			fileName = Base64ImageUtil.base64ImgTo120x120(base64, account);
			if(StringUtil.isNotNull(fileName)){
				f = new File(ConstantsUtil.getDefaultSaveFileFolder() +"file" +File.separator+fileName);
				filePathBean.setCreateTime(new Date());
				filePathBean.setCreateUser(user);
				filePathBean.setOrder(order);
				filePathBean.setPath(fileName);
				filePathBean.setSize("120x120");
				filePathBean.setWidth(120);
				filePathBean.setHeight(120);
				filePathBean.setLenght(f.length());
				filePathBean.setStatus(ConstantsUtil.STATUS_NORMAL);
				filePathBean.setTableName(tableName);
				filePathBean.setTableUuid(uuid);
				filePathMapper.save(filePathBean);
				maps = new HashMap<String, Object>();
				maps.put("id", filePathBean.getId());
				maps.put("path", filePathBean.getPath());
				filePaths.add(maps);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		
		
		//保存320x400
		/*filePathBean = new FilePathBean();					
		try {
			fileName = Base64ImageUtil.base64ImgTo320x400(base64, account);
			if(StringUtil.isNotNull(fileName)){
				f = new File(ConstantsUtil.getDefaultSaveFileFolder() +"file" +File.separator+fileName);
				filePathBean.setCreateTime(new Date());
				filePathBean.setCreateUser(user);
				filePathBean.setOrder(order);
				filePathBean.setPath(fileName);
				filePathBean.setSize("320x400");
				filePathBean.setWidth(320);
				filePathBean.setHeight(400);
				filePathBean.setLenght(f.length());
				filePathBean.setStatus(ConstantsUtil.STATUS_NORMAL);
				filePathBean.setTableName(tableName);
				filePathBean.setTableUuid(uuid);
				return filePathMapper.save(filePathBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		//执行文件的上传到云存储服务器操作
		if(filePaths.size() > 0){
			logger.info("zhinging");
			int fid = 0;
			for(int i = 0; i <filePaths.size(); i++){
				fid = cloudStoreHandler.executeSingleUpload(filePaths.get(i));
				if(fid > 0){
					updateUploadQiniu(fid, QINIU_SERVER_URL + StringUtil.changeNotNull(filePaths.get(i).get("path")));
				}
			}
			/*List<Map<String, Object>> fileBeans = cloudStoreHandler.executeUpload(filePaths);
			if(fileBeans != null && fileBeans.size() >0){
				for(Map<String, Object> m: fileBeans){
					if(m.containsKey("id")){
						updateUploadQiniu(StringUtil.changeObjectToInt(m.get("id")), ConstantsUtil.QINIU_SERVER_URL + StringUtil.changeNotNull(m.get("path")));
					}
				}
			}*/
		}
		
		return true;
	}
	
	/**
	 * 保存每一个filePath对象
	 * @param order
	 * @param base64
	 * @param user
	 * @param uuid
	 * @throws Exception
	 */
	@Override
	public boolean saveEachFile(int order, String base64, UserBean user, String uuid, String tableName){
		return saveEachFile(order, base64, user, uuid, tableName, null);
	}

	@Override
	public String downloadBase64Str(JSONObject jo, UserBean user,
			HttpRequestInfoBean request){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOneMoodImgs(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getUserImageByLimit(JSONObject jo,
			UserBean user, HttpRequestInfoBean request) {
		
		return null;
	}

	@Override
	public boolean saveSourceAndEachFile(String filePath, UserBean user,
			String uuid, String tableName, int order, String version, String desc) {
		boolean result = false;
		
		//先把filePath从temporary文件夹下面移动到File文件夹下面
		File file = new File(filePath);
		if(!file.exists()){
			logger.error("源文件："+filePath+"不存在，直接返回保存失败");
			return result;
		}
		
		String fileName = filePath.substring(filePath.lastIndexOf("/")+ 1, filePath.length());
		//对源图，从temporary文件夹移动到File文件夹中
		String newPath = ConstantsUtil.getDefaultSaveFileFolder() +"file" + File.separator+fileName;
		File fileNew = new File(newPath);
		if(fileNew.exists()){
			fileNew.deleteOnExit();
		}else{
			try {
				fileNew.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			//临时文件复制到file文件夹下
			FileCopyUtils.copy(file, fileNew);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		
		//是图像
		if(Base64ImageUtil.isSupportType(fileName)){
			//暂时先使用以前的方式，获取bitmap
			try {
				String base64 = Base64ImageUtil.convertImageToBase64(filePath, fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()));	
				result = saveEachFile(order, base64, user, uuid, tableName, fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			FilePathBean filePathBean;
			
			//保存文件
			filePathBean = new FilePathBean();
			filePathBean.setCreateTime(new Date());
			filePathBean.setCreateUserId(user.getId());
			filePathBean.setPicOrder(order);
			filePathBean.setPath(fileName);
			filePathBean.setPicSize("source");
			filePathBean.setLenght(fileNew.length());
			filePathBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			filePathBean.setTableName(tableName);
			filePathBean.setTableUuid(uuid);
			filePathBean.setFileVersion(version);
			filePathBean.setFileDesc(desc);
			try {
				result = filePathMapper.save(filePathBean) > 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		
		if(file != null)
			file.delete();
		return result;
	}

	@Override
	public boolean canDownload(int fileOwnerId, String fileName) {
		List<Map<String, Object>> list = filePathMapper.executeSQL("select count(id) from "+DataTableType.文件.value+" where create_user_id = ? and status=? and (path = ? or qiniu_path=? )", fileOwnerId, ConstantsUtil.STATUS_NORMAL, fileName, fileName);
		return list != null && list.size() > 0;
	}

	@Override
	public Map<String, Object> getUploadFileByLimit(JSONObject jo,
			UserBean user, HttpRequestInfoBean request) {
		logger.info("FilePathServiceImpl-->getUploadFileByLimit():jo="+jo.toString());
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", 10); //每页的大小
		long lastId = JsonUtil.getLongValue(jo, "last_id");
		long firstId = JsonUtil.getLongValue(jo, "first_id");
		//String tableName = ConstantsUtil.UPLOAD_FILE_TABLE_NAME;
		
		//执行的方式，现在支持：uploading(向上刷新)，lowloading(向下刷新)，firstloading(第一次刷新)
		String method = JsonUtil.getStringValue(jo, "method");
		
		ResponseMap message = new ResponseMap();
		
		List<Map<String,Object>> r = new ArrayList<Map<String,Object>>();
		StringBuffer sql = new StringBuffer();
		logger.info("执行的方式是："+method +",pageSize:"+pageSize+",lastId:"+lastId+",firstId:"+firstId);
		//下刷新
		if(method.equalsIgnoreCase("lowloading")){
			sql.append("select f.id, f.path, f.is_upload_qiniu, f.table_name, f.table_uuid, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" from "+DataTableType.文件.value+" f");
			sql.append(" where f.status = ? and f.create_user_id = ? and f.id < ? order by f.id desc limit 0,?");
			r = filePathMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(), lastId, pageSize);
			
		//上刷新
		}else if(method.equalsIgnoreCase("uploading")){
			sql.append("select f.id, f.path, f.is_upload_qiniu, f.table_name, f.table_uuid, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" from "+DataTableType.文件.value+" f");
			sql.append(" where f.status = ? and f.create_user_id = ? and f.id > ?  limit 0,?");
			r = filePathMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(), firstId, pageSize);
			
		//第一次刷新
		}else if(method.equalsIgnoreCase("firstloading")){
			sql.append("select f.id, f.path, f.is_upload_qiniu, f.table_name, f.table_uuid, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" from "+DataTableType.文件.value+" f");
			sql.append(" where f.status = ? and f.create_user_id = ? order by f.id desc limit 0,?");
			r = filePathMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(), pageSize);
		}
		message.put("success", true);
		message.put("message", r);
		return message.getMap();
	}

	@Override
	public boolean updateUploadQiniu(int fId, String qiniuPath) {
		logger.info("FilePathServiceImpl-->updateUploadQiniu():文件ID="+fId+",文件路径："+qiniuPath);
		return filePathMapper.updateSql(EnumUtil.getBeanClass(EnumUtil.getTableCNName(DataTableType.文件.value)), " set qiniu_path=? , is_upload_qiniu=?, modify_time=? where id=? ", qiniuPath, ConstantsUtil.STATUS_NORMAL, new Date(), fId) > 0;
	}
	@Override
	public List<Map<String, Object>> executeSQL(String sql, Object... params) {
		return filePathMapper.executeSQL(sql, params);
	}
	
}
