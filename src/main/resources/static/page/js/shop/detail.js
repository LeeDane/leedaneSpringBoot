layui.use(['carousel', 'form', 'laypage', "laydate"], function(){
	  var carousel = layui.carousel
	  ,form = layui.form;
	  //图片轮播
	  carousel.render({
	    elem: '#test10'
	    ,width: '100%'
	    ,height: '280px'
	    ,interval: 5000
	  });
	  
	  var laydate = layui.laydate;
	  //日期
	  laydate.render({
	    elem: '#date'
	  });
	  
	  var element = layui.element;
	  //监听Tab切换，以改变地址hash值
	  element.on('tab(docDemoTabBrief)', function(data){
		  console.log(this); //当前Tab标题所在的原始DOM元素
		  console.log(data.index); //得到当前Tab的所在下标
		  
		  
		  switch(data.index){
		  	case 0:
		  		resetParticlesHeight
			  break;
		  	case 1:
			  	//初始化获取评论
				getComments();
		  		break;
		  	case 2:   //大事件
		  		console.log(data.elem); //得到当前的Tab大容器
		  		getBigEvents();
			  break;
		  	case 3:
			  	  //日期
			  	  laydate.render({
			  	    elem: '#date'
			  	  });
			  	// 基于准备好的dom，初始化echarts实例
			  	
			    myChart = echarts.init(document.getElementById('main'));
			    // 使用刚指定的配置项和数据显示图表。
		        myChart.setOption(option);
		  		resetParticlesHeight();
		  		break;
		  	case 4:
		  		resetParticlesHeight();
		  		break;
		  }
		});
	  
	  laypage = layui.laypage;
	  resetParticlesHeight();
});

var $container;
var $commentContainer;
var laypage;
$(function(){
	$bigEventContainer = $(".big-event-container");
	$container = $(".layui-container");
	$container.on("click", ".reply-other-btn", function(){
		$(this).closest(".reply-group").find(".reply-container").toggle("fast");
	});
	
	$commentContainer = $("#comment-list-container");
});


/********************************    搜索     ********************************************/
/**
 * 搜索操作
 * @param obj
 * @param type 商店固定是4，商品固定是5
 */
function searchShop(obj, type){
	var $SearchKey = $("input[name='shop-search-value']");
	if(isEmpty($SearchKey.val())){
		layer.msg("请先输入检索的内容！");
		$SearchKey.focus();
		return;
	}
	window.open('/shop/s?q='+$SearchKey.val() +"&tp=" + type, '_blank');	
}

/**
 * 添加到心愿单的点击操作
 * @param obj
 */
