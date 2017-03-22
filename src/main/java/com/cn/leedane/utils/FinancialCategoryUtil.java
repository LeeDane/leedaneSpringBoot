package com.cn.leedane.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cn.leedane.model.FinancialOneLevelCategoryBean;
import com.cn.leedane.model.FinancialTwoLevelCategoryBean;
import com.cn.leedane.service.FinancialOneCategoryService;
import com.cn.leedane.service.FinancialTwoCategoryService;
import com.cn.leedane.springboot.SpringUtil;

/**
 * 记账分类工具类
 * @author LeeDane
 * 2016年12月8日 下午9:07:40
 * Version 1.0
 */
public class FinancialCategoryUtil {
	
	private static FinancialCategoryUtil instance = null;
	
	public static synchronized FinancialCategoryUtil getInstance() {
		if (instance == null) {
			synchronized (FinancialCategoryUtil.class) {
				if (instance == null) {
					instance = new FinancialCategoryUtil();
				}
			}
		}
		return instance;
	}
	
	private static int createUserId;
	private static List<FinancialOneLevelCategoryBean> oneLevelCategoryBeans;
	
	private static List<FinancialTwoLevelCategoryBean> twoLevelCategoryBeans;

	@SuppressWarnings("unchecked")
	public static List<FinancialOneLevelCategoryBean> getOneLevelCategoryBeans() {
		
		if(oneLevelCategoryBeans == null){
			//数据库获取一级分类
			FinancialOneCategoryService<FinancialOneLevelCategoryBean> categoryService = (FinancialOneCategoryService<FinancialOneLevelCategoryBean>) SpringUtil.getBean("financialOneCategoryService");
			oneLevelCategoryBeans = categoryService.getAllDefault(createUserId);
		}
		return oneLevelCategoryBeans;
	}

	@SuppressWarnings("unchecked")
	public static List<FinancialTwoLevelCategoryBean> getTwoLevelCategoryBeans() {
		
		if(twoLevelCategoryBeans == null){
			//数据库获取二级分类
			FinancialTwoCategoryService<FinancialTwoLevelCategoryBean> categoryService = (FinancialTwoCategoryService<FinancialTwoLevelCategoryBean>) SpringUtil.getBean("financialTwoCategoryService");
			twoLevelCategoryBeans = categoryService.getAllDefault(createUserId);
		}
		return twoLevelCategoryBeans;
	}

	private FinancialCategoryUtil(){
		createUserId = OptionUtil.adminUser.getId();
		//获取一级分类
		if(CollectionUtil.isEmpty(getOneLevelCategoryBeans()))
			initOneGategoryData();
		
		//获取二级分类
		if(CollectionUtil.isEmpty(getTwoLevelCategoryBeans()))
			initTwoGategoryData();
	}

