package com.cn.leedane.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 文件路径service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:33:11
 * Version 1.0
 */
@Transactional
public interface FilePathService <T extends IDBean>{
	
	/**
	 * 将base64单个临时保存的文件转化成文件路径保存在FilePath表中
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public boolean saveEachTemporaryBase64ToFilePath(JSONObject jo, UserBean user, HttpServletRequest request)  throws Exception;
	
	/**
	 * 获取单张图片的base64字符串
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public String downloadBase64Str(JSONObject jo, UserBean user, HttpServletRequest request) throws Exception;

	/**
	 * 获取单张图片的图片列表信息
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public String getOneMoodImgs(JSONObject jo, UserBean user,
			HttpServletRequest request);
	
	/**
	 * 保存每一个filePath对象
	 * 里面会根据默认的分辨率分别生成多个不同固定大小的图片
	 * @param order
	 * @param base64
	 * @param user
	 * @param uuid
	 * @param tableName 表的名称
	 * @throws Exception
	 */
	public boolean saveEachFile(int order, String base64, UserBean user, String uuid, String tableName) throws Exception;
	
	/**
	 * 保存每一个filePath对象
	 * @param order
	 * @param base64
	 * @param user
	 * @param uuid
	 * @param tableName
	 * @param sourcePath
	 * @return
	 * @throws Exception
	 */
	public boolean saveEachFile(int order, String base64, UserBean user, String uuid, String tableName, String sourcePath) throws Exception;

	/**
	 * 分页获取该用户图片文件的路径列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> getUserImageByLimit(JSONObject jo,
			UserBean user, HttpServletRequest request);
	
	/**
	 * 保存源文件和其他不同分辨率大小的文件(filePath最好不要存放在file文件夹下)
	 * @param filePath
	 * @param user
	 * @param uuid
	 * @param tableName
	 * @param order
	 * @param version
	 * @param desc
	 * @return
	 */
	public boolean saveSourceAndEachFile(String filePath, UserBean user, String uuid, String tableName, int order, String version, String desc);
	
	/**
	 * 检验文件是否可以被下载
	 * @param fileOwnerId
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean canDownload(int fileOwnerId, String fileName);
	
	/**
	 * 分页获取该用户上传的文件的路径列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getUploadFileByLimit(JSONObject jo,
			UserBean user, HttpServletRequest request);
	
	/**
	 * 更新标记该文件已经上传到存储服务器
	 * @param fId
	 * @param qiniuPath
	 * @return
	 */
	public boolean updateUploadQiniu(int fId, String qiniuPath);
	/**
	 * 执行SQL对应字段的List<Map<String,Object>
	 * @param sql sql语句,参数直接写在语句中，存在SQL注入攻击de风险，慎用
	 * @param params ?对应的值
	 * @return
	 */
	public List<Map<String, Object>> executeSQL(String sql, Object ...params);
}
