var financials;
var OneCategorys; //缓存一级分类对象
var twoCategorys;//缓存二级分类对象
var locations; //位置信息

var emptySearch = true;//是否是空查询(没有任何条件去查询)
var last_id = 0;
var last_addition_time = '';
var method = 'firstloading';
//浏览器可视区域页面的高度
var winH = $(window).height(); 
var canLoadData = true;
var isLoad = false;

var isChart = false;
//基于准备好的dom，初始化echarts实例
//收支
var chartPayments;
//基于准备好的dom，初始化echarts实例
var chartTimes;


var chartCategorys;

window.addEventListener("resize", function () {
	chartPayments.resize();
	chartCategorys.resize();
	chartTimes.resize();
});

$(function(){
	chartPayments = echarts.init(document.getElementById('chart-payments'));
	chartCategorys = echarts.init(document.getElementById('chart-categorys'));
	chartTimes = echarts.init(document.getElementById('chart-times'));
	
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-financial").addClass("active");
	
	$(".senior-condition-btn").click(function(){
		$(".senior-condition").toggle("fast");
	});
	
	$(".chart-or-list").click(function(){
		notifyChartOrListChange();
	});
	
	//获取一级分类
	getOneCategorys();
	
	//获取位置信息
	getLocations();
	queryPaging({t: Math.random()});
	
	$(window).scroll(function (e) {
		e = e || window.event;
	    if (e.wheelDelta) {  //判断浏览器IE，谷歌滑轮事件             
	        if (e.wheelDelta > 0) { //当滑轮向上滚动时
	            return;
	        }
	    } else if (e.detail) {  //Firefox滑轮事件
	        if (e.detail> 0) { //当滑轮向上滚动时
	            return;
	        }
	    }
	    var pageH = $(document.body).height(); //页面总高度 
	    var scrollT = $(window).scrollTop(); //滚动条top 
	    var height = (pageH-winH-scrollT)/winH;
	    if(!isLoad && height < 0.20 && canLoadData && emptySearch && !isChart){
	    	isLoad = true;
	    	method = 'lowloading';
	    	queryPaging();
	    }
	}); 
	
});

/**
 * 通知切换试图或者列表
 */
function notifyChartOrListChange(){
	isChart = !isChart;
	if(isChart){
		$('.chart-or-list').text("切换列表");
		viewChart($('.chart-or-list'));
	}else{
		$('.chart-or-list').text("切换图表");
		viewList();
	}
}
/**
 * 获取一级分类
 */
function getOneCategorys(){
	$.ajax({
		url : "/fn/category/ones",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			if(data.isSuccess && data.message.length > 0){
				oneCategorys = data.message;
				//获取二级分类
				getTwoCategorys();
			}else{
				layer.msg(data.message);
			}
		},
		error : function(data) {
			ajaxError(data);
		}
	});
}

/**
 * 获取二级分类
 */
function getTwoCategorys(){
	$.ajax({
		url : "/fn/category/twos",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			if(data.isSuccess){
				twoCategorys = data.message;
				$('[name="levels"]').append('<option>请选择</option>');
				for(var i = 0; i < twoCategorys.length; i++){
					var oneCategoryValue = "";
					for(var j = 0; j < oneCategorys.length; j++){
						if(twoCategorys[i].one_level_id == oneCategorys[j].id){
							oneCategoryValue = oneCategorys[j].category_value;
							break;
						}
					}
					$('[name="levels"]').append('<option>'+ oneCategoryValue + '>>>' + twoCategorys[i].category_value +'</option>');
				}
			}else{
				layer.msg(data.message);
			}
		},
		error : function(data) {
			ajaxError(data);
		}
	});
}

/**
 * 获取位置信息
 */
function getLocations(){
	$.ajax({
		url : "/fn/locations/all",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			if(data.isSuccess){
				locations = data.message;
			}else{
				layer.msg(data.message);
			}
		},
		error : function(data) {
			ajaxError(data);
		}
	});
}


