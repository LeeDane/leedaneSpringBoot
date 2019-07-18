var isFirst = false;
var $tree;
var categoryId = -1;
layui.use(['layer'], function(){
	layer = layui.layer;
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

    //描述信息的点击
	$("#fh5co-main").find(".fh5co-desc").on("click", function(index){
	layer.msg("删除成功!",{ icon: 1, time: 1000 });
		layer.confirm('是否修改该图片的描述信息!',
		    {
                btn: ['确定', '取消']
            }, function (index, layero) {
                    layer.msg("删除成功!",{ icon: 1, time: 1000 });
             }
         );
	});

	$tree = $('#tree');
	getChildNodes(0, 131);//获取分类列表
	getWebPhotos();
});
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

/**
 * 获取直接一级的节点
 * @param nodeId
 * @param pid
 */
function getChildNodes(nodeId, pid){
	 var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/cg/category/"+ pid +"/children",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				var treeData = data.message;
				if(!isFirst){
				    var all = {id: -1, text: "全部"};
				    treeData.push(all);
					/**
					 * 展示数据并同时添加展开事件的监听(事件的监听必须在绑定数据之后)
					 */
					$tree.treeview({data: treeData, enableLinks: false, onNodeSelected: function(event, data) {
						
	
					}, onNodeExpanded: function(event, data) {
						getChildNodes(data.nodeId, data.id);
	
					}});
					isFirst = true;
				}else{
					$tree.treeview("addNodes", [nodeId, { node: treeData}]); 
				}

				if($(window).width() < 900)
                        setTimeout(function(){
                            $("#fh5co-main").css("margin-top", $("#tree").height() + "px");
                        }, 500);

			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 获取网络图片
 */
function getWebPhotos(){
	/*if(!canLoadData){
		layer.msg("无更多数据");
		return;
	}*/
	isLoad = true;
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/gl/photos?" + jsonToGetRequestParams(getPhotosRequestParams()),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
/*
			for(var i = 0; i < 10; i++){
			    var html = '<div class="item">'+
                                  '<div class="animate-box bounceIn animated">'+
                                      '<a href="http://pic.onlyloveu.top/test_20190627190608mmexport1561555416483.jpg?imageslim" class="image-popup fh5co-board-img" title="Lorem ipsum dolor sit amet, consectetur adipisicing elit. Explicabo, eos?"><img src="http://pic.onlyloveu.top/test_20190627190608mmexport1561555416483.jpg?imageslim" alt="Free HTML5 Bootstrap template"></a>'+
                                  '</div>'+
                                  '<div class="fh5co-desc">Lorem ipsum dolor sit amet, consectetur adipisicing elit. Explicabo, eos?</div>'+
                              '</div>';

                  //找到底的容器元素
                  var $lowContrainer = findLowContrainer();
                  $lowContrainer.append(html);
			}*/

			if(method == 'firstloading'){
                $(".column-item1").empty();
                $(".column-item2").empty();
                $(".column-item3").empty();
                $(".column-item4").empty();
            }
			if(data != null && data.isSuccess){
                $("#fh5co-board").find(".no-result").remove();
				if(data.message.length == 0){
					canLoadData = false;
//					layer.msg("该分组下还没有找到图片", {time: 2000, icon: 5});
                    if(method == 'firstloading'){
					    $("#fh5co-board").append("<div class='row no-result'>该分组下还没有找到图片。</div>");
					 }else{
					    $("#fh5co-board").append("<div class='row no-result'>已经没有更多图片。</div>");
					 }
					return;
				}

				if(method == 'firstloading'){
					photos = data.message;
					for(var i = 0; i < photos.length; i++){
						 var html = '<div class="item">'+
                                          '<div class="animate-box">'+
                                              '<a href="'+ photos[i].path +'" class="image-popup fh5co-board-img" title="Lorem ipsum dolor sit amet, consectetur adipisicing elit. Explicabo, eos?">'+
                                              '<img src="'+ photos[i].path +'" alt="'+ photos[i].gallery_desc +'"></a>'+
                                          '</div>'+
                                          '<div class="fh5co-desc" onclick="descEdit('+ data.message[i].id + ',\'' + data.message[i].gallery_desc +'\');">'+ photos[i].gallery_desc +'</div>'+
                                      '</div>';

                         //找到底的容器元素
                         var $lowContrainer = findLowContrainer();
                         $lowContrainer.append(html);

						if(i == 0)
							first_id = photos[i].id;
						if(i == photos.length -1)
							last_id = photos[i].id;
					}
				}else{
					var currentIndex = photos.length;
					for(var i = 0; i < data.message.length; i++){
//						photos.push(data.message[i]);

						var html = '<div class="item">'+
                                          '<div class="animate-box">'+
                                              '<a href="'+ data.message[i].path +'" class="image-popup fh5co-board-img" title="Lorem ipsum dolor sit amet, consectetur adipisicing elit. Explicabo, eos?">'+
                                              '<img src="'+ data.message[i].path +'" alt="'+ data.message[i].gallery_desc +'"></a>'+
                                          '</div>'+
                                          '<div class="fh5co-desc" onclick="descEdit('+ data.message[i].id + ',\'' + data.message[i].gallery_desc +'\');">'+ data.message[i].gallery_desc +'</div>'+
                                      '</div>';

                         //找到底的容器元素
                         var $lowContrainer = findLowContrainer();
                         $lowContrainer.append(html);

//						findColumnToAdd(data.message[i], currentIndex +i);
						if(i == data.message.length -1)
							last_id = data.message[i].id;
					}
				}
				/*resetSideHeight();
				asynchronousPhotos();*/
			}else{
				ajaxError(data);
			}
			isLoad = false;
            setTimeout(function(){
                 magnifPopup();
                 offCanvass();
                 mobileMenuOutsideClick();
                 animateBoxWayPoint();
             }, 500);

		},
		error : function(data) {
			layer.close(loadi);
			isLoad = false;
			ajaxError(data);
		}
	});
}

