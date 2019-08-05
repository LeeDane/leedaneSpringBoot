layui.use(['layer', 'laypage', 'util'], function(){
   layer = layui.layer;
   laypage = layui.laypage;
   util = layui.util;
  /*initPage(".pagination", "getMessages");*/
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

	$("#to-page-top").on("click", function(){
        scrollToPageTop(1000);
    });


});
var messages;
var type; //通知类型

/**
** 更新未读取数量
*/
function updateNoReadNumber(){
    var currentMsg = $(".nav-msg").hasClass("active");
    if(noReadNumbers && noReadNumbers.length > 0){
        for(var i = 0 ; i < noReadNumbers.length; i++){
            if(noReadNumbers[i]['key'] == '全部'){
                if(!currentMsg)
                    break;
            }else{
                if(currentMsg){
                    var lis = $("#notification-tabs").find("li");
                    for(var j = 0; j < lis.length; j++){
                        if(lis.eq(j).attr("data-value") == noReadNumbers[i]['key']){
                            lis.eq(j).find("a").text(noReadNumbers[i]['key'] +'(' + noReadNumbers[i]['value'] +')');
                        }
                    }
                }
            }
        }
    }else{
        getNoReadNumber();
    }
}

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
					if(currentIndex == 0){
						$("#message-list-content").append('暂时没有更多的数据！');
					}else{
						$("#message-list-content").append('已经没有更多的消息啦，请重新选择！');
						/*pageDivUtil(data.total);*/
					}
					return;
				}
				for(var i = 0; i < messages.length; i++){
					$("#message-list-content").append(buildEachMessageRow(i, messages[i]));
				}
				/*pageDivUtil(data.total);*/
				
				//执行一个laypage实例
				 laypage.render({
				    elem: 'item-pager' //注意，这里的 test1 是 ID，不用加 # 号
				    ,layout: ['prev', 'page', 'next', 'count', 'skip']
				    ,count: data.total //数据总数，从服务端得到
				    ,limit: pageSize
				    ,theme: '#337ab7'
				    , curr: currentIndex + 1
				    ,jump: function(obj, first){
					    //obj包含了当前分页的所有参数，比如：
					    console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
					    console.log(obj.limit); //得到每页显示的条数
					    if(!first){
					    	currentIndex = obj.curr -1;
					    	getMessages();
					    }
					  }
				 });

				 updateNoReadNumber();
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
        var createTime = new Date(changeNotNullString(message.create_time));
        createTime = setTimeAgo(createTime);
		var html = '<div class="row notification-list notification-list-padding" data-id="'+ message.id +'">'+
			   			'<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2" style="text-align: center;">'+
							'<img src="'+ changeNotNullString(message.user_pic_path) +'" width="40" height="40" class="img-rounded">'+
						'</div>'+
						'<div class="col-lg-11 col-md-11 col-sm-10 col-xs-10">'+
					       	'<div class="list-group">'+
						       		'<div class="list-group-item notification-list-item active">'+
						       			'<a href="JavaScript:void(0);" onclick="linkToMy('+ message.from_user_id +')" target="_blank" class="marginRight">'+ changeNotNullString(message.account)+'</a>'+
						       			'<span class="marginRight publish-time">发表于:'+ createTime +'</span>'+
						       		'</div>';
							html += '<div class="list-group-item notification-list-item">'+
										'<div class="row">';
									if(isNotEmpty(message.source)){
								    html += '<div class="col-lg-12 '+ (isNotEmpty(message.source_account) && isNotEmpty(message.source_user_id) ? 'hand" onclick="linkToTable(\''+ message.table_name +'\', '+ message.table_id +', '+ message.source_user_id +')"' :'"') +'>'+
												'<blockquote>'+ message.source;
													if(isNotEmpty(message.source_account)){
												html += '<small><cite>'+ message.source_account +'</cite>&nbsp;&nbsp;'+ changeNotNullString(message.source_create_time) +'</small>';
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
				       					 if(isAdmin || message.to_user_id == loginUserId){
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