function querySearch(params){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/fn/query?" + jsonToGetRequestParams(params),
		dataType: 'json', 
		beforeSend:function(){
			//显示列表
			isChart = true;
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				//清空原来的数据
				$(".financial-list-row").remove();
				
				financials = data.message;
				if(data.message.length == 0){
					layer.msg("无更多数据");
					notifyChartOrListChange();
					return;
				}
				
				for(var i = 0; i < data.message.length; i++)
					buildEachRow(data.message[i], i);
				
				notifyChartOrListChange();
			}else{
				layer.msg(data.message);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 获取记账请求列表
 */
function getQueryPagingParaams(){
	var pageSize = 15;
	if(method != 'firstloading')
		pageSize = 10;
	return {pageSize: pageSize, last_id: last_id, last_addition_time: last_addition_time, method: method, t: Math.random()};
}
/**
 * 查询获取当前用户的基本信息
 * @param uid
 */
function queryPaging(params){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/fn/financials?" + jsonToGetRequestParams(getQueryPagingParaams()),
		dataType: 'json', 
		beforeSend:function(){
			//显示列表
			isChart = true;
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				if(data.message.length == 0){
					canLoadData = false;
					layer.msg("无更多数据");
					notifyChartOrListChange();
					return;
				}
					
				if(method == 'firstloading'){
					//清空原来的数据
					$(".financial-list-row").remove();
					financials = data.message;
					for(var i = 0; i < financials.length; i++){
						buildEachRow(financials[i], i);
						if(i == financials.length -1){
							last_id = financials[i].id;	
							last_addition_time = financials[i].addition_time;
						}
					}
				}else{
					var currentIndex = financials.length;
					for(var i = 0; i < data.message.length; i++){
						financials.push(data.message[i]);
						buildEachRow(data.message[i], currentIndex);
						if(i == data.message.length -1){
							last_id = data.message[i].id;
							last_addition_time = data.message[i].addition_time;
						}	
					}
				}
				notifyChartOrListChange();
			}else{
				layer.msg(data.message);
			}
			isLoad = false;
		},
		error : function(data) {
			layer.close(loadi);
			isLoad = false;
			ajaxError(data);
		}
	});
}

/**
 * 构建每一行的记录
 * @param financial
 * @param index
 */
function buildEachRow(financial, index){
	var rowHtml = "";
	
	//有图的情况下
	if(financial.has_img && isNotEmpty(financial.path)){
		rowHtml += '<div class="row financial-list-row">'+
					   		'<div class="col-lg-2">'+
							'<img width="100%" height="160" class="img-rounded hand" alt="" src="'+financial.path+'"  onclick="showSingleImg(this);"/>'+
						'</div>'+
						'<div class="col-lg-9">'+
							'<div class="row" style="font-family: \'微软雅黑\'; font-size: 15px; margin-top: 10px;">'+
								'<div class="col-lg-4">'+ financial.one_level + '>>' + financial.two_level + '</div>'+
								'<div class="col-lg-3">金额：¥ '+ financial.money + '</div>'+
								'<div class="col-lg-5">'+ '记账时间：'+ financial.addition_time +'</div>'+
							'</div>'+
							'<div class="row" style="font-family: \'宋体\'; font-size: 15px;margin-top: 5px;">'+
								'<div class="col-lg-12">'+ changeNotNullString(financial.location) +'</div>'+
							'</div>'+
							'<div class="row" style="font-family: \'微软雅黑\'; font-size: 17px;margin-top: 5px;">'+
								'<div class="col-lg-12">'+ changeNotNullString(financial.financial_desc) + '</div>'+
							'</div>'+
							'<div class="row" style="font-family: \'宋体\'; font-size: 12px;margin-top: 5px; color: gray;">'+
								'<div class="col-lg-12">'+ '创建时间：'+ financial.create_time + '</div>'+
							'</div>'+
							'<div class="row" style="font-family: \'宋体\'; font-size: 12px;margin-top: 5px; color: gray;">'+
								'<div class="col-lg-12">'+ '状态：' + (financial.status == 1? '正常': (financial.status == 0 ? '禁用' : '删除')) +'</div>'+
							'</div>'+
						'</div>'+
						'<div class="col-lg-1">'+
							'<div class="row" style="margin-top: 10px; text-align: right;"><button type="button" class="btn btn-primary" onclick="editFinancial('+ index +');">编辑</button></div>'+
							'<div class="row" style="margin-top: 10px; text-align: right;"><button type="button" class="btn btn-primary" onclick="deleteFinancial('+ index +');">删除</button></div>'+
						'</div>'+
					'</div>';
	}else{
		rowHtml += '<div class="row financial-list-row">'+
				   		'<div class="col-lg-11">'+
						'<div class="row" style="font-family: \'微软雅黑\'; font-size: 15px;">'+
							'<div class="col-lg-4">'+ financial.one_level + '>>' + financial.two_level + '</div>'+
							'<div class="col-lg-3">金额：¥ '+ financial.money + '</div>'+
							'<div class="col-lg-5">'+ '记账时间：'+ financial.addition_time +'</div>'+
						'</div>'+
						'<div class="row" style="font-family: \'宋体\'; font-size: 15px;margin-top: 5px;">'+
							'<div class="col-lg-12">'+ changeNotNullString(financial.location) +'</div>'+
						'</div>'+
						'<div class="row" style="font-family: \'微软雅黑\'; font-size: 15px;margin-top: 5px;">'+
							'<div class="col-lg-12">'+ changeNotNullString(financial.financial_desc) + '</div>'+
						'</div>'+
						'<div class="row" style="font-family: \'宋体\'; font-size: 12px;margin-top: 5px; color: gray;">'+
							'<div class="col-lg-12">'+ '创建时间：'+ financial.create_time + '</div>'+
						'</div>'+
						'<div class="row" style="font-family: \'宋体\'; font-size: 12px;margin-top: 5px; color: gray;">'+
							'<div class="col-lg-12">'+ '状态：' + (financial.status == 1? '正常': (financial.status == 0 ? '禁用' : '删除')) +'</div>'+
						'</div>'+
					'</div>'+
					'<div class="col-lg-1">'+
						'<div class="row" style="margin-top: 10px; text-align: right;"><button type="button" class="btn btn-primary" onclick="editFinancial('+ index +');">编辑</button></div>'+
						'<div class="row" style="margin-top: 10px; text-align: right;"><button type="button" class="btn btn-primary" onclick="deleteFinancial('+ index +');">删除</button></div>'+
					'</div>'+
			'</div>';
	}
	$(".main-container").append(rowHtml);
}

