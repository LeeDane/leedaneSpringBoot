var type;
var pageSize = 10;
var currentIndex = 0;
var materials;
var totalPage = 0;
var $image;
var $materialListContainer;
var selectObjs = {}; //用map集合包装， key为id， 值为链接
$(function(){
	$materialListContainer = $("#material-row-container");
	var containerWidth = $materialListContainer.width();
	//动态计算模态框的宽度，适配手机
	if($(window).width() > (1100 + 30)){//浏览器当前窗口可视区域宽度
		var materialListImgWidth = Math.floor((containerWidth / 3));
		materialListImgHight = materialListImgWidth - 30;
	}else{
		//为什么这里减去30是因为弹出的模态框离窗口有边距
		materialListImgHight = Math.floor((modalWidth / 3)) - 30;
		console.log("高度是："+ materialListImgHight);
	}

	if(isNotEmpty(tabName)){
		$("#material-tabs").find("li").each(function(index){
			if($(this).attr("data-value") == tabName){
				type = tabName;
				$(this).addClass("active");
			}else{
				$(this).removeClass("active");
			}
		});
	}else{
		 $("#material-tabs").find("li").eq(0).addClass("active");
		 type = $("#material-tabs").find("li").eq(0).attr("data-value")
	}
	getMaterials();
	
	//获取通知类型
	$("#material-tabs").find("li").on("click", function(index){
		$("#material-tabs").find("li").removeClass("active");
		$(this).addClass("active");
		type = $(this).attr("data-value");
		currentIndex = 0;
		//清空已经选择的数据
		selectObjs = {};
		$("#select-list").find(".has-select-file").remove();
		getMaterials();
	});
	
	$(document).on("click", "div.thumbnail", function(event){
		event.stopPropagation();//阻止冒泡 
		var $obj = $(this);
		var material = $obj.parent().data("material");
		
		var hasSelect = false;
		var size = 0;
		for(selectObj in selectObjs){
			size ++;
		}
		
		if($obj.hasClass("click-select")){
			$obj.removeClass("click-select");
			$obj.find(".thumbnail-top").remove();
			 delete selectObjs[material.id]; 
			 $("#select-file-"+ material.id).remove();
		}else{
			//添加
			if(size > (select - 1)){
				layer.msg("抱歉，最多只能选择"+ select +"条数据！");
				return;
			}
			selectObjs[material.id] = material.qiniu_path;
			$obj.addClass("click-select");
			$obj.append('<div class="thumbnail-top"></div>');
			if(type == "图像"){
				$("#select-list").append('<div class="has-select-file" id="select-file-'+ material.id +'" class="cut-text"><span class="badge" style="background-color: red; margin-right: 3px;">x</span><a href="'+ material.qiniu_path +'">'+ getFileName(material.qiniu_path)+'</a></div>');
			}else
				$("#select-list").append('<div class="has-select-file" id="select-file-'+ material.id +'" class="cut-text"><span class="badge" style="background-color: red; margin-right: 3px;">x</span><a href="'+ material.qiniu_path +'">'+ getFileName(material.qiniu_path)+'</a></div>');
		}
		resetContrainHeight();
	});
});

/**
 * 选择后重置列表的距离
 */
function resetContrainHeight(){
	var height = $("#select-list").height();
	$("#material-tabs").css("margin-top", (height + 20) +"px");
}

/**
 * 获取素材列表
 * @param bid
 */
