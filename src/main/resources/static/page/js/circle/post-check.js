var posts;
var $circleEditModal;
var $tableContainer;

$(function(){
	initPage(".pagination", "getNoCheckList");
	$tableContainer = $(".table tbody");
	
	//帖子标题的点击事件
	$(document).on("click", ".post-detail", function(event){
		event.stopPropagation();//阻止冒泡
		var post = $(this).closest(".row-item").data("post");
		if(post)
			window.open("/cc/"+post.circle_id +"/post/"+ post.id +"/audit", "_self");
	});
	
	//同意帖子的点击事件
	$(document).on("click", ".post-agree", function(event){
		event.stopPropagation();//阻止冒泡
		var post = $(this).closest(".row-item").data("post");
		if(post)
			//询问框
			layer.confirm('您确定将帖子《'+ post.title +'》审核通过吗？', {
			  btn: ['确定','放弃'] //按钮
			}, function(){
				var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
				$.ajax({
					type: 'POST',
					url : "/cc/"+ post.circle_id +"/post/"+ post.id +"/check",
					data: {status: 1},
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
			}, function(){
			  
			});
	});
	
	//不同意帖子的点击事件
	$(document).on("click", ".post-no-agree", function(event){
		event.stopPropagation();//阻止冒泡
		var post = $(this).closest(".row-item").data("post");
		layer.confirm('您要将帖子《'+ post.title +'》审核不通过吗？不通过将删除该帖子并无法恢复，请慎重！', {
			  btn: ['确定','点错了'] //按钮
		}, function(){
			layer.prompt({title: '请输入审核不通过的原因(必填)', formType: 0}, function(pass, promptIndex){
			    var index = layer.load(1, {
			    	shade: [0.1,'#fff'] //0.1透明度的白色背景
			    });
			  	var loadi = layer.load('努力加载中…');
				$.ajax({
					type : "POST",
					url : "/cc/"+ post.circle_id +"/post/"+ post.id +"/check",
					data: {status: 2, reason: pass},
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
		}, function(){
		});
	});
	//获取等待审核的帖子列表
	getNoCheckList();
});

/**
 * 获取等待审核的帖子列表
 * @param bid
 */
function getNoCheckList(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	var params = {page_size: pageSize, current: currentIndex, total: totalPage, t: Math.random()};
	$.ajax({
		url : "/cc/"+ circleId +"/post/nochecks?t="+ Math.random(),
		dataType: 'json',
		beforeSend:function(){
			$tableContainer.find("tr").remove();
			$(".pagination").empty();
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				posts = data.message;
				if(posts.length == 0){
					if(currentIndex == 0){
						$tableContainer.append('<tr><td colspan="7">该圈子已经没有待审核的帖子！</td></tr>');
					}else{
						$tableContainer.append('<tr><td colspan="7">已经没有更多的待审核的帖子啦，请重新选择！</td></tr>');
						pageDivUtil(data.total);
					}
					return;
				}
				for(var i = 0; i < posts.length; i++){
					$tableContainer.append(buildEachPostRow(i, posts[i]));
					$("#row-index-"+i).data("post", posts[i]);
				}
				pageDivUtil(data.total);
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
 * 构建每一行圈子帖子html
 * @param index
 * @param post
 * @returns {String}
 */
function buildEachPostRow(index, post){
	var html = '<tr class="row-item" id="row-index-'+ index +'">'+ 
					'<td width="50"><img alt="" src="'+ (isEmpty(post.user_pic_path) ? '' : post.user_pic_path) +'" width="25" height="25"/></td>'+ 
					'<td width="80" class="cut-text"><a href="javascript:void(0);" onclick="linkToMy('+ post.create_user_id +');">'+ post.account +'</a></td>'+ 
					'<td class="post-detail hand"><a>'+ (isEmpty(post.title) ? '' : post.title) +'</a></td>'+ 
					'<td>'+ (isEmpty(post.digest) ? '' : post.digest) +'</td>'+
					'<td width="100">'+ post.create_time +'</td>'+ 
					'<td width="140">'+
						'<button type="button" class="btn btn-success btn-sm post-agree" style="margin-right: 10px;">通过</button>'+
						'<button type="button" class="btn btn-danger btn-sm post-no-agree" style="margin-right: 10px;">不通过</button>'+
					'</td>'+ 
				'</tr>';
	return html;
}
