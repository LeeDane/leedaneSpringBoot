layui.use(['laypage', 'layer', 'util'], function(){
	  laypage = layui.laypage;
	  layer = layui.layer;
	  util = layui.util;
	  getInit(); //获取初始化数据
	  getPosts();
});
var posts;
var $postListContainer;
/**
 * 当前页面的索引
 */
var currentIndex = 0;

/**
 * 每一页最多的数
 */
var pageSize = 8;

/**
 * 解决动态html无法绑定tooltip和popover
 */
function bindTool(){
	$("[data-toggle='tooltip']").tooltip();
	$(".tooltip").css("display", "block");
	$("[data-toggle='popover']").popover();
}

$(function(){
	 //$("script[src='../static/js/cpu_memory.js']").remove();
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
		var post = $(this).closest(".panel").data("post");
		linkToPostDetail(post.circle_id, post.id);
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
	
	//修改的点击事件
	$(document).on("click", ".post-update", function(event){
		event.stopPropagation();//阻止冒泡
		var postId = $(this).data("id");
		if(isEmpty(postId)){
			layer.msg("该帖子不存在，请联系管理员核实");
			return;
		}
		window.open("/cc/"+circleId+"/write?postId="+postId, "_self");
	});
	
	//加入圈子
	$(".into-circle").click(function(){
		$.ajax({
			url : "/cc/"+ circleId +"/join/check",
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
	
});

/**
 * 获取初始化数据
 * @param
 */
function getInit(){
	$.ajax({
		url : "/cc/"+ circleId +"/init",
		dataType: 'json',
		beforeSend:function(){
			
		},
		success : function(data) {
			if(data.isSuccess){
				$("#todayVisitors").text(data.message.todayVisitors);
				$("#visitors").text(data.message.visitors);
				$("#allContribute").text(data.message.allContribute);
				$("#myContribute").text(data.message.myContribute);
				$(".memberNumber").text(data.message.memberNumber);
				if(isNotEmpty(data.message.admins) && data.message.admins.length > 0){
					for(var i = 0; i < data.message.admins.length; i++)
						$("#admins").append('<a th:onclick="linkToMy('+ data.message.admins[i].member_id +');" href="javascript:void(0);">'+ data.message.admins[i].account +'</a>&nbsp;&nbsp;');
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
 * 获取帖子
 * @param bid
 */
function getPosts(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	var params = {page_size: pageSize, current: currentIndex, t: Math.random()};
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
					}
					return;
				}
				for(var i = 0; i < posts.length; i++){
					$postListContainer.append(buildEachPostRow(i, posts[i]));
					$("#post-list-"+i).data("post", posts[i]);
				}
				$("#postNumbers").text(data.total);
				bindTool();
				//pageDivUtil(data.total);
				
				//执行一个laypage实例
				 laypage.render({
				    elem: 'item-pager' //注意，这里的 test1 是 ID，不用加 # 号
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
					    	getPosts();
					    }
					  }
				 });
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
        var createTime = setTimeAgo(post.create_time);
		var html = '<div class="panel panel-default" id="post-list-'+ index +'">'+
						'<div class="panel-heading">'+
							'<p>'+ post.title +'</p>'+
							'<div>'+
								'<button type="button" class="btn btn-default btn-xs user-img" data="'+ post.create_user_id +'">'+
								  '<img src="'+ post.user_pic_path +'" class="img-circle" style="width: 20px; height: 20px" /> '+ post.account+
								  '</button>&nbsp;&nbsp;'+ '<span class="label label-info tag">'+ (post.role_type == 1 ? '圈主': (post.role_type == 2 ? '管理员': '普通')) +'</span>&nbsp;&nbsp;'+ createTime +
							'</div>';
						if(isNotEmpty(post.tag)){
					html +='<p style="margin-top: 10px;">标签：';
							var tagArray = post.tag.split(",");
							for(var tagIndex = 0; tagIndex < tagArray.length; tagIndex++){
						html += '<span class="post-tag">'+ tagArray[tagIndex] +'</span>';
							}
					html += '</p>';
						}
					html += /*'<div class="pull-right" title="标题" data-container="body" data-toggle="popover" data-content="Popover内容 ">'+
								'<img src="/page/images/more.png" class="" style="width: 20px; height: 20px" />'+
							'</div>'+*/
						'</div>'+
						'<div class="panel-body panel-table-container">';
						if(post.pid > 0){
					html += '<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12">'+
								'<blockquote>'+
									'<div class="cut-text hand"><a '+ (post.blockquote ? 'data-toggle="tooltip" data-placement="left" title="查看该帖子详细信息"': '') +' onclick="linkToPostDetail('+ post.circle_id +','+ post.pid +');">'+ post.blockquote_content +'</a></div>';
									if(post.blockquote){
								html += '<small><cite>'+ post.blockquote_account +'</cite>&nbsp;&nbsp;'+ post.blockquote_time +'</small>';
									}
						html +='</blockquote>'+
							'</div>';
						}
					html +=	'<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12 cut-text2" data-toggle="tooltip" data-placement="left" title="'+ changeNotNullString(post.digest) +'">'+ changeNotNullString(post.digest) +'</div>';
					if(isNotEmpty(post.imgs)){
						var imgArray = post.imgs.split(";");
						for(var imgIndex = 0; imgIndex < imgArray.length; imgIndex++){
							
							if(isVideo(imgArray[imgIndex])){
								html += '<div class="col-lg-4 col-sm-4 col-md-12 col-xs-12 img-container">';
								html += getVideoHtml(imgArray[imgIndex]);
								html += '</div>';
							}else if(isAudio(imgArray[imgIndex])){
								html += '<div class="col-lg-4 col-sm-4 col-md-12 col-xs-12 img-container">';
								html += getAudioHtml(imgArray[imgIndex]);
								html += '</div>';
							}else if(isImg(imgArray[imgIndex])){
								html += '<div class="col-lg-4 col-sm-4 col-md-12 col-xs-12 img-container">'+
										'<img src="'+ imgArray[imgIndex] +'" class="img-responsive post-item-img hand" onClick="showImgDialog('+ imgIndex+', \''+ post.imgs +'\');" />'+
										'</div>';
							}else{
								layer.msg(getSupportTypeStr());
							}
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
						html += '<span class="glyphicon glyphicon-trash post-delete hand" data-toggle="tooltip" data-placement="left" title="删除" aria-hidden="true"></span>&nbsp;&nbsp;';
					}
					
					if(loginUserId == post.create_user_id){
						html += '<span class="glyphicon glyphicon-pencil post-update hand" data-toggle="tooltip" data-placement="left" title="编辑" aria-hidden="true" data-id="'+ post.id +'"></span>&nbsp;&nbsp;';
					}
								
						html += '<span class="glyphicon glyphicon-comment hand post-comment" data-toggle="tooltip" data-placement="left" title="评论" aria-hidden="true"></span>&nbsp;'+ post.comment_number +'&nbsp;&nbsp;'+
								'<span class="glyphicon glyphicon-share-alt hand post-transmit" data-toggle="tooltip" data-placement="left" title="转发" aria-hidden="true"></span>&nbsp;'+ post.transmit_number +'&nbsp;&nbsp;'+
								'<span class="glyphicon glyphicon-thumbs-up hand post-zan" data-toggle="tooltip" data-placement="left" title="很赞" aria-hidden="true"></span>&nbsp;'+ post.zan_number +'&nbsp;&nbsp;'+
								'<span class="glyphicon glyphicon-credit-card hand" data-toggle="tooltip" data-placement="left" title="打赏" aria-hidden="true"></span>&nbsp;'+ 0 +'&nbsp;&nbsp;'+
								'<span class="hand link-to-post-detail" data="'+ post.id +'">查看详情</a>'+
							'</p>'+
						'</div>'+
					'</div>';
	
	return html;
}

/**
 * 展示图片的链接
 * @param index  当前心情的索引
 * @param imgs 当前心情图片的索引
 */
function showImgDialog(index, imgs){
	var json = {
			  "title": "相册标题", //相册标题
			  "id": 0, //相册id
			  "start": index //初始显示的图片序号，默认0
			};
	var datas = new Array();
	var photos = imgs.split(";");
	for(var i = 0; i < photos.length; i++){
		var each = {};
		var path = photos[i];
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

/**
 * 申请加入圈子
 * @param answer
 */
function joinCircle(answer){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type: 'POST',
		data: {cid: circleId, answer: answer},
		url : "/cc/"+ circleId +"/join",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg(data.message + "，1秒后自动刷新");
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
		url : "/cc/"+ circleId +"/leave",
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
		url : "/cc/"+circleId+"/clockIn",
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