package com.cn.leedane.mall.taobao.api;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import java.util.Date;
import java.util.Map;

public class TbkRebateOrderGetRequest extends BaseTaobaoRequest<TbkRebateOrderGetResponse>{

	private String fields; //查询的字段

	private Date startTime; //开始时间
	private long span;
	private long pageNo;
	private long pageSize;
	private Long taskId;

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getTaskId() {
		return this.taskId;
	}

	public String getApiMethodName() {
		return "taobao.tbk.rebate.order.get";
	}

	public Map<String, String> getTextParams() {
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("fields", this.fields);
		txtParams.put("start_time", this.startTime);
		txtParams.put("span", this.span);
		txtParams.put("page_no", this.pageNo);
		txtParams.put("page_size", this.pageSize);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TbkRebateOrderGetResponse> getResponseClass() {
		return TbkRebateOrderGetResponse.class;
	}

	public void check() throws ApiRuleException {
		//RequestCheckUtils.checkNotEmpty(taskId, "taskId");
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setSpan(long span) {
		this.span = span;
	}

	public void setPageNo(long pageNo) {
		this.pageNo = pageNo;
	}

	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}
	
	
}