function addWish(obj){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		contentType:"application/json",
		type : "post",
		url : "/shop/product/"+ productId +"/wish",
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg(data.message +"，1秒后自动刷新");
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

/*****************************************       获取大事件         ********************************************************/
var $bigEventContainer;
var bigEvents;
var bigEvent_pageSize = 100;
var bigEvent_currentIndex = 0;
var bigEvent_totalPage = 0;
function getBigEvents(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/shop/product/"+ productId +"/bigEvents?"+ jsonToGetRequestParams(getBigEventsRequestParams()),
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			$bigEventContainer.empty();
			if(data.isSuccess){
				bigEvents = data.message;
				if(bigEvents.length == 0){
					if(bigEvent_currentIndex == 0){
						$bigEventContainer.append('还没有大事件！');
					}else{
						$bigEventContainer.append('已经没有更多的大事件啦，请重新选择！');
					}
					return;
				}
				for(var i = 0; i < bigEvents.length; i++){
					$bigEventContainer.append(buildBigEventRow(i, bigEvents[i]));
				}
			}else{
				ajaxError(data);
			}
			isLoad = false;
			resetParticlesHeight();
		},
		error : function(data) {
			isLoad = false;
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 展示每行大事件的数据行
 * @param index
 * @param bigEvent
 */
function buildBigEventRow(index, bigEvent){
	var html = '<li class="layui-timeline-item">'+
				    '<i class="layui-icon layui-timeline-axis"></i>'+
				    '<div class="layui-timeline-content layui-text">'+
				      '<h3 class="layui-timeline-title">'+ bigEvent.create_time +'</h3>'+
				      '<p>'+ bigEvent.text +'</p>'+
				    '</div>'+
				  '</li>';
	return html;
}

/**
 * 获取大事件请求列表参数
 */
function getBigEventsRequestParams(){
	return {page_size: bigEvent_pageSize, current: bigEvent_currentIndex, total: bigEvent_totalPage, t: Math.random()};
}

/****************************************   评论   ********************************************************/

var comment_pageSize = 10;
var comment_currentIndex = 0;
var comment_totalPage = 0;
var comments;
/**
 * 获取评论请求列表参数
 */
function getCommentsRequestParams(){
	return {format_time: true, current: comment_currentIndex, total: comment_totalPage, table_name: "t_shop_product", table_id: productId, showUserInfo: true, page_size: comment_pageSize, t: Math.random()};
}

/**
 * 获取该商品的评论内容
 * @param bid
 */
function getComments(bid){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		//contentType: "application/x-www-form-urlencoded;charset=UTF-8",
		contentType:"application/json",
		url : "/cm/comments/paging?"+ jsonToGetRequestParams(getCommentsRequestParams()),
		dataType: 'json',
		beforeSend:function(){
			$commentContainer.empty();
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				if(data.message.length == 0){
					if(comment_currentIndex == 0){
						$commentContainer.append("还没有发表过任何评论，请给TA评论吧！");
					}else{
						$commentContainer.append("已经没有更多的评论啦，请重新选择！");
					}
				}else{
					comments = data.message;
					for(var i = 0; i < comments.length; i++){
						buildEachCommentRow(i, comments[i]);
					}
				}
				
				if(comment_currentIndex == 0 && (!comments || comments.length ==0)){
					$("#comment-pager").empty();
				}else{
					//执行一个laypage实例
					 laypage.render({
					    elem: 'comment-pager' //注意，这里的 test1 是 ID，不用加 # 号
					    ,layout: ['prev', 'page', 'next', 'count', 'skip']
					    ,count: data.total //数据总数，从服务端得到
					    ,limit: comment_pageSize
					    , curr: comment_currentIndex + 1
					    ,jump: function(obj, first){
						    //obj包含了当前分页的所有参数，比如：
						    console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
						    console.log(obj.limit); //得到每页显示的条数
						    if(!first){
						    	comment_currentIndex = obj.curr -1;
						    	getComments();
						    }
						  }
					 });
				}
			}else{
				ajaxError(data);
			}
			
			resetParticlesHeight();
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
		var html = '<div class="layui-row">'+
						'<div class="layui-col-md12 layui-col-xs12">'+
							'<div>'+
								'<img alt="" src="'+ comment.user_pic_path +'" style="width: 25px; height: 25px;" />'+
								'&nbsp;&nbsp;<a>'+ comment.account +'</a>&nbsp;&nbsp;<span style="font-size: 11px;">'+ comment.create_time +'</span>';
								if(isNotEmpty(comment.blockquote_content)){
							html += '<blockquote class="layui-elem-quote layui-quote-nm" style="margin-top: 5px; margin-left: 30px;">'+
										comment.blockquote_content +
									  '<br />——'+  comment.blockquote_account + '&nbsp;&nbsp;<span style="font-size: 10px;">' +changeNotNullString(comment.blockquote_time) + '</span>' +
									'</blockquote>';
								}
						html += '<p style="margin-top: 10px; text-indent: 2em;">'+ comment.content +
								'</p>'+
								'<div class="reply-group" style="text-align: right; " >'+
									'<a class="layui-btn layui-btn-normal layui-btn-small reply-other-btn" style="">回复TA</a>'+
									'<div class="reply-container" style="display: none;">'+
										'<textarea placeholder="请输入您想对TA的内容" class="layui-textarea reply-comment-text" style="margin-top: 5px;"></textarea>'+
										'<a class="layui-btn layui-btn-normal layui-btn-small" style="margin-top: 5px;" onclick="commentItem(this, '+ comment.id +', '+ comment.table_id+');">评论</a>	'+				
									'</div>'+
								'</div>'+
								'<hr />'+
							'</div>'+
						'</div>'+
					'</div>';
	
		$commentContainer.append(html);
}

/**
 * 评论别人的评论
 * @param obj
 */
function commentItem(obj, pid, bid){
	var content = $(obj).closest(".reply-container").find(".reply-comment-text").val();
	var params = {table_name: "t_shop_product", table_id: bid, content: content, pid: pid, froms: "web端", t: Math.random()};
	doAddComment(params);
}

/**
 * 对这个商品的添加评论
 * @param obj
 */
function addComment(obj){
	var addCommentObj = $("#comment").find('[name="add-comment"]');
	if(isEmpty(addCommentObj.val())){
		addCommentObj.focus();
		layer.msg("评论内容不能为空");
		return;
	}
	var params = {table_name: "t_shop_product", table_id: productId, content: addCommentObj.val(), froms: "web端", t: Math.random()};
	doAddComment(params);
}

/**
 * 添加对商品的评论内容
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
