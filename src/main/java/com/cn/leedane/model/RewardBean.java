package com.cn.leedane.model;

/**
 * 打赏实体bean
 * @author LeeDane
 * 2017年5月30日 下午7:13:54
 * version 1.0
 */
public class RewardBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5156266985978281068L;
	
	/**
	 * 打赏金额
	 */
	private float money;
	
	/**
	 * 打赏留言信息
	 */
	private String rewardDesc;
	
	/**
	 * 来自什么方式
	 */
	private String froms;
	
	/**
	 * 打赏对象的类型(对象表名)必须
	 */
	private String tableName;
	
	/**
	 * 打赏对象的表ID，必须
	 */
	private int tableId;
	
	/**
	 * 打赏的支付方式
	 */
	private int type;
	
	/**
	 * 是否打赏成功
	 */
	private boolean success;

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public String getRewardDesc() {
		return rewardDesc;
	}

	public void setRewardDesc(String rewardDesc) {
		this.rewardDesc = rewardDesc;
	}

	public String getFroms() {
		return froms;
	}

	public void setFroms(String froms) {
		this.froms = froms;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	

}
