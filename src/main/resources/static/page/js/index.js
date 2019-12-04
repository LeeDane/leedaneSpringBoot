var $blogContainer;
layui.use(['layer', 'laypage'], function(){
	layer = layui.layer;
	laypage = layui.laypage;
	$("[data-toggle='tooltip']").tooltip();
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-blog").addClass("active");
    $blogContainer = $("#blog-container");
	getWebBackgroud();
	getMainContentData();
	getCarouselImgs();//获取推荐的轮播图信息

	$("#to-page-top").on("click", function(){
        scrollToPageTop(1000);
    });
});
var imgs = [];
var blogs;
//浏览器可视区域页面的高度
var winH = $(window).height();
var isLoad = false;
//获取背景图
function getWebBackgroud(){
	 $.ajax({
			data : "t="+Math.random(),
			url : "wc/background",
			beforeSend:function(){
			},
			success : function(data) {
				if(data != null && data.isSuccess){
					$(".main_bg").css('background', 'url("'+data.message+'")');
				}else
					ajaxError(data);
			},
			error : function(data) {
				ajaxError(data);
			}
		});
}

/**
 * 获取内容数据
 */
function getMainContentData(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		dataType: 'json',
		url : "/bg/blogs/paging?" +jsonToGetRequestParams(getMainContentRequestParams()),
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			$blogContainer.empty();
			if(data != null && data.isSuccess){
				/*if(method == 'firstloading')
					$blogContainer.find(".row-list").remove();*/
				if(data.message.length == 0){
					canLoadData = false;
					$blogContainer.append('<div class="row row-list">没有更多数据</div>');
					return;
				}

                blogs = data.message;
                for(var i = 0; i < blogs.length; i++){
                    //判断是否有图
                    if(data.message[i].has_img)
                        $blogContainer.append(buildHasImgRow(i, blogs[i]));
                    else
                        $blogContainer.append(buildNotHasImgRow(i, blogs[i]));
                }
                scrollToPageTop(1000);
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
                            getMainContentData();
                        }
                      }
                 });
			}else{
				ajaxError(data);
			}
			console.log(data);
			isLoad = false;
		},
		error : function(data) {
			isLoad = false;
			layer.close(loadi);
			ajaxError(data);
		}
	});
}
 
//构建有图的情况下的html
function buildHasImgRow(index, blog){
	var html ='<div class="row row-list row_'+index+'">'+
			      '<div class="col-lg-3 col-padding-eight">'+
			      	  	'<img width="100%" height="324" class="img-rounded" alt="" src="'+ blog.img_url +'" onClick="showImg('+ index +');">'+
			      '</div>'+
			      '<div class="col-lg-9 col-padding-eight" style="min-height:324px;">'+
						'<div class="panel panel-info has-img-panel-info">'+
							'<div class="panel-heading">'+
								'<div class="page-header">'+
								    '<h2 class="hand cut-text" onclick="goToReadFull('+ blog.id+')">'+ blog.title +
								        '<small>'+
								        	
										'</small>'+
								    '</h2>';
								    if(blog.stick > 0 || !blog.can_comment || !blog.can_transmit){
						html +=   '<div style="margin-bottom: 8px;">';
						                 if(blog.stick > 0){
                                        html += '<span class="label label-danger" data-toggle="tooltip" data-placement="right" title="" onmouseover="$(this).tooltip(\'show\')" data-original-title="该文章已置顶">已置顶</span>';
                                        }
                                        if(!blog.can_comment){
                                        html += '<span class="label label-danger" data-toggle="tooltip" data-placement="right" title="" onmouseover="$(this).tooltip(\'show\')" data-original-title="该文章已设置禁止评论">禁止评论</span>';
                                        }

                                        if(!blog.can_transmit){
                                        html += '<span class="label label-danger" data-toggle="tooltip" data-placement="right" title="" onmouseover="$(this).tooltip(\'show\')" data-original-title="该文章已设置禁止转发">禁止转发</span>';
                                        }

                        html +=  '</div>';
								    }
							html += '<ol class="breadcrumb">'+
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
								
						html += '<button type="button" class="btn btn-primary btn-default" href="javascript:void(0);" onclick="my('+ blog.create_user_id + ');">'+
									  			'<span class="glyphicon glyphicon-user"></span> '+ blog.account +
												'</button>'+
								'<button data-toggle="tooltip" title="查看文章的详细信息" onMouseOver="$(this).tooltip(\'show\')" type="button" class="btn btn-primary btn-default" onclick="goToReadFull('+ blog.id+')">'+
									  	'<span class="glyphicon glyphicon-phone"></span> 查看全文'+
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
	var html = '<div class="row row-list row_'+index+'">'+
			      '<div class="col-lg-12 col-padding-eight">'+
		      	  	'<div class="panel panel-info">'+
						'<div class="panel-heading">'+
							'<div class="page-header">'+
							    '<h2 class="hand cut-text" onclick="goToReadFull('+ blog.id+')">'+ blog.title +
							        '<small>'+
							        	
									'</small>'+
							    '</h2>';
							    if(blog.stick > 0 || !blog.can_comment || !blog.can_transmit){
                    html +=   '<div style="margin-bottom: 8px;">';
                                     if(blog.stick > 0){
                                    html += '<span class="label label-danger" data-toggle="tooltip" data-placement="right" title="" onmouseover="$(this).tooltip(\'show\')" data-original-title="该文章已置顶">已置顶</span>';
                                    }
                                    if(!blog.can_comment){
                                    html += '<span class="label label-danger" data-toggle="tooltip" data-placement="right" title="" onmouseover="$(this).tooltip(\'show\')" data-original-title="该文章已设置禁止评论">禁止评论</span>';
                                    }

                                    if(!blog.can_transmit){
                                    html += '<span class="label label-danger" data-toggle="tooltip" data-placement="right" title="" onmouseover="$(this).tooltip(\'show\')" data-original-title="该文章已设置禁止转发">禁止转发</span>';
                                    }

                    html +=  '</div>';
                        }
					    html += '<ol class="breadcrumb">'+
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
							
					html +='<button type="button" class="btn btn-primary btn-default" href="javascript:void(0);" onclick="my('+ blog.create_user_id + ');">'+
								  			'<span class="glyphicon glyphicon-user"></span> '+ blog.account +
											'</button>'+
							'<button data-toggle="tooltip" title="查看文章的详细信息" onMouseOver="$(this).tooltip(\'show\')" type="button" class="btn btn-primary btn-default" onclick="goToReadFull('+ blog.id+')">'+
							  	'<span class="glyphicon glyphicon-phone"></span> 查看全文'+
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
	pageSize = 10;
    return {page_size: pageSize, current: currentIndex, total: totalPage, t: Math.random()};
}

