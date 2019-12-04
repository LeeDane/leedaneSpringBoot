package com.cn.leedane.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.cn.leedane.handler.circle.CirclePostHandler;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.StringUtil;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 公共的处理类
 * @author LeeDane
 * 2016年4月6日 下午12:11:43
 * Version 1.0
 */
@PropertySource(value = "classpath:leedane.properties",encoding = "utf-8")
@Component
public class CommonHandler {
	@Autowired
	private MoodHandler moodHandler;
	
	@Autowired
	private BlogHandler blogHandler;

	@Autowired
	private FriendHandler friendHandler;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private CirclePostHandler circlePostHandler;

	/**
	 * 通过表名和表ID获取该资源对象的展示内容
	 * @param tableName
	 * @param tableId
	 * @param user 当前登录的用户
	 * @return
	 */
	public String getContentByTableNameAndId(String tableName, long tableId, UserBean user){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		//只支持心情和博客获取源
		if(DataTableType.心情.value.equalsIgnoreCase(tableName)){
			list = moodHandler.getMoodDetail(tableId, user, true);
		}else if(DataTableType.博客.value.equalsIgnoreCase(tableName)){
			list = blogHandler.getBlogDetail(tableId, user, true);
		}else if(DataTableType.帖子.value.equalsIgnoreCase(tableName)){
			list = circlePostHandler.getPostDetail(tableId, user);
		}else{
			return "";
		}
		String content = LeedanePropertiesConfig.newInstance().getString("constant.source.delete.tip");
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
			int oldLength = content.length();
			int length = oldLength > 35 ? 35 : oldLength ;
			content = (StringUtil.isNotNull(account) ? (account + ":"): "")+ content.substring(0, length);//展示的源文的前面35个字符
			//对截取的添加......表示
			if(oldLength > (length + StringUtil.changeNotNull(account).length()))
				content += "......";
		}else{
			content = LeedanePropertiesConfig.newInstance().getString("constant.source.delete.tip");
		}
		
		//logger.info("tableName:"+tableName+",tableId:"+tableId+",content:"+content);
		return content;
	}
	
	/**
	 * 通过表名和表ID获取该资源对象的展示内容, 创建人id,创建人账号名称， 创建时间
	 * @param tableName
	 * @param tableId
	 * @param user 当前登录的用户
	 * @return
	 */
	public Map<String, Object> getSourceByTableNameAndId(String tableName, int tableId, UserBean user){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		//只支持心情和博客获取源
		if(DataTableType.心情.value.equalsIgnoreCase(tableName)){
			list = moodHandler.getMoodDetail(tableId, user, true);
		}else if(DataTableType.博客.value.equalsIgnoreCase(tableName)){
			list = blogHandler.getBlogDetail(tableId, user, true);
		}else if(DataTableType.帖子.value.equalsIgnoreCase(tableName)){
			list = circlePostHandler.getPostDetail(tableId, user);
		}else{
			return result;
		}
		String content = LeedanePropertiesConfig.newInstance().getString("constant.source.delete.tip");
		if(StringUtil.isNull(tableName) || tableId < 1){
			return result;
		}

		if(list != null && list.size() ==1 && list.get(0) != null){
			Map<String, Object> data = list.get(0);
			content = getContent(data);
			result.put("source_user_id", StringUtil.changeObjectToInt(data.get("create_user_id")));
			result.put("source_account", StringUtil.changeNotNull(data.get("account")));
			result.put("source_create_time", StringUtil.changeNotNull(data.get("create_time")));
		}
		//对原始的内容进行35个长度的截取
		if(StringUtil.isNotNull(content)){
			int oldLength = content.length();
			int length = oldLength > 35 ? 35 : oldLength ;
			content = content.substring(0, length);//展示的源文的前面35个字符
			//对截取的添加......表示
			if(oldLength > length)
				content += "......";
		}else{
			content = LeedanePropertiesConfig.newInstance().getString("constant.source.delete.tip");
		}
		result.put("source", content);
		//logger.info("tableName:"+tableName+",tableId:"+tableId+",content:"+content);
		return result;
	}
	
	/**
	 * 获取实体对象
	 * @param tableName
	 * @param tableId
	 * @param user
	 * @return
	 */
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
