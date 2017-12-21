package com.cn.leedane.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 素材service接口类
 * @author LeeDane
 * 2017年5月22日 上午10:09:35
 * version 1.0
 */
@Transactional//对其实现类也自动添加事务
public interface MaterialService <T extends IDBean>{
	/**
	 * 保存素材
	 * * "{'path':'http://img.baidu.com/fjff.jpg', 'desc':'网络图片', 'width': 100, 'height':100, 'length':1040449}"
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> save(JSONObject jo, UserBean user, HttpServletRequest request);

	
	/**
	 * 检查该图片是否已经加入图库
	 * @param user
	 * @param path
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean isExist(UserBean user, String path);

	

	/**
	 * 取消素材
	 * @param materialId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(int materialId, UserBean user, HttpServletRequest request) ;
	
	/**
	 * 分页获取指定用户素材的图片的路径列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getMaterialByLimit(JSONObject jo,
			UserBean user, HttpServletRequest request);
	
}
