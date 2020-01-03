layui.use(['layer'], function(){
	layer = layui.layer;
	winH = $(window).height();
	//默认查询操作
	queryNoChecks();
	
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
	    if(!isLoad && height < 0.20 && canLoadData && !isLoad){
	    	isLoad = true;
	    	method = 'lowloading';
	    	queryNoChecks();
	    }
	});
});
var blogs;
var last_id = 0;
var first_id = 0;
var method = 'firstloading';
//浏览器可视区域页面的高度
var winH; 
var canLoadData = true;
var isLoad = false;

/**
 * 获取请求列表
 */
function getQueryPagingParams(){
	var pageSize = 15;
	if(method != 'firstloading')
		pageSize = 10;
	return {pageSize: pageSize, last_id: last_id, first_id: first_id, method: method, t: Math.random()};
}

/**
* 查询
*/
function queryNoChecks(params){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/bg/noChecks?"+ jsonToGetRequestParams(getQueryPagingParams()),
		dataType: 'json', 
		beforeSend:function(){
		
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				if(data.message.length == 0){
					canLoadData = false;
					layer.msg("无更多数据");
					return;
				}
					
				if(method == 'firstloading'){
					//清空原来的数据
					$(".each-row").remove();
					blogs = data.message;
					for(var i = 0; i < blogs.length; i++){
						buildEachRow(blogs[i], i);
						if(i == 0)
							first_id = blogs[i].id;
						if(i == blogs.length -1)
							last_id = blogs[i].id;
					}
				}else{
					var currentIndex = blogs.length;
					for(var i = 0; i < data.message.length; i++){
						blogs.push(data.message[i]);
						buildEachRow(data.message[i], currentIndex);
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
			layer.close(loadi);
			isLoad = false;
			ajaxError(data);
		}
	});
}

function buildEachRow(blog, index){
	var html = '<tr class="each-row" height="50">'+
					'<td>'+ (index + 1) +'</td>'+
					'<td>'+ changeNotNullString(blog.title) +'</td>'+
					'<td><a href="/dt/'+ blog.id +'" target="_blank">'+ changeNotNullString(blog.digest) +'</a></td>'+
					'<td><a href="/my/'+blog.create_user_id+'" target="_blank">'+ changeNotNullString(blog.account) +'<a/></td>'+
					'<td>'+ changeNotNullString(blog.create_time) +'</td>'+
					'<th width="150" class="text-align-center"><button type="button" class="btn btn-primary" onclick="showCheckDialog(this, '+ index +');">立即审核</button></th>'+
				'</tr>';
	$(".main-table tbody").append(html);
	
}
/**
* 展示审核弹出层
*/
function showCheckDialog(obj, index){
	var blog = blogs[index];
	$("#check-blog").find(".modal-body-check label:first-child").addClass("active");
	$("#check-blog").find(".modal-body-check label:eq(1)").removeClass("active");
	$("#check-blog").find('.reason-group').addClass("hide");
	$("#check-blog").attr("data-id", blog.id);
	$("#check-blog").modal("show");
	$("#check-blog").find(".check-btn").on("click", function(){
		if($(this).find("input").attr("id") == "no-agree"){
			$("#check-blog").find(".reason-group").removeClass("hide");
		}else{
			$("#check-blog").find(".reason-group").addClass("hide");
		}
	});
}

/**
**	审核
*/
function check(obj){
	var blogId = $("#check-blog").attr("data-id");
	var agree = false;
	if(isEmpty(blogId)){
		layer.msg("页面参数有误，请重新刷新！");
		return;
	}
	
	var reason = "";
	if($("#check-blog").find("#no-agree").parent("label").hasClass("active")){
		if(isEmpty($("#check-blog").find('[name="reason"]').val())){
			layer.msg("请输入不通过原因！");
			$("#check-blog").find('[name="reason"]').focus();
			return;
		}else{
			reason = $("#check-blog").find('[name="reason"]').val();
			agree = false;
		}
	}else{
		agree = true;
	}
	
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "put",
		data : {blog_id: blogId, reason: reason, agree: agree, t: Math.random()},
		url : "/bg/check",
		dataType: 'json', 
		beforeSend:function(){
		
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				layer.msg(data.message);
				$("#check-blog").modal("hide");
				layer.msg(data.message +",1秒钟后自动刷新");
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