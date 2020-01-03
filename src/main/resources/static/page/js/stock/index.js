layui.use(['layer', 'laydate', 'util'], function(){
	layer = layui.layer;
	laydate = layui.laydate;
	util = layui.util;
	
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-stock").addClass("active");
	
  	//获取屏幕的宽度
 	//浏览器可视区域页面的宽度
 	var winW = $(window).width(); 
 	if(winW < 900){
 		$aa = $(".laydate-theme-molv .layui-laydate-main");
 		$aa.attr("max-width", (winW - 20) +"px!important")
 	}
 	$stocksContrainer = $('#stocks-contrainer');
 	init();
 	
 	$(".stock-operate-btn").click(function(){
 		var formControl = $('#stockForm').find(".form-control");
 		
 		var params = {};
 		var flag = true;
 		formControl.each(function(index){
 			var name = $(this).attr("name");
 			var empty = $(this).attr("empty");
 			if(empty && empty == "false" && isEmpty($(this).val())){
 				$(this).focus();
 				layer.msg($(this).attr("placeholder"));
 				flag = false;
 				return flag;
 			}
 				
 			params[name] = $(this).val();
 		});
 		
 		if(flag){
 			var dataId = $(this).closest("#stock-operate").find("#stockForm").attr("data-id");
 			dataId = dataId && parseInt(dataId) >=0 ? parseInt(dataId): 0;
 			if(dataId > 0){
 				params["id"] = dataId ;
 			}
 			
 			/*if(isNotEmpty(params.modify_time)){
 				if(isEmpty(params.modify_time_date)){
 					layer.msg("请先选择日期");
 					formControl.find("name=['modify_time_date']").focus();
 					return;
 				}
 				
 				
 			}*/
 			params["modify_time"] = params.modify_time_date + " " + params.modify_time +":00";
 			var loadi = layer.load('努力加载中…');
 			$.ajax({
 				type : dataId > 0 ? "PUT": "post",
 				data: params,
 				url : dataId > 0 ? "/stock/"+ dataId: "/stock/add",
 				dataType: 'json', 
 				beforeSend:function(){
 				},
 				success : function(data) {
 						layer.close(loadi);
 						layer.msg(data.message);
 						if(data.success){
 							if(dataId > 0)
 								window.location.reload();
 							else
 								window.open("/stock", "_self");
 						}
 						
 				},
 				error : function(dd) {
 					layer.close(loadi);
 					layer.msg("网络请求失败");
 				}
 			});
 		}
 	});
 	
 	$(".stock-buy-operate-btn").click(function(){
 		var formControl = $('#stockBuyForm').find(".form-control");
 		
 		var params = {};
 		var flag = true;
 		formControl.each(function(index){
 			var name = $(this).attr("name");
 			var empty = $(this).attr("empty");
 			if(empty && empty == "false" && isEmpty($(this).val())){
 				$(this).focus();
 				layer.msg($(this).attr("placeholder"));
 				flag = false;
 				return flag;
 			}
 				
 			params[name] = $(this).val();
 		});
 		
 		if(flag){
 			var stockId = $(this).closest("#stock-buy-operate").find("#stockBuyForm").attr("stock-id");
 			var dataId = $(this).closest("#stock-buy-operate").find("#stockBuyForm").attr("stock-buy-id");
 			dataId = dataId && parseInt(dataId) >=0 ? parseInt(dataId): 0;
 			if(dataId > 0){
 				params["id"] = dataId ;
 			}
 			params["modify_time"] = params.modify_time_date + " " + params.modify_time +":00";
 			params["stock_id"] = stockId ;
 			var loadi = layer.load('努力加载中…');
 			$.ajax({
 				type : dataId > 0 ? "PUT": "post",
 				data: params,
 				url : dataId > 0 ? "/stock/"+ stockId + "/buy/" + dataId: "/stock/"+ stockId+"/buy/add",
 				dataType: 'json', 
 				beforeSend:function(){
 				},
 				success : function(data) {
 						layer.close(loadi);
 						layer.msg(data.message);
 						if(data.success){
 							if(dataId > 0)
 								window.location.reload();
 							else
 								window.open("/stock", "_self");
 						}
 						
 				},
 				error : function(dd) {
 					layer.close(loadi);
 					layer.msg("网络请求失败");
 				}
 			});
 		}
 	});
 	
 	$(".stock-sell-operate-btn").click(function(){
 		var formControl = $('#stockSellForm').find(".form-control");
 		
 		var params = {};
 		var flag = true;
 		formControl.each(function(index){
 			var name = $(this).attr("name");
 			var empty = $(this).attr("empty");
 			if(empty && empty == "false" && isEmpty($(this).val())){
 				$(this).focus();
 				layer.msg($(this).attr("placeholder"));
 				flag = false;
 				return flag;
 			}
 				
 			params[name] = $(this).val();
 		});
 		
 		if(flag){
 			var stockId = $(this).closest("#stock-sell-operate").find("#stockSellForm").attr("stock-id");
 			var stockBuyId = $(this).closest("#stock-sell-operate").find("#stockSellForm").attr("stock-buy-id");
 			var dataId = $(this).closest("#stock-sell-operate").find("#stockSellForm").attr("stock-sell-id");
 			dataId = dataId && parseInt(dataId) >=0 ? parseInt(dataId): 0;
 			if(dataId > 0){
 				params["id"] = dataId ;
 			}
 			params["modify_time"] = params.modify_time_date + " " + params.modify_time +":00";
 			params["stock_id"] = stockId ;
 			params["stock_buy_id"] = stockBuyId ;
 			//params["modify_time"] = '2018-01-01 10:10:10'
 			var loadi = layer.load('努力加载中…');
 			$.ajax({
 				type : dataId > 0 ? "PUT": "post",
 				data: params,
 				url : dataId > 0 ? "/stock/"+ stockId + "/buy/" + stockBuyId +"/sell/" +dataId: "/stock/"+ stockId+"/buy/"+ stockBuyId +"/sell/add",
 				dataType: 'json', 
 				beforeSend:function(){
 				},
 				success : function(data) {
 						layer.close(loadi);
 						layer.msg(data.message);
 						if(data.success){
 							if(dataId > 0)
 								window.location.reload();
 							else
 								window.open("/stock", "_self");
 						}
 						
 				},
 				error : function(dd) {
 					layer.close(loadi);
 					layer.msg("网络请求失败");
 				}
 			});
 		}
 	});
});