/**
 * 搜索
 */
function search(){
	emptySearch = true;
	var params = {};
	params.t = Math.random();
	var searchKey = $('[name="searchKey"]').val();
	if(isNotEmpty(searchKey)){
		emptySearch = false;
		params.key = searchKey;
	}

	var startTime = $('[name="startTime"]').val();
	if(isNotEmpty(startTime)){
		emptySearch = false;
		params.start = startTime;
	}
		
	var endTime = $('[name="endTime"]').val();
	if(isNotEmpty(endTime)){
		emptySearch = false;
		params.end = endTime;
	}
	
	var levels = $('[name="levels"]').val();
	if(isNotEmpty(levels) && '请选择' != levels){
		emptySearch = false;
		params.levels = levels;
	}
	
	if(!emptySearch)
		querySearch(params);
	else{
		last_id = 0;
		last_addition_time = '';
		method = 'firstloading';
		queryPaging();
	}	
}

/**
 * 编辑记账
 * @param index
 */
function editFinancial(index){
	if(typeof(financials) != 'undefined' && financials.length > 0 && financials.length > index)
		buildEditUserinfo(financials[index]);
}

function deleteFinancial(index){
	if(typeof(financials) != 'undefined' && financials.length > 0 && financials.length > index){
		
		layer.confirm('您要删除该条记录吗？', {
			  btn: ['确定','点错了'] //按钮
		}, function(){
			var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
			$.ajax({
				type : "delete",
				url : "/fn/financial?fid="+ financials[index].id,
				dataType: 'json', 
				beforeSend:function(){
				},
				success : function(data) {
					layer.close(loadi);
					if(data.isSuccess){
						layer.msg("记账信息删除成功,1秒钟后自动刷新");
						setTimeout("window.location.reload();", 1000);
					}else{
						layer.msg(data.message);
					}
				},
				error : function(data) {
					layer.close(loadi);
					ajaxError(data);
				}
			});
		}, function(){
		});
	}
}

/**
 * 添加编辑记账信息的弹出模块
 */
