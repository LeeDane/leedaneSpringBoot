var last_id = 0;
var first_id = 0;
var method = 'firstloading';
//浏览器可视区域页面的高度
var winH = $(window).height(); 
var canLoadData = true;
var isLoad = false;

var appVersions;
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
		url : "/av/versions?" + jsonToGetRequestParams(getQueryPagingParams()),
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
					appVersions = data.message;
					for(var i = 0; i < appVersions.length; i++){
						buildEachRow(appVersions[i], i);
						if(i == 0)
							first_id = appVersions[i].id;
						if(i == appVersions.length -1)
							last_id = appVersions[i].id;		
					}
				}else{
					var currentIndex = appVersions.length;
					for(var i = 0; i < data.message.length; i++){
						appVersions.push(data.message[i]);
						buildEachRow(data.message[i], currentIndex);
						if(i == data.message.length -1)
							last_id = data.message[i].id;
					}
				}
			}else{
				layer.msg(data.message);
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
function buildEachRow(appVersion, index){
	var html = '<div class="row list-row">'+
			   		'<div class="col-lg-12">'+
					 	'<div class="row">'+
					 		'<div class="col-lg-12">'+
						 		'<div class="col-lg-4">版本号：'+ appVersion.file_version +'</div>'+
				  			 	'<div class="col-lg-4">文件大小：'+ (appVersion.lenght/1024/1024).toFixed(2) +'M</div>'+
				  			 	'<div class="col-lg-4">发布时间：'+ appVersion.create_time +'</div>'+
						 	'</div>'+
						 '</div>'+
						'<div class="row">'+
				  			 '<div class="col-lg-12">'+ appVersion.file_desc +'</div>'+
						 '</div>'+
						 '<div class="row">'+
				  			 '<div class="col-lg-12">'+
				  			 	'<a type="button" class="btn btn-primary" href="'+ appVersion.path+'">下载</a>'+
				  			 '</div>'+
					 	'</div>'+
				'</div>'+
		'</div>';
	
	$(".mainContainer").append(html);
}