var $stocksContrainer;
var initData;
/**
 * 获取留言板列表
 */
function init(){
	$.ajax({
		url : "/stock/init?t=" + Math.random(),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			if(data.success){
				if(data.message.length == 0){
					$stocksContrainer.append('请先添加一支股票！');
					return;
				}
				
				initData = data.message;
				var html = '';
				for(var i = 0; i < data.message.length; i++){
					var stockDisplay = data.message[i];
						html += '<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12">'+
									  '<fieldset>'+
									  '<legend onclick="stockClick(this);">'+ stockDisplay.name +'('+ stockDisplay.code +')&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +'<small>'+ stockDisplay.time +'&nbsp;&nbsp;持仓'+ stockDisplay.holding +'</small>'+
									  	'<button type="button" onclick="stockBuyAdd(this, event, '+ i +', -1);" class="btn btn-xs" style="margin-left: 10px;"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>  购买</button>'+
									  	'<button type="button" onclick="stockEdit(this, event, '+ i +');" class="btn btn-info btn-xs operate-button" style="margin-left: 10px;"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span>  编辑</button>'+
										'<button type="button" onclick="stockDelete(this, event, '+ stockDisplay.id +');" class="btn btn-danger btn-xs operate-button" style="margin-left: 10px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span>  删除</button></legend>'+
									  '<div '+ (i > 0 ? 'style="display:none"': '') +'>'+
									    	'<div class="">'+
											  '<table class="layui-table" data-id="'+ stockDisplay.id +'">'+
											    '<colgroup>'+
											      '<col width="150" />'+
											      '<col width="150" />'+
											      '<col width="150" />'+
											      '<col width="150" />'+
											      '<col width="150" />'+
											      '<col />'+
											    '</colgroup>'+
											    '<thead>'+
											      '<tr>'+
											      	'<th>买入(股)</th>'+
											        '<th>持股数</th>'+
											        '<th>买入单价(元)</th>'+
											        '<th>买入时间</th>'+
											        '<th>操作时间</th>'+
											        '<th>操作</th>'+
											      '</tr> '+
											    '</thead>'+
											    '<tbody>';
										for(var j = 0; j < stockDisplay.buys.length; j++){
										var buyDisplay = stockDisplay.buys[j];
										html +=  '<tr onclick="buyLineClick(this);" style="cursor: pointer;">'+
											        '<td>'+ buyDisplay.number +'</td>'+
											        '<td>'+ buyDisplay.holding +'</td>'+
											        '<td>'+ buyDisplay.price +'</td>'+
											        '<td>'+ buyDisplay.time +'</td>'+
											        '<td>'+ buyDisplay.createTime +'</td>'+
											        '<td>'+(buyDisplay.sellOut? '': '<button type="button" class="btn btn-xs operate-button" onclick="stockSellAdd(this, event, '+ i +','+ j +', -1);" style="margin-left: 10px;"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>  卖出</button>')+'<button type="button" onclick="stockBuyEdit(this, event, '+ i +', '+ j +');" class="btn btn-info btn-xs operate-button" style="margin-left: 10px;"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span>  编辑</button><button type="button" onclick="stockBuyDelete(this, event, '+ stockDisplay.id +', '+ buyDisplay.id +');" class="btn btn-danger btn-xs operate-button" style="margin-left: 10px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span>  删除</button></td>'+
											      '</tr>'+
											      '<tr class="sell-contrainer" '+ (i == 0 && j == 0 ? '' : 'style="display: none;"') +'>'+
											      	'<td colspan="6">'+
											      		'<table class="layui-table" lay-skin="line" data-id="'+ buyDisplay.id +'">'+
														    '<colgroup>'+
														      '<col width="150" />'+
														      '<col width="150" />'+
														      '<col width="150" />'+
														      '<col width="150" />'+
														      '<col width="150" />'+
														      '<col />'+
														    '</colgroup>'+
														    '<thead>'+
														      '<tr style="background-color: #d9edf7!important;">'+
														        '<th>卖出(股)</th>'+
														        '<th>单价(元)</th>'+
														        '<th>是否赚钱</th>'+
														        '<th>卖出时间</th>'+
														        '<th>操作时间</th>'+
														        '<th>操作</th>'+
														      '</tr> '+
														    '</thead>'+
														    '<tbody>';
										for(var x = 0; x < buyDisplay.sells.length; x++){
											var sellDisplay = buyDisplay.sells[x];
													 html  += '<tr>'+
														        '<td>'+ sellDisplay.number +'</td>'+
														        '<td>'+ sellDisplay.price +'</td>'+
														        '<td><font color="red">'+ (sellDisplay.price > buyDisplay.price? "赚钱": "亏损") +'</font></td>'+
														        '<td>'+ sellDisplay.time +'</td>'+
														        '<td>'+ sellDisplay.createTime +'</td>'+
														        '<td><button type="button" onclick="stockSellEdit(this, event, '+i+','+j+','+x+');" class="btn btn-info btn-xs operate-button" style="margin-left: 10px;"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span>  编辑</button><button type="button" onclick="stockSellDelete(this, event, '+ stockDisplay.id +', '+ buyDisplay.id +', '+ sellDisplay.id +');" class="btn btn-danger btn-xs operate-button" style="margin-left: 10px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span>  删除</button></td>'+
														      '</tr>';
										}
													html += '</tbody>'+
														  '</table>'+
											      	'</td>'+
											      '</tr>';
										}
										html +=  '</tbody>'+
											  '</table>'+
											'</div>'+
									  '</div>'+
									'</fieldset>'+
							   '</div>';
				}
				
				$stocksContrainer.html(html);
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			ajaxError(data);
		}
	});
}

