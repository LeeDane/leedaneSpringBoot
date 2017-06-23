var pageSize = 8;
var currentIndex = 0;
var posts;
var totalPage = 0;
var $postListContainer;
/**
 * 解决动态html无法绑定tooltip和popover
 */
function bindTool(){
	$("[data-toggle='tooltip']").tooltip();
	$(".tooltip").css("display", "block");
	$("[data-toggle='popover']").popover();
}

$(function(){
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-circle").addClass("active");
	$postListContainer = $("#post-list");
	bindTool();
	//圈子用户头像图片的绑定
	$(document).on("click", ".user-img", function(event){
		event.stopPropagation();//阻止冒泡 
		var $img = $(this);
		var id = $img.attr("data");
		linkToMy(id);
	});
	
	//查看帖子详情
	$(document).on("click", ".link-to-post-detail", function(event){
		event.stopPropagation();//阻止冒泡 
		var $span = $(this);
		var id = $span.attr("data");
		linkToMy(id);
	});
	
	//赞的点击事件
	$(document).on("click", ".post-zan", function(event){
		event.stopPropagation();//阻止冒泡
		addZan($(this));
	});
	
	//评论的点击事件
	$(document).on("click", ".post-comment", function(event){
		event.stopPropagation();//阻止冒泡
		addComment($(this));
	});
	
	//转发的点击事件
	$(document).on("click", ".post-transmit", function(event){
		event.stopPropagation();//阻止冒泡
		addTransmit($(this));
	});
	
	//删除的点击事件
	$(document).on("click", ".post-delete", function(event){
		event.stopPropagation();//阻止冒泡
		deletePost($(this));
	});
	
	//加入圈子
	$(".into-circle").click(function(){
		$.ajax({
			url : "/cc/join/check?cid="+circleId,
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				if(data.isSuccess){
					if(data.message){
						var question = data.question;
						layer.prompt({title: question, formType: 0}, function(pass, promptIndex){
						  var index = layer.load(1, {
						    shade: [0.1,'#fff'] //0.1透明度的白色背景
						  });
						  joinCircle(pass);
						});
					}else{
						//询问框
						layer.confirm('您确定加入该圈子吗？', {
						  btn: ['确定','放弃'] //按钮
						}, function(){
							joinCircle();
						}, function(){
						  
						});
					}
				}else{
					ajaxError(data);
				}
			},
			error : function(data) {
				ajaxError(data);
			}
		});
	});
	
	//离开圈子
	$(".circle-leave").click(function(){
		//询问框
		layer.confirm('您确定退出该圈子吗？退出将清空您关于该圈子的全部记录，不可恢复，请谨慎！', {
		  btn: ['确定','放弃'] //按钮
		}, function(){
			leaveCircle();
		}, function(){
		  
		});
	});
	//add-clock-in打卡
	$(".add-clock-in").click(function(){
		doClockIn();
	});

	getPosts();
});

/**
 * 获取帖子
 * @param bid
 */