function buildEditUserinfo(financial){
	//添加编辑用户信息的弹出模块html
	var editHtml = '<form role="form" data-id="'+changeObjNotNullString(financial, 'id')+'" class="myForm">'+
						'<div class="form-group">'+
						 	'<label for="sex">记账模型</label>'+
						 	'<select class="form-control" name="model"  onchange="modelChange(this)">';
								if(typeof(financial) != 'undefined' && financial.model == 1){
									editHtml += '<option>支出</option>';
									editHtml += '<option selected>收入</option>';
								}else{
									editHtml += '<option selected>支出</option>';
									editHtml += '<option>收入</option>';
								}
	
				editHtml += '</select>'+
						'</div>'+
						'<div class="form-group">'+
						  '<label for="sex">记账分类</label>'+
						  	'<select class="form-control" name="category">';
							
				editHtml += '</select>'+
					'</div>'+
					'<div class="form-group">'+
					  '<label for="birth_day">金额</label>'+
					  '<input type="number" class="form-control" name="money" value="'+changeObjNotNullString(financial, 'money')+'">'+
					'</div>'+
					'<div class="form-group">'+
					  '<label for="qq">记账时间</label>'+
					  '<input type="datetime-local" class="form-control" name="additionTime" placeholder="请点击选择时间" value="'+formatDateTime(changeObjNotNullString(financial, 'addition_time'))+'">'+
					'</div>'+
					'<div class="form-group">'+
						  '<label for="mobile_phone">位置</label>'+
						  '<select class="form-control" name="location">';
						if(locations != undefined){
							editHtml += '<option>请选择</option>';
							for(var i= 0; i < locations.length;i++){
								if(typeof(financial) != 'undefined' && isNotEmpty(financial.location) && locations[i].location == financial.location)
									editHtml += '<option selected>'+locations[i].location+'</option>';
								else
									editHtml += '<option>'+locations[i].location+'</option>';
							}
						}
				editHtml +='</select>'+
					'</div>'+
					'<div class="form-group">'+
					  '<label for="personal_introduction">描述信息</label>'+
					  '<textarea class="form-control" name="financialDesc" placeholder="请输入详细描述信息">'+changeObjNotNullString(financial, 'financial_desc')+'</textarea>'+
					'</div>'+
					'<div class="form-group">'+
					  '<label for="qq">图片链接</label>'+
					  '<input type="text" class="form-control" name="image" placeholder="图片链接(为空表示没有图片)" value="'+changeObjNotNullString(financial, 'path')+'">'+
					'</div>'+
					'</form';
		$(".modal-body-edit-financialinfo").html(editHtml);
		$("#edit-financial-info").modal("show");
		
		if(typeof(financial) != 'undefined'){
			var editText = financial.one_level + '>>>' + financial.two_level;
			changeModel(financial.model, editText);
		}else{
			changeModel(2, null);
		}
}

function modelChange(obj){
	var model = $(obj).val();
	if(model == "收入")
		changeModel(1);
	else
		changeModel(2);
}

/**
 * 选择类型改变之后
 * @param model
 */
function changeModel(model, editText){
	
	var selectHtml = '';
	if(twoCategorys != undefined){
		//selectHtml += '<option>请选择</option>';
		for(var i = 0; i < twoCategorys.length; i++){
			
			var oneCategoryValue = ""; 
			for(var j = 0; j < oneCategorys.length; j++){
				if(oneCategorys[j].model == model &&  twoCategorys[i].one_level_id == oneCategorys[j].id){
					oneCategoryValue = oneCategorys[j].category_value;
					break;
				}
			}
			if(isNotEmpty(oneCategoryValue)){
				var thisText = oneCategoryValue + '>>>' + twoCategorys[i].category_value;
				if(typeof(editText) != 'undefined' && editText == thisText){
					selectHtml += '<option selected>'+ thisText +'</option>';
				}else{
					selectHtml += '<option>'+ thisText +'</option>';
				}
			}
		}
	}
	
	$('[name="category"]').html(selectHtml);
}

/**
 * 添加记账信息
 */
function addInfo(){
	buildEditUserinfo();
}

/**
 * 点击编辑
 * @param obj
 */
function editInfo(obj){
	var params = {};
	if(buildParams(obj, params)){
		var o = {};
		o.data = JSON.stringify(params);
		o.t = Math.random();
		//o.imei = "201601010156651";
		doAddOrEdit(o);
	}
}

