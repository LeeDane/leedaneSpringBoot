var last_id = 0;
var first_id = 0;
var method = 'firstloading';
var imgs = [];

var blogs;
//浏览器可视区域页面的高度
var winH = $(window).height(); 
var isLoad = false;
$(function(){
	//layer.msg($('.main_bg').offset().top)
	//getLogin();
	//getScore();
	getWebBackgroud();
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
	    if(!isLoad && height < 0.20){
	    	isLoad = true;
	    	method = 'lowloading';
	    	getMainContentData();
	    }
	}); 
	getMainContentData();
});

function getLogin(){
	var params = {account: 'leedane', password: $.md5("456"), t: Math.random()};
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "post",
		data : params,
		url : getBasePath() +"leedane/user/login.action",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			layer.msg(data);
		},
		error : function() {
			layer.close(loadi);
			layer.msg("网络请求失败");
		}
	});
}

function getScore(){
	var params = {account: 'leedane', password: $.md5("456"), t: Math.random()};
	$.ajax({
		type : "post",
		data : params,
		url : getBasePath() +"leedane/score/paging.action",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
				layer.msg(data);
		},
		error : function() {
			layer.msg("网络请求失败");
		}
	});
}

//获取背景图
function getWebBackgroud(){
	 $.ajax({
			type : "post",
			data : "t="+Math.random(),
			url : getBasePath() +"leedane/webConfig/background.action",
			beforeSend:function(){
			},
			success : function(data) {
				if(data != null)
					$(".main_bg").css('background', 'url("'+data+'")');
				else
					layer.msg("获取背景图片失败");
			},
			error : function() {
				layer.msg("网络请求失败");
			}
		});
}

//获取内容数据
function getMainContentData(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "post",
		data : getMainContentRequestParams(),
		dataType: 'json',  
		url : getBasePath() +"leedane/blog/paging.action",
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data != null && data.isSuccess){
				if(method == 'firstloading')
					$(".container").empty();
				
				if(data.message.length == 0){
					canLoadData = false;
					layer.msg("无更多数据");
					return;
				}
				
				if(method == 'firstloading'){
					blogs = data.message;
					for(var i = 0; i < blogs.length; i++){
						//判断是否有图
						if(data.message[i].has_img)
							$(".container").append(buildHasImgRow(i, blogs[i]));
						else
							$(".container").append(buildNotHasImgRow(i, blogs[i]));
						if(i == 0)
							first_id = blogs[i].id;
						if(i == blogs.length -1)
							last_id = blogs[i].id;
					}
				}else{
					var currentIndex = blogs.length;
					for(var i = 0; i < data.message.length; i++){
						blogs.push(data.message[i]);
						//判断是否有图
						if(data.message[i].has_img)
							$(".container").append(buildHasImgRow(currentIndex + i, data.message[i]));
						else
							$(".container").append(buildNotHasImgRow(currentIndex + i, data.message[i]));
						
						if(i == data.message.length -1)
							last_id = data.message[i].id;
					}
				}
				resetSideHeight();
			}else{
				layer.msg(data.message);
			}
			console.log(data);
			isLoad = false;
		},
		error : function() {
			layer.close(loadi);
			isLoad = false;
			layer.msg("网络请求失败");
		}
	});
}