/**
 * 新增股票
 * @param obj
 * @param e
 * @param index  股票的id,为空或者小于0表示新增
 */
function addStock(obj, e, index){
	var dateStr = new Date().Format("yyyy-MM-dd");
	var timeStr = new Date().Format("HH:mm");
	stopEventClick(e);
    //alert("新增股票");
	var edit = index >= 0;
	var stock = edit? initData[index]: null;
var editHtml = '<form role="form" id="stockForm" data-id="'+ (edit ? stock.id: 0) +'">'+
				'<div class="form-group">'+
				  '<label >股票名称<span style="font-size: 10px;"><font color="red">*</font>必填</span></label>'+
				  '<input empty="false" type="text" class="form-control" name="name" placeholder="请输入股票名称" value="'+changeNotNullString(stock != null? stock.name: '')+'">'+
				'</div>'+
				'<div class="form-group">'+
				  '<label >股票代码<span style="font-size: 10px;"><font color="red">*</font>必填</span></label>'+
				  '<input empty="false" type="number" class="form-control" name="code" placeholder="请输入股票代码" value="'+changeNotNullString(stock != null? stock.code: '')+'">'+
				'</div>'+
				'<div class="form-group">'+
				  '<label for="birth_day">购买股票时间<span style="font-size: 10px;"><font color="red">*</font>必填</span></label>'+
				  '<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12">'+
				  '<div class="col-lg-6 col-sm-6 col-md-6 col-xs-6">'+
				  	'<input empty="false" placeholder="请先选择购买日期" type="date" class="form-control col-lg-4 col-sm-4 col-md-4 col-xs-4"  name="modify_time_date" value="'+changeNotNullString(stock != null? stock.modityDate : dateStr)+'">'+
				  '</div>'+
				  '<div class="col-lg-6 col-sm-6 col-md-6 col-xs-6">'+
				  	'<input empty="false" placeholder="请先选择购买时间" type="time" class="form-control col-lg-4 col-sm-4 col-md-4 col-xs-4" name="modify_time" value="'+changeNotNullString(stock != null? stock.modityTime : timeStr)+'">'+
				  '</div>'+
				  '</div>'+
				'</div>'+
				'</form';
			$(".modal-body-stock-operate").html(editHtml);
    $("#stock-operate").modal("show");
}