/**
 * 执行编辑、新增操作
 */
function doAddOrEdit(params){
	$.ajax({
		type : "post",
		data : params,
		url : "/fn/financial",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			if(data.isSuccess){
				layer.msg("修改记账信息成功,1秒钟后自动刷新");
				setTimeout("window.location.reload();", 1000);
			}else{
				layer.msg(data.message);
			}
		},
		error : function() {
			layer.msg("网络请求失败");
		}
	});
}

/**
 * 构建记账信息请求参数
 * @param params
 */
function buildParams(obj, params){
	
	var model = $('[name="model"]').val();
	if(isEmpty(model)){
		layer.msg("记账模型不能为空");
		$('[name="model"]').focus();
		return false;
	}else{
		if('收入' == model)
			params.model = 1;
		else
			params.model = 2;
	}
	
	var category = $('[name="category"]').val();
	if(category != '请选择'){
		var levels = category.split(">>>");
		params.one_level = levels[0];
		params.two_level = levels[1];
	}
		
	var money = $('[name="money"]').val();
	if(isEmpty(money)){
		layer.msg("金额不能为空");
		$('[name="money"]').focus();
		return false;
	}else{
		params.money = money;
	}
	
	var additionTime = $('[name="additionTime"]').val();
	if(isEmpty(additionTime)){
		layer.msg("记账时间不能为空");
		$('[name="additionTime"]').focus();
		return false;
	}else{
		params.addition_time = formatStringToDateFormattime(additionTime);
	}
		
	var location = $('[name="location"]').val();
	if(location != '请选择')
		params.location = location;
	
	var financialDesc = $('[name="financialDesc"]').val();
	if(isNotEmpty(financialDesc)){
		params.financial_desc = financialDesc;
	}
	
	var id = $(obj).closest('.modal').find("form").attr("data-id");
	if(isNotEmpty(id)){
		params.id = parseInt(id);
	}
	
	//图片链接
	var image = $('[name="image"]').val();
	if(isNotEmpty(image)){
		params.has_img = true;
		params.path = image;
	}
	
	params.status = 1;
	return true;
}


var bartType;
var endTime;
var startTime;
var additionTimeSubstringStart;
var additionTimeSubstringEnd;
var incomeTotal = 0, spendTotal = 0;//收支总数
var mapCategorys = {};

/**
 * 切换图表
 */
function viewChart(obj){
	$(".financial-list-row").addClass("hide");
	$(".financial-chart").removeClass("hide");
	if(typeof(financials) != 'undefined' && financials.length > 0){
		
		incomeTotal = 0;
		spendTotal = 0;
		mapCategorys = {};
		
		buildChartInitData();
		createPiePaymentsChart();
		createPieCategorysChart();
		createBarChart();
	}
	chartPayments.resize();
	chartCategorys.resize();
	chartTimes.resize();
}

/**
 * 构建初始化数据
 */
function buildChartInitData(){
	var setYears = new Set();
	var setMonths = new Set();
	var setDays = new Set();
	var financial;
	for(var i = 0; i < financials.length; i++){
		financial = financials[i];
		setYears.add(financial.addition_time.substring(0, 4));
		setMonths.add(financial.addition_time.substring(5, 7));
		setDays.add(financial.addition_time.substring(8, 10));
		if(financial.model == 1)
			incomeTotal += financial.money;
		else
			spendTotal += financial.money;
		
		if(financial.two_level in mapCategorys){
			var oldMoney = mapCategorys[financial.two_level];
			mapCategorys[financial.two_level] = oldMoney + financial.money;
		}else{
			mapCategorys[financial.two_level] = financial.money;
		}
	}
	
	incomeTotal = incomeTotal.toFixed(2); //取小数点后2位
	spendTotal = spendTotal.toFixed(2); //取小数点后2位
	
	 if(setYears.size > 1){
         barType = 2;
         endTime = financials[0].addition_time.substring(0, 4) + "年";
         startTime = financials[financials.length -1].addition_time.substring(0, 4) + "年";
         additionTimeSubstringStart = 0;
         additionTimeSubstringEnd = 4;
         return;
     }

     if(setMonths.size > 1){
         barType = 1;
         endTime = financials[0].addition_time.substring(0, 4) + "年" + financials[0].addition_time.substring(5, 7) + "月";
         startTime = financials[financials.length -1].addition_time.substring(0, 4) + "年" + financials[financials.length -1].addition_time.substring(5, 7) + "月";
         additionTimeSubstringStart = 5;
         additionTimeSubstringEnd = 7;
         return;
     }
     if(setDays.size > 1){
         barType = 0;
         endTime = financials[0].addition_time.substring(5, 7) + "月" + financials[0].addition_time.substring(8, 10) + "日";
         startTime = financials[financials.length -1].addition_time.substring(5, 7) + "月" +financials[financials.length -1].addition_time.substring(8, 10) + "日";
         additionTimeSubstringStart = 8;
         additionTimeSubstringEnd = 10;
         return;
     }
     
     barType = 3;
     endTime = financials[0].addition_time.substring(8, 10) + "日" + financials[0].addition_time.substring(11, 13) + "时";
     startTime = financials[financials.length -1].addition_time.substring(8, 10) + "日" + financials[financials.length -1].addition_time.substring(11, 13) + "时";
     additionTimeSubstringStart = 11;
     additionTimeSubstringEnd = 13;
     return;
}