//构建有图的情况下的html
function buildHasImgRow(index, blog){
	var html ='<div class="row row_'+index+'">'+
			      '<div class="col-lg-3 col-padding-eight">'+
			      	  	'<img width="100%" height="324" class="img-rounded" alt="" src="'+ blog.img_url +'" onClick="showImg('+ index +');">'+
			      '</div>'+
			      '<div class="col-lg-9 col-padding-eight" style="min-height:324px;">'+
						'<div class="panel panel-info has-img-panel-info">'+
							'<div class="panel-heading">'+
								'<div class="page-header">'+
								    '<h1 class="hand" onclick="goToReadFull('+ blog.id+')">'+ blog.title +
								        '<small>'+
								        	
										'</small>'+
								    '</h1>'+
								    '<ol class="breadcrumb">'+
								    	'<li>'+ changeNotNullString(blog.category) +'</li>'+
										'<li class="active">'+ blog.create_time +'</li>'+
									'</ol>';
									if(isNotEmpty(blog.tag)){
										var t = blog.tag;
										var tags = t.split(',');
										for(var i = 0; i < tags.length; i++){
											if(i == 0)
												html += '<span class="label label-default tag" style="font-size: 13px;">'+ tags[i]+'</span>';
												
											if(i == 1)
												html += '<span class="label label-primary tag" style="font-size: 13px;">'+ tags[i]+'</span>';
												
											if(i == 2)
												html += '<span class="label label-success tag" style="font-size: 13px;">'+ tags[i]+'</span>';
										}
									}
									
						html += '</div>'+
							'</div>'+
							'<div class="panel-body hand" onclick="goToReadFull('+ blog.id+')">'+ (blog.digest.length > 100 ? (blog.digest + '...') : blog.digest)+
							'</div>'+
							'<div class="panel-footer">';
								if(isLogin){
									html += '<div class="btn-group dropup">'+
												'<button type="button" class="btn btn-primary dropdown-toggle btn-default" '+
														'data-toggle="dropdown">操作 <span class="caret"></span>'+
												'</button>'+
												'<ul class="dropdown-menu" role="menu">'+
													'<li><a class="attention" href="javascript:void(0);" onclick="attention('+ blog.id+','+ index+ ');">关注</a></li>'+
													'<li><a class="collection" href="javascript:void(0);" onclick="collect('+ blog.id+','+ index+ ');">收藏</a></li>';
										if(isAdmin || loginUserId == blog.create_user_id){
											html += '<li><a class="delete-blog" href="javascript:void(0);" onclick="updateBlog('+ blog.id+','+ index+ ');">编辑</a></li>'+
													'<li class="divider"></li>'+
													'<li><a href="javascript:void(0);" onclick="deleteBlog('+ blog.id+','+ index+ ');">删除</a></li>';
										}else{
											html += '<li><a class="report-blog" href="javascript:void(0);" onclick="reportBlog('+ blog.id+','+ index+ ');">举报</a></li>';
										}
													
										html += '</ul>'+
											'</div>';
								}
								
						html +='<button type="button" class="btn btn-primary btn-default ">'+
									  			'<span class="glyphicon glyphicon-phone"></span> '+ blog.froms +
												'</button>'+
								'<button type="button" class="btn btn-primary btn-default" href="javascript:void(0);" onclick="my('+ blog.create_user_id + ');">'+
									  			'<span class="glyphicon glyphicon-user"></span> '+ blog.account +
												'</button>'+
								/*'<button type="button" class="btn btn-primary" onclick="goToReadFull('+ blog.id+')">阅读全文</button>'+*/
							'</div>'+
						'</div>'+
			      '</div>'+
			   '</div>';
		return html;
}

//构建无图的情况下的html
function buildNotHasImgRow(index, blog){
	var html = '<div class="row row_'+index+'">'+
			      '<div class="col-lg-12 col-padding-eight">'+
		      	  	'<div class="panel panel-info">'+
						'<div class="panel-heading">'+
							'<div class="page-header">'+
							    '<h1 class="hand" onclick="goToReadFull('+ blog.id+')">'+ blog.title +
							        '<small>'+
							        	
									'</small>'+
							    '</h1>'+
							    '<ol class="breadcrumb">'+
							    	'<li>' +changeNotNullString(blog.category)+ '</li>'+
									'<li class="active">'+ blog.create_time +'</li>'+
								'</ol>';
								if(isNotEmpty(blog.tag)){
									var t = blog.tag;
									var tags = t.split(',');
									for(var i = 0; i < tags.length; i++){
										if(i == 0)
											html += '<span class="label label-default tag" style="font-size: 13px;">'+ tags[i]+'</span>';
											
										if(i == 1)
											html += '<span class="label label-primary tag" style="font-size: 13px;">'+ tags[i]+'</span>';
											
										if(i == 2)
											html += '<span class="label label-success tag" style="font-size: 13px;">'+ tags[i]+'</span>';
									}
								}
					html += '</div>'+
						'</div>'+
						'<div class="panel-body hand" onclick="goToReadFull('+ blog.id+')">'+ (blog.digest.length > 100 ? (blog.digest + '...') : blog.digest)+
						'</div>'+
						'<div class="panel-footer">';
							if(isLogin){
								html += '<div class="btn-group dropup">'+
											'<button type="button" class="btn btn-primary dropdown-toggle btn-default" '+
													'data-toggle="dropdown">操作 <span class="caret"></span>'+
											'</button>'+
											'<ul class="dropdown-menu" role="menu">'+
												'<li><a class="attention" href="javascript:void(0);" onclick="attention('+ blog.id+','+ index+ ');">关注</a></li>'+
												'<li><a class="collection" href="javascript:void(0);" onclick="collection('+ blog.id+','+ index+ ');">收藏</a></li>';
									if(isAdmin || loginUserId == blog.create_user_id){
										html += '<li><a class="delete-blog" href="javascript:void(0);" onclick="updateBlog('+ blog.id+','+ index+ ');">编辑</a></li>'+
												'<li class="divider"></li>'+
												'<li><a href="javascript:void(0);" onclick="deleteBlog('+ blog.id+','+ index+ ');">删除</a></li>';
									}else{
										html += '<li><a class="report-blog" href="javascript:void(0);" onclick="reportBlog('+ blog.id+','+ index+ ');">举报</a></li>';
									}
												
									html += '</ul>'+
										'</div>';
							}
							
					html +='<button type="button" class="btn btn-primary btn-default ">'+
								  			'<span class="glyphicon glyphicon-phone"></span> '+ blog.froms +
											'</button>'+
							'<button type="button" class="btn btn-primary btn-default" href="javascript:void(0);" onclick="my('+ blog.create_user_id + ');">'+
								  			'<span class="glyphicon glyphicon-user"></span> '+ blog.account +
											'</button>'+
							/*'<button type="button" class="btn btn-primary" onclick="goToReadFull('+ blog.id+')">阅读全文</button>'+*/
						'</div>'+
					'</div>'+
		      '</div>'+
		   '</div>';
	return html;
}

