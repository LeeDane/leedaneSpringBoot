package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 图库service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:33:27
 * Version 1.0
 */
@Transactional//对其实现类也自动添加事务
public interface GalleryService <T extends IDBean>{
	/**
	 * 添加或修改图库
	 * * "{'path':'http://img.baidu.com/fjff.jpg', 'desc':'网络图片', 'width': 100, 'height':100, 'length':1040449}"
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel manageLink(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	/**
	 * 检查该图片是否已经加入图库
	 * @param user
	 * @param path
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean isExist(UserBean user, String path);

	

	/**
	 * 移除图库
	 * {'gid':1}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel delete(long gid, JSONObject jo, UserBean user, HttpRequestInfoBean request) ;
	
	/**
	 * 分页获取指定用户图库的图片的路径列表
	 * "{'uid':1, 'pageSize':5, 'last_id': 1, 'first_id':1, 'method':'lowloading'}"(uploading, firstloading)
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResponseModel all(JSONObject jo,
			UserBean user, HttpRequestInfoBean request);

	/**
	 * 分页获取指定用户图库的图片的路径列表
	 * "{'uid':1, 'pageSize':5, 'last_id': 1, 'first_id':1, 'method':'lowloading'}"(uploading, firstloading)
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResponseModel paging(JSONObject jo,
										 UserBean user, HttpRequestInfoBean request);
	
}
