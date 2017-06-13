var pageSize = 8;
var currentIndex = 0;
var messages;
var totalPage = 0;
var type; //通知类型
$(function(){
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-msg").addClass("active");
	
	if(isNotEmpty(tabName)){
		$("#notification-tabs").find("li").each(function(index){
			if($(this).attr("data-value") == tabName){
				type = tabName;
				$(this).addClass("active");
				return true;
			}else{
				$(this).removeClass("active");
			}
		});
	}
	
	if(isEmpty(type)){
		var li0 = $("#notification-tabs").find("li").eq(0);
		li0.addClass("active");
		type = li0.attr("data-value");
	}
	
	if(isEmpty(type)){
		layer.msg("无法获取通知类型");
		return;
	}
	
	//获取通知类型
	$("#notification-tabs").find("li").on("click", function(index){
		$("#notification-tabs").find("li").removeClass("active");
		$(this).addClass("active");
		type = $(this).attr("data-value");

		currentIndex = 0;
		getMessages();
	});
	
	/**
	 * 标记为已读
	 */
	$(".container").on("click", ".tag-read-btn", function(){
		var dataId = $(this).closest(".notification-list").attr("data-id");
		if(dataId > 0){
			var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
			$.ajax({
				type : "put",
				data:{nid: dataId, read: true},
				dataType: 'json',  
				url : "/nf/notification",
				beforeSend:function(){
				},
				success : function(data) {
					layer.close(loadi);
					if(data.isSuccess){
						layer.msg(data.message + ",1秒后自动刷新");
						setTimeout(linkToSpecifiedTab(type), 1000);
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
	});
	
	$(".container").on("click", ".delete-other-btn", function(){
		var dataId = $(this).closest(".notification-list").attr("data-id");
		if(dataId > 0){
			layer.confirm('您要删除该评论吗？', {
				  btn: ['确定','点错了'] //按钮
			}, function(){
				var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
				$.ajax({
					type : "delete",
					dataType: 'json',  
					url : "/nf/notification?nid="+ dataId,
					beforeSend:function(){
					},
					success : function(data) {
						layer.close(loadi);
						if(data.isSuccess){
							layer.msg(data.message + ",1秒后自动刷新");
							setTimeout(linkToSpecifiedTab(type), 1000);
						}else{
							ajaxError(data);
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
	});
	
	getMessages();
});

/**
 * 当前tab标记为已读
 */
function allRead(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "put",
		data:{type: type, read: true},
		dataType: 'json',  
		url : "/nf/notification/all",
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg(data.message + ",1秒后自动刷新");
				setTimeout(linkToSpecifiedTab(type), 1000);
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
 * 跳转到指定的tab
 * @param tabName
 */
function linkToSpecifiedTab(tabName){
	window.location.href="/msg?tab="+tabName;
}

/**
 * 获取我的消息列表
 * @param bid
 */
function getMessages(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	var params = {page_size: pageSize, current: currentIndex, total: totalPage, type: type, t: Math.random()};
	$.ajax({
		url : "/nf/notifications/paging?"+ jsonToGetRequestParams(params),
		dataType: 'json',
		beforeSend:function(){
			$("#message-list-content").empty();
			$(".pagination").empty();
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				messages = data.message;
				if(messages.length == 0){
					$("#message-list-content").append('空空的，还没有数据');
					return;
				}
				for(var i = 0; i < messages.length; i++){
					$("#message-list-content").append(buildEachMessageRow(i, messages[i]));
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
 * 构建每一行消息html
 * @param index
 * @param message
 * @returns {String}
 */
function buildEachMessageRow(index, message){
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
