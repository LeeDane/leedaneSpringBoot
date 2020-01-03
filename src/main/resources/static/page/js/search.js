layui.use(['layer', 'laypage', 'util'], function(){
   layer = layui.layer;
   laypage = layui.laypage;
   util = layui.util;
  $(".common-search").remove();
  	searchKey = getURLParam(decodeURI(window.location.href), "q");
  	if(isEmpty(searchKey)){
  		layer.msg("获取不到您要检索的关键字！");
//  		return;
  	}

  	$("#common-search-text").val(searchKey);

  	doSearch();

    $ContentContainer = $("#content-contrainer");
	//获取类型
	$("#notification-tabs").find("li").on("click", function(index){
		$("#notification-tabs").find("li").removeClass("active");
		$(this).addClass("active");
		type = parseInt($(this).attr("data-type"));
		currentIndex = 0;
		doSearch();
	});

	$(".senior-condition-btn").click(function(){
        $(".senior-condition").toggle("fast");
    });
});
var type = 1; //查询类型, 0表示博客
var accurate = 0;//0表示模糊查询
var searchKey;
var $ContentContainer;
var startDate = '';
var endDate = "";
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

	//获取地址栏的信息
    var url = window.location.href;
//    window.location.href = url = url.replace("q="+ searchKey, "q="+ searchText);
	searchKey = searchText;
	currentIndex = 0;
	doSearch();
}


function getRequestParams(){
	//return {pageSize: pageSize, last_id: last_id, first_id: first_id, method: method, toUserId: uid, t: Math.random()};
	startDate = $('[name="startTime"]').val();
    endDate = $('[name="endTime"]').val();
	return {page_size: pageSize, current: currentIndex, total: totalPage, keyword: searchKey, type: type, desc: true, accurate: accurate, startDate: startDate, endDate: endDate , t: Math.random()};
}

