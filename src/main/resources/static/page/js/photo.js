var last_id = 0;
var first_id = 0;
var method = 'firstloading';

var photos;

var column1Height = 0;
var column2Height = 0;
var column3Height = 0;

var canLoadData = true;

//浏览器可视区域页面的高度
var winH = $(window).height(); 
var isLoad = false;
$(function(){
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
	    if(!isLoad && height < 0.20){
	    	isLoad = true;
	    	method = 'lowloading';
	    	getWebPhotos();
	    }
	}); 
	
	getWebPhotos();
});

/**
 * 获取网络图片
 */
function getWebPhotos(){
	if(!canLoadData){
		layer.msg("无更多数据");
		return;
	}
	isLoad = true;
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/gl/photos?" + jsonToGetRequestParams(getPhotosRequestParams()),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			
			if(data != null && data.isSuccess){
				
				if(data.message.length == 0){
					canLoadData = false;
					layer.msg("无更多数据");
					return;
				}
				
				if(method == 'firstloading'){
					photos = data.message;
					$("#column-01").empty();
					$("#column-02").empty();
					$("#column-03").empty();
					column1Height = 0;
					column2Height = 0;
					column3Height = 0;
					
					for(var i = 0; i < photos.length; i++){
						findColumnToAdd(photos[i], i);
						if(i == 0)
							first_id = photos[i].id;
						if(i == photos.length -1)
							last_id = photos[i].id;
					}
				}else{
					var currentIndex = photos.length;
					for(var i = 0; i < data.message.length; i++){
						photos.push(data.message[i]);
						findColumnToAdd(data.message[i], currentIndex +i);
						if(i == data.message.length -1)
							last_id = data.message[i].id;
					}
				}
				resetSideHeight();
				asynchronousPhotos();
			}else{
				ajaxError(data);
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
 * 获取请求参数
 */
function getPhotosRequestParams(){
	var pageSize = 15;
	if(method != 'firstloading')
		pageSize = 10;
	return {pageSize: pageSize, last_id: last_id, first_id: first_id, method: method, t: Math.random()};
	//return "?page_size="+ pageSize +"&last_id="+ last_id +"&first_id=" + first_id + "&method="+ method+"&t="+Math.random();
}

//展示图片的链接
function showImg(index){
	var photo = photos[index];
	var json = {
			  "title": "相册标题", //相册标题
			  "id": 0, //相册id
			  "start": index //初始显示的图片序号，默认0
			};
	var datas = new Array();
	for(var i = 0; i < photos.length; i++){
		var each = {};
		each.alt = photos[i].gallery_desc;
		each.pid = photos[i].id;//图片id
		var path = photos[i].path;
		if(path.indexOf("7xnv8i.com1.z0.glb.clouddn.com") < 0){
			path = $(".index_"+i).attr("src");
		}
		each.src = path;//原图地址
		each.alt = path;//缩略图地址
		datas.push(each);
	}
	
	json.data = datas;
	
	layer.photos({
	    photos: json
	    ,shift: 1 //0-6的选择，指定弹出图片动画类型，默认随机
	  });
}

var photosAjaxArray = new Array();
function asynchronousPhotos(){
	$(".out-link").each(function(index){
		var object = $(this);
		var ajax = $.ajax({
			url : "/tl/networdImage?url="+object.attr("temp-src"),
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				if(data.isSuccess){
					var dd = $(".out-link").eq(index);
					object.attr("src", data.message);
					object.removeClass("out-link");
				}
					
				else
					ajaxError(data);
			},
			error : function(data) {
				ajaxError(data);
			}
		});
		photosAjaxArray.push(ajax);
		//setTimeout()
	});
	
}

//页面关闭和刷新执行方法
window.onbeforeunload = onbeforeunload_handler;
window.onunload = onunload_handler;
function onbeforeunload_handler() {//页面关闭执行
	if(photosAjaxArray.length > 0){
		for(var i = 0; i < photosAjaxArray.length; i++){
			photosAjaxArray[i].abort(); //取消ajax请求
		}
	}
}
function onunload_handler() {//页面关闭执行
	if(photosAjaxArray.length > 0){
		for(var i = 0; i < photosAjaxArray.length; i++){
			photosAjaxArray[i].abort(); //取消ajax请求
		}
	}
}
/**
 * 找宽度最小的列去添加图片
 * @param photo
 * @param index
 */
function findColumnToAdd(photo, index){
	var min = Math.min(column1Height, column2Height, column3Height);
	
	var width = $("#column-01").width();
	var height = photo.height;
	
	//限制图片最大为宽度的1.5倍
	if(width * 1.5 < height)
		height = width * 1.5;
	var img;

	//判断是否是内部链接
	if(photo.path.indexOf("7xnv8i.com1.z0.glb.clouddn.com") >= 0){
		img = '<img width="100%" title="'+ (isNotEmpty(photo.desc) ? isNotEmpty(photo.desc) : '') +'" title" height="'+height+'" style="margin-top: 10px;" class="img-rounded index_'+index+'" alt="" src="'+ photo.path+'" onclick="showImg('+index+')" onmouseover="imgHandOver(this, '+ index +');" onmouseout="imgHandOut(this, '+ index +');">';

	}else{
		img = '<img width="100%" title="'+ (isNotEmpty(photo.desc) ? isNotEmpty(photo.desc) : '') +'" height="'+height+'" style="margin-top: 10px;" class="img-rounded out-link index_'+index+'" alt="" temp-src="'+ photo.path+'" onclick="showImg('+index+')" onmouseover="imgHandOver(this, '+ index +');" onmouseout="imgHandOut(this, '+ index +');">';
	}
	if(min == column1Height){
		$("#column-01").append(img);
		column1Height += height;
		return;
	}
	
	if(min == column2Height){
		$("#column-02").append(img);
		column2Height += height;
		return;
	}
	
	if(min == column3Height){
		$("#column-03").append(img);
		column3Height += height;
		return;
	}
}

/**
 * 图片的鼠标悬停事件
 * @param obj
 * @param index
 */
function imgHandOver(obj, index){
	var photo = photos[index];
	if(isNotEmpty(photo['gallery_desc'])){
		var top = $(obj).offset().top;
		var left = $(obj).offset().left;
		$("body").append('<span id="desc_'+ index +'">'+ photo['gallery_desc'] +'</span>');
		$("#desc_"+index).css("position", "absolute");
		$("#desc_"+index).css("top", top);
		$("#desc_"+index).css("left", left);
		$("#desc_"+index).css("color", "#fff");
		$("#desc_"+index).css("background-color", "#666");
	}
	console.log("over");
}

/**
 * 图片的鼠标离开事件
 * @param obj
 * @param index
 */
function imgHandOut(obj, index){
	$("#desc_"+index).remove();
	console.log("out");
}

/**
 * 添加图片链接
 * @param params
 */
function addLink(params){
	var loadi = layer.load('努力加载中…');
	$.ajax({
		type : "post",
		data: params,
		url : "gl/photo",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
				layer.msg(data.message);
				layer.close(loadi);
				if(data.isSuccess){
					$(".gallery-link").val("");
					$(".gallery-desc").val("");
					$(".gallery-width").val("");
					$(".gallery-height").val("");
					$(".gallery-length").val("");
					$("#add-grallery").modal("hide");
					//重新刷新一下整个列表
					canLoadData = true;
					method = 'firstloading';
					getWebPhotos();
				}
		},
		error : function() {
			layer.close(loadi);
			layer.msg("网络请求失败");
		}
	});
	
}
