var pageSize = 8;
var currentIndex = 0;
var materials;
var totalPage = 0;
var fileIndex = 0; //未上传文件的索引
var isUploading = false;//标记当前是否在上传
$(function(){
	
	//动态计算模态框的宽度，适配手机
	if($(window).width() > (1100 + 30)){//浏览器当前窗口可视区域宽度
		$("#upload-img-modal").find(".modal-dialog").css("width", 1100);
	}else{
		//为什么这里减去30是因为弹出的模态框离窗口有边距
		$("#upload-img-modal").find(".modal-dialog").css("width", $(window).width() -30);
	}
	//alert($(document).width());//浏览器当前窗口文档对象宽度
	//alert($(document.body).width());//浏览器当前窗口文档body的宽度
	//alert($(document.body).outerWidth(true));//浏览器当前窗口文档body的总宽度 包括border padding margin
	
	//getMaterials();
});

/**
 * 获取素材列表
 * @param bid
 */
function getMaterials(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	var params = {page_size: pageSize, current: currentIndex, total: totalPage, t: Math.random()};
	$.ajax({
		url : "/mt/materials?"+ jsonToGetRequestParams(params),
		dataType: 'json',
		beforeSend:function(){
			$("#material-list-content").empty();
			$(".pagination").empty();
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				materials = data.message;
				if(materials.length == 0){
					$("#material-list-content").append('空空的，还没有数据');
					return;
				}
				for(var i = 0; i < materials.length; i++){
					$("#material-list-content").append(buildEachMaterialRow(i, materials[i]));
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
 * 添加单个文件
 */
function addFile(){
	fileIndex ++;
	$("#add-file").remove();
	$("body").append('<form id="form-id-'+ fileIndex +'" style="display: none;"><input id="add-file-'+ fileIndex +'" type="file" name="file" onchange="haveFileInut(this);"/></form>');
	$('#add-file-'+ fileIndex +'').click();
}
/**
 * 添加单个图片
 */
function addImage(){
	$('#upload-img-modal').modal({backdrop: 'static', keyboard: false});
	$("#upload-img-modal").modal('show');
}

/**
 * 对文件选择后的监听
 * @param obj
 */
function haveFileInut(obj){
	var files = $(obj);
	var filePath = files.val();
	var file = files[0];
	//var fileName = file.val();
	var fileName=filePath.replace(/^.+?\\([^\\]+?)(\.[^\.\\]*?)?$/gi,"$1");  //正则表达式获取文件名，不带后缀
	var fileExt=filePath.replace(/.+\./,"");   //正则表达式获取后缀
	var fileFullName = fileName + "." + fileExt; //文件的全称，包括后缀
	
	$("#upload-material-table").append('<tr class="each-row" id="add-file-row-'+ fileIndex +'">'+ 
											'<td>'+ fileFullName +'</td>'+ 
											'<td onclick="addDesc(this);" onkeydown="saveDesc(event, this);"></td>'+ 
											'<td><div class="progress progress-striped">'+ 
												    '<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">'+ 
												    	'<span class="tip-text">已完成0%</span>'+ 
												    '</div>'+ 
												'</div>'+ 
											'</td>'+ 
											'<td><a class="uploadFile" href="javascript:void(0);" onclick="uploadFile(this);" style="margin-left: 10px;">上传</a><a href="javascript:void(0);" onclick="deleteFile(this);" style="margin-left: 10px;">移除</a></td>'+ 
										'</tr>');
	$("#add-file-row-"+ fileIndex).data("path", fileFullName);//把值保存在内存中
	$("#add-file-row-"+fileIndex).data("index", fileIndex);
	$("#add-file-row-"+fileIndex).data("length", file.files[0].size);
	//$("#add-file").remove();
}

var uuid;
var task;
var preAddFileIndex = 1;
/**
 * 单个上传文件
 * @param obj
 */
function uploadFile(obj){
	//判断是否上传成功(成功直接返回)
	if($(obj).closest("tr").hasClass("complete")){
		return;
	}
	
	if(isUploading){
		layer.msg("当前有任务在上传，请稍等。。。");
		return;
	}
	
	isUploading = true;
	
	preAddFileIndex = $(obj).closest("tr").data("index");
	$("#add-file-row-"+ preAddFileIndex).find(".progress-bar").css("width", "0%");
	$("#add-file-row-"+ preAddFileIndex).find(".tip-text").text("已完成0%");
	//var file = $("#add-file-"+ fileIndex)[0];
	//var form = $("#form-id-"+ fileIndex).closest("form");
	//form.submit();
	//var formData = new FormData($("#form-id-"+ fileIndex).closest("form")); //这个获取的form无法解析
	var formData = new FormData(document.getElementById("form-id-"+ preAddFileIndex));//表单id
	//formData.append("file", document.getElementById("form-id-"+ fileIndex));
	  // XMLHttpRequest 对象
    /*var xhr = new XMLHttpRequest();
    xhr.open("post", '/wul/upload', true);
    xhr.onload = function() {
        // ShowSuccess("上传完成");
        alert("上传完成");
        $("#batchUploadBtn").attr('disabled', false);
        $("#batchUploadBtn").val("上传");
        $("#progressBar").parent().removeClass("active");
        $("#progressBar").parent().hide();
        //$('#myModal').modal('hide');
    };
    xhr.upload.addEventListener("progress", progressFunction, false);
    xhr.send(formData);*/
	$.ajax({
	    url: '/wul/upload',
	    type: 'POST',
	    cache: false,
	    data: formData,
	    processData: false,
	    contentType: false,
	    dataType: 'json', 
	}).success(function(data) {
		if(data != null && data.isSuccess){
			//开始启动请求去获取进度条
			//定时50毫秒去查询进度
			uuid = data.message;
			task = setInterval("getProgress()","50");
		}else{
			isUploading = false;
			ajaxError(data);
		}
	}).error(function(res) {
		isUploading = false;
		ajaxError(data);
	});
}

/**
 * 移除单个文件
 * @param obj
 */
function deleteFile(obj){
	layer.confirm('您要移除该文件记录吗？', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		$(obj).closest("tr").remove();
		layer.closeAll();
	}, function(){
	});
}

/**
 * 获取进度条
 */
function getProgress(){
	$.ajax({
		url : "/wul/getProgress/"+uuid,
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			if(data != null && data.isSuccess){
				var pro = data.message;
				if(pro == 100){
					getQiNiuFilePath(uuid);//获取七牛的文件路径
					clearInterval(task);
				}
				
				if(pro == 9999){//上传失败代码
					clearInterval(task);
					//$("#add-file-row-"+ preAddFileIndex).find(".progress-bar").css("width", "0%");
					$("#add-file-row-"+ preAddFileIndex).find(".uploadFile").text("重试");
					isUploading = false;
					return;
				}
				$("#add-file-row-"+ preAddFileIndex).find(".progress-bar").css("width", pro +"%");
				$("#add-file-row-"+ preAddFileIndex).find(".tip-text").text("已完成"+ pro +"%");
				
				console.log("当前的进度是："+ pro)
			}
		},
		error : function(data) {
			isUploading = false;
			ajaxError(data);
		}
	});
}

/**
 * 获取文件在七牛云存储的路径
 * @param uuid
 */
function getQiNiuFilePath(uuid){
	$.ajax({
		url : "/wul/getQiNiuPath/"+uuid,
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			if(data != null && data.isSuccess){
				$("#add-file-row-"+ preAddFileIndex).addClass("complete");
				$("#add-file-row-"+ preAddFileIndex).find(".uploadFile").remove();
				$("#add-file-row-"+ preAddFileIndex).data("qiniu_path", data.message);
			}else{
				$("#add-file-row-"+ preAddFileIndex).find(".uploadFile").text("重试");
			}
			isUploading = false;
		},
		error : function(data) {
			ajaxError(data);
			isUploading = false;
		}
	});
}


/**
 * 添加描述信息
 * @param obj
 */
function addDesc(obj){
	var $tr = $(obj).closest("tr");
	var desc;
	if($(obj).find("input") && $(obj).find("input").length > 0){
		desc = $(obj).find("input").val();
	}else{
		desc = $tr.data("desc");
	}
	
	$(obj).text('');
	$(obj).append('<input type="text" placeholder="回车保存描述"/>');
	$(obj).find("input").focus();
	if(isNotEmpty(desc))
		$(obj).find("input").val(desc);
}

/**
 * 保存描述信息
 * @param obj
 */
function saveDesc(event, obj){
	var e = event || window.event || arguments.callee.caller.arguments[0];
    if(e && e.keyCode==13){ // enter 键
    	var text = $(obj).find("input").val();
    	$(obj).find("input").remove();
    	$(obj).text(text);
    	$(obj).closest("tr").data("desc", text);
   }
}

/**
 * 构建每一行消息html
 * @param index
 * @param material
 * @returns {String}
 */
function buildEachMaterialRow(index, material){
		var html = '<div class="row notification-list notification-list-padding" data-id="'+ message.id +'">'+
			   			'<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2">'+
							'<img src="'+ changeNotNullString(message.user_pic_path) +'" width="40" height="40" class="img-rounded">'+
						'</div>'+
						'<div class="col-lg-11 col-md-11 col-sm-10 col-xs-10">'+
					       	'<div class="list-group">'+
						       		'<div class="list-group-item notification-list-item active">'+
						       			'<a href="JavaScript:void(0);" onclick="linkToMy('+ message.from_user_id +')" target="_blank" class="marginRight">'+ changeNotNullString(message.account)+'</a>'+
						       			'<span class="marginRight publish-time">发表于:'+ changeNotNullString(message.create_time) +'</span>'+
						       		'</div>';
							html += '<div class="list-group-item notification-list-item">'+
										'<div class="row">';
									if(isNotEmpty(message.source)){
								    html += '<div class="col-lg-12 '+ (isNotEmpty(message.source_account) && isNotEmpty(message.source_user_id) ? 'hand" onclick="linkToTable(\''+ message.table_name +'\', '+ message.table_id +', '+ message.source_user_id +')"' :'"') +'>'+
												'<blockquote>'+ message.source;
													if(isNotEmpty(message.source_account)){
												html += '<small><cite>'+ message.source_account +'</cite>&nbsp;&nbsp;'+ changeNotNullString(message.create_time) +'</small>';
													}
										html +='</blockquote>'+
											'</div>';
									}
									html += '<div class="col-lg-12">'+ changeNotNullString(message.content) +'</div>'+
										'</div>'+
									'</div>';
								if(isLogin){
							html += '<div class="list-group-item notification-list-item">'+
										'<div class="row">'+
							       				'<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12 text-align-right">';
										if(!message.is_read){
											html += '<button class="btn btn-sm btn-primary pull-right tag-read-btn" style="width: 80px;" type="button">标为已读</button>';
										}
				       					 if(isAdmin && message.to_user_id == loginUserId){
				       						 html += '<button class="btn btn-sm btn-primary pull-right delete-other-btn" style="width: 60px; margin-right: 5px;" type="button">删除</button>';
				       					 }
							       					 
							       		html += '</div>'+
							       		'</div>'+
								   '</div>';
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
    getMessages();
}

/**
 * 点击向左的按钮
 */
function goIndex(index){
	currentIndex = index;
	getMessages();
}

/**
 * 点击向左的按钮
 */
function pre(){
	currentIndex --;
	if(currentIndex < 0)
		currentIndex = 0;
	getMessages();
}


/**
 * 点击向右的按钮
 */
function next(){
	currentIndex ++;
	if(currentIndex > totalPage)
		currentIndex = totalPage;
	getMessages();
}

/**
 * 获取子页面的getdata()
 */
function getData(){
	var formData;
	try{
		formData = document.getElementById("add-photo-iframe").contentWindow.getData();
	}catch(err){
		//在这里处理错误
		layer.msg("请选择图片："+err.message);
		return;
	 }
	
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
	    url: '/wul/upload/imgage',
	    type: 'POST',
	    cache: false,
	    data: formData,
	    processData: false,
	    contentType: false,
	    dataType: 'json', 
	}).success(function(data) {
		layer.close(loadi);//关闭
		
		if(data != null && data.isSuccess){
			fileIndex++; //新增1
			var fileName = formData.get("file-name");
			var qiniuPath = data.message;//获取新的链接
			$("#upload-material-table").append('<tr class="complete each-row" id="add-file-row-'+ fileIndex +'">'+ 
					'<td>'+ fileName +'</td>'+ 
					'<td onclick="addDesc(this);" onkeydown="saveDesc(event, this);"></td>'+ 
					'<td><div class="progress progress-striped">'+ 
						    '<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: 100%;">'+ 
						        '<span class="tip-text">已完成100%</span>'+ 
						    '</div>'+ 
						'</div>'+ 
					'</td>'+ 
					'<td><a href="javascript:void(0);" onclick="deleteFile(this);" style="margin-left: 10px;">移除</a></td>'+ 
				'</tr>');
			$("#add-file-row-"+ fileIndex).data("path", fileName);//把值保存在内存中
			$("#add-file-row-"+ fileIndex).data("qiniu_path", qiniuPath);//把值保存在内存中
			$("#add-file-row-"+ fileIndex).data("index", fileIndex);//把值保存在内存中
			$("#add-file-row-"+ fileIndex).data("length", formData.get("file-size"));//把值保存在内存中
			$('#upload-img-modal').modal('hide');
		}else
			ajaxError(data);
	}).error(function(res) {
		layer.close(loadi);
		layer.msg(res);
	});
}

/**
 * 提交所有的已经上传的文件
 */
function submitFiles(){
	//找到两种都满足的tr
	var $trs = $("#upload-material-table").find(".each-row.complete");
	if($trs && $trs.length < 1){
		layer.msg("请先上传文件！");
		return;
	}
	
	var materials = new Array();
	var flag = false; //标记是否符合要求
	$trs.each(function(index){
		//没有七牛云存储路径的不让提交
		if(isEmpty($(this).data("qiniu_path")))
			flag = true;
		
		materials.push($(this).data());
	});
	
	if(flag){
		layer.msg("无法获取服务器文件路径，请刷新重试！");
		return;
	}
	
	$.ajax({
		type: 'post',
		url : "/mt/material",
		data: {"materials": JSON.stringify(materials)},
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			if(data != null && data.isSuccess){
				layer.msg(data.message +",1秒钟后自动刷新");
				setTimeout("window.location.reload();", 1000);
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			ajaxError(data);
		}
	});
}