/**
 * 获取柱状图的数据
 */
function createBarChart(){
	var setBarXAxis = new Set();//柱状图的数据
	for(var i = 0; i < financials.length; i++){
		setBarXAxis.add(parseInt(financials[i].addition_time.substring(additionTimeSubstringStart, additionTimeSubstringEnd))); 
	}
	var xAxisArray = new Array();
	setToArray(setBarXAxis, xAxisArray);
	xAxisArray.sort(sortArray(false));
	var xAxisData = new Array();
	for(var i = 0; i < xAxisArray.length; i++){
		var t = '';
		switch (barType) {
		case 0:
			t = '日';
			break;
		case 1:
			t = '月';	
			break;
		case 2:
			t = '年';
			break;
		case 3:
			t = '时';
			break;
		}
		xAxisData.push(xAxisArray[i] +t);
	}
	
	var seriesIncomeData = new Array();
	var seriesSpendData = new Array();
	var seriesIncomeMap = {};
	var seriesSpendMap = {};
	for(var j = 0 ; j < xAxisArray.length; j++){
		var xAxis = xAxisArray[j];
		for(var i = 0; i < financials.length; i++){
			var financial = financials[i];
			if(xAxis == financial.addition_time.substring(additionTimeSubstringStart, additionTimeSubstringEnd)){
				if(financial.model == 1){
					if(xAxis in seriesIncomeMap){
						seriesIncomeMap[xAxis] = financial.money + seriesIncomeMap[xAxis];
					}else{
						seriesIncomeMap[xAxis] = financial.money;
					}
				}else{
					if(xAxis in seriesSpendMap){
						seriesSpendMap[xAxis] = financial.money + seriesSpendMap[xAxis];
					}else{
						seriesSpendMap[xAxis] = financial.money;
					}
				}
			}
		}
	}
	
	for(var j = 0 ; j < xAxisArray.length; j++){
		var xAxis = xAxisArray[j];
		if(xAxis in seriesIncomeMap){
			seriesIncomeData.push(seriesIncomeMap[xAxis].toFixed(2));
		}else{
			seriesIncomeData.push(0);
		}
		if(xAxis in seriesSpendMap){
			seriesSpendData.push(seriesSpendMap[xAxis].toFixed(2));
		}else{
			seriesSpendData.push(0);
		}
	}
	// 指定图表的配置项和数据
    var option = {
    	    tooltip : {
    	        trigger: 'axis',
    	        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
    	            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
    	        }
    	    },
    	    legend: {
    	        data:['收入','支出']
    	    },
    	    grid: {
    	        left: '3%',
    	        right: '4%',
    	        bottom: '3%',
    	        containLabel: true
    	    },
    	    xAxis : [
    	        {
    	            type : 'category',
    	            data : xAxisData
    	        }
    	    ],
    	    yAxis : [
    	        {
    	            type : 'value'
    	        }
    	    ],
    	    series : [
    	        {
    	            name:'收入',
    	            type:'bar',
    	            data: seriesIncomeData
    	        },
    	        {
    	            name:'支出',
    	            type:'bar',
    	            data: seriesSpendData
    	        }
    	    ]
    	};

    // 使用刚指定的配置项和数据显示图表。
    chartTimes.setOption(option);
}