/**
 * 获取轮播图数据
 */
function getCarouselImgs(){
	$.ajax({
		url : "/bg/carouselImgs?num=5&method=recommend&t="+Math.random(),
		beforeSend:function(){
		},
		success : function(data) {
			$("#main-container .carousel-indicators").empty();
			$("#main-container .carousel-inner").empty();
			if(data != null && data.isSuccess){
				if(data.message.length >0){
					for(var i = 0; i < data.message.length; i++){
						var html = '<li data-target="#carousel-example-generic" data-slide-to="0" '+(i == 0 ? 'class="active"': '')+'></li>';
						$("#main-container .carousel-indicators").append(html);
						var inner = '<div class="item '+(i == 0 ? 'active': '')+'">'+
						              	'<img onclick="goToReadFull('+ data.message[i].id +')" class="carousel-img hand" src="'+ changeNotNullString(data.message[i].img_url) +'" alt="'+ changeNotNullString(data.message[i].title) +'"/>'+
					              		'<div class="carousel-caption">'+
					              			'<h1>'+ changeNotNullString(data.message[i].title) +'</h1>'+
					              			'<p>'+ changeNotNullString(data.message[i].digest) +'</p>'+
					              		'</div>'+
						            '</div>';
						$("#main-container .carousel-inner").append(inner);
					}
				}else{
					$("#carousel-example-generic").remove();
				}
			}else{
				$("#carousel-example-generic").remove();
				ajaxError(data);
			}
		},
		error : function(data) {
			ajaxError(data);
		}
	});
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
		url : "/at/attention",
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg(data.message);
				$(".row_"+index).find(".attention").text("已关注");
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
		url : "/cl/collection",
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg(data.message);
				$(".row_"+index).find(".collection").text("已收藏");
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
			type : "delete",
			dataType: 'json',  
			url : "/bg/blog?b_id="+ id,
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					layer.msg(data.message);
					$(".row_"+index).remove();
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
/**
 * 
 * @param id
 * @param index
 * @returns
 */
 function updateBlog(id, index){
	window.open("/pb?bid="+id, "_self");
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
 /*function doReport(obj){
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
 }*/
 
/**
 * 链接到个人中心
 * @param create_user_id
 */
function my(create_user_id){
	if(isEmpty(create_user_id)){
		layer.msg("该用户不存在，请联系管理员核实");
		return;
	}
	linkToMy(create_user_id);
}
