package com.cn.leedane.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONObject;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 图库service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:33:27
 * Version 1.0
 */
@Transactional("txManager")//对其实现类也自动添加事务
public interface GalleryService <T extends IDBean>{
	/**
	 * 把链接加入图库
	 * * "{'path':'http://img.baidu.com/fjff.jpg', 'desc':'网络图片', 'width': 100, 'height':100, 'length':1040449}"
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> addLink(JSONObject jo, UserBean user, HttpServletRequest request) throws Exception;

	
	/**
	 * 检查该图片是否已经加入图库
	 * @param user
	 * @param path
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean isExist(UserBean user, String path);

	

	/**
	 * 取消关注
	 * {'gid':1}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(JSONObject jo, UserBean user, HttpServletRequest request) ;
	
	/**
	 * 分页获取指定用户图库的图片的路径列表
	 * "{'uid':1, 'pageSize':5, 'last_id': 1, 'first_id':1, 'method':'lowloading'}"(uploading, firstloading)
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> getGalleryByLimit(JSONObject jo,
			UserBean user, HttpServletRequest request);
	
}