function getPosts(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	var params = {page_size: pageSize, current: currentIndex, total: totalPage, t: Math.random()};
	$.ajax({
		url : "/cc/"+ circleId +"/posts?"+ jsonToGetRequestParams(params),
		dataType: 'json',
		beforeSend:function(){
			$postListContainer.empty();
			$(".pagination").empty();
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				posts = data.message;
				if(posts.length == 0){
					if(currentIndex == 0){
						$postListContainer.append('该圈子还没有帖子，您可以<a href="/cc/'+ circleId +'/write" class="btn btn-warning btn-sm circle-post" role="button" data-toggle="tooltip" title="发表关于该圈子的帖子">写帖子</a>');
						bindTool();
					}else{
						$postListContainer.append('已经没有更多的帖子！');
						pageDivUtil(data.total);
					}
					return;
				}
				for(var i = 0; i < posts.length; i++){
					$postListContainer.append(buildEachPostRow(i, posts[i]));
					$("#post-list-"+i).data("post", posts[i]);
				}
				bindTool();
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
 * 构建每一行帖子html
 * @param index
 * @param post
 * @returns {String}
 */
function buildEachPostRow(index, post){
		var html = '<div class="panel panel-default" id="post-list-'+ index +'">'+
						'<div class="panel-heading">'+
							'<p>'+ post.title +'</p>'+
							'<div>'+
								'<button type="button" class="btn btn-default btn-xs user-img" data="'+ post.create_user_id +'">'+
								  '<img src="'+ post.user_pic_path +'" class="img-circle" style="width: 20px; height: 20px" /> '+ post.account+
								'</button>&nbsp;&nbsp;'+ post.create_time +
							'</div>';
						if(isNotEmpty(post.tag)){
					html +='<p style="margin-top: 10px;">标签：';
							var tagArray = post.tag.split(",");
							for(var tagIndex = 0; tagIndex < tagArray.length; tagIndex++){
						html += '<span class="post-tag">'+ tagArray[tagIndex] +'</span>';
							}
					html += '</p>';
						}
					html += '<div class="pull-right" title="Title" data-container="body" data-toggle="popover" data-content="Popover 中的一些内容 —— options 方法 ">'+
								'<img src="/page/images/more.png" class="" style="width: 20px; height: 20px" />'+
							'</div>'+
						'</div>'+
						'<div class="panel-body panel-table-container">';
						if(post.pid > 0){
					html += '<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12">'+
								'<blockquote>'+
									'<div class="cut-text hand"><a '+ (post.blockquote ? 'data-toggle="tooltip" data-placement="left" title="查看该帖子详细信息"': '') +'>'+ post.blockquote_content +'</a></div>';
									if(post.blockquote){
								html += '<small><cite>'+ post.blockquote_account +'</cite>&nbsp;&nbsp;'+ post.blockquote_time +'</small>';
									}
						html +='</blockquote>'+
							'</div>';
						}
					html +=	'<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12 cut-text2">'+ post.content +'</div>';
					if(isNotEmpty(post.imgs)){
						var imgArray = post.imgs.split(";");
						for(var imgIndex = 0; imgIndex < imgArray.length; imgIndex++){
					html += '<div class="col-lg-4 col-sm-4 col-md-12 col-xs-12 img-container">'+
								'<img src="'+ imgArray[imgIndex] +'" style="width: 100%; height: 180px;" class="img-responsive" onClick="" />'+
							'</div>';		
						}
					}
				html +='</div>'+
						'<div class="panel-footer" style="color: #666;">';
							if(isNotEmpty(post.zan_users)){
								var users = post.zan_users.split(";");
								var userStr = "";
								for(var i = 0; i < users.length; i++){
									var user = users[i];
									if(isNotEmpty(user) && user.split(",").length == 2){
										if(i != users.length -1)
											userStr += '<a href="JavaScript:void(0);" onclick="linkToMy('+ user.split(",")[0] +')">'+ changeNotNullString(user.split(",")[1]) +'</a>、';
										else
											userStr += '<a href="JavaScript:void(0);" onclick="linkToMy('+ user.split(",")[0] +')">'+ changeNotNullString(user.split(",")[1]) +'</a>';
									}
								}
							html += '<p>'+ userStr +'&nbsp;等'+ post.zan_number +'人觉得很赞</p>';
							}
					html += '<p>';
					if(isCreater || isCircleAdmin || loginUserId == post.create_user_id){
						html += '<span class="glyphicon glyphicon-trash post-delete hand" aria-hidden="true"></span>&nbsp;&nbsp;';
					}
								
						html += '<span class="glyphicon glyphicon-comment hand post-comment" aria-hidden="true"></span>&nbsp;'+ post.comment_number +'&nbsp;&nbsp;'+
								'<span class="glyphicon glyphicon-share-alt hand post-transmit" aria-hidden="true"></span>&nbsp;'+ post.transmit_number +'&nbsp;&nbsp;'+
								'<span class="glyphicon glyphicon-thumbs-up hand post-zan" aria-hidden="true"></span>&nbsp;'+ post.zan_number +'&nbsp;&nbsp;'+
								'<span class="glyphicon glyphicon-credit-card hand" aria-hidden="true"></span>&nbsp;'+ post.comment_number +'&nbsp;&nbsp;'+
								'<span class="hand link-to-post-detail" data="'+ post.id +'">查看详情</a>'+
							'</p>'+
						'</div>'+
					'</div>';
	
	return html;
}

/**
 * 申请加入圈子
 * @param answer
 */
function joinCircle(answer){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type: 'POST',
		data: {cid: circleId, answer: answer},
		url : "/cc/join",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg("加入圈子成功，1秒后自动刷新");
				reloadPage(1000);
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
 * 离开该圈子
 */
function leaveCircle(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type: 'DELETE',
		url : "/cc/leave/"+ circleId,
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg("离开该圈子成功，1秒后自动刷新");
				reloadPage(1000);
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
 * 执行打卡操作
 */
function doClockIn(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type: 'POST',
		data: {forms: 'web网页端'},
		url : "/cc/clockIn/"+circleId,
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg("您已经打卡成功，明天记得早点过来，1秒后自动刷新");
				reloadPage(1000);
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
 * 添加赞
 * @param id
 */
function addZan(obj){
	var post = $(obj).closest(".panel").data("post");
	var loadi = layer.load('努力加载中…');
	$.ajax({
		type : "post",
		data: {table_name: 't_circle_post', content: '喜欢', froms: '网页端', table_id: post.id},
		url : '/cc/'+ circleId +'/post/'+ post.id +'/zan',
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					layer.msg(data.message + "，1秒后自动刷新");
					reloadPage(1000);
				}else
					layer.msg(data.message);
				
		},
		error : function() {
			layer.close(loadi);
			layer.msg("网络请求失败");
		}
	});
}

/**
 * 添加评论
 * @param obj
 */
function addComment(obj){
	var post = $(obj).closest(".panel").data("post");
	var params = {table_name: 't_circle_post', table_id: post.id, froms: 'web网页端'};
	
	//prompt层
	layer.prompt({title: '请输入您想说的内容', formType: 0}, function(pass, promptIndex){
	  var index = layer.load(1, {
	    shade: [0.1,'#fff'] //0.1透明度的白色背景
	  });
	  var loadi = layer.load('努力加载中…');
	  params.content = pass;
	  $.ajax({
			type : "post",
			data: params,
			url : '/cc/'+ circleId+ '/post/'+ post.id +'/comment',
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
					layer.close(loadi);
					if(data.isSuccess){
						layer.msg(data.message+ "，1秒后自动刷新");
						reloadPage(1000);
					}else{
						layer.msg(data.message);
						layer.close(loadi);
					}
			},
			error : function() {
				layer.close(loadi);
				layer.msg("网络请求失败");
			}
		});
	});
	
}

/**
 * 添加转发
 * @param obj
 */
function addTransmit(obj){
	var post = $(obj).closest(".panel").data("post");
	var params = {froms: 'web网页端'};
	
	//prompt层
	layer.prompt({title: '请输入您想说的内容', formType: 0}, function(pass, promptIndex){
	  var index = layer.load(1, {
	    shade: [0.1,'#fff'] //0.1透明度的白色背景
	  });
	  var loadi = layer.load('努力加载中…');
	  params.content = pass;
	  params.title = "转发帖子";
	  $.ajax({
			type : "post",
			data: params,
			url : '/cc/'+ circleId+ '/post/'+ post.id +'/transmit',
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
					layer.close(loadi);
					if(data.isSuccess){
						layer.msg(data.message+ "，1秒后自动刷新");
						reloadPage(1000);
					}else{
						layer.msg(data.message);
						layer.close(loadi);
					}
			},
			error : function() {
				layer.close(loadi);
				layer.msg("网络请求失败");
			}
		});
	});
}

/**
 * 删除帖子
 * @param obj
 */
function deletePost(obj){
	var post = $(obj).closest(".panel").data("post");	
	
	layer.confirm('您要删除该条帖子吗？删除掉将无法恢复，请慎重！', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		//判断是否是自己的帖子
		if(post.create_user_id == loginUserId){
			doDelete(post.id, '');
		}else{
			//prompt层
			layer.prompt({title: '请输入您要删除该帖子的原因(必填)', formType: 0}, function(pass, promptIndex){
			  var index = layer.load(1, {
			    shade: [0.1,'#fff'] //0.1透明度的白色背景
			  });
			  doDelete(post.id, pass);
			});
		}
	}, function(){
	});
	
}

/**
 * 执行删除操作
 * @param reason
 */
function doDelete(postId, reason){
	var loadi = layer.load('努力加载中…');
	$.ajax({
		type : "DELETE",
		url : '/cc/'+ circleId +'/post/'+ postId +"?reason="+ reason,
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					layer.msg(data.message+ "，1秒后自动刷新");
					reloadPage(1000);
				}else{
					layer.msg(data.message);
					layer.close(loadi);
				}
		},
		error : function() {
			layer.close(loadi);
			layer.msg("网络请求失败");
		}
	});
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
    getPosts();
}

/**
 * 点击向左的按钮
 */
function goIndex(index){
	currentIndex = index;
	getPosts();
}

/**
 * 点击向左的按钮
 */
function pre(){
	currentIndex --;
	if(currentIndex < 0)
		currentIndex = 0;
	getPosts();
}


/**
 * 点击向右的按钮
 */
function next(){
	currentIndex ++;
	if(currentIndex > totalPage)
		currentIndex = totalPage;
	getPosts();
}