/**
 * 编辑股票
 * @param obj
 * @param e
 * @param index
 */
function stockEdit(obj, e, index){
	stopEventClick(e);
	addStock(obj, e, index);
}

/**
 * 删除股票
 * @param obj
 * @param e
 * @param index
 */
function stockDelete(obj, e, id){
	stopEventClick(e);
	layer.confirm('您确定要删除该股票吗？此是不可逆行为，请慎重!', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "DELETE",
			url : "/stock/"+ id,
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data != null && data.success){
					layer.msg(data.message);
					window.location.reload();
				}else{
					ajaxError(data);
				}
				console.log(data);
			},
			error : function(data) {
				layer.close(loadi);
				ajaxError(data);
			}
		});
	}, function(){
	});
}


/**
 * 新增买入记录
 * @param obj
 * @param e
 * @param stockIndex
 * @param stockBuyIndex
 */
function stockBuyAdd(obj, e, stockIndex, stockBuyIndex){
	stopEventClick(e);
	var dateStr = new Date().Format("yyyy-MM-dd");
	var timeStr = new Date().Format("HH:mm");
	//alert("新增买入记录");
	stockIndex = parseInt(stockIndex);
	var edit = stockBuyIndex >= 0;
	var stockBuy = edit? initData[stockIndex].buys[stockBuyIndex]: null;
var editHtml = '<form role="form" id="stockBuyForm" stock-buy-id="'+ (edit ? stockBuy.id: 0) +'" stock-id="'+ initData[stockIndex].id +'">'+
				'<div class="form-group">'+
				  '<label >买入数量(股)<span style="font-size: 10px;"><font color="red">*</font>必填</span></label>'+
				  '<input empty="false" type="text" class="form-control" name="number" placeholder="请输入股票买入数量" value="'+changeNotNullString(stockBuy != null? stockBuy.number: '')+'">'+
				'</div>'+
				'<div class="form-group">'+
				  '<label >买入单价(元)<span style="font-size: 10px;"><font color="red">*</font>必填</span></label>'+
				  '<input empty="false" type="number" class="form-control" name="price" placeholder="请输入股票买入单价" value="'+changeNotNullString(stockBuy != null? stockBuy.price: '')+'">'+
				'</div>'+
				'<div class="form-group">'+
				  '<label for="birth_day">卖出股票时间<span style="font-size: 10px;"><font color="red">*</font>必填</span></label>'+
				  '<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12">'+
				  '<div class="col-lg-6 col-sm-6 col-md-6 col-xs-6">'+
				  	'<input empty="false" placeholder="请先选择买入日期" type="date" class="form-control col-lg-4 col-sm-4 col-md-4 col-xs-4"  name="modify_time_date" value="'+changeNotNullString(stockBuy != null? stockBuy.modifyDate : dateStr)+'">'+
				  '</div>'+
				  '<div class="col-lg-6 col-sm-6 col-md-6 col-xs-6">'+
				  	'<input empty="false" placeholder="请先选择买入时间" type="time" class="form-control col-lg-4 col-sm-4 col-md-4 col-xs-4" name="modify_time" value="'+changeNotNullString(stockBuy != null? stockBuy.modifyTime : timeStr)+'">'+
				  '</div>'+
				  '</div>'+
				'</div>'+
				'</form';
			$(".modal-body-stock-buy-operate").html(editHtml);
    $("#stock-buy-operate").modal("show");
}

