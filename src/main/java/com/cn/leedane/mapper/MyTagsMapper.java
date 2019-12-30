package com.cn.leedane.mapper;

import com.cn.leedane.model.MoodBean;
import com.cn.leedane.model.MyTagsBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 我的标签mapper接口类
 * @author LeeDane
 * 2019年12月27日 下午11:12:40
 * Version 1.0
 */
public interface MyTagsMapper extends BaseMapper<MyTagsBean>{

	/**
	 * 获取我的标签列表
	 * @param uid
	 * @return
	 */
	public List<MyTagsBean> getTags(@Param("userId") long uid);


	/**
	 * 删除我的标签列表
	 * @param uid
	 * @return
	 */
	public int deleteTags(@Param("userId") long uid);

	/**
	 * 批量保存标签
	 * @param tagsBeans
	 * @return
	 */
	public int batchSave(@Param("tags") List<MyTagsBean> tagsBeans);
}
