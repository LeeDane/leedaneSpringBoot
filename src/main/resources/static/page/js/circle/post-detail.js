layui.use(['layer', 'laypage'], function(){
	layer = layui.layer;
	laypage = layui.laypage;
	$container= $(".container");
	$commentContainer = $("#comment-list-container");
	//initPage(".pagination", "getComments", 10);
	
	if(!audit)
		getComments();
	$container.on("click", ".reply-other-btn", function(){
		$(this).closest(".list-group").find(".reply-container").toggle("fast");
	});
	
	$("[data-toggle='tooltip']").tooltip();
	$(".tooltip").css("display", "block");
	//删除的点击事件
	$(document).on("click", ".post-delete", function(event){
		event.stopPropagation();//阻止冒泡
		deletePost($(this));
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
	
	$container.on("click", ".delete-other-btn", function(){
		var dataId = $(this).closest(".comment-list").attr("data-id");
		var createUserId = $(this).closest(".comment-list").attr("create-user-id");
		if(dataId > 0 && createUserId > 0){
			layer.confirm('您要删除该评论吗？', {
				  btn: ['确定','点错了'] //按钮
			}, function(){
				var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
				$.ajax({
					type : "delete",
					dataType: 'json',  
					url : "/cm/comment?cid="+ dataId+"&create_user_id="+createUserId,
					beforeSend:function(){
					},
					success : function(data) {
						layer.close(loadi);
						if(data.isSuccess){
							layer.msg(data.message + ",1秒后自动刷新");
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
			}, function(){
			});
		}
	});
	if(!audit)
		buildZanUser();  
	
	if(imgs){
		var imgArr = imgs.split(";");
		for(var i = 0; i < imgArr.length; i++){
			
			var html = '';
			if(isVideo(imgArr[i])){
				html += '<div class="col-lg-4 col-sm-4">';
				html += getVideoHtml(imgArr[i]);
				html += '</div>';
			}else if(isAudio(imgArr[i])){
				html += '<div class="col-lg-4 col-sm-4">';
				html += getAudioHtml(imgArr[i]);
				html += '</div>';
			}else if(isImg(imgArr[i])){
				html += '<div class="col-lg-4">' +
		   					'<img src="'+ imgArr[i] +'" class="img-responsive post-item-img" onClick="showSingleImg(this);" />'+
				   		'</div>';
			}else{
				layer.msg(getSupportTypeStr());
			}
			$("#img-container").append(html);
		}
	}
});
var comments;
var $container;
var $commentContainer;



/**
 * 生成点赞的用户列表
 */
function buildZanUser(){
	if(isNotEmpty(zanUsers)){
		var users = zanUsers.split(";");
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
		$("#zan-users").html('<div class="zan_user">'+ userStr +'等'+ zanNumber +'人觉得很赞</div>');
	}
}

/**
 * 删除帖子
 * @param obj
 */
function deletePost(obj){	
	layer.confirm('您要删除该条帖子吗？删除掉将无法恢复，请慎重！', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		//判断是否是自己的帖子
		if(createUserId == loginUserId){
			doDelete(postId, '');
		}else{
			//prompt层
			layer.prompt({title: '请输入您要删除该帖子的原因(必填)', formType: 0}, function(pass, promptIndex){
			  var index = layer.load(1, {
			    shade: [0.1,'#fff'] //0.1透明度的白色背景
			  });
			  doDelete(postId, pass);
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
 * 添加赞
 * @param id
 */
function addZan(obj){
	var loadi = layer.load('努力加载中…');
	$.ajax({
		type : "post",
		data: {table_name: 't_circle_post', content: '喜欢', froms: '网页端', table_id: postId},
		url : '/cc/'+ circleId +'/post/'+ postId +'/zan',
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
 * 获取评论请求列表参数
 */
function getCommentsRequestParams(){
	return {current: currentIndex, total: totalPage, table_name: "t_circle_post", table_id: postId, showUserInfo: true, page_size: pageSize, t: Math.random()};
}

/**
 * 获取帖子的评论内容
 * @param bid
 */
function getComments(bid){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		//contentType: "application/x-www-form-urlencoded;charset=UTF-8",
		url : "/cm/comments/paging?"+ jsonToGetRequestParams(getCommentsRequestParams()),
		dataType: 'json',
		beforeSend:function(){
			$commentContainer.empty();
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				if(data.message.length == 0){
					if(currentIndex == 0){
						$commentContainer.append("还没有发表过任何评论，请给TA评论吧！");
					}else{
						$commentContainer.append("已经没有更多的评论啦，请重新选择！");
						//pageDivUtil(data.total);
					}
					return;
				}
				comments = data.message;
				for(var i = 0; i < comments.length; i++){
					buildEachCommentRow(i, comments[i]);
				}
				//pageDivUtil(data.total);
				
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
					    	getComments();
					    }
					  }
				 });
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
 * 构建每一行评论html
 * @param comment
 * @param index
 */
function buildEachCommentRow(index, comment){
		var html = '<div class="row comment-list comment-list-padding" data-id="'+ comment.id+'" create-user-id="'+ comment.create_user_id+'" >'+
			   			'<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2">'+
							'<img src="'+ changeNotNullString(comment.user_pic_path) +'" width="40" height="40" class="img-rounded hand center-block">'+
						'</div>'+
						'<div class="col-lg-11 col-md-11 col-sm-10 col-xs-10">'+
					       	'<div class="list-group">'+
						       		'<div class="list-group-item comment-list-item active">'+
						       			'<a href="JavaScript:void(0);" onclick="linkToMy('+ comment.create_user_id +')" target="_blank" class="marginRight">'+ changeNotNullString(comment.account)+'</a>'+
						       			'<span class="marginRight publish-time">发表于:'+ changeNotNullString(comment.create_time) +'</span>'+
						       			'<span class="marginRight publish-time">来自:'+ changeNotNullString(comment.froms) +'</span>'+
						       		'</div>';
							html += '<div class="list-group-item comment-list-item">'+
										'<div class="row">';
									if(isNotEmpty(comment.blockquote_content)){
								    html += '<div class="col-lg-12">'+
											    '<blockquote>'+ comment.blockquote_content;
												if(isNotEmpty(comment.blockquote_account)){
											html += '<small><cite>'+ comment.blockquote_account +'</cite>&nbsp;&nbsp;'+ changeNotNullString(comment.blockquote_time) +'</small>';
												}
										html +='</blockquote>'+
											'</div>';
									}
									html += '<div class="col-lg-12">'+ changeNotNullString(comment.content) +'</div>'+
										'</div>'+
									'</div>';
								if(isLogin){
							html += '<div class="list-group-item comment-list-item">'+
										'<div class="row">'+
							       				'<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12 text-align-right">'+
							       					 '<button class="btn btn-sm btn-primary btn-block pull-right reply-other-btn" style="width: 60px;" type="button">回复TA</button>';
				       					 if(isAdmin || comment.create_user_id == loginUserId){
				       						 html += '<button class="btn btn-sm btn-primary pull-right delete-other-btn" style="width: 60px; margin-right: 5px;" type="button">删除</button>';
				       					 }
										html +=	'</div>'+
							       		'</div>'+
							       		'<div class="row">'+
								       		'<div class="col-lg-12 reply-container" table-id="'+ comment.table_id+'" table-name="'+ comment.table_name +'" style="display: none;">'+
									    		'<div class="row">'+
									    			'<div class="col-lg-12">'+
									    				'<form class="form-signin" role="form">'+
										    			     '<fieldset>'+
										    				     '<textarea class="form-control reply-comment-text" placeholder="回复TA，最多250个文字。"> </textarea>'+
										    			     '</fieldset>'+
										    			 '</form>'+
									    			'</div>'+
									    			'<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12 text-align-right" style="margin-top: 10px;">'+
									    				'<button class="btn btn-sm btn-info btn-block pull-right" style="width: 60px;" type="button" onclick="commentItem(this, '+ comment.id +', '+ comment.table_id +');">评论</button>'+
									    			'</div>'+
									    		'</div>'+
									    	'</div>'+
								    	'</div>'+
								   '</div>';
								}
					html += '</div>'+
					'</div>'+
			'</div>';
	
		$commentContainer.append(html);
}

/**
 * 评论别人的评论
 * @param obj
 */
function commentItem(obj, pid, postId){
	var content = $(obj).closest(".reply-container").find(".reply-comment-text").val();
	if(isEmpty(content)){
		layer.msg("评论内容不能为空");
		$(obj).closest(".reply-container").find(".reply-comment-text").focus();
		return;
	}
	var params = {table_name: "t_circle_post", table_id: postId, content: content, pid: pid, froms: "web端", t: Math.random()};
	doAddComment(params);
}

/**
 * 添加转发
 * @param obj
 */
function addTransmit(obj){
	var addTransmitObj = $("#comment").find('[name="add-comment"]');
	if(isEmpty(addTransmitObj.val())){
		addTransmitObj.focus();
		layer.msg("转发内容不能为空");
		return;
	}
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	 var params = {froms: 'web网页端'};
	 params.content = addTransmitObj.val();
	 params.title = "转发帖子";
	 $.ajax({
		type : "post",
		data: params,
		url : '/cc/'+ circleId+ '/post/'+ postId +'/transmit',
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
 * 添加评论
 * @param obj
 */
function addComment(obj){
	var addCommentObj = $("#comment").find('[name="add-comment"]');
	if(isEmpty(addCommentObj.val())){
		addCommentObj.focus();
		layer.msg("评论内容不能为空");
		return;
	}
	var params = {table_name: "t_circle_post", table_id: postId, content: addCommentObj.val(), froms: "web端", t: Math.random()};
	doAddComment(params);
}

/**
 * 获取帖子的评论内容
 * @param params
 */
function doAddComment(params){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "post",
		data : params,
		url : "/cc/"+ circleId +"/post/"+ params.table_id +"/comment",
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg("评论成功,1秒钟后自动刷新");
				setTimeout("window.location.reload();", 1000);
			}else{
				layer.msg("添加评论失败，"+data.message);
			}
		},
		error : function() {
			layer.close(loadi);
			layer.msg("网络请求失败");
		}
	});
}

function dd(){
	
}