/**
 * 编辑买入记录
 * @param obj
 * @param e
 * @param stockIndex
 * @param stockBuyIndex
 */
function stockBuyEdit(obj, e, stockIndex, stockBuyIndex){
	stopEventClick(e);
	//alert("编辑买入记录");
	stockBuyAdd(obj, e, stockIndex, stockBuyIndex);
}

/**
 * 删除买入记录
 * @param obj
 * @param e
 * @param stockId
 * @param stockBuyId
 */
function stockBuyDelete(obj, e, stockId, stockBuyId){
	stopEventClick(e);
	//alert("删除买入记录");
	layer.confirm('您确定要删除该股票购买记录吗？此是不可逆行为，请慎重!', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "DELETE",
			url : "/stock/"+ stockId +"/buy/"+ stockBuyId,
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data != null && data.success){
					layer.msg(data.message);
					window.location.reload();
				}else{
					ajaxError(data);
				}
				console.log(data);
			},
			error : function(data) {
				layer.close(loadi);
				ajaxError(data);
			}
		});
	}, function(){
	});
}


/**
 * 新增股票卖出记录
 * @param obj
 * @param e
 * @param stockId
 * @param stockBuyId
 * @param stockSellId
 */
function stockSellAdd(obj, e, stockIndex, stockBuyIndex, stockSellIndex){
	stopEventClick(e);
	//alert("新增卖出记录");
	var dateStr = new Date().Format("yyyy-MM-dd");
	var timeStr = new Date().Format("HH:mm");
	stockIndex = parseInt(stockIndex);
	stockBuyIndex = parseInt(stockBuyIndex);
	var edit = stockSellIndex >= 0;
	var stockSell = edit? initData[stockIndex].buys[stockBuyIndex].sells[stockSellIndex]: null;
var editHtml = '<form role="form" id="stockSellForm" stock-sell-id="'+ (edit ? stockSell.id: 0) +'" stock-id="'+ initData[stockIndex].id +'" stock-buy-id="'+ initData[stockIndex].buys[stockBuyIndex].id +'">'+
				'<div class="form-group">'+
				  '<label >卖出数量(股)<span style="font-size: 10px;"><font color="red">*</font>必填</span></label>'+
				  '<input empty="false" type="text" class="form-control" name="number" placeholder="请输入股票卖出数量" value="'+changeNotNullString(stockSell != null? stockSell.number: '')+'">'+
				'</div>'+
				'<div class="form-group">'+
				  '<label >卖出单价(元)<span style="font-size: 10px;"><font color="red">*</font>必填</span></label>'+
				  '<input empty="false" type="number" class="form-control" name="price" placeholder="请输入股票卖出单价" value="'+changeNotNullString(stockSell != null? stockSell.price: '')+'">'+
				'</div>'+
				'<div class="form-group">'+
				  '<label for="birth_day">卖出时间<span style="font-size: 10px;"><font color="red">*</font>必填</span></label>'+
				  '<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12">'+
				  '<div class="col-lg-6 col-sm-6 col-md-6 col-xs-6">'+
				  	'<input empty="false" placeholder="请先选择卖出日期" type="date" class="form-control col-lg-4 col-sm-4 col-md-4 col-xs-4"  name="modify_time_date" value="'+changeNotNullString(stockSell != null? stockSell.modifyDate : dateStr)+'">'+
				  '</div>'+
				  '<div class="col-lg-6 col-sm-6 col-md-6 col-xs-6">'+
				  	'<input empty="false" placeholder="请先选择卖出日期" type="time" class="form-control col-lg-4 col-sm-4 col-md-4 col-xs-4" name="modify_time" value="'+changeNotNullString(stockSell != null? stockSell.modifyTime : timeStr)+'">'+
				  '</div>'+
				  '</div>'+
				'</div>'+
				'</form';
			$(".modal-body-stock-sell-operate").html(editHtml);
    $("#stock-sell-operate").modal("show");
}

