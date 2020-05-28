package com.cn.leedane.task.spring.scheduling;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.mapper.mall.S_ProductMapper;
import com.cn.leedane.mapper.mall.S_StatisticsMapper;
import com.cn.leedane.mapper.mall.S_WishMapper;
import com.cn.leedane.model.CommentBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.model.mall.S_StatisticsBean;
import com.cn.leedane.model.mall.S_WishBean;
import com.cn.leedane.service.CommentService;
import com.cn.leedane.service.VisitorService;
import com.cn.leedane.service.mall.S_WishService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 商品统计的任务
 * @author LeeDane
 * 2017年11月13日 下午3:35:04
 * version 1.0
 */
@Component("productStatistics")
public class ProductStatistics extends AbstractScheduling{
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private S_StatisticsMapper statisticsMapper;
	
	@Autowired
	private S_WishMapper wishMapper;
	
	@Autowired
	private S_ProductMapper productMapper;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Autowired
	private CommentService<CommentBean> commentService;
	
	@Autowired
	private VisitorService<VisitorBean> visitorService;
	
	@Autowired
	private S_WishService<S_WishBean> wishService;
	
	/**
	 * 静态变量保存所有的商品
	 */
	private static Map<Integer, Integer> productMap = new LinkedHashMap<Integer, Integer>();
	
	/**
	 * 执行爬取方法
	 */
	@Override
	public void execute() throws SchedulerException {
		//logger.info(DateUtil.getSystemCurrentTime("yyyy-MM-dd HH:mm:ss") + ":Sanwen:crawl()");
		try {
			
			productMap.clear();
			//取所有的商品
			List<Map<String, Object>> productsList = productMapper.getProducts();
			if(CollectionUtil.isNotEmpty(productsList)){
				for(Map<String, Object> mp: productsList)
					productMap.put(StringUtil.changeObjectToInt(mp.get("id")), 0);
				doExcute(productMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("商品统计的任务出现异常：execute()", e);
		}
	}

	private void doExcute(Map<Integer, Integer> productMap2) {
		if(productMap.isEmpty())
			return;
		Iterator<Map.Entry<Integer, Integer>> it = productMap.entrySet().iterator();  
		S_StatisticsBean statisticsBean;
		UserBean user = OptionUtil.adminUser;	
		
		//获取昨天的日期条件
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -1);
		Date yesterDay = calendar.getTime();
		String yesterDayString = DateUtil.DateToString(yesterDay, "yyyy-MM-dd");
		//String toDayString = DateUtil.DateToString(new Date(), "yyyy-MM-dd");
		
        while(it.hasNext()){  
            Map.Entry<Integer, Integer> entry = it.next();  
            int productId = entry.getKey();
            int value = entry.getValue();
            try{
            	//计算心愿单的数量
            	statisticsBean = new S_StatisticsBean();
            	statisticsBean.setCreateUserId(user.getId());
            	statisticsBean.setCreateTime(new Date());
            	statisticsBean.setProductId(productId);
            	statisticsBean.setStatus(ConstantsUtil.STATUS_NORMAL);
            	statisticsBean.setText(DateUtil.DateToString(yesterDay, "yyyy年MM月dd日")+ "心愿单数量");
            	statisticsBean.setDate(yesterDay);
            	
            	//心愿单数
            	statisticsBean.setWishTotal(wishService.getWishTotal(productId, yesterDayString));
            	
            	//评论数
            	statisticsBean.setCommentTotal(commentService.getTotal("t_comment", " where table_name='"+ DataTableType.商店商品.value +"' and table_id = " +productId +" and status = 1 and DATE_FORMAT(create_time, '%Y-%c-%d') = '"+ yesterDayString +"'"));
            	
            	//访问数
            	statisticsBean.setVisitorTotal(visitorService.getVisitorsByTime(DataTableType.商店商品.value, productId, yesterDayString));//测试改成今天

            	//查找是否已经存在相同的记录，是的话就删再保存
            	List<S_StatisticsBean> s_StatisticsBeans = statisticsMapper.findRecord(productId, yesterDayString);
            	if(CollectionUtil.isNotEmpty(s_StatisticsBeans)){
            		statisticsMapper.delete(s_StatisticsBeans.get(0));
            	}
            	//计算购买的数量statistics_type
            	statisticsMapper.save(statisticsBean);
            	it.remove(); //都处理成功就从map中删除
            }catch(Exception e){
        		if(value > 2){ //超过3次直接发有信息给管理员账号
            		notificationHandler.sendErrorNotification("商品统计出错， 商品id是"+ productId +", 原因是："+ e.getMessage(), "t_mall_statistics", productId, null);
            		it.remove();
            	}else{
            		entry.setValue(value + 1); //不超过将value加1
            	}
            }
        }  
        
        doExcute(productMap);
	}
}
