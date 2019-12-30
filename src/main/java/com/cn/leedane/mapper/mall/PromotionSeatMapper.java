package com.cn.leedane.mapper.mall;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_PromotionSeatBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 推广位管理mapper接口类
 * @author LeeDane
 * 2019年12月7日 下午11:33:02
 * version 1.0
 */
public interface PromotionSeatMapper extends BaseMapper<S_PromotionSeatBean>{
	/**
	 * 分页获取推广位列表
	 * @param platform
	 * @param status
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> paging(@Param("platform") String platform, @Param("status") int status,
                                            @Param("start") int start, @Param("pageSize") int pageSize,
											@Param("orderField") String	orderField, @Param("orderType") String orderType);

	/**
	 * 分页获取推广位列表的总数
	 * @param platform
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> pagingTotal(@Param("platform") String platform, @Param("status") int status);

	/**
	 * 在该平台还未分配推广位的用户列表
	 * @param platform
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> noallot(@Param("platform") String platform, @Param("status") int status);

	/**
	 * 获取所在平台最大的id
	 * @param platform
	 * @return
	 */
	public String getMaxId(@Param("platform") String platform);

	/**
	 * 批量保存推广位
	 * @param promotionSeatBeans
	 * @return
	 */
	public int batchSave(@Param("promotionSeatBeans") List<S_PromotionSeatBean> promotionSeatBeans);

	/**
	 * 获取用户的推广位列表
	 * @param userId
	 * @return
	 */
	public List<S_PromotionSeatBean> myPromotions(@Param("userId") long userId);
}
