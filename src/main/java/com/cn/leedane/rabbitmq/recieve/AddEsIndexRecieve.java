package com.cn.leedane.rabbitmq.recieve;

import com.cn.leedane.handler.MoodHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.MoodBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.mybatis.SqlProvider;
import com.cn.leedane.mybatis.table.TableFormat;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.impl.HumpToUnderLineFormat;
import com.cn.leedane.springboot.ElasticSearchUtil;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.StringUtil;
import org.apache.log4j.Logger;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 日志消费者
 * @author LeeDane
 * 2016年8月5日 下午3:27:02
 * Version 1.0
 */
@Component
public class AddEsIndexRecieve implements IRecieve{
	private Logger logger = Logger.getLogger(getClass());
	public final static String QUEUE_NAME = "add_es_index_rabbitmq";

	@Autowired
	private UserHandler userHandler;

	public UserHandler getUserHandler() {
		if(userHandler == null)
			userHandler = (UserHandler)SpringUtil.getBean("userHandler");
		return userHandler;
	}

	@Autowired
	private MoodHandler moodHandler;

	public MoodHandler getMoodHandler() {
		if(moodHandler == null)
			moodHandler = (MoodHandler)SpringUtil.getBean("moodHandler");
		return moodHandler;
	}

	@Override
	public String getQueueName() {
		return QUEUE_NAME;
	}

	@Override
	public Class<?> getQueueClass() {
		return null;
	}

	@Override
	public boolean excute(Object obj) {
		boolean success = false;
		logger.info("开始从消息队列中执行索引加入");
		try{
			ElasticSearchUtil elasticSearchUtil = (ElasticSearchUtil)SpringUtil.getBean("elasticSearchUtil");
			if(obj instanceof BlogBean){
				BlogBean blogBean = (BlogBean)obj;
				HashMap<String, Object> buildFields = new HashMap<String, Object>();
				XContentBuilder content = build(ConstantsUtil.SEARCH_TYPE_BLOG, blogBean, buildFields);
				success = elasticSearchUtil.add(EnumUtil.DataTableType.博客.value, blogBean.getId(), buildFields, content );
			}else if(obj instanceof MoodBean){
				MoodBean moodBean = (MoodBean)obj;
				HashMap<String, Object> buildFields = new HashMap<String, Object>();
				XContentBuilder content = build(ConstantsUtil.SEARCH_TYPE_MOOD, moodBean, buildFields);
				success = elasticSearchUtil.add(EnumUtil.DataTableType.心情.value, moodBean.getId(), buildFields, content );
			}else if(obj instanceof UserBean){
				UserBean userBean = (UserBean)obj;
				HashMap<String, Object> buildFields = new HashMap<String, Object>();
				XContentBuilder content = build(ConstantsUtil.SEARCH_TYPE_USER, userBean, buildFields);
				success = elasticSearchUtil.add(EnumUtil.DataTableType.用户.value, userBean.getId(), buildFields, content );
			}else if(obj instanceof OperateLogBean){
				OperateLogBean operateLogBean = (OperateLogBean)obj;
				HashMap<String, Object> buildFields = new HashMap<String, Object>();
				XContentBuilder content = build(ConstantsUtil.SEARCH_TYPE_OPERATE_LOG, operateLogBean, buildFields);
				success = elasticSearchUtil.add(EnumUtil.DataTableType.操作日志.value, operateLogBean.getId(), buildFields, content );
			}

		}catch(Exception e){
			logger.error("AddEsIndexRecieve出错啦", e);
			e.printStackTrace();
		}
		logger.info("结束从消息队列中执行索引加入， 结果是："+ success);
		return success;
	}
	
	@Override
	public boolean errorDestroy() {
		return true;
	}


	/**
	 * 构建索引参数
	 * @param searchType
	 * @param bean
	 * @param buildFields
	 * @return
	 */
	private XContentBuilder build(int searchType, Object bean, HashMap<String, Object> buildFields){
		Class<?> beanClass = bean.getClass();
		SqlProvider provider = new SqlProvider();
		TableFormat tableFormat = new HumpToUnderLineFormat();
		Field[] fields = provider.getFields(beanClass);
		try {
			XContentBuilder content = XContentFactory.jsonBuilder().startObject();
			content.startObject("properties");
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				//特殊处理serialVersionUID
				if(field.getName().equalsIgnoreCase("serialVersionUID")){
					continue;
				}

				Column column = field.getAnnotation(Column.class);
				String columnName = "";
				if (column != null) {
					if (!column.required())
						continue;
					columnName = column.value();
				}
				if (StringUtils.isEmpty(columnName)) {
					columnName = tableFormat.getColumnName(field.getName());
				}
				field.setAccessible(true);
				Object value = field.get(bean);
				if (value != null) {
					columnName = columnName.toLowerCase();
					if(value instanceof Date) { //对日期做处理
						content.startObject(columnName)
								.field("type", "date")
								.field("format", "yyyy-MM-dd HH:mm:ss")
								.endObject();

						value = DateUtil.DateToString((Date) value);
					}else if(value instanceof String){
						content.startObject(columnName)
								.field("type", "text")
								.field("analyzer", "ik_max_word")
								.field("search_analyzer", "ik_max_word")
								.endObject();
					}
					buildFields.put(columnName, value);
				}
			}

			String userPicPath = null;
			if(buildFields.containsKey("create_user_id")){
				int createUserId = StringUtil.changeObjectToInt(buildFields.get("create_user_id"));
				userPicPath = getUserHandler().getUserPicPath(createUserId, "30x30");
				if(searchType == ConstantsUtil.SEARCH_TYPE_USER){

				}else if(searchType == ConstantsUtil.SEARCH_TYPE_MOOD){
					if(StringUtil.changeObjectToBoolean(buildFields.get("has_img"))){
						String uuid = StringUtil.changeNotNull(buildFields.get("uuid"));
						buildFields.put("imgs", getMoodHandler().getMoodImg(EnumUtil.DataTableType.心情.value, uuid, ConstantsUtil.DEFAULT_PIC_SIZE));
					}
					buildFields.put("account", getUserHandler().getUserName(createUserId));
				}else if(searchType == ConstantsUtil.SEARCH_TYPE_BLOG){
					buildFields.put("account", getUserHandler().getUserName(createUserId));
				}else if(searchType == ConstantsUtil.SEARCH_TYPE_OPERATE_LOG){
					buildFields.put("account", getUserHandler().getUserName(createUserId));
				}
			}else{
				if(searchType == ConstantsUtil.SEARCH_TYPE_USER){
					userPicPath = getUserHandler().getUserPicPath(StringUtil.changeObjectToInt(buildFields.get("id")), "30x30");
				}
			}

			buildFields.put("user_pic_path", userPicPath);
			content.endObject().endObject();
			return content;
		} catch (Exception e) {
			logger.error("AddEsIndexRecieve build XContentBuilder is exceptoin",  e);
		}
		return null;
	}
}
