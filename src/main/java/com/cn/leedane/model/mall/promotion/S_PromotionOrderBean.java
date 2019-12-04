package com.cn.leedane.model.mall.promotion;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 推广的支付订单的实体bean
 * @author LeeDane
 * 2018年3月26日 下午6:38:49
 * version 1.0
 */
@Table(value="t_mall_promotion_order")
public class S_PromotionOrderBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 创建时间
	 */
	@Column(value="p_create_time")
	@Field
	private String pCreateTime;
	
	/**
	 * 点击时间
	 */
	@Column(value="p_click_time")
	@Field
	private String pClickTime;
	
	/**
	 * 商品信息
	 */
	@Column(value="p_product_title")
	@Field
	private String pProductTitle;
	
	/**
	 * 商品ID
	 */
	@Column(value="p_product_id")
	@Field
	private String pProductId;
	
	/**
	 * 掌柜旺旺
	 */
	@Column(value="p_shop_talk")
	@Field
	private String pShopTalk;
	
	/**
	 * 所属店铺
	 */
	@Column(value="p_shop_name")
	@Field
	private String pShopName;
	
	/**
	 * 商品数量
	 */
	@Column(value="p_product_count")
	@Field
	private String pProductCount;
	
	/**
	 * 商品单价
	 */
	@Column(value="p_product_price")
	@Field
	private String pProductPrice;
	
	/**
	 * 订单状态
	 */
	@Column(value="p_order_status")
	@Field
	private String pOrderStatus;
	
	/**
	 * 订单类型
	 */
	@Column(value="p_order_type")
	@Field
	private String pOrderType;
	
	/**
	 * 收入比例
	 */
	@Column(value="p_income_ratio")
	@Field
	private String pIncomeRatio;
	
	/**
	 * 分成比例
	 */
	@Column(value="p_commission_ratio")
	@Field
	private String pCommissionRatio;
	
	/**
	 * 付款金额
	 */
	@Column(value="p_pay_amount")
	@Field
	private String pPayAmount;
	
	/**
	 * 效果预估
	 */
	@Column(value="p_effect_estimates")
	@Field
	private String pEffectEstimates;
	
	/**
	 * 结算金额
	 */
	@Column(value="p_settlement_amount")
	@Field
	private String pSettlementAmount;
	
	/**
	 * 预估收入
	 */
	@Column(value="p_estimated_income")
	@Field
	private String pEstimatedIncome;
	
	/**
	 * 结算时间
	 */
	@Column(value="p_settlement_time")
	@Field
	private String pSettlementTime;
	
	/**
	 * 佣金比率
	 */
	@Column(value="p_commission_rate")
	@Field
	private String pCommissionRate;
	
	/**
	 * 佣金金额
	 */
	@Column(value="p_commission_amount")
	@Field
	private String pCommissionAmount;
	
	/**
	 * 补贴比率
	 */
	@Column(value="p_subsidy_ratio")
	@Field
	private String pSubsidyRatio;
	
	/**
	 * 补贴金额
	 */
	@Column(value="p_subsidy_amount")
	@Field
	private String pSubsidyAmount;
	
	/**
	 * 补贴类型
	 */
	@Column(value="p_subsidy_type")
	@Field
	private String pSubsidyType;
	
	/**
	 * 成交平台
	 */
	@Column(value="p_transaction_platform")
	@Field
	private String pTransactionPlatform;
	
	/**
	 * 第三方服务来源
	 */
	@Column(value="p_third_party_service_source")
	@Field
	private String pThirdPartyServiceSource;
	
	/**
	 * 订单编号
	 */
	@Column(value="p_order_no")
	@Field
	private String pOrderNo;
	
	/**
	 * 类目名称
	 */
	@Column(value="p_category_name")
	@Field
	private String pCategoryName;
	
	/**
	 * 来源媒体ID
	 */
	@Column(value="p_source_media_id")
	@Field
	private String pSourceMediaId;
	
	/**
	 * 来源媒体名称
	 */
	@Column(value="p_source_media_name")
	@Field
	private String pSourceMediaName;
		
	/**
	 * 广告位ID
	 */
	@Column(value="p_advertising_id")
	@Field
	private String pAdvertisingId;
	
	/**
	 * 广告位名称
	 */
	@Column(value="p_advertising_name")
	@Field
	private String pAdvertisingName;
}