//获取请求参数
function getMainContentRequestParams(){
	var pageSize = 5;
	if(method != 'firstloading')
		pageSize = 5;
	return {pageSize: pageSize, last_id: last_id, first_id: first_id, method: method, t: Math.random()};
}

//展示图片的链接
function showImg(index){
	var blog = blogs[index];
	var json = {
			  "title": "相册标题", //相册标题
			  "id": 0, //相册id
			  "start": 0, //初始显示的图片序号，默认0
			  "data": [   //相册包含的图片，数组格式
			    {
			      "alt": blog.title,
			      "pid": 0, //图片id
			      "src": blog.img_url, //原图地址
			      "thumb": blog.img_url //缩略图地址
			    }
			  ]
			};
	layer.photos({
	    photos: json
	    ,shift: 1 //0-6的选择，指定弹出图片动画类型，默认随机
	  });
}


/**
 * 关注
 * @param id
 * @param index
 */
function attention(id, index){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "post",
		data : {table_name: "t_blog", table_id: id, t: Math.random()},
		dataType: 'json',  
		url : getBasePath() +"leedane/attention/add.action",
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			layer.msg(data.message);
			if(data.isSuccess){
				$(".row_"+index).find(".attention").text("已关注");
			}
		},
		error : function() {
			layer.close(loadi);
			layer.msg("网络请求失败");
		}
	});
}

/**
 * 收藏
 * @param id
 * @param index
 */
function collection(id, index){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "post",
		data : {table_name: "t_blog", table_id: id, t: Math.random()},
		dataType: 'json',  
		url : getBasePath() +"leedane/collection/add.action",
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			layer.msg(data.message);
			if(data.isSuccess){
				$(".row_"+index).find(".collection").text("已收藏");
			}
		},
		error : function() {
			layer.close(loadi);
			layer.msg("网络请求失败");
		}
	});
}


/**
 * 删除
 * @param id
 * @param index
 */
function deleteBlog(id, index){
	layer.confirm('您要删除该篇文章记录吗？', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type : "post",
			data : {b_id: id, t: Math.random()},
			dataType: 'json',  
			url : getBasePath() +"leedane/blog/deleteBlog.action",
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				layer.msg(data.message);
				if(data.isSuccess){
					$(".row_"+index).remove();
				}
			},
			error : function() {
				layer.close(loadi);
				layer.msg("网络请求失败");
			}
		});
	}, function(){
	});
}
/**
 * 
 * @param id
 * @param index
 * @returns
 */
 function updateBlog(id, index){
	window.open(getBasePath() + "page/publish-blog.jsp?bid="+id, "_self");
 }
 
 /**
  * 举报博客（弹出模态框）
  * @param id
  * @param index
  */
 function reportBlog(id, index){
	 var blogId = parseInt(id);
	 if(blogId < 1){
		 layer.msg("无法获取文章");
		 return;
	 }
	 $("#report-blog").modal('show'); 
	 $("#report-modal-body").find(".report-btn").removeClass('active');
	 $("#report-modal-body").find(".default-report-btn").addClass('active');
	 $("#report-modal-body").find('textarea').val('');
	 $("#report-modal-body").attr("blog-id", id)
 }
 
 /**
  * 执行举报操作
  */
 function doReport(obj){
	 var blogId = $("#report-modal-body").attr("blog-id");
	 if(typeof(blogId) == 'undefined' || parseInt(blogId) < 1){
		 layer.msg("无法获取文章");
		 return;
	 }
	 
	 var textObj = $("#report-modal-body").find('textarea');
	 var reason = textObj.val();
	 if(isEmpty(reason)){
		 layer.msg("请先输入举报原因");
		 textObj.focus();
		 return;
	 }
	 
	 var type = $("#report-modal-body").find(".btn-group label.active").children("input").attr("type-value");
	 var anonymous = $('[name="anonymous"]:checked').val();
	 
	 layer.confirm('您要该篇文章吗？注意：不实的举报将影响到您的信用和积分。', {
		  btn: ['确定','点错了'] //按钮
	 }, function(){
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type : "post",
			data : {table_name: 't_blog', table_id: blogId, type: type, reason: reason, anonymous : anonymous, t: Math.random()},
			dataType: 'json',  
			url : getBasePath() +"leedane/report/add.action",
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				layer.msg(data.message);
				
				if(data.isSuccess)
					$("#report-blog").modal('hide'); 
			},
			error : function() {
				layer.close(loadi);
				layer.msg("网络请求失败");
			}
		});
	}, function(){
	});
 }
 
/**
 * 链接到个人中心
 * @param create_user_id
 */
function my(create_user_id){
	if(isEmpty(create_user_id)){
		layer.msg("该用户不存在，请联系管理员核实");
		return;
	}
	window.open(getBasePath() +"page/my.jsp?uid="+create_user_id, "_blank");
}
