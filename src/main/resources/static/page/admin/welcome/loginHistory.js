var last_id = 0;
var first_id = 0;
var method = 'firstloading';
//浏览器可视区域页面的高度
var winH = $(window).height(); 
var canLoadData = true;
var isLoad = false;

var loginHostorys;
$(function(){			
	queryPaging();
	
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
	    if(!isLoad && height < 0.20 && canLoadData){
	    	isLoad = true;
	    	method = 'lowloading';
	    	queryPaging();
	    }
	}); 
});

/**
 * 查询获取列表
 * @param uid
 */
function queryPaging(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/ol/logins?" +jsonToGetRequestParams(getQueryPagingParams()),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				if(data.message.length == 0){
					canLoadData = false;
					layer.msg("无更多数据");
					return;
				}
					
				if(method == 'firstloading'){
					//清空原来的数据
					$(".list-row").remove();
					loginHostorys = data.message;
					for(var i = 0; i < loginHostorys.length; i++){
						buildEachRow(loginHostorys[i], i);
						if(i == 0)
							first_id = loginHostorys[i].id;
						if(i == loginHostorys.length -1)
							last_id = loginHostorys[i].id;		
					}
				}else{
					var currentIndex = loginHostorys.length;
					for(var i = 0; i < data.message.length; i++){
						loginHostorys.push(data.message[i]);
						buildEachRow(data.message[i], currentIndex);
						if(i == data.message.length -1)
							last_id = data.message[i].id;
					}
				}
			}else{
				ajaxError(data);
			}
			isLoad = false;
		},
		error : function(data) {
			isLoad = false;
			layer.close(loadi);
			ajaxError(data);
		}
	});
}
/**
 * 获取请求列表
 */
function getQueryPagingParams(){
	var pageSize = 15;
	if(method != 'firstloading')
		pageSize = 10;
	return {pageSize: pageSize, last_id: last_id, first_id: first_id, method: method, t: Math.random()};
}
/**
 * 构建每一行数据
 */
function buildEachRow(loginHistory, index){
	var html = '<div class="row list-row">'+
   					'<div class="col-lg-12">'+
				   		'<div class="row">'+
				   			'<div class="col-lg-5">登录方式：'+ loginHistory.method +'</div>'+
				   			'<div class="col-lg-5">登录时间：'+ loginHistory.create_time +'</div>'+
				   			'<div class="col-lg-2">状态：'+ (loginHistory.status == 1? '正常': '异常') +'</div>'+
				   		'</div>'+
				   		'<div class="row">'+
				   			'<div class="col-lg-6 cut-text" title='+ changeNotNullString(loginHistory.browser) +'">浏览器：'+ changeNotNullString(loginHistory.browser) +'</div>'+
				   			'<div class="col-lg-6">IP地址：'+ changeNotNullString(loginHistory.ip) +'</div>'+
				   		'</div>'+
				   	'</div>'+
				'</div>';
	$(".mainContainer").append(html);
}