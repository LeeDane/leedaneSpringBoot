layui.use(['layer', 'laypage', 'util'], function(){
	layer = layui.layer;
	laypage = layui.laypage;
	util = layui.util;
	/*initPage(".pagination", "getMoods");*/
	$cOTItemContainer = $("#comment-or-transmit-item");
	$moodMardownContent = $("#push-mood-text");
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-my").addClass("active");

	$('[data-toggle="tooltip"]').tooltip();

	/*initPage(".pagination", "getMoods", 9);*/
	$moodContainer = $("#mood-container");
	$operateItemsContrainer = $("#operate-item-list");
	
	$(".edit-user-info-btn").on("click", function(){
		//检验手机号码
		var phone = $('[name="mobile_phone"]').val();
		if(isEmpty(phone)){
			layer.msg("请输入手机号码");
			$('[name="mobile_phone"]').focus();
			return;
		}
		
		var json = serializeArrayToJsonObject($(".myForm").serializeArray());
		editUserinfo(json);
	});
		
	$cOTItemContainer.on('scroll',function(){
		var groups = $cOTItemContainer.find(".list-group");
		var totalH = 0;
		groups.each(function(){
			totalH += $(this).height();
		});
		console.log("totalH--->"+ totalH);
	    // div 滚动了
		var pageH = $cOTItemContainer.height(); //页面总高度 
		console.log("pageH"+ pageH);
	    var scrollT = $cOTItemContainer.scrollTop(); //滚动条top 
	    console.log("scrollT"+ scrollT);
	 
	    var height = (pageH-scrollT)/winH;
	  
	    if (!ct_isLoad && totalH - scrollT - pageH < 50 && ct_canLoadData) {
	        // 滚动到底部了
	        console.log('开始执行加载更多');
	        ct_isLoad = true;
	    	ct_method = 'lowloading';
	    	asynchronousLoadData();
	    }
	});  
  
  var clipboard = new Clipboard('.do-copy-btn');
	  
  clipboard.on('success', function(e) {
      //console.info('Action:', e.action);
      //console.info('Text:', e.text);
      //console.info('Trigger:', e.trigger);
      //console.info('Trigger:', e.trigger.previousSibling.id);
      //$("#"+ e.trigger.previousSibling.id).text(e.text);
	  $operateItemsContrainer.modal("hide");
	  layer.msg("文字已复制成功！");
      layer.tips('此文字已复制成功', "#"+ e.trigger.previousSibling.id, {time: 1000});
      //layer.msg("链接已复制成功");
      //e.clearSelection();
  });

  clipboard.on('error', function(e) {
      //console.error('Action:', e.action);
      //console.error('Trigger:', e.trigger);
	  layer.msg("文字复制失败！");
	  layer.tips('此文字复制失败', "#"+ e.trigger.previousSibling.id, {time: 1000});
  });

  loadUserInfo();
  getMoods();
  getMessageBoards();//获取留言板列表
  getVisitors(); //获取访客列表
  getAttentions();//获取关注列表
  getFans();//获取粉丝列表
});
var userinfo;
var moods = [];
var isLoad = false;
//浏览器可视区域页面的高度
var winH = $(window).height(); 
var monthArray = new Array();
var $moodContainer;
var $moodMardownContent;
var $commentOrTransmitItemContainer;
var $operateItemsContrainer ;

/**
 * 获取留言板列表
 */
function getMessageBoards(){
	$.ajax({
		url : "/cm/user/"+ uid+ "/messageBoards?page_size=15&t=" + Math.random(),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			$("#message-boards tr").remove();
			if(data.isSuccess){
				for(var i = 0; i < data.message.length; i++){
					var board = data.message[i];
					var html = '<div>'+
									'<img src="'+ changeNotNullString(board.user_pic_path) +'" width="30" height="30" class="img-rounded img-circle" onclick="linkToMy('+ board.create_user_id+');"/>'+
									'<span style="word-break:break-all; margin-left: 10px;" title="'+ board.content+'">'+ board.content+'</span>'+
								'</div><hr style="filter: alpha(opacity=100,finishopacity=0,style=2)" width="90%" color="#E6E6FA" size=1 />';
					$("#message-boards").append(html);
				}
			}else{
				ajaxError(data);
			}
			$("#message-boards").append('<div style="text-align: right;"><a type="button" class="btn btn-primary btn-xs" href="/user/'+uid+'/board"><span class="glyphicon glyphicon-blackboard"></span> 留言板</a></div>');
		},
		error : function(data) {
			ajaxError(data);
		}
	});
}

/**
 * 获取访客列表
 */
