package com.cn.leedane.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;

import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.model.UserBean;

/**
 * 公共的处理类
 * @author LeeDane
 * 2016年4月6日 下午12:11:43
 * Version 1.0
 */
public class CommonHandler {
	
	@Autowired
	private MoodHandler moodHandler;
	
	public void setMoodHandler(MoodHandler moodHandler) {
		this.moodHandler = moodHandler;
	}
	
	@Autowired
	private BlogHandler blogHandler;
	
	public void setBlogHandler(BlogHandler blogHandler) {
		this.blogHandler = blogHandler;
	}
	
	@Autowired
	private FriendHandler friendHandler;
	
	public void setFriendHandler(FriendHandler friendHandler) {
		this.friendHandler = friendHandler;
	}
	/**
	 * 通过表名和表ID获取该资源对象的展示内容
	 * @param tableName
	 * @param tableId
	 * @param user 当前登录的用户
	 * @param account 当前这个资源的作者的称呼
	 * @return
	 */
	public String getContentByTableNameAndId(String tableName, int tableId, UserBean user){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		//只支持心情和博客获取源
		if(DataTableType.心情.value.equalsIgnoreCase(tableName)){
			list = moodHandler.getMoodDetail(tableId, user, true);
		}else if(DataTableType.博客.value.equalsIgnoreCase(tableName)){
			list = blogHandler.getBlogDetail(tableId, user, true);
		}else{
			return "";
		}
		String content = ConstantsUtil.SOURCE_DELETE_TIP;
		if(StringUtil.isNull(tableName) || tableId < 1){
			return content;
		}
		
		
		String account = null;
		if(list != null && list.size() ==1 && list.get(0) != null){
			content = getContent(list.get(0));
			account = StringUtil.changeNotNull(list.get(0).get("account"));
		}
		//对原始的内容进行35个长度的截取
		if(StringUtil.isNotNull(content)){
			int length = content.length() > 35 ? 35 : content.length() ;
			content = (StringUtil.isNotNull(account) ? (account + ":"): "")+ content.substring(0, length);//展示的源文的前面35个字符
		}else{
			content = ConstantsUtil.SOURCE_DELETE_TIP;
		}
		
		//System.out.println("tableName:"+tableName+",tableId:"+tableId+",content:"+content);
		return content;
	}
	
	/**
	 * 获取实体对象
	 * @param tableName
	 * @param tableId
	 * @param user
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Object getBeanByTableNameAndId(String tableName, int tableId, UserBean user){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		//只支持心情和博客获取源
		if(DataTableType.心情.value.equalsIgnoreCase(tableName)){
			list = moodHandler.getMoodDetail(tableId, user, true);
			if(CollectionUtil.isNotEmpty(list)){
				JSONArray.toList(net.sf.json.JSONArray.fromObject(list));
			}
		}else if(DataTableType.博客.value.equalsIgnoreCase(tableName)){
			list = blogHandler.getBlogDetail(tableId, user, true);
			if(CollectionUtil.isNotEmpty(list)){
				JSONArray.toList(net.sf.json.JSONArray.fromObject(list));
			}
		}else{
			return null;
		}
		
		return null;
	}
	
	/**
	 * 获取资源的内容(先获取标题，没有标题获取摘要，没有摘要获取内容)
	 * @param details
	 * @return
	 */
	private String getContent(Map<String, Object> details){
		String content = null;
		if(details == null || details.isEmpty()){
			return content;
		}
		
		if(details.containsKey("title")){
			content = StringUtil.changeNotNull(details.get("title"));
		}else{
			if(details.containsKey("digest")){
				content = StringUtil.changeNotNull(details.get("digest"));
			}else{
				if(details.containsKey("content")){
					content = StringUtil.changeNotNull(details.get("content"));
				}
			}
		}
		return content;
	}

}
