
var last_id = 0;
var first_id = 0;
var maxCommentNumber = 250; //最大的评论数量
var $commentListContainer = null;
var commentPageSize = 0;
layui.use(['layer', 'util'], function(){
	layer = layui.layer;
	util = layui.util;
	if(isEmpty(mid)){
		layer.msg("心情不存在");
		return;
	}
	getDetail();

    $("[data-toggle='tooltip']").tooltip();
	$(".tooltip").css("display", "block");
	$commentListContainer = $("#comment-list-container");
	getComments();
	$container= $(".container");
	$container.on("click", ".reply-other-btn", function(){
		$(this).closest(".list-group").find(".reply-container").toggle("fast");
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
						if(data.success){
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
	    if(height < 0.20 && !isLoad && canLoadData){
	    	isLoad = true;
	    	method = 'lowloading';
	    	getComments();
	    }
	});

	$(".can-comment-number").html(maxCommentNumber);
	$("#comment").find('[name="add-comment"]').attr("placeholder", "评论这篇心情，最多"+ maxCommentNumber +"个文字。");
});
var method = 'firstloading';
var comments;
//浏览器可视区域页面的高度
var winH = $(window).height(); 
var isLoad = false;
var canLoadData = true;
var $container;

/**
 * 获取博客的基本信息
 * @param bid
 */
function getDetail(){
	$.ajax({
		url : "/md/detail?mid="+ mid +"&t="+ Math.random(),
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			if(data.success && data.message.length > 0){
				var mood = data.message[0];
				var moodCreateTime = setTimeAgo(mood.create_time);
				$("#b-account").find("span").html(changeNotNullString(mood.account));
				$("#b-account").find("img").attr("src", changeNotNullString(mood.user_pic_path));
				$("#b-account").attr("onclick", 'linkToMy('+ mood.create_user_id +')');
				$("#b-create-time").html(moodCreateTime);
				$("#b-comment-number").html(mood.comment_number);
				$("#b-transmit-number").html(mood.transmit_number);
				$("#b-zan-number").html(mood.zan_number);
				$("#b-read-number").html(mood.read_number);
				
				if(isNotEmpty(mood.zan_users)){
					var users = mood.zan_users.split(";");
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
					$("#zan-users").html('<div class="zan_user">'+ userStr +'等'+ mood.zan_number +'人觉得很赞</div>');
				}
				
				$(".row-content").html(data.message[0].content);
				if(isNotEmpty(mood.imgs)){
					var imgs = mood.imgs.split(";");
					var $moodMediaContrainer = $("#mood-media-contrainer");

					for(var i = 0; i < imgs.length; i++){
						var html = '';
						if(isVideo(imgs[i])){
							/*html += '<div class="col-lg-4 col-sm-4">';*/
							html += getVideoHtml(imgs[i]);
							/*html += '</div>';*/
						}else if(isAudio(imgs[i])){
							/*html += '<div class="col-lg-9 col-sm-9">';*/
							html += getAudioHtml(imgs[i]);
							/*html += '</div>';*/
						}else if(isImg(imgs[i])){
							html += '<div class="col-lg-4 col-sm-4">'+
									'<img src="'+ imgs[i] +'" width="100%" height="180px" class="img-responsive" onClick="showSingleImg(this);" />'+
									'</div>';
						}else{
							layer.msg(getSupportTypeStr());
						}
						$moodMediaContrainer.append(html);
					}
				}

				//不能评论
				if(!mood.can_comment){
				     $("#comment").find('[name="add-comment"]').attr('placeholder','由于用户设置，无法评论');
				     $("#comment").find('[name="add-comment"]').attr('readonly','readonly');
				     $("#comment").find("button").prop('disabled', true); // 按钮灰掉，且不可点击。
				}
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			ajaxError(data);
		}
	});
}

/**
 * 获取评论请求列表参数
 */
function getCommentsRequestParams(){
	commentPageSize = 15;
	if(method != 'firstloading')
		commentPageSize = 5;
	return {table_name: "t_mood", table_id: mid, showUserInfo: true, pageSize: commentPageSize, last_id: last_id, first_id: first_id, method: method, t: Math.random()};
}

/**
 * 获取博客的评论内容
 * @param bid
 */
function getComments(bid){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		//contentType: "application/x-www-form-urlencoded;charset=UTF-8",
		url : "/cm/comments?"+ jsonToGetRequestParams(getCommentsRequestParams()),
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				if(method == 'firstloading')
					$(".comment-list").remove();
				
				if(data.message.length == 0){
					canLoadData = false;
					var html = '<div class="row comment-list comment-list-padding"><div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">已无更多评论数据</div></div>';
					$commentListContainer.append(html);
					return;
				}
				
				if(method == 'firstloading'){
					comments = data.message;
					for(var i = 0; i < comments.length; i++){
						buildEachCommentRow(i, comments[i]);
						if(i == 0)
							first_id = comments[i].id;
						if(i == comments.length -1)
							last_id = comments[i].id;
					}

					if(comments.length < commentPageSize){
					    canLoadData = false;
                        var html = '<div class="row comment-list comment-list-padding"><div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">已无更多评论数据</div></div>';
                        $commentListContainer.append(html);
					}
                    return;
				}else{
					var currentIndex = comments.length;
					for(var i = 0; i < data.message.length; i++){
						comments.push(data.message[i]);
						buildEachCommentRow(currentIndex + i, data.message[i]);
						
						if(i == data.message.length -1)
							last_id = data.message[i].id;
					}
				}
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
 * 构建每一行评论html
 * @param comment
 * @param index
 */
function buildEachCommentRow(index, comment){
		var html = '<div class="row comment-list comment-list-padding" data-id="'+ comment.id+'" create-user-id="'+ comment.create_user_id+'">'+
			   			'<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2">'+
							'<img src="'+ changeNotNullString(comment.user_pic_path) +'" width="40" height="40" class="img-rounded hand center-block">'+
						'</div>'+
						'<div class="col-lg-11 col-md-11 col-sm-10 col-xs-10">'+
					       	'<div class="list-group">'+
						       		'<div class="list-group-item comment-list-item active">'+
						       			'<a href="JavaScript:void(0);" onclick="linkToMy('+ comment.create_user_id +')" target="_blank" class="marginRight">'+ changeNotNullString(comment.account)+'</a>'+
						       			'<span class="marginRight publish-time">'+ setTimeAgo(comment.create_time) +'</span>'+
						       			'<span class="marginRight publish-time">来自:'+ changeNotNullString(comment.froms) +'</span>'+
						       		'</div>';
							html += '<div class="list-group-item comment-list-item">'+
										'<div class="row">';

						            html += '<div class="col-lg-12 comment-list-item-main">'+ changeNotNullString(comment.content) +'</div>';
                                    html += blockquote(comment);
									/*if(isNotEmpty(comment.blockquote_content)){
								    html += '<div class="col-lg-12">'+
											    '<blockquote>'+ comment.blockquote_content;
												if(isNotEmpty(comment.blockquote_account)){
											html += '<small><cite>'+ comment.blockquote_account +'</cite>&nbsp;&nbsp;'+ setTimeAgo(comment.blockquote_time) +'</small>';
												}
										html +='</blockquote>'+
											'</div>';
									}
									html += '<div class="col-lg-12">'+ changeNotNullString(comment.content) +'</div>'+*/
								html +=	'</div>'+
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
									    		'<div class="row to-comment-contrainer">'+
									    			'<div class="col-lg-12">'+
									    				'<form class="form-signin" role="form">'+
										    			     '<fieldset>'+

										    				     '<textarea class="form-control reply-comment-text" onkeyup="updateNumber(this);" placeholder="回复TA，最多'+ maxCommentNumber +'个文字。"></textarea>'+
										    			     '</fieldset>'+
										    			 '</form>'+
									    			'</div>'+
									    			'<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12 text-align-right" style="margin-top: 10px;">'+
									    			    '<span style="font-family: \'微软雅黑\'; font-size: 0.8em;">您还可以输入<font class="can-comment-number" color="red">'+ maxCommentNumber +'</font>个文字</span>'+
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
	
	$commentListContainer.append(html);
}

/**
 * 评论别人的评论
 * @param obj
 */
function commentItem(obj, pid, mid){
    var $content = $(obj).closest(".reply-container").find(".reply-comment-text");
	var content = $content.val();
	if(isEmpty(content)){
        $content.focus();
        layer.msg("评论内容不能为空");
        return;
    }

    if(content.length > maxCommentNumber){
        $content.focus();
        layer.msg("评论字数不能超过"+ maxCommentNumber +"个");
        return;
    }
	var params = {table_name: "t_mood", table_id: mid, content: content, pid: pid, froms: "web端", t: Math.random()};
	doAddComment(params);
}

/**
**对评论输入字数的监听
*/
function updateNumber(obj){
    var addCommentObj = $(obj);
    if(!isEmpty(addCommentObj.val())){
        var left = maxCommentNumber - addCommentObj.val().length;
        addCommentObj.closest(".to-comment-contrainer").find(".can-comment-number").html(left);
        if(left >= 0){
            $(obj).closest("div").removeClass("has-error");
        }else{
            $(obj).closest("div").addClass("has-error");
        }
    }else{
        $("#can-comment-number").html(maxCommentNumber);
        $(obj).closest("div").removeClass("has-error");
    }
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

    if(addCommentObj.val().length > maxCommentNumber){
        addCommentObj.focus();
        layer.msg("评论字数不能超过"+ maxCommentNumber +"个");
        return;
    }
	var params = {table_name: "t_mood", table_id: mid, content: addCommentObj.val(), froms: "web端", t: Math.random()};
	doAddComment(params);
}

/**
 * 获取博客的评论内容
 * @param bid
 */
function doAddComment(params){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "post",
		data : params,
		url : "/cm/comment",
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
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