/**
* 执行搜索操作
*/
function doSearch(){
    var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可

    $("#notification-tabs li").find(".badge").remove();
	$.ajax({
		type : "get",
		url : "/s/es?" +jsonToGetRequestParams(getRequestParams()),
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
		    layer.close(loadi);
			if(data.success && isNotEmpty(data.message)){
				$("#search-need-time").text(data.consumeTime);

				var lists = data.message[0];
				$ContentContainer.empty();
				var hits = lists.hits;
				$("#notification-tabs .active").find("a").append('<span class="badge pull-right">'+ lists.total +'</span>');
				if(hits.length == 0){
				    if(currentIndex == 0){
                        $ContentContainer.append('<div class="col-lg-12">没有检索到数据，请换个关键词试试！未登录的用户可以登录后再来检索。</div>');
                    }else{
                        $ContentContainer.append('<div class="col-lg-12">已经没有更多的数据啦！</div>');
                        /*pageDivUtil(data.total);*/
                    }
                    return;
				}else{
				    if(type == 1){
                        buildBlogs(hits);
                    }

                    if(type == 2){
                        buildMoods(hits);
                    }

                     if(type == 3){
                        buildUsers(hits);
                    }

                    if(type == 6){
                        buildLogs(hits);
                    }

                    scrollToPageTop(1000);
                    //执行一个laypage实例
                     laypage.render({
                        elem: 'item-pager' //注意，这里的 test1 是 ID，不用加 # 号
                        ,layout: ['prev', 'page', 'next', 'count', 'skip']
                        ,count: lists.total //数据总数，从服务端得到
                        ,limit: pageSize
                        ,theme: '#337ab7'
                        , curr: currentIndex + 1
                        ,jump: function(obj, first){
                            //obj包含了当前分页的所有参数，比如：
                            console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                            console.log(obj.limit); //得到每页显示的条数
                            if(!first){
                                currentIndex = obj.curr -1;
                                doSearch();
                            }
                          }
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
}

/**
 * 创建展示用户结果
 * @param hits
 */
function buildUsers(hits){
	for(var i = 0 ; i < hits.length; i++){
		$ContentContainer.append(buildUserEach(hits[i]));
	}
}

/**
 * 创建每个用户的展示结果
 * @param user
 * @returns {String}
 */
function buildUserEach(user){
    var lastRequestTime = '';
    if(isNotEmpty(user.last_request_time)){
        lastRequestTime = setTimeAgo(user.last_request_time);
    }
	var html = '<div class="col-lg-12" id="message-list-content">'+
                    '<div class="row notification-list notification-list-padding" data-id="">'+
                        '<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2" style="text-align: center;">'+
                            '<img src="'+ user.user_pic_path +'" width="60" height="60" class="img-rounded" />'+
                        '</div>'+
                        '<div class="col-lg-11 col-md-11 col-sm-10 col-xs-10">'+
                            '<div class="list-group">'+
                                '<div class="list-group-item notification-list-item active">'+
                                    '<a href="JavaScript:void(0);" onclick="linkToMy('+ user.id +')" target="_blank" class="marginRight">'+ changeNotNullString(user.account) +'</a>';
                                  if(user.is_admin)
                                    html += '<span class="badge">管理员</span>';
                html +=  '</div>'+
                                '<div class="list-group-item notification-list-item">'+
                                    '<table class="table table-hover">'+
                                        '<caption>基本信息</caption>'+
                                        '<tbody>'+
                                        '<tr>'+
                                            '<td>性别</td>'+
                                            '<td>'+ changeNotNullString(user.sex) +'</td>'+
                                        '</tr>'+
                                         '<tr>'+
                                            '<td>中文名</td>'+
                                            '<td>'+ changeNotNullString(user.china_name) +'</td>'+
                                        '</tr>'+
                                        '<tr>'+
                                            '<td>出生日期</td>'+
                                            '<td>'+ changeNotNullString(user.birth_day) +'</td>'+
                                        '</tr>'+
                                        '<tr>'+
                                            '<td>qq</td>'+
                                            '<td>'+ changeNotNullString(user.qq) +'</td>'+
                                        '</tr>'+
                                        '<tr>'+
                                            '<td>手机号码</td>'+
                                            '<td>'+ changeNotNullString(user.mobile_phone) +'</td>'+
                                        '</tr>'+
                                        '<tr>'+
                                            '<td>邮箱</td>'+
                                            '<td>'+ changeNotNullString(user.email) +'</td>'+
                                        '</tr>'+
                                        '<tr>'+
                                            '<td>学历</td>'+
                                            '<td>'+ changeNotNullString(user.education_background) +'</td>'+
                                        '</tr>'+
                                        '<tr>'+
                                            '<td>注册时间</td>'+
                                            '<td>'+ changeNotNullString(user.register_time) +'</td>'+
                                        '</tr>'+
                                        '<tr>'+
                                            '<td>简介</td>'+
                                            '<td>'+ changeNotNullString(user.personal_introduction) +'</td>'+
                                        '</tr>'+
                                        '<tr>'+
                                            '<td>最后请求时间</td>'+
                                            '<td>'+ lastRequestTime +'</td>'+
                                        '</tr>'+
                                        '</tbody>'+
                                    '</table>'+
                                '</div>'+
                            '</div>'+
                        '</div>'+
                    '</div>'+
                '</div>';
	return html;
}

/**
 * 创建展示文章结果
 * @param hits
 */
function buildBlogs(hits){
	for(var i = 0 ; i < hits.length; i++){
		$ContentContainer.append(buildBlogEach(hits[i]));
	}
}

/**
 * 创建每篇文章的展示结果
 * @param blog
 * @returns {String}
 */
function buildBlogEach(blog){
    var modifyTime = setTimeAgo(blog.modify_time);
	var html = '<div class="col-lg-12" id="message-list-content">'+
                    '<div class="row notification-list notification-list-padding" data-id="">'+
                        '<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2" style="text-align: center;">'+
                            '<img src="'+ blog.user_pic_path +'" width="40" height="40" class="img-rounded" />'+
                        '</div>'+
                        '<div class="col-lg-11 col-md-11 col-sm-10 col-xs-10">'+
                            '<div class="list-group">'+
                                '<div class="list-group-item notification-list-item active">'+
                                    '<a href="JavaScript:void(0);" onclick="linkToMy('+ blog.create_user_id +')" target="_blank" class="marginRight">'+ blog.account +'</a>'+
                                    '<span class="marginRight publish-time">发布于: '+ modifyTime +'</span>'+
                                '</div>'+
                                '<div class="list-group-item notification-list-item">'+
                                    '<div class="row">'+
                                        '<div class="col-lg-12"><h4>'+ blog.title +'</h4></div>'+
                                        '<div class="col-lg-12 hand" onclick="linkToTable(122)">'+
                                            '<blockquote>'+
                                                    '<small>'+ blog.digest +'</small>'+
                                            '</blockquote>'+
                                        '</div>'+
                                    '</div>'+
                                '</div>';
                                if(blog.has_img){
                                     html +=  '<div class="list-group-item notification-list-item"><div class="row" style="margin-top: 5px;"><div class="col-lg-12 col-sm-12"><font color="#ff0000" size="4">有附件,请打开详情查看</font></div></div></div>';
                                 }
                         html +=
                                '<div class="list-group-item notification-list-item">'+
                                    '<div class="row">'+
                                        '<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12 text-align-right">'+
                                            '<button class="btn btn-sm btn-primary pull-right tag-read-btn" onclick="goToReadFull('+ blog.id +')" style="width: 80px;" type="button">查看</button>'+
                                        '</div>'+
                                    '</div>'+
                                '</div>'+
                            '</div>'+
                        '</div>'+
                    '</div>'+
                '</div>';
	return html;
}

/**
 * 创建展示心情结果
 * @param hits
 */
function buildMoods(hits){
	for(var i = 0 ; i < hits.length; i++){
		$ContentContainer.append(buildMoodEach(i, hits[i]));
	}
}

/**
 * 创建每篇心情的展示结果
 * @param index
 * @param mood
 * @returns {String}
 */
function buildMoodEach(index, mood){
    var modifyTime = setTimeAgo(mood.modify_time);
	var html = '<div class="col-lg-12" id="message-list-content">'+
                    '<div class="row notification-list notification-list-padding" data-id="">'+
                        '<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2" style="text-align: center;">'+
                            '<img src="'+ mood.user_pic_path +'" width="40" height="40" class="img-rounded" />'+
                        '</div>'+
                        '<div class="col-lg-11 col-md-11 col-sm-10 col-xs-10">'+
                            '<div class="list-group">'+
                                '<div class="list-group-item notification-list-item active">'+
                                    '<a href="JavaScript:void(0);" onclick="linkToMy('+ mood.create_user_id +')" target="_blank" class="marginRight">'+ changeNotNullString(mood.account) +'</a>'+
                                    '<span class="marginRight publish-time">发布于: '+ modifyTime +'</span>'+
                                '</div>'+
                                '<div class="list-group-item notification-list-item">'+
                                    '<div class="row">'+
                                        '<div class="col-lg-12"><h4>'+ changeNotNullString(mood.content) +'</h4></div>'+
                                    '</div>'+
                                '</div>';
                                   if(mood.has_img){
                                         html +=  '<div class="list-group-item notification-list-item"><div class="row" style="margin-top: 5px;"><div class="col-lg-12 col-sm-12"><font color="#ff0000" size="4">有附件,请打开详情查看</font></div></div></div>';
                                        /*var imgs = mood.imgs.split(";");
                                        for(var i = 0; i < imgs.length; i++){
                                            html +=  '<div class="col-lg-4 col-sm-4">'+
                                                            '<img src="'+ imgs[i] +'" width="100%" height="180px" class="img-rounded" onclick="showImg(0, 0);" />'+
                                                        '</div>';
                                        }*/
                                   }
                            html +=
                                '<div class="list-group-item notification-list-item">'+
                                    '<div class="row">'+
                                        '<div class="col-lg-12 col-sm-12 col-md-12 col-xs-12 text-align-right">'+
                                            '<button class="btn btn-sm btn-primary pull-right tag-read-btn" onclick="goToReadMoodFull('+ mood.id + ',' + mood.create_user_id +')" style="width: 80px;" type="button">查看</button>'+
                                        '</div>'+
                                    '</div>'+
                                '</div>'+
                            '</div>'+
                        '</div>'+
                    '</div>'+
                '</div>';
				
	return html;
}

/**
 * 创建展示日志结果
 * @param hits
 */
function buildLogs(hits){
	for(var i = 0 ; i < hits.length; i++){
		$ContentContainer.append(buildLogEach(hits[i]));
	}
}

/**
 * 创建每条日志的展示结果
 * @param blog
 * @returns {String}
 */
function buildLogEach(log){
    var modifyTime = setTimeAgo(log.modify_time);
	var html = '<div class="col-lg-12" id="message-list-content">'+
                    '<div class="row notification-list notification-list-padding" data-id="">'+
                        '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">'+
                            '<div class="list-group">'+
                                '<div class="list-group-item notification-list-item active">'+
                                    '<a href="JavaScript:void(0);" onclick="linkToMy('+ log.create_user_id +')" target="_blank" class="marginRight">'+ changeNotNullString(log.account) +'</a>'+
                                    '<span class="marginRight publish-time">操作时间: '+ modifyTime +'</span>'+
                                    '<span class="publish-time">操作类型：'+ log.operate_type +'</span>'+
                                '</div>'+
                                '<div class="list-group-item notification-list-item">'+
                                    '<div class="row">'+
                                        '<div class="col-lg-12"><h4>'+ log.subject +'</h4></div>'+
                                        '<div class="col-lg-12 hand" onclick="linkToTable(122)">'+
                                            '<blockquote>'+
                                                '<span>ip: '+ changeNotNullString(log.ip) +'</span>'+
                                                '<span>&nbsp;&nbsp;来自: '+ changeNotNullString(log.location) +'</span>'+
                                                '<small>浏览器信息： '+ changeNotNullString(log.browser) +'</small>'+
                                            '</blockquote>'+
                                        '</div>'+
                                    '</div>'+
                                '</div>'+
                            '</div>'+
                        '</div>'+
                    '</div>'+
                '</div>';
	return html;
}