/*
**找出最大的容器
**
*/
function findLowContrainer(){
    var height1 = $(".column-item1").height();
    var height2 = $(".column-item2").height();
    var height3 = $(".column-item3").height();
    var height4 = $(".column-item4").height();

    var height = height1;
    if(height >= height2){
        height = height2;
    }

    if(height >= height3){
        height = height3;
    }

    if(height >= height4){
        height = height4;
    }

    if(height == height1)
        return  $(".column-item1");
    if(height == height2)
        return  $(".column-item2");
    if(height == height3)
        return  $(".column-item3");
    if(height == height4)
        return  $(".column-item4");
}

/**
 * 获取请求参数
 */
function getPhotosRequestParams(){
	var pageSize = 150;
	if(method != 'firstloading')
		pageSize = 10;
	return {pageSize: pageSize, last_id: last_id, first_id: first_id, method: method, category: categoryId, t: Math.random()};
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
		if(path.indexOf("7xnv8i.com1.z0.glb.clouddn.com") < 0 || path.indexOf("pic.onlyloveu.top") < 0){
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
	if(photo.path.indexOf("7xnv8i.com1.z0.glb.clouddn.com") >= 0 || photo.path.indexOf("pic.onlyloveu.top") >= 0){
		img = '<img width="100%" title="'+ (isNotEmpty(photo.desc) ? isNotEmpty(photo.desc) : '') +'" height="'+height+'" style="margin-top: 10px;" class="img-show img-rounded index_'+index+'" alt="" src="'+ photo.path+'" onclick="showImg('+index+')" onmouseover="imgHandOver(this, '+ index +');" onmouseout="imgHandOut(this, '+ index +');">';
	}else{
		img = '<img width="100%" title="'+ (isNotEmpty(photo.desc) ? isNotEmpty(photo.desc) : '') +'" height="'+height+'" style="margin-top: 10px;" class="img-show out-link index_'+index+'" alt="" temp-src="'+ photo.path+'" onclick="showImg('+index+')" onmouseover="imgHandOver(this, '+ index +');" onmouseout="imgHandOut(this, '+ index +');">';
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
		$("body").append('<span id="desc_'+ index +'" >'+ photo['gallery_desc'] +'</span>');
		var $desc = $("#desc_"+index);
		$desc.css("position", "absolute");
		$desc.css("top", top);
		$desc.css("left", left);
		$desc.css("color", "#fff");
		$desc.css("background-color", "#666");
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

/**
 * 获取素材的回调函数
 * @param links
 */
function afterSelectMaterial(links){
	$(".gallery-link").val(links);
	$(".gallery-desc").val("从素材选择");
}

/**
 * 项的点击
 */
function customItemClick(node){
//	console.log(node);
       //如果是展开或者收起操作就不加载数据了
    if(node.nodes == null || node.nodes.length == 0){
        categoryId = node.id;
        last_id = 0;
        first_id = 0;
        method = 'firstloading';
        getWebPhotos();
    }


	if($(window).width() < 900)
        setTimeout(function(){
            $("#fh5co-main").css("margin-top", $("#tree").height() + "px");
        }, 500);
}

/**
 * 根tree的选择器类
 */
function getTreeRootId(){
	return "tree";
}

/**
*   描述信息的点击编辑
*/
function descEdit(id, desc){
    layer.prompt({title: '是否修改该图片的描述信息!', formType: 2}, function(pass, index){
          layer.close(index);
           if(isNotEmpty(pass)){
                var loadi = layer.load('更新描述中…'); //需关闭加载层时，执行layer.close(loadi)即可
                	$.ajax({
                		url : "/gl/photos?" + jsonToGetRequestParams(getPhotosRequestParams()),
                		dataType: 'json',
                		beforeSend:function(){
                		},
                		success : function(data) {
                			layer.close(loadi);

                		},
                		error : function(data) {
                			layer.close(loadi);
                			isLoad = false;
                			ajaxError(data);
                		}
                	});
           }
    });
}