function getMaterials(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	var params = {page_size: pageSize, current: currentIndex, total: totalPage, type: type, t: Math.random()};
	$.ajax({
		url : "/mt/materials?"+ jsonToGetRequestParams(params),
		dataType: 'json',
		beforeSend:function(){
			$materialListContainer.empty();
			$(".pagination").empty();
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				materials = data.message;
				if(materials.length == 0){
					$materialListContainer.append('<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">空空的，还没有数据</div>');
					return;
				}
				
				//判断是否已经被选择
				var selects = $("#select-list").find(".has-select-file");
				var matchSelect = selects.length > 0;
				
				for(var i = 0; i < materials.length; i++){
					if(type == "图像"){
						$materialListContainer.append(buildEachMaterialImgRow(i, materials[i]));
					}else{
						$materialListContainer.append(buildEachMaterialFileRow(i, materials[i]));
					}
					$("#material-row-"+ i).data("material", materials[i]);
				}
				resetContrainHeight();
				for(var i = 0; i < materials.length; i++){
					if(matchSelect){
						for(var sl in selectObjs){
							//匹配到了
							if(parseInt(sl) == materials[i].id){
								$("#material-row-"+ i).find("div.thumbnail").addClass("click-select");
								$("#material-row-"+ i).find("div.thumbnail").append('<div class="thumbnail-top"></div>');
								break;
							}
						}
					}
				}
				pageDivUtil(data.total);
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
 * 构建每一行图像html
 * @param index
 * @param material
 * @returns {String}
 */
function buildEachMaterialImgRow(index, material){
		var html = '<div class="col-lg-4 col-md-4 col-sm-6 col-xs-6" id="material-row-'+ index +'">'+
						'<div class="thumbnail">'+
						      '<img width="100%" style="height: '+materialListImgHight+'px;" src="'+ material.qiniu_path +'" alt="..." />'+
						      '<div class="caption">'+
						        	'<div class="cut-text">'+ changeNotNullString(material.create_time) +'</div>';
			        	if(isEmpty(material.material_desc)){
			        		html += '<h5 class="cut-text">暂无描述</h5>';   	
			        	}else{
			        		html += '<h5 class="cut-text" title="'+ material.material_desc  +'">'+ material.material_desc +'</h5>';
			        	}
						
			        		html += '</div>'+
					    '</div>'+
				   '</div>';
	
	return html;
}

/**
 * 获取文件名称
 */
function getFileName(filePath){
	//获取文件名，不带后缀
	var file_name=filePath.replace(/(.*\/)*([^.]+).*/ig,"$2");
	//获取文件后缀
	var FileExt=filePath.replace(/.+\./,"");
	return file_name + '.' +FileExt;
}

/**
 * 构建每一行文件html
 * @param index
 * @param material
 */
function buildEachMaterialFileRow(index, material){
	var html = '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12" id="material-row-'+ index +'">'+
					'<div class="thumbnail">'+
					      '<div class="cut-text" style="margin-left: 10px; margin-right: 10px;">文件路径：<a href="'+ material.qiniu_path +'" title="'+ material.qiniu_path +'">'+ getFileName(material.qiniu_path) +'</a></div>'+
					      '<div class="caption">'+
					        	'<div class="cut-text">时间：'+ changeNotNullString(material.create_time) + '&nbsp;&nbsp;&nbsp;&nbsp; 文件大小：'+ parseFloat(material.length/ 1024/ 1024).toFixed(2) +'M</div>';
					if(isEmpty(material.material_desc)){
						html += '<h5 class="cut-text">暂无描述</h5>';   	
					}else{
						html += '<h5 class="cut-text" title="'+ material.material_desc  +'">'+ material.material_desc +'</h5>';
					}
					
						html += '</div>'+
				    '</div>'+
				'</div>';

	return html;
}

/**
 * 生成分页div
 * @param total
 */
function pageDivUtil(total){
	var html = '<li>'+
					'<a href="javascript:void(0);" onclick="pre();" aria-label="Previous">'+
						'<span aria-hidden="true">&laquo;</span>'+
					'</a>'+
				'</li>';
	totalPage = parseInt(Math.ceil(total / pageSize));
	var start = 0;
	var end = totalPage > start + 10 ? start + 10: totalPage;
	
	var selectHtml = '<li><select class="form-control" onchange="optionChange()">';
	for(var i = 0; i < totalPage; i++){
		if(currentIndex == i)
			selectHtml += '<option name="pageIndex" selected="selected" value="'+ i +'">'+ (i + 1) +'</option>';
		else
			selectHtml += '<option name="pageIndex" value="'+ i +'">'+ (i + 1) +'</option>';
	}
	
	for(var i = start; i < end; i++){
		if(currentIndex == i)
			html += '<li class="active"><a href="javascript:void(0);" onclick="goIndex('+ i +');">'+ (i+1) +'</a></li>';
		else
			html += '<li><a href="javascript:void(0);" onclick="goIndex('+ i +');">'+ (i+1) +'</a></li>';
	}
	html += '<li>'+
				'<a href="javascript:void(0);" onclick="next();" aria-label="Next">'+
					'<span aria-hidden="true">&raquo;</span>'+
				'</a>'+
			'</li>';
	
	selectHtml += '</select></li>';
	
	html += selectHtml;
	$(".pagination").html(html);
}

/**
 * 选择改变的监听
 */
function optionChange(){
	var objS = document.getElementsByTagName("select")[0];
    var index = objS.options[objS.selectedIndex].value;
    currentIndex = index;
    getMaterials();
}

/**
 * 点击向左的按钮
 */
function goIndex(index){
	currentIndex = index;
	getMaterials();
}

/**
 * 点击向左的按钮
 */
function pre(){
	currentIndex --;
	if(currentIndex < 0)
		currentIndex = 0;
	getMaterials();
}


/**
 * 点击向右的按钮
 */
function next(){
	currentIndex ++;
	if(currentIndex > totalPage)
		currentIndex = totalPage;
	getMaterials();
}

/**
 * 父窗口调用获取选择的图片
 */
function getSelectData(){
	return selectObjs;
}