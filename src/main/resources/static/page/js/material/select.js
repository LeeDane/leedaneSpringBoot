layui.use(['layer', 'laypage'], function(){
	layer = layui.layer;
	laypage = layui.laypage;
	pageSize = 10;
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
	$tabsContainer = $("#material-tabs");
	
	if(isNotEmpty(tabName)){
		$tabsContainer.find("li").each(function(index){
			if($(this).attr("data-value") == tabName){
				type = tabName;
				$(this).addClass("active");
			}else{
				$(this).removeClass("active");
			}
		});
	}else{
		$tabsContainer.find("li").eq(0).addClass("active");
		 type = $tabsContainer.find("li").eq(0).attr("data-value")
	}
	getMaterials();
	
	//获取通知类型
	$tabsContainer.find("li").on("click", function(index){
		$tabsContainer.find("li").removeClass("active");
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
				$("#select-list").append('<div class="has-select-file" id="select-file-'+ material.id +'" class="cut-text"><span class="glyphicon glyphicon-ok" style="color: green; margin-right: 5px;"></span><a href="'+ material.qiniu_path +'">'+ getFileName(material.qiniu_path)+'</a></div>');
			}else
				$("#select-list").append('<div class="has-select-file" id="select-file-'+ material.id +'" class="cut-text"><span class="glyphicon glyphicon-ok" style="color: green; margin-right: 5px;"></span><a href="'+ material.qiniu_path +'">'+ getFileName(material.qiniu_path)+'</a></div>');
		}
		resetContrainHeight();
	});  
});
var type;
var materials;
var $image;
var $materialListContainer;
var $tabsContainer;
var selectObjs = {}; //用map集合包装， key为id， 值为链接

/**
 * 选择后重置列表的距离
 */
function resetContrainHeight(){
	var height = $("#select-list").height();
	$tabsContainer.css("margin-top", (height + 20) +"px");
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
					if(currentIndex == 0){
						$materialListContainer.append('<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">暂时没有素材，请前往素材管理中心添加！</div>');
					}else{
						$materialListContainer.append('<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">已经没有更多的素材，请重新选择！</div>');
					}
					return;
				}
				
				//判断是否已经被选择
				var selects = $("#select-list").find(".has-select-file");
				var matchSelect = selects.length > 0;
				
				for(var i = 0; i < materials.length; i++){
					if(type == "图像"){
						$materialListContainer.append(buildEachMaterialImgRow(i, materials[i]));
					}else if(type == "音频"){
						$materialListContainer.append(buildEachMaterialVideoOrAudioRow(i, materials[i]));
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
				
				//执行一个laypage实例
				 laypage.render({
				    elem: 'list-pager' //注意，这里的 test1 是 ID，不用加 # 号
				    ,layout: ['prev', 'page', 'next', 'count', 'skip']
				    ,count: data.total //数据总数，从服务端得到
				    ,limit: pageSize
				    , curr: currentIndex + 1
				    ,jump: function(obj, first){
					    //obj包含了当前分页的所有参数，比如：
					    console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
					    console.log(obj.limit); //得到每页显示的条数
					    if(!first){
					    	currentIndex = obj.curr -1;
					    	getMaterials();
					    }
					  }
				 });
				//pageDivUtil(data.total);
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
					        	'<div class="cut-text">时间：'+ changeNotNullString(material.create_time) + '&nbsp;&nbsp;&nbsp;&nbsp; 文件大小：'+ formatFileSize(material.length)+ '</div>';
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
 * 构建每一行音频html
 * @param index
 * @param material
 */
function buildEachMaterialVideoOrAudioRow(index, material){
	var path = material.path;
	var html = '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12" id="material-row-'+ index +'">'+
					'<div class="thumbnail">'+
					      '<div class="cut-text" style="margin-left: 10px; margin-right: 10px;">文件路径：<a href="'+ material.qiniu_path +'" title="'+ material.qiniu_path +'">'+ getFileName(material.qiniu_path) +'</a></div>'+
					      '<div class="caption">'+
					        	'<div class="cut-text">时间：'+ changeNotNullString(material.create_time) + '&nbsp;&nbsp;&nbsp;&nbsp; 文件大小：'+ formatFileSize(material.length) +'</div>';
					
					if(isVideo(path)){
						html += '<div class="row"><div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">';
						html += getVideoHtml(material.qiniu_path);
						html += '</div></div>';
					}
					
					if(isAudio(path)){
						html += '<div class="row"><div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">';
						html += getAudioHtml(material.qiniu_path);
						html += '</div></div>';
					}
					
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
 * 父窗口调用获取选择的图片
 */
function getSelectMaterialData(){
	return selectObjs;
}