function getVisitors(){
	$.ajax({
		url : "/vt/user/"+ uid+ "/visitors?table_name=t_mood&table_id="+uid+"&page_size=5&t=" + Math.random(),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			$("#visitors-list li").remove();
			if(data.isSuccess){
				if(data.message.length == 0){
					$("#visitors-list").append('<li class="list-group-item">暂无访客数据</li>');
					return;
				}
					
				for(var i = 0; i < data.message.length; i++){
					var visitor = data.message[i];
					var liHtml = '<div class="row" onclick="linkToMy('+ visitor.create_user_id+');">';
					if(visitor.is_friend){
						liHtml += '<span class="badge">好友</span>';
					}
					if(visitor.is_fan){
						liHtml += '<span class="badge">粉丝</span>';
					}
					 	liHtml +='<img class="img-circle" style="margin-left: 20px;" alt="" width="30" height="30" src="'+changeNotNullString(visitor.user_pic_path)+'"/><span>&nbsp;&nbsp;&nbsp;&nbsp;'+ changeNotNullString(visitor.account) +'&nbsp;('+ visitor.create_time +')</span>';
					 	liHtml += '<hr style="filter: alpha(opacity=100,finishopacity=0,style=2)" width="90%" color="#E6E6FA" size=1 />';
						liHtml += '</div>';
					$("#visitors-list").append(liHtml);
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
 * 获取关注列表
 */
function getAttentions(){
	$.ajax({
		url : "/fs/toAttentions?pageSize=5&method=firstloading&toUserId="+ uid  +"&t=" + Math.random(),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			//$("#attentions").remove();
			if(data.isSuccess){
				if(data.message.length == 0){
					$("#attentions").append('<div class="list-group-item">TA暂无关注数据</div>');
					return;
				}
					
				for(var i = 0; i < data.message.length; i++){
					var attention = data.message[i];
					var html = '<div class="row" onclick="linkToMy('+ attention.user_id +');" style="cursor: pointer;">'+
					   				'<img class="img-circle" style="margin-left: 20px;" alt="" width="30" height="30" src="'+ attention.user_pic_path +'" /><span>&nbsp;&nbsp;&nbsp;&nbsp;'+ attention.account+ (isNotEmpty(attention.remark) ? '('+attention.remark+')&nbsp;&nbsp;': "&nbsp;&nbsp;") + attention.create_time +'</span>'+
					   			'</div><hr style="filter: alpha(opacity=100,finishopacity=0,style=2)" width="90%" color="#E6E6FA" size=1 />';
					$("#attentions").append(html);
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
 * 获取粉丝列表
 */
function getFans(){
	$.ajax({
		url : "/fs/toFans?pageSize=5&method=firstloading&toUserId="+ uid  +"&t=" + Math.random(),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			//$("#visitors-list li").remove();
			if(data.isSuccess){
				if(data.message.length == 0){
					//$("#visitors-list").append('<li class="list-group-item">暂无访客数据</li>');
					$("#fans").append('<div class="list-group-item">TA暂无粉丝数据</div>');
					return;
				}
					
				for(var i = 0; i < data.message.length; i++){
					var fan = data.message[i];
					var html = '<div class="row" onclick="linkToMy('+ fan.user_id +');" style="cursor: pointer;">'+
					   				'<img class="img-circle" style="margin-left: 20px;" alt="" width="30" height="30" src="'+ fan.user_pic_path +'" /><span>&nbsp;&nbsp;&nbsp;&nbsp;'+ fan.account + (isNotEmpty(fan.remark) ? '('+fan.remark+'&nbsp;&nbsp;)': "&nbsp;&nbsp;") + fan.create_time +'</span>'+
					   			'</div><hr style="filter: alpha(opacity=100,finishopacity=0,style=2)" width="90%" color="#E6E6FA" size=1 />';
					$("#fans").append(html);
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
 * 获取当前用户的基本信息
 * @param uid
 */
function loadUserInfo(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/us/searchByIdOrAccount?searchUserIdOrAccount="+ uid +"&fan=true&t=" + Math.random(),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			//layer.msg(JSON.stringify(userinfo));
			if(data.isSuccess){
				
				userinfo = data.userinfo;
				if(isNotEmpty(userinfo.user_pic_path))
					$("#user-img").html('<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12"><img src="'+ userinfo.user_pic_path +'" width="120px" height="120px" class="img-circle center-block"></div>');
				
				var descHtml = '<div>'+ 
									'<div class="animate three">';
				var account = userinfo.account;
				if(isNotEmpty(data.remark)){
					account = account + "("+ data.remark +")";
				}
							if(isNotEmpty(account)){
								for(var i = 0; i < account.length; i++){
									descHtml += '<span>'+ account.charAt(i) +'</span>';	
								}
							}
				
						descHtml +=	'</div>'+
								'</div>';
								if(isNotEmpty(userinfo.personal_introduction))
                    descHtml +='<div class="h4" style="max-height: 38px;overflow-y:auto;">'+ userinfo.personal_introduction+'</div>';
								
				if(isLoginUser){
					descHtml +=	'<button type="button" class="btn btn-primary btn-xs" data-toggle="modal" data-target="#edit-user-info">'+
								  '<span class="glyphicon glyphicon-pencil" ></span> 编辑个人资料'+
								'</button>'+
								'<button id="sign_button" type="button" class="btn btn-primary btn-xs" style="margin-left:5px;" disabled="disabled">'+
								  
								'</button>';
				}else{
					
					if(!data.fan){
						descHtml +=	'<button type="button" class="btn btn-primary btn-xs" onclick="attentionTa();">'+
									  '关注TA'+
									'</button>';
					}else{
						descHtml +=	'<button type="button" class="btn btn-primary btn-xs" onclick="cancelAttentionTa();">'+
									  '取消关注TA'+
									'</button>';
					}
					
				}
				
				$("#user-desc").html(descHtml);
				
				if(isLoginUser)
					isTodaySignIn();//加载是否今天已经签到
				
				buildShowUserinfo();
				buildEditUserinfo();
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
 * 执行关注TA的函数
 */
function attentionTa(){
	layer.prompt({
		  formType: 0,
		 // value: '',
		  title: '可以给TA设置备注名，并确认',
		  //area: ['800px', '350px'], //自定义文本域宽高
		  yes: function(index, layero){
		     //alert(layero.find(".layui-layer-input").val());
		     layer.close(index);
		     var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
				$.ajax({
					type: "POST",
					url : "/fs/fan?"+ jsonToGetRequestParams({"toUserId": uid, "remark": layero.find(".layui-layer-input").val()}),
					dataType: 'json', 
					beforeSend:function(){
					},
					success : function(data) {
						layer.close(loadi);
						if(data != null && data.isSuccess){
							layer.msg(data.message);
							window.location.reload();
						}else{
							ajaxError(data);
						}
						console.log(data);
					},
					error : function(data) {
						layer.close(loadi);
						ajaxError(data);
					}
				});
		  }
		});
		
	/*layer.prompt({title: '可以给TA设置备注名，并确认', formType: 0}, function(pass, index){
		  layer.close(index);
		  var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
			$.ajax({
				type: "POST",
				url : "/fs/fan?"+ jsonToGetRequestParams({"toUserId": uid, "remark": pass}),
				dataType: 'json', 
				beforeSend:function(){
				},
				success : function(data) {
					layer.close(loadi);
					if(data != null && data.isSuccess){
						layer.msg(data.message);
						window.location.reload();
					}else{
						ajaxError(data);
					}
					console.log(data);
				},
				error : function(data) {
					layer.close(loadi);
					ajaxError(data);
				}
			});
	});*/
	
	
}

/**
 * 执行取消关注TA的函数
 */
function cancelAttentionTa(){
	
	layer.confirm('您取消关注TA吗？', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "DELETE",
			url : "/fs/fan?"+ jsonToGetRequestParams({"toUserIds": uid}),
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data != null && data.isSuccess){
					layer.msg(data.message);
					window.location.reload();
				}else{
					ajaxError(data);
				}
				console.log(data);
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
 * 获取心情请求列表
 */
function getMoods(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/md/moods/paging?"+ jsonToGetRequestParams(getMoodRequestParams()),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			$moodContainer.empty();
			if(data != null && data.isSuccess){
				if(data.message.length == 0){
					if(currentIndex == 0){
						$moodContainer.append("还没有发表过任何心情，请发一篇心情吧！");
					}else{
						$moodContainer.append("已经没有更多的心情啦，请重新选择！");
						/*pageDivUtil(data.total);*/
					}
					return;
				}
				
				moods = data.message;
				for(var i = 0; i < moods.length; i++){
					//添加每一行到心情容器
					$moodContainer.append(buildMoodRow(i, moods[i]/*, ifFlagNew, flagMonth*/));
				}
				//pageDivUtil(data.total);
				console.log(monthArray);
				//resetSideHeight();
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
					    	getMoods();
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
			layer.close(loadi);
			isLoad = false;
			ajaxError(data);
		}
	});
}

function isInMonthArray(str){
	if(monthArray.length == 0)
		return false;
	for(var i = 0; i < monthArray.length; i++){
		if(monthArray[i] == str){
			return true;
		}
	}
	
	return false;
}

/**
 * 构建每一行心情列表的数据
 * @param index
 * @param mood
 * @param ifFlagNew 为true表示需要添加flag
 * @param flagMonth 当ifFlagNew为true有效
 * @returns {String}
 */
function buildMoodRow(index, mood, ifFlagNew, flagMonth){
	/*var html = '<div id="section1" class="row_'+ index +'">'+
					'<h1>'+ mood.content+'</h1>'+
					'<p>Try to scroll this section and look at the navigation list while scrolling!</p>'+
				'</div>';*/

	var moodTime = new Date(changeNotNullString(mood.create_time));
	/*moodTime = setTimeAgo(moodTime.getFullYear(), moodTime.getMonth(), moodTime.getDate(),
	                    moodTime.getHours(), moodTime.getMinutes(), moodTime.getSeconds());*/
    moodTime = setTimeAgo(moodTime);
	var html = '<div class="list-group" id="'+(ifFlagNew? 'month-'+flagMonth: '')+'">'+
				    '<div class="list-group-item active">'+
						'<div class="row">'+
							'<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cut-text">'+
								'<span class="list-group-item-heading" title="'+ changeNotNullString(mood.froms) +'">来自：'+ changeNotNullString(mood.froms) +
						        '</span>'+
							'</div>'+
							'<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 pull-right cut-text" style="text-align: right;">'+
								'<span class="list-group-item-heading" style="margin-right: 5px;">'+ moodTime +
						        '</span>'+
						        '<span class="list-group-item-heading glyphicon glyphicon-option-vertical cursor" onclick="showItemListModal('+ index +')" data-toggle="tooltip" data-placement="left" title="点击，查看更多操作" onMouseOver="$(this).tooltip(\'show\')">'+
						        '</span> '+
							'</div>'+
						'</div>'+
					'</div>'+
					'<div class="list-group-item">'+
						'<div class="list-group-item-text tip-list">';
						if(isLoginUser && mood.status == 5){
							html += '<span class="label label-warning" data-toggle="tooltip" data-placement="right" title="该心情是私有的，其他人无法查看" onMouseOver="$(this).tooltip(\'show\')">私有</span>';
						}
							html += '<span class="label '+ (mood.can_comment? 'label-default' : 'label-danger') +'"  '+ (mood.can_comment ? '': 'data-toggle="tooltip" data-placement="right" title="该心情禁止任何人评论" onMouseOver="$(this).tooltip(\'show\')"') + '>'+ (mood.can_comment? '可以评论':'禁止评论') +'</span>'+
							'<span class="label '+ (mood.can_transmit? 'label-default' : 'label-danger') +'" '+ (mood.can_transmit ? '': 'data-toggle="tooltip" data-placement="right" title="该心情禁止任何人转发" onMouseOver="$(this).tooltip(\'show\')"') + '>'+ (mood.can_transmit? '可以转发':'禁止转发') +'</span>'+
						'</div>'+
					    '<div class="list-group-item-text" style="margin-top: 5px;" id="couponValue-'+ index+'">'+ changeNotNullString(mood.content) +
					    '</div>'+
					    '<div class="list-group-item-text" style="margin-top: 5px; color: red;" id="fanValue-'+ index+'">'+
					    '</div>';
				if(isNotEmpty(mood.location)){
					html += '<p class="location">位置：'+ changeNotNullString(mood.location) +'</p>';
				}
					html += '<div class="row" style="margin-top: 5px;">';
				if(isNotEmpty(mood.imgs)){
					var imgs = mood.imgs.split(";");
					for(var i = 0; i < imgs.length; i++){
						
						if(isVideo(imgs[i])){
							/*html += '<div class="col-lg-9 col-sm-9">';*/
							html += getVideoHtml(imgs[i]);
							/*html += '</div>';*/
						}else if(isAudio(imgs[i])){
							/*html += '<div class="col-lg-9 col-sm-9">';*/
							html += getAudioHtml(imgs[i]);
							/*html += '</div>';*/
						}else if(isImg(imgs[i])){
							html += '<div class="col-lg-4 col-sm-4">'+
									'<img src="'+ imgs[i] +'" width="100%" height="180px" class="img-responsive" onClick="showImg('+ index +', '+ i +');" />'+
									'</div>';
						}else{
							layer.msg(getSupportTypeStr());
						}
					}
				}
				html += '</div>';
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
					html += '<div class="zan_user">'+ userStr +'等'+ mood.zan_number +'人觉得很赞</div>';
					}
					
			html +=	'</div>'+
					'<div class="list-group-item list-group-item-operate">'+
					     '<button type="button" class="btn btn-primary btn-sm" onclick="showCommentOrTransmit(1, '+ index +')">评论('+ mood.comment_number+')</button>'+
					     '<button type="button" class="btn btn-primary btn-sm" onclick="showCommentOrTransmit(2, '+ index +')">转发('+ mood.transmit_number+')</button>'+
					     '<button type="button" class="btn btn-primary btn-sm" href="javascript:void(0);" onclick="goToReadMoodFull('+ mood.id +', '+ uid +');" >查看详细</button>'+
					'</div>'+
				'</div>';
	return html;
}

/**
 * 获取心情请求列表
 */
function getMoodRequestParams(){
	//return {pageSize: pageSize, last_id: last_id, first_id: first_id, method: method, toUserId: uid, t: Math.random()};
	return {page_size: pageSize, current: currentIndex, total: totalPage, to_user_id: uid, t: Math.random()};
}
/**
 * 页面展示的用户基本信息
 */
function buildShowUserinfo(){

    //处理最后请求时间
    var requestTime = "";
    if(isNotEmpty(userinfo.last_request_time)){
        requestTime = setTimeAgo(new Date(userinfo.last_request_time));
    }
	var infoHtml = '<div class="table-responsive">'+
						'<table class="table table-striped">'+
						  '<tr>'+
						    '<td>性别</td>'+
						    '<td>'+ changeNotNullString(userinfo.sex) +'</td>'+
						  '</tr>'+
						  '<tr>'+
						     '<td>出生日期</td>'+
						     '<td>'+ changeNotNullString(userinfo.birth_day) +'</td>'+
						  '</tr>'+
						  '<tr>'+
						     '<td>qq</td>'+
						     '<td>'+ changeNotNullString(userinfo.qq) +'</td>'+
						  '</tr>'+
						  '<tr>'+
						     '<td>手机号码</td>'+
						     '<td>'+ changeNotNullString(userinfo.mobile_phone) +'</td>'+
						  '</tr>'+
						  '<tr>'+
						     '<td>邮箱</td>'+
						     '<td>'+ changeNotNullString(userinfo.email) +'</td>'+
						  '</tr>'+
						  '<tr>'+
						     '<td>学历</td>'+
						     '<td>'+ changeNotNullString(userinfo.education_background) +'</td>'+
						  '</tr>'+
						  '<tr>'+
						     '<td>注册时间</td>'+
						     '<td>'+ changeNotNullString(userinfo.register_time) +'</td>'+
						  '</tr>'+
						  '<tr>'+
						     '<td>最后请求时间</td>'+
						     '<td>'+ requestTime +'</td>'+
						  '</tr>'+
						'</tbody>'+
						'</table>'+
					'</div>';
		$("#user_info").html(infoHtml);
}
/**
 * 添加编辑用户信息的弹出模块
 */
function buildEditUserinfo(){
	//添加编辑用户信息的弹出模块html
	var editHtml = '<form role="form" class="myForm">'+
	                '<div class="form-group">'+
                      '<label for="head">头像(点击下方文本框选择新头像)</label>'+
                      '<input type="text" class="form-control" name="head" placeholder="请选择个人头像" onclick="createSelectMaterialModal(this, 1, 1, \'afterSelectHead\');" readonly="readonly" value="'+changeNotNullString(userinfo.user_pic_path)+'">'+
                    '</div>'+
	                '<div class="form-group">'+
					  '<label for="sex">性别</label>'+
					  '<select class="form-control" name="sex">'+
						'<option value="男"'+ (userinfo.sex == '男'? ' selected="selected" ': '')+'>男</option>'+
						'<option value="女"'+ (userinfo.sex == '女'? ' selected="selected" ': '')+'>女</option>'+
						'<option value="未知"'+ (userinfo.sex == '未知'? ' selected="selected" ': '')+'>未知</option>'+
						'</select>'+
					'</div>'+
					'<div class="form-group">'+
					  '<label for="birth_day">出生日期</label>'+
					  '<input type="date" class="form-control" name="birth_day" value="'+changeNotNullString(userinfo.birth_day)+'">'+
					'</div>'+
					'<div class="form-group">'+
					  '<label for="qq">QQ</label>'+
					  '<input type="number" class="form-control" name="qq" placeholder="请输入QQ号码" value="'+changeNotNullString(userinfo.qq)+'">'+
					'</div>'+
					'<div class="form-group">'+
					  '<label for="mobile_phone">手机号码</label>'+
					  '<input type="number" class="form-control" name="mobile_phone" placeholder="请输入11位的手机号码" value="'+changeNotNullString(userinfo.mobile_phone)+'">'+
					'</div>'+
					'<div class="form-group">'+
					  '<label for="email">邮箱</label>'+
					  '<input type="email" class="form-control" name="email" placeholder="请输入电子邮箱地址" value="'+changeNotNullString(userinfo.email)+'">'+
					'</div>'+
					'<div class="form-group">'+
					  '<label for="education_background">学历</label>'+
					  '<select class="form-control" name="education_background">'+
					  	'<option value="博士后"'+ (userinfo.education_background == '博士后'? ' selected="selected" ': '')+'>博士后</option>'+
					  	'<option value="博士"'+ (userinfo.education_background == '博士'? ' selected="selected" ': '')+'>博士</option>'+
						'<option value="硕士"'+ (userinfo.education_background == '硕士'? ' selected="selected" ': '')+'>硕士</option>'+
						'<option value="本科"'+ (userinfo.education_background == '本科'? ' selected="selected" ': '')+'>本科</option>'+
						'<option value="大专"'+ (userinfo.education_background == '大专'? ' selected="selected" ': '')+'>大专</option>'+
						'<option value="中专"'+ (userinfo.education_background == '中专'? ' selected="selected" ': '')+'>中专</option>'+
						'<option value="高中"'+ (userinfo.education_background == '高中'? ' selected="selected" ': '')+'>高中</option>'+
						'<option value="初中"'+ (userinfo.education_background == '初中'? ' selected="selected" ': '')+'>初中</option>'+
						'<option value="小学"'+ (userinfo.education_background == '小学'? ' selected="selected" ': '')+'>小学</option>'+
						'<option value="文盲"'+ (userinfo.education_background == '文盲'? ' selected="selected" ': '')+'>文盲</option>'+
						'</select>'+
					'</div>'+
					'<div class="form-group">'+
					  '<label for="personal_introduction">个人简介</label>'+
					  '<textarea class="form-control" name="personal_introduction" placeholder="请输入个人简介">'+changeNotNullString(userinfo.personal_introduction)+'</textarea>'+
					'</div>'+
					'</form';
		$(".modal-body-edit-userinfo").html(editHtml);
}

/**
 * 执行编辑用户的基本信息
 */
function editUserinfo(params){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "put",
		data : params,
		url : "/us/user/base",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			
			if(data.isSuccess){
				layer.msg("基本信息更新成功！");
				window.location.reload();
			}else{
				layer.msg(data.message);
			}
		},
		error : function() {
			layer.close(loadi);
			layer.msg("网络请求失败");
		}
	});
}

/**
 * 展示选项列表modal
 * @param index
 */
function showItemListModal(index){
	$operateItemsContrainer.modal("show");
	var mood = moods[index];
	var html = '<li class="list-group-item cursor" onclick="goToReadMoodFull('+ mood.id +', '+ uid +');">查看</li>'+
			    '<li class="list-group-item cursor" onclick="addZan('+ mood.id +')">赞</li>'+
			    '<li class="list-group-item cursor" onclick="translation('+ index +');">翻译</li>'+
			    '<li class="list-group-item cursor do-copy-btn" data-clipboard-action="copy" data-clipboard-target="#couponValue-'+ index +'">复制文字</li>';
	if(isLoginUser){
		html += '<li class="list-group-item cursor" onclick="deleteMood('+ mood.id +')">删除</li>'+
				'<li class="list-group-item cursor" onclick="updateIsSelfStatus('+ mood.status +','+ mood.id +');">'+
			        '<span class="badge '+ (mood.status == 5? 'badge-warning': '') +'">'+ (mood.status == 5? '私有': '非私有') +'</span>'+
			        	'设置是否私有'+
			    '</li>'+
			    '<li class="list-group-item cursor" onclick="updateCommentStatus('+ mood.can_comment +','+ mood.id +');">'+
			        '<span class="badge '+ (mood.can_comment? '': 'badge-danger') +'">'+ (mood.can_comment? '已启用': '已禁用') +'</span>'+
			        	'设置是否能评论'+
			    '</li>'+
			    '<li class="list-group-item cursor" onclick="updateTransmitStatus('+ mood.can_transmit+','+ mood.id+')">'+
			        '<span class="badge '+ (mood.can_transmit? '': 'badge-danger') +'">'+ (mood.can_transmit? '已启用': '已禁用') +'</span>'+
			        	'设置是否能转发'+
			    '</li>';
	}
			    
	
	$("#operate-list").html(html);
}

/**
 * 添加赞
 * @param id
 */
function addZan(id){
	var loadi = layer.load('努力加载中…');
	$.ajax({
		type : "post",
		data: {table_name: 't_mood', content: '喜欢', froms: '网页端', table_id: id},
		url : "/lk/zan",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg("点赞成功");
				window.location.reload();
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
 * 翻译
 * @param id
 */
function translation(index){
	var content = moods[index].content;
	if(content){
		var loadi = layer.load('努力加载中…');
		$.ajax({
			type : "GET",
			data: {content: content},
			url : "/tl/fanyi",
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					$("#fanValue-"+index).text("翻译结果："+data.message);
					$operateItemsContrainer.modal("hide");
				}else
					layer.msg(data.message);
			},
			error : function() {
				layer.close(loadi);
				layer.msg("网络请求失败");
			}
		});
	}
	
}

/**
 * 删除心情
 * @param id
 */
function deleteMood(id){
	layer.confirm('您要删除该条心情记录吗？', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var loadi = layer.load('努力加载中…');
		$.ajax({
			type : "delete",
			url : "/md/mood?mid="+id,
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
					layer.close(loadi);
					layer.msg(data.message);
					if(data.isSuccess){
						window.location.reload();
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

function updateIsSelfStatus(status, id){
	var loadi = layer.load('努力加载中…');
	$.ajax({
		type : "put",
		data: {status: (status == 1 ? 5 : 1), mid: id},
		url : "/md/mood",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
				layer.close(loadi);
				layer.msg(data.message);
				if(data.isSuccess){
					window.location.reload();
				}
		},
		error : function() {
			layer.close(loadi);
			layer.msg("网络请求失败");
		}
	});
}

/**
 * 更新评论状态
 * @param can
 * @param table_id
 */
function updateCommentStatus(can, table_id){
	var loadi = layer.load('努力加载中…');
	$.ajax({
		type : "put",
		data: {table_name: 't_mood', can_comment: !can, table_id: table_id},
		url : "/cm/comment",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
				layer.close(loadi);
				layer.msg(data.message);
				if(data.isSuccess){
					window.location.reload();
				}
		},
		error : function() {
			layer.close(loadi);
			layer.msg("网络请求失败");
		}
	});
}

/**
 * 更新转发状态
 * @param can
 * @param table_id
 */
function updateTransmitStatus(can, table_id){
	var loadi = layer.load('努力加载中…');
	$.ajax({
		type : "put",
		data: {table_name: 't_mood', can_transmit: !can, table_id: table_id},
		url : "/ts/transmit",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
				layer.close(loadi);
				layer.msg(data.message);
				if(data.isSuccess){
					window.location.reload();
				}
				
		},
		error : function() {
			layer.close(loadi);
			layer.msg("网络请求失败");
		}
	});
}

/**
 * 判断今天是否已经签到
 */
function isTodaySignIn(){
	$.ajax({
		url : "/si/currentDateIsSignIn?id="+ uid,
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			if(data.isSuccess){
				$("#sign_button").html('<span class="glyphicon glyphicon-time" ></span> 已签到');
			}else{
				$("#sign_button").removeAttr("disabled");
				$("#sign_button").attr("onclick", "signin();");
				$("#sign_button").html('<span class="glyphicon glyphicon-time"></span> 立即签到');
			}
		},
		error : function(data) {
			ajaxError(data);
		}
	});
}

/**
 * 执行签到操作
 */
function signin(){
	var loadi = layer.load('努力加载中…');
	$.ajax({
		type : "post",
		data: {id: uid},
		url : "/si/signIn",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					layer.msg("签到成功");
					$("#sign_button").attr("disabled", "disabled");
					$("#sign_button").html('<span class="glyphicon glyphicon-time" ></span> 已签到');
				}else{
					layer.msg(data.message);
				}
				
		},
		error : function() {
			layer.close(loadi);
			layer.msg("网络请求失败");
		}
	});
}


//评论转发相关------------------------------------------------------------------
var ct_last_id = 0;
var ct_first_id = 0;
var ct_method = 'firstloading';
var cts = [];
var ct_isLoad = false;
var ct_type = 0;
var ct_id = 0; //当前心情的ID
var ct_canLoadData = true;//标记是否还能下拉请求
var ct_click_index = -1;  //点击的评论/转发的索引
var $commentOrTransmitText = $('#comment-or-transmit-text');
/**
 * 展示评论或者转发列表
 * @param type 1表示评论，2表示转发
 * @param index 当前心情的索引位置
 */
function showCommentOrTransmit(type, index){
	$("#comment-or-transmit-list").modal("show");
	$commentOrTransmitText.text("");
	if(type == 1){
		$("#commentOrTransmitListModalLabel").text("评论列表");
		if(!moods[index].can_comment){
		    $commentOrTransmitText.attr('placeholder','由于用户设置，无法评论');
		    $commentOrTransmitText.attr('readonly','readonly');
		    $commentOrTransmitText.parent().find("button").prop('disabled', true); // 按钮灰掉，且不可点击。
		}else{
		     $commentOrTransmitText.attr('placeholder','评论点什么吧');
		     $commentOrTransmitText.removeAttr('readonly');
		     $commentOrTransmitText.parent().find("button").prop('disabled', false);
		}
	}else{
		$("#commentOrTransmitListModalLabel").text("转发列表");
		if(!moods[index].can_transmit){
            $commentOrTransmitText.attr('placeholder','由于用户设置，无法转发');
            $commentOrTransmitText.attr('readonly','readonly');
            $commentOrTransmitText.parent().find("button").prop('disabled', true); // 按钮灰掉，且不可点击。
        }else{
             $commentOrTransmitText.attr('placeholder','说点什么吧');
             $commentOrTransmitText.removeAttr('readonly');
             $commentOrTransmitText.parent().find("button").prop('disabled', false);
        }
	}
	var id = moods[index].id;
	ct_type = type;
	ct_id = id;
	ct_last_id = 0;
	ct_first_id = 0;
	ct_method = 'firstloading';
	cts = [];
	ct_isLoad = false;
	ct_canLoadData = true;
	ct_click_index = -1;

	asynchronousLoadData();
}

/**
 * 获取评论、转发列表的请求参数
 * @param table_id
 * @returns {___anonymous20849_20964}
 */
function getCTRequestParams(table_id){
	pageSize = 15;
	if(ct_method != 'firstloading')
		pageSize = 10;
	//return {pageSize: pageSize, last_id: ct_last_id, first_id: ct_first_id, method: ct_method, table_name: 't_mood', showUserInfo: true, table_id: table_id, t: Math.random()};
	return "?page_size="+ pageSize +"&last_id="+ ct_last_id +"&first_id="+ ct_first_id+"&method="+ ct_method+ "&table_name=t_mood&show_user_info=true&table_id="+ table_id +"&t="+Math.random();
}

/**
 * 异步加载评论或者转发的列表
 * @param type
 * @param id
 */
function asynchronousLoadData(){
	ct_isLoad = true;
	var loadi = layer.load('努力加载中…');
	$.ajax({
		url : (ct_type==1? "/cm/comments": "/ts/transmits")+ getCTRequestParams(ct_id),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
				layer.close(loadi);
				if(data != null && data.isSuccess){
					if(ct_method == 'firstloading')
						$cOTItemContainer.empty();
						//$moodContainer.empty();
					
					$cOTItemContainer.find(".footer-button").remove();
					
					if(ct_method == 'firstloading'){
						cts = data.message;
						for(var i = 0; i < cts.length; i++){
							//添加每一行到心情容器
							$cOTItemContainer.append(buildCommentOrTransmitRow(i, cts[i]));
							if(i == 0)
								ct_first_id = cts[i].id;
							if(i == cts.length -1)
								ct_last_id = cts[i].id;
						}
						if(data.message.length == 0){
							ct_canLoadData = false;
							$cOTItemContainer.html('<button class="btn btn-info">无更多数据</button>');
						}else{
							$cOTItemContainer.append('<button class="btn btn-info footer-button" onclick="asynchronousLoadData()">点击加载更多</button>');
						}
					}else{
						var currentIndex = cts.length;
						for(var i = 0; i < data.message.length; i++){
							cts.push(data.message[i]);
							$cOTItemContainer.append(buildCommentOrTransmitRow(currentIndex + i, data.message[i]));
								
							if(i == data.message.length -1)
								ct_last_id = data.message[i].id;
						}
						if(data.message.length == 0){
							ct_canLoadData = false;
							$cOTItemContainer.append('<button class="btn btn-info footer-button">无更多数据</button>');
						}else{
							$cOTItemContainer.append('<button class="btn btn-info footer-button" onclick="asynchronousLoadData()">点击加载更多</button>');
						}
					}
				}else{
					ajaxError(data);
				}
				console.log(data);
				ct_isLoad = false;
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

function buildCommentOrTransmitRow(index, ct){
	var html = '<div class="list-group">'+
					'<div class="list-group-item">'+
						'<div class="row">'+
							'<div class="col-lg-2 col-sm-2">'+
								'<div>';
						if(isEmpty(ct.user_pic_path))
							html += '<img width="60px" height="60px" class="img-circle">';
						else
							html += '<img src="'+ ct.user_pic_path +'" width="60px" height="60px" class="img-circle">';
						
						html += '</div>'+
								'<div class="commentOrTransmitUser text-info">'+ ct.account +'</div> '+
							'</div>'+
							'<div class="col-lg-10 col-sm-10">'+
								'<div class="row">'+
									'<div class="col-lg-6 col-sm-6 text-muted"><span class="badge">'+ (index +1) +'</span>来自：'+ ct.froms +' </div>'+
									'<div class="col-lg-6 col-sm-6 text-muted" style="text-align: right;">'+ ct.create_time.substring(0, 16) +
										'<button type="button" class="btn btn-primary btn-xs" '+
											    'data-toggle="button" style="margin-left: 5px;" onclick="reply('+index+');">回复'+
											'</button>'+
									'</div>'+
								'</div>'+
								'<div>'+ ct.content+'</div>'+
							'</div>'+
						'</div>'+
					'</div>'+
				'</div>';
		return html;
}
/**
 * 回复某人
 * @param index
 */
function reply(index){
	ct_click_index = index;
	$commentOrTransmitText.attr('placeholder','@'+ changeNotNullString(cts[ct_click_index].account));
	$commentOrTransmitText.focus();
}

/**
 * 发送评论或者转发
 */
function sendCommentOrTransmit(){
	var text = $commentOrTransmitText.val();
	if(isEmpty(text)){
		layer.msg("请说点什么吧");
		$commentOrTransmitText.focus();
		return;
	}
	
	var loadi = layer.load('努力加载中…');
	var params = {table_name: 't_mood', table_id: ct_id, content: text, froms: 'web网页端'};
	if(ct_click_index >= 0){
		params.pid = cts[ct_click_index].id;
	}
	$.ajax({
		type : "post",
		data: params,
		url : (ct_type == 1? '/cm/comment': '/ts/transmit'),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					layer.msg("评论成功");
					window.location.reload();
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


/**发送心情相关的**************************************************************************************************

/**
 * 显示发表评论的
 */
function shoqSendMoodDialog(){
	$("#push-mood").modal("show");
}

/**
 * 发送心情
 */
function sendMood(){
	var text = $moodMardownContent.val();
	if(isEmpty(text)){
		layer.msg("请先输入你想说的");
		$moodMardownContent.focus();
		return;
	}
	
	var loadi = layer.load('努力加载中…');
	
	var links = '';
	var $imgs = $(".select-links").find("img,audio,video"); //获取所有的图片标签
	if($imgs && $imgs.length > 0){
		$imgs.each(function(index){
			links += $(this).attr("src") +';';
		});
		links = deleteLastStr(links);
	}
	
	
	var params = {table_name: 't_mood', table_id: ct_id, content: text, links: links,froms: 'web网页端'};
	if(ct_click_index >= 0){
		params.pid = cts[ct_click_index].id;
	}
	$.ajax({
		type : "post",
		data: params,
		url : "/md/wordAndLink",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					layer.msg("心情发表成功，1秒后自动刷新");
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



/**
 * 选择头像后之后的回调函数
 */
function afterSelectHead(links){
	if(isNotEmpty(links)){
		$(".myForm").find("input[name = 'head']").val(links);
	}
}

/**
 * 选择素材之后的回调函数
 */
function afterSelect(links){
	$(".select-links").empty();
	//$(".select-links").text(links);
	if(isNotEmpty(links)){
		var imgs = links.split(";");
		var html = '';
		for(var i = 0; i < imgs.length; i++){
			
			if(isVideo(imgs[i])){
				/*html += '<div class="col-lg-9 col-sm-9">';*/
				html += getVideoHtml(imgs[i]);
				/*html += '</div>';*/
			}else if(isAudio(imgs[i])){
				/*html += '<div class="col-lg-9 col-sm-9">';*/
				html += getAudioHtml(imgs[i]);
				/*html += '</div>';*/
			}else if(isImg(imgs[i])){
				html += '<div class="col-lg-4 col-sm-4">'+
							'<img src="'+ changeNotNullString(imgs[i])+'" style="width: 100%; max-height: 180px;" class="img-responsive" />'+
						'</div>';
			}else{
				layer.msg(getSupportTypeStr());
				return false;
			}
		}
		$(".select-links").html(html);
	}
}

function testClick(obj){
	//$(this).animate({scrollTop: "10"});
	var t = $(obj);
	$("body").animate({scrollTop: t.offset().top - 50}, 800);
	//t.animate({"scrollTop": 10}, 800);​
}