/**
 * 编辑卖出记录
 * @param obj
 * @param e
 * @param stockIndex
 * @param stockBuyIndex
 * @param stockSellIndex
 */
function stockSellEdit(obj, e, stockIndex, stockBuyIndex, stockSellIndex){
	stopEventClick(e);
	//alert("编辑卖出记录");
	stockSellAdd(obj, e, stockIndex, stockBuyIndex, stockSellIndex)
}

/**
 * 删除卖出记录
 * @param obj
 * @param e
 * @param stockId
 * @param stockBuyId
 * @param stockSellId
 */
function stockSellDelete(obj, e, stockId, stockBuyId, stockSellId){
	stopEventClick(e);
	//alert("删除卖出记录");
	layer.confirm('您确定要删除该股票卖出记录吗？此是不可逆行为，请慎重!', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "DELETE",
			url : "/stock/"+ stockId +"/buy/"+ stockBuyId+"/sell/"+ stockSellId,
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data != null && data.success){
					layer.msg(data.message);
					window.location.reload();
				}else{
					ajaxError(data);
				}
				console.log(data);
			},
			error : function(data) {
				layer.close(loadi);
				ajaxError(data);
			}
		});
	}, function(){
	});
}

function stopEventClick(e){
	e = window.event || e;
    if (e.stopPropagation) {
        e.stopPropagation();      //阻止事件 冒泡传播
    } else {
        e.cancelBubble = true;   //ie兼容
    }
}

/**
 * 股票代码的点击事件
 */
function stockClick(obj){
	$(obj).next("div").toggle(200);
}

/**
 * 展开卖出列表的事件
 * @param obj
 */
function buyLineClick(obj){
	var $sell = $(obj).next("tr");
	if($sell.hasClass("sell-contrainer")){
		//$sell.toggleClass("show");
		$sell.toggle(200);
		//$sell.slideUp(300);
		/*if($sell.attr("hide"))
			$sell.removeClass("hide");
		else
			$sell.addClass("hide");*/
		/*if($sell.hasClass("hide"))
			$sell.removeClass("hide");
		else
			$sell.addClass("hide");*/
	}
}