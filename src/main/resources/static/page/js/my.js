var userinfo;
var moods = [];
var isLoad = false;
var currentIndex = 0;
var pageSize = 8;
var totalPage = 0;
//浏览器可视区域页面的高度
var winH = $(window).height(); 
var monthArray = new Array();
$(function(){
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-my").addClass("active");
	
	loadUserInfo();
	getMoods();
	getMessageBoards();//获取留言板列表
	
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
		
	$('#comment-or-transmit-item').on('scroll',function(){
		var groups = $("#comment-or-transmit-item").find(".list-group");
		var totalH = 0;
		groups.each(function(){
			totalH += $(this).height();
		});
		console.log("totalH--->"+ totalH);
	    // div 滚动了
		var pageH = $("#comment-or-transmit-item").height(); //页面总高度 
		console.log("pageH"+ pageH);
	    var scrollT = $("#comment-or-transmit-item").scrollTop(); //滚动条top 
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
});


/**
 * 获取留言板列表
 */
function getMessageBoards(){
	$.ajax({
		url : "/cm/user/"+ uid+ "/messageBoards?page_size=5&t=" + Math.random(),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			$("#message-boards tr").remove();
			if(data.isSuccess){
				for(var i = 0; i < data.message.length; i++){
					var html = '<tr>'+
									'<td width="40px"><img src="'+ changeNotNullString(data.message[i].user_pic_path) +'" width="30" height="30" class="img-rounded img-circle"></td>'+
									'<td>'+ data.message[i].content+'</td>'+
								'</tr>';
					$("#message-boards").append(html);
				}
			}else{
				ajaxError(data);
			}
			$("#message-boards").append('<tr><td colspan="2" style="text-align: center;"><a type="button" class="btn btn-primary btn-xs" href="/user/'+uid+'/board"><span class="glyphicon glyphicon-pencil"></span> 留言板</a></td></tr>');
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
		url : "/us/searchByIdOrAccount?searchUserIdOrAccount="+ uid +"&t=" + Math.random(),
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
				
				var descHtml = '<div class="h3">'+ 
									userinfo.account + (userinfo.is_admin && uid == loginUserId ? '<span class="badge" style="margin-left:5px;">管理员</span>': '')+
								'</div>'+
								'<div class="h4" style="max-height: 38px;overflow-y:auto;">'+ userinfo.personal_introduction+'</div>';
								
				if(isLoginUser){
					descHtml +=	'<button type="button" class="btn btn-primary btn-xs" data-toggle="modal" data-target="#edit-user-info">'+
								  '<span class="glyphicon glyphicon-pencil" ></span> 编辑个人资料'+
								'</button>'+
								'<button id="sign_button" type="button" class="btn btn-primary btn-xs" style="margin-left:5px;" disabled="disabled">'+
								  
								'</button>';
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
			$("#mood-container").empty();
			if(data != null && data.isSuccess){
				//if(method == 'firstloading')
					//$("#float-month").empty();
					//$("#mood-container").empty();
				
				if(data.message.length == 0){
					$("#mood-container").append("空空的，还没有数据");
					return;
				}
				
				moods = data.message;
				for(var i = 0; i < moods.length; i++){
					
					var ifFlagNew = false;
					var flagMonth = moods[i].create_time.substring(0, 7);
					if(!isInMonthArray(flagMonth)){
						ifFlagNew = true
						monthArray.push(flagMonth);
						$("#float-month").append('<li  class="active"><a href="#mood-'+flagMonth+'">'+ flagMonth +'</a></li>');
					}
					
					//添加每一行到心情容器
					$("#mood-container").append(buildMoodRow(i, moods[i], ifFlagNew, flagMonth));
					if(i == 0)
						first_id = moods[i].id;
					if(i == moods.length -1)
						last_id = moods[i].id;
					
					
				}
				pageDivUtil(data.total);
				console.log(monthArray);
				resetSideHeight();
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

/**
 * 生成分页div
 * @param total
 */
function pageDivUtil(total){
	var html = '<li>'+
					'<a href="javascript:void(0);" onclick="pre();" aria-label="Previous">'+
						'<span aria-hidden="true">&laquo;</span>'+
					'</a>'+
				'</li>';
	totalPage = parseInt(Math.ceil(total / pageSize));
	var start = 0;
	var end = totalPage > start + 6 ? start + 6: totalPage;
	
	var selectHtml = '<li><select class="form-control" onchange="optionChange()">';
	for(var i = 0; i < totalPage; i++){
		if(currentIndex == i)
			selectHtml += '<option name="pageIndex" selected="selected" value="'+ i +'">'+ (i + 1) +'</option>';
		else
			selectHtml += '<option name="pageIndex" value="'+ i +'">'+ (i + 1) +'</option>';
	}
	
	for(var i = start; i < end; i++){
		if(currentIndex == i)
			html += '<li class="active"><a href="javascript:void(0);" onclick="goIndex('+ i +');">'+ (i+1) +'</a></li>';
		else
			html += '<li><a href="javascript:void(0);" onclick="goIndex('+ i +');">'+ (i+1) +'</a></li>';
	}
	html += '<li>'+
				'<a href="javascript:void(0);" onclick="next();" aria-label="Next">'+
					'<span aria-hidden="true">&raquo;</span>'+
				'</a>'+ 
			'</li>';
	
	selectHtml += '</select>共计：' +total +'条记录</li>';
	
	html += selectHtml;
	$(".pagination").html(html);
}

/**
 * 选择改变的监听
 */
function optionChange(){
	var objS = document.getElementsByTagName("select")[0];
    var index = objS.options[objS.selectedIndex].value;
    currentIndex = index;
    getMoods();
}

/**
 * 点击向左的按钮
 */
function goIndex(index){
	currentIndex = index;
	getMoods();
}

/**
 * 点击向左的按钮
 */
function pre(){
	currentIndex --;
	if(currentIndex < 0)
		currentIndex = 0;
	getMoods();
}


/**
 * 点击向右的按钮
 */
function next(){
	currentIndex ++;
	if(currentIndex > totalPage)
		currentIndex = totalPage;
	getMoods();
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
	var html = '<div class="list-group" id="'+(ifFlagNew? 'month-'+flagMonth: '')+'">'+
				    '<div class="list-group-item active">'+
						'<div class="row">'+
							'<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cut-text">'+
								'<span class="list-group-item-heading" title="'+ changeNotNullString(mood.froms) +'">来自：'+ changeNotNullString(mood.froms) +
						        '</span>'+
							'</div>'+
							'<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 pull-right cut-text" style="text-align: right;">'+
								'<span class="list-group-item-heading" style="margin-right: 5px;">'+ changeNotNullString(mood.create_time).substring(0, 16) +
						        '</span>'+
						        '<span class="list-group-item-heading glyphicon glyphicon-chevron-down cursor" onclick="showItemListModal('+ index +')">'+
						        '</span> '+
							'</div>'+
						'</div>'+
					'</div>'+
					'<div class="list-group-item">'+
						'<div class="list-group-item-text tip-list">';
						if(isLoginUser && mood.status == 5){
							html += '<span class="label label-warning">私有</span>';
						}
							html += '<span class="label '+ (mood.can_comment? 'label-default' : 'label-success') +'">'+ (mood.can_comment? '可以评论':'禁止评论') +'</span>'+
							'<span class="label '+ (mood.can_transmit? 'label-default' : 'label-success') +'">'+ (mood.can_transmit? '可以转发':'禁止转发') +'</span>'+
						'</div>'+
					    '<div class="list-group-item-text" style="margin-top: 5px;">'+ changeNotNullString(mood.content) +
					    '</div>';
				if(isNotEmpty(mood.location)){
					html += '<p class="location">位置：'+ changeNotNullString(mood.location) +'</p>';
				}
					html += '<div class="row" style="margin-top: 5px;">';
				if(isNotEmpty(mood.imgs)){
					var imgs = mood.imgs.split(";");
					for(var i = 0; i < imgs.length; i++){
						html += '<div class="col-lg-4 col-sm-4">'+
					      			'<img src="'+ imgs[i] +'" width="100%" height="180px" class="img-responsive" onClick="showImg('+ index +', '+ i +');">'+
						      	'</div>';
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
					html += '<div class="zan_user">'+ userStr +'等'+ users.length +'人觉得很赞</div>';
					}
					
			html +=	'</div>'+
					'<div class="list-group-item list-group-item-operate">'+
					     '<button type="button" class="btn btn-primary btn-sm" onclick="showCommentOrTransmit(1, '+ index +')">评论('+ mood.comment_number+')</button>'+
					     '<button type="button" class="btn btn-primary btn-sm" onclick="showCommentOrTransmit(2, '+ index +')">转发('+ mood.transmit_number+')</button>'+
					     '<button type="button" class="btn btn-primary btn-sm" href="javascript:void(0);" onclick="goToReadFull('+ mood.id +');">查看详细</button>'+
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
						     '<td>'+ changeNotNullString(userinfo.last_request_time) +'</td>'+
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
	var editHtml = '<form role="form" class="myForm"><div class="form-group">'+
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
		url : "us/user/base",
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
 * 跳转到心情详细阅读
 * @param id
 */
function goToReadFull(id){
	if(isEmpty(id)){
		layer.msg("该心情不存在，请联系管理员核实");
		return;
	}
	window.open("/user/"+uid+"/mood/"+id+"/dt", "_blank");
}

/**
 * 展示选项列表modal
 * @param index
 */
function showItemListModal(index){
	$("#operate-item-list").modal("show");
	var mood = moods[index];
	var html = '<li class="list-group-item cursor" onclick="goToReadFull('+ mood.id +');">查看</li>'+
			    '<li class="list-group-item cursor" onclick="addZan('+ mood.id +')">赞</li>'+
			    '<li class="list-group-item cursor">翻译</li>'+
			    '<li class="list-group-item cursor" onclick="copyToClipBoard(\''+ mood.content +'\');">复制文字</li>';
	if(isLoginUser){
		html += '<li class="list-group-item cursor" onclick="deleteMood('+ mood.id +')">删除</li>'+
				'<li class="list-group-item cursor" onclick="updateIsSelfStatus('+ mood.status +','+ mood.id +');">'+
			        '<span class="badge">'+ (mood.status == 5? '私有': '非私有') +'</span>'+
			        	'设置转为私有'+
			    '</li>'+
			    '<li class="list-group-item cursor" onclick="updateCommentStatus('+ mood.can_comment +','+ mood.id +');">'+
			        '<span class="badge">'+ (mood.can_comment? '已启用': '已禁用') +'</span>'+
			        	'设置是否能评论'+
			    '</li>'+
			    '<li class="list-group-item cursor" onclick="updateTransmitStatus('+ mood.can_transmit+','+ mood.id+')">'+
			        '<span class="badge">'+ (mood.can_transmit? '已启用': '已禁用') +'</span>'+
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
		data: {table_name: 't_mood', content: '', froms: '网页端', table_id: id},
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
/**
 * 展示评论或者转发列表
 * @param type 1表示评论，2表示转发
 * @param index 当前心情的索引位置
 */
function showCommentOrTransmit(type, index){
	$("#comment-or-transmit-list").modal("show");
	if(type == 1){
		$("#commentOrTransmitListModalLabel").text("评论列表");
	}else{
		$("#commentOrTransmitListModalLabel").text("转发列表");
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
	$('#comment-or-transmit-text').attr('placeholder','请说点什么吧');
	asynchronousLoadData();
}

/**
 * 获取评论、转发列表的请求参数
 * @param table_id
 * @returns {___anonymous20849_20964}
 */
function getCTRequestParams(table_id){
	var pageSize = 15;
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
						$("#comment-or-transmit-item").empty();
						//$("#mood-container").empty();
					
					$("#comment-or-transmit-item").find(".footer-button").remove();
					
					if(ct_method == 'firstloading'){
						cts = data.message;
						for(var i = 0; i < cts.length; i++){
							//添加每一行到心情容器
							$("#comment-or-transmit-item").append(buildCommentOrTransmitRow(i, cts[i]));
							if(i == 0)
								ct_first_id = cts[i].id;
							if(i == cts.length -1)
								ct_last_id = cts[i].id;
						}
						if(data.message.length == 0){
							ct_canLoadData = false;
							$("#comment-or-transmit-item").html('<button class="btn btn-info">无更多数据</button>');
						}else{
							$("#comment-or-transmit-item").append('<button class="btn btn-info footer-button" onclick="asynchronousLoadData()">点击加载更多</button>');
						}
					}else{
						var currentIndex = cts.length;
						for(var i = 0; i < data.message.length; i++){
							cts.push(data.message[i]);
							$("#comment-or-transmit-item").append(buildCommentOrTransmitRow(currentIndex + i, data.message[i]));
								
							if(i == data.message.length -1)
								ct_last_id = data.message[i].id;
						}
						if(data.message.length == 0){
							ct_canLoadData = false;
							$("#comment-or-transmit-item").append('<button class="btn btn-info footer-button">无更多数据</button>');
						}else{
							$("#comment-or-transmit-item").append('<button class="btn btn-info footer-button" onclick="asynchronousLoadData()">点击加载更多</button>');
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
	$('#comment-or-transmit-text').attr('placeholder','@'+ changeNotNullString(cts[ct_click_index].account));
	$('#comment-or-transmit-text').focus();
}

/**
 * 发送评论或者转发
 */
function sendCommentOrTransmit(){
	var text = $('#comment-or-transmit-text').val();
	if(isEmpty(text)){
		layer.msg("请说点什么吧");
		$('#comment-or-transmit-text').focus();
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

/**
 * 复制内容到粘贴板
 * @param text
 */
function copyToClipBoard(text){
	window.clipboardData.setData("Text",text); 
	layer.msg(text +"已经成功复制到粘贴板");
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
	var text = $('#push-mood-text').val();
	if(isEmpty(text)){
		layer.msg("请先输入你想说的");
		$('#push-mood-text').focus();
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
		url : "/md/sendWord",
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

function testClick(obj){
	//$(this).animate({scrollTop: "10"});
	var t = $(obj);
	$("body").animate({scrollTop: t.offset().top - 50}, 800);
	//t.animate({"scrollTop": 10}, 800);​
}