layui.use(['layer', 'laypage', 'util'], function(){
	layer = layui.layer;
	laypage = layui.laypage;
	util = layui.util;
	/*initPage(".pagination", "getMessageBoards");*/
	$commentListContainer = $("#comment-list-container");
	$container= $(".container");
	$container.on("click", ".reply-other-btn", function(){
		$(this).closest(".list-group").find(".reply-container").toggle("fast");
	});
	
	$container.on("click", ".delete-other-btn", function(){
		var dataId = $(this).closest(".comment-list").attr("data-id");
		var createUserId = $(this).closest(".comment-list").attr("create-user-id");
		if(dataId > 0 && createUserId > 0){
			layer.confirm('您要删除该留言吗？', {
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
	
	getMessageBoards();  
});
var messageBoards;
var $commentListContainer;
var $container;

/**
 * 获取留言请求列表参数
 */
function getMessageBoardsRequestParams(){
	return {page_size: pageSize, current: currentIndex, total: totalPage, t: Math.random()};
}

/**
 * 获取留言内容
 * @param bid
 */
function getMessageBoards(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/cm/user/"+ uid +"/messageBoards?"+ jsonToGetRequestParams(getMessageBoardsRequestParams()),
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			$commentListContainer.empty();
			if(data.success){
				messageBoards = data.message;
				if(messageBoards.length == 0){
					if(currentIndex == 0){
						$commentListContainer.append('还没有留言，请给TA留言！');
					}else{
						$commentListContainer.append('已经没有更多的留言啦，请重新选择！');
						/*pageDivUtil(data.total);*/
					}
					return;
				}
				for(var i = 0; i < messageBoards.length; i++){
					$commentListContainer.append(buildEachCommentRow(i, messageBoards[i]));
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
					    	getMessageBoards();
					    }
					  }
				 });
				resetParticlesHeight();
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
 * 构建每一行留言html
 * @param comment
 * @param index
 */
function buildEachCommentRow(index, comment){
		var html = '<div class="row comment-list comment-list-padding" data-id="'+ comment.id +'" create-user-id="'+ comment.create_user_id+'">'+
			   			'<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2">'+
							'<img src="'+ changeNotNullString(comment.user_pic_path) +'" width="40" height="40" class="img-rounded">'+
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
									html += '<div class="col-lg-12 comment-list-item-main">'+ changeNotNullString(comment.content) +'</div>';
                                   html += blockquote(comment);
									/*if(isNotEmpty(comment.blockquote_content)){
                                        html += '<div class="col-lg-12">'+
                                                    '<blockquote>'+ comment.blockquote_content;
                                                        if(isNotEmpty(comment.blockquote_account)){
                                                    html += '<small><cite>'+ comment.blockquote_account +'</cite>&nbsp;&nbsp;'+ changeNotNullString(comment.blockquote_time) +'</small>';
                                                        }
                                            html +='</blockquote>'+
                                                '</div>';
                                        }*/

							    html +=	'</div>'+
									'</div>';
								if(isLogin){
							html += '<div class="list-group-item comment-list-item">'+
										'<div class="row">'+
							       				'<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12 text-align-right">'+
							       					 '<button class="btn btn-sm btn-primary pull-right reply-other-btn" style="width: 60px;" type="button">回复TA</button>';
				       					 if(true/*isAdmin || comment.create_user_id == loginUserId*/){
				       						 html += '<button class="btn btn-sm btn-primary pull-right delete-other-btn" style="width: 60px; margin-right: 5px;" type="button">删除</button>';
				       					 }
							       					 
							       		html += '</div>'+
							       		'</div>'+
							       		'<div class="row">'+
								       		'<div class="col-lg-12 reply-container" table-id="'+ comment.table_id+'" table-name="'+ comment.table_name +'" style="display: none;">'+
									    		'<div class="row">'+
									    			'<div class="col-lg-12">'+
									    				'<form class="form-signin" role="form">'+
										    			     '<fieldset>'+
										    				     '<textarea class="form-control reply-comment-text" placeholder="回复TA，最多250个文字。"></textarea>'+
										    			     '</fieldset>'+
										    			 '</form>'+
									    			'</div>'+
									    			'<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12 text-align-right" style="margin-top: 10px;">'+
									    				'<button class="btn btn-sm btn-info pull-right" style="width: 60px;" type="button" onclick="commentItem(this, '+ comment.id +', '+ comment.create_user_id +');">回复</button>'+
									    			'</div>'+
									    		'</div>'+
									    	'</div>'+
								    	'</div>'+
								   '</div>';
								}
					html += '</div>'+
					'</div>'+
			'</div>';
	
	return html;
}

/**
 * 评论别人的留言
 * @param obj
 */
function commentItem(obj, pid){
	var content = $(obj).closest(".reply-container").find(".reply-comment-text").val();
	var params = {table_name: "t_message_board", check: false, table_id: uid, content: content, pid: pid, froms: "web端", t: Math.random()};
	doAddComment(params);
}

/**
 * 添加留言
 * @param obj
 */
function addComment(obj){
	var addCommentObj = $("#comment").find('[name="add-comment"]');
	if(isEmpty(addCommentObj.val())){
		addCommentObj.focus();
		layer.msg("请先说点什么吧");
		return;
	}
	var params = {table_name: "t_message_board", check: false, table_id: uid, content: addCommentObj.val(), froms: "web端", t: Math.random()};
	doAddComment(params);
}

/**
 * 留言内容
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
				layer.msg("留言成功,1秒钟后自动刷新");
				setTimeout("window.location.reload();", 1000);
			}else{
				layer.msg("留言失败，"+data.message);
			}
		},
		error : function() {
			layer.close(loadi);
			layer.msg("网络请求失败");
		}
	});
}