	/**
     * 第一次使用初始化一级分类
     */
    @SuppressWarnings("unchecked")
	public static List<FinancialOneLevelCategoryBean> initOneGategoryData(){
        oneLevelCategoryBeans = new ArrayList<>();

        Date date = new Date();

        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean1 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean1.setCategoryOrder(101);
        FinancialOneLevelCategoryBean1.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean1.setCategoryValue("食品酒水");
        FinancialOneLevelCategoryBean1.setIsDefault(true);
        FinancialOneLevelCategoryBean1.setSystem(true);
        FinancialOneLevelCategoryBean1.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean1.setCreateTime(date);
        FinancialOneLevelCategoryBean1.setModel(ConstantsUtil.FINANCIAL_MODEL_SPEND);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean1);

        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean14 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean14.setCategoryOrder(102);
        FinancialOneLevelCategoryBean14.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean14.setCategoryValue("日常购物");
        FinancialOneLevelCategoryBean14.setSystem(true);
        FinancialOneLevelCategoryBean14.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean14.setCreateTime(date);
        FinancialOneLevelCategoryBean14.setModel(ConstantsUtil.FINANCIAL_MODEL_SPEND);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean14);

        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean2 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean2.setCategoryOrder(103);
        FinancialOneLevelCategoryBean2.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean2.setCategoryValue("衣服饰品");
        FinancialOneLevelCategoryBean2.setSystem(true);
        FinancialOneLevelCategoryBean2.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean2.setCreateTime(date);
        FinancialOneLevelCategoryBean2.setModel(ConstantsUtil.FINANCIAL_MODEL_SPEND);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean2);

        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean3 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean3.setCategoryOrder(104);
        FinancialOneLevelCategoryBean3.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean3.setCategoryValue("居家物业");
        FinancialOneLevelCategoryBean3.setSystem(true);
        FinancialOneLevelCategoryBean3.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean3.setCreateTime(date);
        FinancialOneLevelCategoryBean3.setModel(ConstantsUtil.FINANCIAL_MODEL_SPEND);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean3);

        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean4 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean4.setCategoryOrder(105);
        FinancialOneLevelCategoryBean4.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean4.setCategoryValue("行车交通");
        FinancialOneLevelCategoryBean4.setSystem(true);
        FinancialOneLevelCategoryBean4.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean4.setCreateTime(date);
        FinancialOneLevelCategoryBean4.setModel(ConstantsUtil.FINANCIAL_MODEL_SPEND);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean4);

        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean5 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean5.setCategoryOrder(106);
        FinancialOneLevelCategoryBean5.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean5.setCategoryValue("交流通讯");
        FinancialOneLevelCategoryBean5.setSystem(true);
        FinancialOneLevelCategoryBean5.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean5.setCreateTime(date);
        FinancialOneLevelCategoryBean5.setModel(ConstantsUtil.FINANCIAL_MODEL_SPEND);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean5);

        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean6 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean6.setCategoryOrder(107);
        FinancialOneLevelCategoryBean6.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean6.setCategoryValue("休闲娱乐");
        FinancialOneLevelCategoryBean6.setSystem(true);
        FinancialOneLevelCategoryBean6.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean6.setCreateTime(date);
        FinancialOneLevelCategoryBean6.setModel(ConstantsUtil.FINANCIAL_MODEL_SPEND);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean6);

        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean7 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean7.setCategoryOrder(108);
        FinancialOneLevelCategoryBean7.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean7.setCategoryValue("学习进修");
        FinancialOneLevelCategoryBean7.setSystem(true);
        FinancialOneLevelCategoryBean7.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean7.setCreateTime(date);
        FinancialOneLevelCategoryBean7.setModel(ConstantsUtil.FINANCIAL_MODEL_SPEND);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean7);

        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean8 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean8.setCategoryOrder(109);
        FinancialOneLevelCategoryBean8.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean8.setCategoryValue("人情往来");
        FinancialOneLevelCategoryBean8.setSystem(true);
        FinancialOneLevelCategoryBean8.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean8.setCreateTime(date);
        FinancialOneLevelCategoryBean8.setModel(ConstantsUtil.FINANCIAL_MODEL_SPEND);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean8);

        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean10 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean10.setCategoryOrder(110);
        FinancialOneLevelCategoryBean10.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean10.setCategoryValue("金融银行");
        FinancialOneLevelCategoryBean10.setSystem(true);
        FinancialOneLevelCategoryBean10.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean10.setCreateTime(date);
        FinancialOneLevelCategoryBean10.setModel(ConstantsUtil.FINANCIAL_MODEL_SPEND);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean10);

        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean9 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean9.setCategoryOrder(111);
        FinancialOneLevelCategoryBean9.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean9.setCategoryValue("生活保健");
        FinancialOneLevelCategoryBean9.setSystem(true);
        FinancialOneLevelCategoryBean9.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean9.setCreateTime(date);
        FinancialOneLevelCategoryBean9.setModel(ConstantsUtil.FINANCIAL_MODEL_SPEND);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean9);

        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean11 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean11.setCategoryOrder(112);
        FinancialOneLevelCategoryBean11.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean11.setCategoryValue("其他杂项");
        FinancialOneLevelCategoryBean11.setSystem(true);
        FinancialOneLevelCategoryBean11.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean11.setCreateTime(date);
        FinancialOneLevelCategoryBean11.setModel(ConstantsUtil.FINANCIAL_MODEL_SPEND);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean11);

        //收入大类
        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean12 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean12.setCategoryOrder(113);
        FinancialOneLevelCategoryBean12.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean12.setCategoryValue("职业收入");
        FinancialOneLevelCategoryBean12.setSystem(true);
        FinancialOneLevelCategoryBean12.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean12.setCreateTime(date);
        FinancialOneLevelCategoryBean12.setModel(ConstantsUtil.FINANCIAL_MODEL_INCOME);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean12);

        FinancialOneLevelCategoryBean FinancialOneLevelCategoryBean13 = new FinancialOneLevelCategoryBean();
        FinancialOneLevelCategoryBean13.setCategoryOrder(114);
        FinancialOneLevelCategoryBean13.setStatus(ConstantsUtil.STATUS_NORMAL);
        FinancialOneLevelCategoryBean13.setCategoryValue("其他收入");
        FinancialOneLevelCategoryBean13.setSystem(true);
        FinancialOneLevelCategoryBean13.setCreateUserId(createUserId);
        FinancialOneLevelCategoryBean13.setCreateTime(date);
        FinancialOneLevelCategoryBean13.setModel(ConstantsUtil.FINANCIAL_MODEL_INCOME);
        oneLevelCategoryBeans.add(FinancialOneLevelCategoryBean13);
        
        //数据库批量插入
		FinancialOneCategoryService<FinancialOneLevelCategoryBean> categoryService = (FinancialOneCategoryService<FinancialOneLevelCategoryBean>) SpringUtil.getBean("financialOneCategoryService");
		if(categoryService.batchInsert(oneLevelCategoryBeans))
			oneLevelCategoryBeans = categoryService.getAllDefault(createUserId);

        return oneLevelCategoryBeans;
    }
    
    
    /**
     * 通过一级分类的名称获取一级分类的ID
     * @param value
     * @return
     */
    public static int getOneLevelIdByValue(String value){
        int id = 0;
        if(StringUtil.isNull(value))
            return id;
        List<FinancialOneLevelCategoryBean> oneLevelCategories = getOneLevelCategoryBeans();
        if(!CollectionUtil.isEmpty(oneLevelCategories)){
            for(FinancialOneLevelCategoryBean category: oneLevelCategories){
                if(value.equals(category.getCategoryValue())){
                    id = category.getId();
                    break;
                }
            }
        }
        return id;
    }
    
    /**
     * 通过一级分类的Id获取一级分类的名称
     * @param oneLevelId
     * @return
     */
    public static String getValueByOneLevelId(int oneLevelId){
        String value = "";
        if(oneLevelId < 1)
            return value;
        List<FinancialOneLevelCategoryBean> oneLevelCategories = getOneLevelCategoryBeans();
        if(!CollectionUtil.isEmpty(oneLevelCategories)){
            for(FinancialOneLevelCategoryBean category: oneLevelCategories){
                if(oneLevelId == category.getId()){
                	value = category.getCategoryValue();
                    break;
                }
            }
        }
        return value;
    }
    
	/**
     * 第一次使用初始化二级分类
     */
    @SuppressWarnings("unchecked")
	public static List<FinancialTwoLevelCategoryBean> initTwoGategoryData(){
        
        twoLevelCategoryBeans = new ArrayList<FinancialTwoLevelCategoryBean>();
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("食品酒水"), "早餐", false, 1, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("食品酒水"), "午餐", false, 2, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("食品酒水"), "晚餐", true, 3, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("食品酒水"), "夜宵", false, 4, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("食品酒水"), "水果", false, 5, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("食品酒水"), "零食", false, 6, EnumUtil.FinancialIcons.饮料.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("食品酒水"), "饮料", false, 7, EnumUtil.FinancialIcons.饮料.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("食品酒水"), "买菜做饭", false, 8, EnumUtil.FinancialIcons.买菜做饭.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("食品酒水"), "请客吃饭", false, 9, EnumUtil.FinancialIcons.支付.value, true));

        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("日常购物"), "网上商城", false, 1, EnumUtil.FinancialIcons.购物车.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("日常购物"), "超市", false, 2, EnumUtil.FinancialIcons.商店.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("日常购物"), "便利店", false, 3, EnumUtil.FinancialIcons.购物.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("日常购物"), "杂货店", false, 4, EnumUtil.FinancialIcons.购物.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("日常购物"), "地摊", false, 5, EnumUtil.FinancialIcons.购物.value, true));

        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("衣服饰品"), "衣服裤子", false, 1, EnumUtil.FinancialIcons.包包.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("衣服饰品"), "鞋帽包包", false, 2, EnumUtil.FinancialIcons.包包.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("衣服饰品"), "化妆饰品", false, 3, EnumUtil.FinancialIcons.支付.value, true));

        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("居家物业"), "日常用品", false, 1, EnumUtil.FinancialIcons.支出.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("居家物业"), "水电煤气", false, 2, EnumUtil.FinancialIcons.支出.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("居家物业"), "房租", false, 3, EnumUtil.FinancialIcons.住房.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("居家物业"), "物业管理", false, 4, EnumUtil.FinancialIcons.住房.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("居家物业"), "维修保养", false, 5, EnumUtil.FinancialIcons.支出.value, true));

        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("行车交通"), "羊城通", false, 1, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("行车交通"), "公共车", false, 2, EnumUtil.FinancialIcons.地铁.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("行车交通"), "地铁", false, 3, EnumUtil.FinancialIcons.地铁.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("行车交通"), "出租车", false, 4, EnumUtil.FinancialIcons.出租车.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("行车交通"), "滴滴出行", false, 5, EnumUtil.FinancialIcons.出租车.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("行车交通"), "Uber", false, 6, EnumUtil.FinancialIcons.出租车.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("行车交通"), "私家车", false, 7, EnumUtil.FinancialIcons.出租车.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("行车交通"), "其他车费", false, 8, EnumUtil.FinancialIcons.交通.value, true));

        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("交流通讯"), "手机", false, 1, EnumUtil.FinancialIcons.手机.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("交流通讯"), "笔记本", false, 2, EnumUtil.FinancialIcons.笔记本.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("交流通讯"), "话费充值", false, 3, EnumUtil.FinancialIcons.话费充值.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("交流通讯"), "上网费", false, 4, EnumUtil.FinancialIcons.上网费.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("交流通讯"), "快递费", false, 5, EnumUtil.FinancialIcons.快递.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("交流通讯"), "其他电子", false, 6, EnumUtil.FinancialIcons.上网费.value, true));

        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("休闲娱乐"), "景区门票", false, 1, EnumUtil.FinancialIcons.支出.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("休闲娱乐"), "酒店住宿", false, 2, EnumUtil.FinancialIcons.旅店.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("休闲娱乐"), "运动健身", false, 3, EnumUtil.FinancialIcons.运动.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("休闲娱乐"), "腐败聚会", false, 4, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("休闲娱乐"), "休闲玩乐", false, 5, EnumUtil.FinancialIcons.蛋糕.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("休闲娱乐"), "旅游度假", false, 6, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("休闲娱乐"), "宠物宝贝", false, 7, EnumUtil.FinancialIcons.支付.value, true));

        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("学习进修"), "学习资料", false, 1, EnumUtil.FinancialIcons.教育.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("学习进修"), "培训进修", false, 2, EnumUtil.FinancialIcons.教育.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("学习进修"), "数码装备", false, 3, EnumUtil.FinancialIcons.笔记本.value, true));

        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("人情往来"), "送礼请客", false, 1, EnumUtil.FinancialIcons.礼物.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("人情往来"), "孝敬长辈", false, 2, EnumUtil.FinancialIcons.支出.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("人情往来"), "还人钱物", false, 3, EnumUtil.FinancialIcons.支出.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("人情往来"), "慈善捐助", false, 4, EnumUtil.FinancialIcons.支出.value, true));

        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("金融银行"), "信用卡", false, 1, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("金融银行"), "微信红包", false, 2, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("金融银行"), "支付宝红包", false, 3, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("金融银行"), "微信转账", false, 4, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("金融银行"), "支付宝转账", false, 5, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("金融银行"), "银行手续", false, 6, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("金融银行"), "投资亏损", false, 7, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("金融银行"), "按揭还款", false, 8, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("金融银行"), "消费税收", false, 9, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("金融银行"), "利息支出", false, 10, EnumUtil.FinancialIcons.支付.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("金融银行"), "赔偿罚款", false, 11, EnumUtil.FinancialIcons.支付.value, true));

        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("生活保健"), "药品费", false, 1, EnumUtil.FinancialIcons.支出.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("生活保健"), "保健费", false, 2, EnumUtil.FinancialIcons.支出.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("生活保健"), "美容费", false, 3, EnumUtil.FinancialIcons.支出.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("生活保健"), "治疗费", false, 4, EnumUtil.FinancialIcons.支出.value, true));

        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("其他杂项"), "其他支出", false, 1, EnumUtil.FinancialIcons.支出.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("其他杂项"), "意外丢失", false, 2, EnumUtil.FinancialIcons.支出.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("其他杂项"), "烂账丢失", false, 3, EnumUtil.FinancialIcons.支出.value, true));

        //收入大类
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("职业收入"), "工资收入", false, 1, EnumUtil.FinancialIcons.现金.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("职业收入"), "利息收入", false, 2, EnumUtil.FinancialIcons.现金.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("职业收入"), "加班收入", false, 3, EnumUtil.FinancialIcons.现金.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("职业收入"), "奖金收入", false, 4, EnumUtil.FinancialIcons.收入.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("职业收入"), "投资收入", false, 5, EnumUtil.FinancialIcons.收入.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("职业收入"), "兼职收入", false, 6, EnumUtil.FinancialIcons.收入.value, true));

        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("其他收入"), "礼金收入", false, 1, EnumUtil.FinancialIcons.收入.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("其他收入"), "中奖收入", false, 2, EnumUtil.FinancialIcons.收入.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("其他收入"), "意外来钱", false, 3, EnumUtil.FinancialIcons.收入.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("其他收入"), "经营所得", false, 4, EnumUtil.FinancialIcons.收入.value, true));
        twoLevelCategoryBeans.add(new FinancialTwoLevelCategoryBean(getOneLevelIdByValue("其他收入"), "借钱", false, 5, EnumUtil.FinancialIcons.收入.value, true));

        //数据库批量插入
  		FinancialTwoCategoryService<FinancialTwoLevelCategoryBean> categoryService = (FinancialTwoCategoryService<FinancialTwoLevelCategoryBean>) SpringUtil.getBean("financialTwoCategoryService");
  		if(categoryService.batchInsert(twoLevelCategoryBeans))
  			twoLevelCategoryBeans = categoryService.getAllDefault(createUserId);

        return twoLevelCategoryBeans;
    }
}
