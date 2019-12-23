package com.cn.leedane.mall.taobao.other;

import com.cn.leedane.mall.taobao.other.NTbkOrder;
import com.taobao.api.TaobaoResponse;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.mapping.ApiListField;

import java.util.List;

public class TbkRebateOrderGetResponse extends TaobaoResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiListField("n_tbk_order")
	@ApiField("n_tbk_order")
	private List<NTbkOrder> n_tbk_order;
}
