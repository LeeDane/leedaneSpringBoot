package com.cn.leedane.model;


/**
 * 断点上传实体类
 * @author LeeDane
 * 2016年7月12日 上午10:56:23
 * Version 1.0
 */
//@Table(name="T_UPLOAD")
public class UploadBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	
	//上传的状态,1：正常，0:禁用，2、删除
	
	/**
	 * 上传到本地路径
	 */
	private String path;
	
	/**
	 * 附属表的uuid
	 */
	private String tableUuid;
	
	/**
	 * 表名称，跟tableId构成唯一
	 */
	private String tableName;
	
	/**
	 * 系列化编号,必须是大于等于1的整数
	 */
	private int serialNumber;
	
	/**
	 * 必须，图像的排序，可以区分图像的位置
	 */
	private int fOrder;
	

	//@Column(length=255)
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	//@Column(name="table_uuid", length =120, nullable=true)
	public String getTableUuid() {
		return tableUuid;
	}

	public void setTableUuid(String tableUuid) {
		this.tableUuid = tableUuid;
	}
	
	//@Column(name="table_name", length= 15, nullable=false)
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	//@Column(name="f_order", length=2, nullable=false)
	public int getfOrder() {
		return fOrder;
	}

	public void setfOrder(int fOrder) {
		this.fOrder = fOrder;
	}

	//@Column(name="serial_number", length=3, nullable=false)
	public int getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}
	
}