/**
 * 将set转化成array
 * @param set
 * @param array
 */
function setToArray(set, array){
	set.forEach(function(item, sameItem, s) {
		array.push(item);
	});
		
}

function sortArray(desc) {
	return function(a,b){
		if(a < b){
	        return desc ? 1 : -1;
	    }else if(a > b){
	        return desc ? -1 : 1;
	    }else{
	        return 0;
	    }
	}
}
/**
 * 创建收支饼状图
 */
function createPiePaymentsChart(){
	
	var legendData = new Array();
	
	
	var option = {

		    title: {
		        text: '总收入：'+ incomeTotal +'元，总消费：'+ spendTotal +'元',
		        subtext: '从' + startTime +'至' + endTime,
		        top: 20
		    },
		    legend: {
	            data:['总收入', '总支出']
	        },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },

		    /*visualMap: {
		        show: false,
		        min: 80,
		        max: 600,
		        inRange: {
		            colorLightness: [0, 1]
		        }
		    },*/
		    series : [
                   {
                       name: '从' + startTime +'至' + endTime +'收支统计',
                       type:'pie',
                       radius : '55%',
                       center: ['50%', '50%'],
                       data:[
                           {value: incomeTotal, name:'总收入'},
                           {value: spendTotal, name:'总支出'}
                       ].sort(function (a, b) { return a.value - b.value}),
                       roseType: 'angle',
                       label: {
                           normal: {
                               textStyle: {
                                   color: 'rgba(255, 255, 255, 0.3)'
                               }
                           }
                       },
                       labelLine: {
                           normal: {
                               lineStyle: {
                                   color: 'rgba(255, 255, 255, 0.3)'
                               },
                               smooth: 0.2,
                               length: 10,
                               length2: 20
                           }
                       },
                       itemStyle: {
                           normal: {
                               color: '#c23531',
                               shadowBlur: 200,
                               shadowColor: 'rgba(0, 0, 0, 0.5)'
                           }
                       }
                   }
               ]
		    /*[
		        {
		            name:'总统计',
		            type:'pie',
		            radius : '55%',
		            center: ['50%', '50%'],
		            data:[
		                {value:incomeTotal, name:'总收入'},
		                {value:spendTotal, name:'总支出'}
		            ].sort(function (a, b) { return a.value - b.value}),
		            roseType: 'angle'
		        }
		    ]*/
		};
	
	 // 使用刚指定的配置项和数据显示图表。
    chartPayments.setOption(option);
}

/**
 * 创建分类饼状图
 */
function createPieCategorysChart(){
	
	var categoryArray = new Array();
	var legendData = new Array();
	for(var mapKey in mapCategorys){
		legendData.push(mapKey);
		categoryArray.push({name: mapKey, value: mapCategorys[mapKey].toFixed(2)});
	}
	
	//排序，取前面10位最大的
	categoryArray.sort(sortByObjectKey('value', true));
	var limit = categoryArray.length > 10 ? 10 : categoryArray.length;
	var newCategoryArray = new Array();
	for(var i = 0 ; i < limit; i++){
		newCategoryArray.push(categoryArray[i]);
	}
	var option = {
		    title : {
		        text: '二级分类Top10',
		        subtext: '从' + startTime +'至' + endTime,
		        x:'center'
		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    legend: {
		        orient: 'vertical',
		        left: 'left',
		        data: legendData
		    },
		    series : [
		        {
		            name: '二级分类Top10统计',
		            type: 'pie',
		            radius : '55%',
		            center: ['50%', '60%'],
		            data: newCategoryArray,
		            itemStyle: {
		                emphasis: {
		                    shadowBlur: 10,
		                    shadowOffsetX: 0,
		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
		                }
		            }
		        }
		    ]
		};
	 // 使用刚指定的配置项和数据显示图表。
    chartCategorys.setOption(option);
}

/**
 * 切换列表
 */
function viewList(){
	$(".financial-chart").addClass("hide");
	$(".financial-list-row").removeClass("hide");
}