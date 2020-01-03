layui.use(['layer'], function(){
	layer = layui.layer;
	$(".common-search").remove();
	searchKey = getURLParam(decodeURI(window.location.href), "q");
	if(isEmpty(searchKey)){
		layer.msg("获取不到您要检索的关键字！");
		return;
	}
	
	$("#common-search-text").val(searchKey);
	
	doSearch();
});
var moods;
var searchKey;

/**
 * 搜索
 * @param obj
 */
function search(obj){
	var searchText = $("#common-search-text").val();
	if(isEmpty(searchText)){
		layer.msg("请输入您要搜索的内容！");
		$("#common-search-text").focus();
		return;
	}
	searchKey = searchText;
	doSearch();
}

function doSearch(){
	$.ajax({
		type : "get",
		url : "/s/es?keyword="+ searchKey +"&t="+ Math.random(),
		dataType: 'json', 
		beforeSend:function(){
			$("#user-result-show").empty();
			$("#mood-result-show").empty();
			$("#blog-result-show").empty();
			$("#search-user-number").text(0);
			$("#search-mood-number").text(0);
			$("#search-blog-number").text(0);
		},
		success : function(data) {
			if(data.success && isNotEmpty(data.message)){
				$("#search-need-time").text(data.consumeTime);
				
				var blogs = data.message["1"];
				if(isNotEmpty(blogs)){
					$("#search-blog-number").text(blogs.length);
					if(blogs.length > 0)
						buildBlogs(blogs);
				}
				
				var moodArrays = data.message["2"];
				if(isNotEmpty(moodArrays)){
					$("#search-mood-number").text(moodArrays.length);
					if(moodArrays.length > 0){
						moods = moodArrays;
						buildMoods(moodArrays);
					}
				}
				
				var users = data.message["3"];
				if(isNotEmpty(users)){
					$("#search-user-number").text(users.length);
					if(users.length > 0)
						buildUsers(users);
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
 * 创建展示用户结果
 * @param users
 */
function buildUsers(users){
	for(var i = 0 ; i < users.length; i++){
		$("#user-result-show").append(buildUserEach(users[i]));
	}
}

/**
 * 创建每个用户的展示结果
 * @param user
 * @returns {String}
 */
function buildUserEach(user){
	var html = '<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2" onclick="linkToMy('+ user.id+')">';
		if(isNotEmpty(user.user_pic_path)){
			html += '<img class="img-circle center-block" alt="" width="40" height="40" src="'+ user.user_pic_path +'">';
		}else{
			html += '<img class="img-circle center-block" alt="" width="40" height="40" src="">';
		}
		 			
			html += '<div class="user-account cut-text" title="'+ user.account+'"><a href="JavaScript:void(0);">'+ user.account+'</a></div>'+
				'</div>';
	return html;
}

/**
 * 创建展示文章结果
 * @param blogs
 */
function buildBlogs(blogs){
	for(var i = 0 ; i < blogs.length; i++){
		$("#blog-result-show").append(buildBlogEach(blogs[i]));
	}
}

/**
 * 创建每篇文章的展示结果
 * @param blog
 * @returns {String}
 */
function buildBlogEach(blog){
	var html = '<div class="row blog-list-row">'+
				   	'<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2" style="text-align: center;margin-top: 10px;">';
		   	if(isNotEmpty(blog.imgUrl)){
		   		html += '<img width="40" height="40" class="img-circle hand center-block" alt="" src="'+ blog.imgUrl +'"  onclick="showSingleImg(this);"/>';
		   	}else{
		   		html += '<img class="img-circle hand center-block" alt="" src=""/>';
		   	}
						
			html += '</div>'+
					'<div class="col-lg-11 col-md-11 col-sm-10 col-xs-10">'+
						'<div class="row" style="font-family: \'微软雅黑\'; font-size: 15px; margin-top: 10px;">'+
							'<div class="col-lg-12">'+
								'<span class="blog-user-name"><a href="JavaScript:void(0);" onclick="linkToMy('+ blog.createUserId+')" title="'+ changeNotNullString(blog.account) +'">'+ changeNotNullString(blog.account) +'</a></span>   '+
								'<span class="blog-create-time">&nbsp;&nbsp;'+ changeNotNullString(blog.createTime)+'</span>   '+
								'<span class="blog-froms">&nbsp;&nbsp;'+ changeNotNullString(blog.froms) +'</span>'+
							'</div>'+
						'</div>'+
						'<div class="row" style="font-family: \'微软雅黑\'; font-size: 15px;margin-top: 5px; margin-bottom: 5px;">'+
							'<div class="col-lg-12">'+ blog.digest +'......</div>'+
						'</div>'+
						'<div class="row">'+
							'<div class="col-lg-12 col-sm-12">';
							if(isNotEmpty(blog.tag)){
								var t = blog.tag;
								var tags = t.split(',');
								for(var i = 0; i < tags.length; i++){
									if(i == 0)
										html += '<span class="label label-default tag" style="font-size: 13px;">'+ tags[i]+'</span>';
										
									if(i == 1)
										html += '<span class="label label-primary tag" style="font-size: 13px; margin-left: 8px;">'+ tags[i]+'</span>';
										
									if(i == 2)
										html += '<span class="label label-success tag" style="font-size: 13px; margin-left: 8px;">'+ tags[i]+'</span>';
								}
							}
					html +='</div>'+
						'</div>'+
						'<div class="row" style="font-family: \'宋体\'; font-size: 12px;margin-top: 5px; color: gray; margin-bottom: 10px;">'+
							'<div class="col-lg-12">'+
								'<button type="button" class="btn btn-primary btn-sm" onclick="goToReadFull('+ blog.id+')">查看详细</button>'+
							'</div>'+
						'</div>'+
					'</div>'+
				'</div>';
	return html;
}

/**
 * 创建展示心情结果
 * @param moods
 */
function buildMoods(moods){
	for(var i = 0 ; i < moods.length; i++){
		$("#mood-result-show").append(buildMoodEach(i, moods[i]));
	}
}

/**
 * 创建每篇心情的展示结果
 * @param index
 * @param mood
 * @returns {String}
 */
function buildMoodEach(index, mood){
	var html = '<div class="row mood-list-row">'+
			   		'<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2" style="text-align: center;margin-top: 10px;">';
   			if(isNotEmpty(mood.user_pic_path)){
   				html += '<img width="40" height="40" class="img-circle hand" alt="" src="'+ mood.user_pic_path +'"  onclick="showSingleImg(this);"/>';
   			}else{
   				html += '<img width="40" height="40" class="img-circle hand" alt="" src=""/>';
   			}	
			html += '</div>'+
					'<div class="col-lg-11 col-md-11 col-sm-10 col-xs-10">'+
						'<div class="row" style="font-family: \'微软雅黑\'; font-size: 15px; margin-top: 10px;">'+
							'<div class="col-lg-12">'+
								'<span class="mood-user-name"><a href="JavaScript:void(0);" onclick="linkToMy('+ mood.createUserId +')">'+ changeNotNullString(mood.account) +'</a></span>  '+ 
								'<span class="mood-create-time">&nbsp;&nbsp;'+ changeNotNullString(mood.createTime) +'</span>'+
								'<span class="mood-froms">&nbsp;&nbsp;'+ changeNotNullString(mood.froms) +'</span>'+
							'</div>'+
						'</div>'+
						'<div class="row" style="font-family: \'微软雅黑\'; font-size: 17px;margin-top: 5px;">'+
							'<div class="col-lg-12">'+ changeNotNullString(mood.content) +'</div>'+
						'</div>';
						if(isNotEmpty(mood.location)){
							html += '<div class="row" style="font-family: \'宋体\'; font-size: 12px;margin-top: 5px; color: gray; margin-bottom: 10px;">'+
										'<div class="col-lg-12">'+ mood.location +'</div>'+
									'</div>';
						}
				if(mood.hasImg && isNotEmpty(mood.imgs)){
					html += '<div class="row">';
					var imgs = mood.imgs.split(";");
					for(var i = 0; i < imgs.length; i++){
						html += '<div class="col-lg-4 col-sm-4" style="margin-top: 5px;">'+
									'<img width="100%" height="180" class="img-rounded hand" alt="" src="'+ imgs[i] +'"  onclick="showImg('+ index+', '+ i +');"/>'+
								'</div>';
					}
					html += '</div>';
				}
				html += '</div>'+
					'</div>';
				
	return html;
}