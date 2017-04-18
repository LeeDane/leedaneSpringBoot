var users;
$(function(){
	
	//默认查询操作
	querySearch({});
	
	$(".senior-condition-btn").click(function(){
		$(".senior-condition").toggle("fast");
	});
	
});

/**
* 搜索
*/
function search(obj){
	var params = {};
	params.t = Math.random();

	var registerStartTime = $('[name="registerStartTime"]').val();
	if(isNotEmpty(registerStartTime)){
		params.register_time_start = registerStartTime;
	}
		
	var registerEndTime = $('[name="registerEndTime"]').val();
	if(isNotEmpty(registerEndTime)){
		params.register_time_end = endTime;
	}
	
	var birthStartTime = $('[name="birthStartTime"]').val();
	if(isNotEmpty(birthStartTime)){
		params.birth_time_start = birthStartTime;
	}
		
	var birthEndTime = $('[name="birthEndTime"]').val();
	if(isNotEmpty(birthEndTime)){
		params.birth_time_end = birthEndTime;
	}
	
	var searchKey = $('[name="searchKey"]').val();
	if(isNotEmpty(searchKey)){
		params.search_key = searchKey;
	}
	querySearch(params);
}

/**
 * 搜索获取用户的基本信息
 * @param uid
 */
function querySearch(params){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/us/websearch?"+ jsonToGetRequestParams(params),
		dataType: 'json', 
		beforeSend:function(){
			//显示列表
			isChart = true;
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				//清空原来的数据
				$(".each-row").remove();
				
				users = data.message;
				if(users.length == 0){
					layer.msg("无更多数据");
					return;
				}
				
				for(var i = 0; i < data.message.length; i++)
					buildEachRow(data.message[i], i);
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

function buildEachRow(user, index){
	var html = '';
	if( typeof(user.user_pic_path) != 'undefined' && isNotEmpty(user.user_pic_path)){
		html += '<div class="row list-row each-row" index='+ index +' data-id='+user.id+'>'+
			    	'<div class="col-lg-12">'+
						 '<div class="row">'+
					 		'<div class="col-lg-2">'+
					 			'<img class="img-circle" alt="" width="90%" height="130" src="'+ user.user_pic_path +'">'+
					 		'</div>'+
					 		'<div class="col-lg-10">'+
					 			'<div class="row">'+
						 			'<div class="col-lg-3">账号：'+ changeNotNullString(user.account) +'</div>'+
						 			'<div class="col-lg-3">真实姓名：'+ changeNotNullString(user.real_name) +'</div>'+
						 			'<div class="col-lg-3">中文名：'+ changeNotNullString(user.china_name) +'</div>'+
						 			'<div class="col-lg-3" padding-five>'+ 
						 				'<div class="row" style="text-align: right;"><button type="button" class="btn btn-primary btn-sm" onclick="showEditUser(this, '+ index +');">编辑</button></div>'+
						 			'</div>'+
					 			'</div>'+
					 			'<div class="row">'+
						 			'<div class="col-lg-3">出生日期：'+ changeNotNullString(user.birth_date) +'</div>'+
						 			'<div class="col-lg-3">性别：'+ changeNotNullString(user.sex) +'</div>'+
						 			'<div class="col-lg-3">籍贯：'+ changeNotNullString(user.nation) +'</div>'+
						 			'<div class="col-lg-3 padding-five">'+ 
						 				'<div class="row" style="text-align: right;"><button type="button" class="btn btn-primary btn-sm" onclick="deleteUser(this, '+ index +');">删除</button></div>'+
						 			'</div>'+
					 			'</div>'+
					 			'<div class="row">'+
						 			'<div class="col-lg-3">QQ号码：'+ changeNotNullString(user.qq) +'</div>'+
						 			'<div class="col-lg-3">手机号码：'+ changeNotNullString(user.mobile_phone) +'</div>'+
						 			'<div class="col-lg-3">注册时间：'+ changeNotNullString(user.register_time) +'</div>'+
						 			'<div class="col-lg-3 padding-five">'+ 
						 				'<div class="row" style="text-align: right;"><button type="button" class="btn btn-primary btn-sm" onclick="showSendMessage(this, '+ index +');">发信息</button></div>'+
						 			'</div>'+
					 			'</div>'+
					 			'<div class="row">'+
						 			'<div class="col-lg-3">邮件：'+ changeNotNullString(user.email) +'</div>'+
						 			'<div class="col-lg-3">教育背景：'+ changeNotNullString(user.education_background) +'</div>'+
						 			'<div class="col-lg-3">出生地：'+ changeNotNullString(user.native_place) +'</div>'+
						 			'<div class="col-lg-3 padding-five">'+ 
						 				'<div class="row" style="text-align: right;"><button type="button" class="btn btn-primary btn-sm" onclick="resetPassword(this, '+ index +');">重置密码</button></div>'+
						 			'</div>'+
					 			'</div>'+
					 			'<div class="row">'+
					 				'<div class="col-lg-9">'+
						 				'<div class="form-group">个人简介：'+ changeNotNullString(user.personal_introduction) +'</div>'+
									'</div>'+
									'<div class="col-lg-3 padding-five">'+ 
						 				'<div class="row" style="text-align: right;"><button type="button" class="btn btn-primary btn-sm" onclick="showUploadHeadLink(this, '+ index +');">上传头像</button></div>'+
						 			'</div>'+
					 			'</div>'+
					 		'</div>'+
						 '</div>'+
					'</div>'+
				'</div>';
				
	}else{
		html += '<div class="row list-row each-row" data-id='+user.id+'>'+
    	'<div class="col-lg-12">'+
			 '<div class="row">'+
		 		'<div class="col-lg-12">'+
		 			'<div class="row">'+
			 			'<div class="col-lg-3">账号：'+ changeNotNullString(user.account) +'</div>'+
			 			'<div class="col-lg-3">真实姓名：'+ changeNotNullString(user.real_name) +'</div>'+
			 			'<div class="col-lg-3">中文名：'+ changeNotNullString(user.china_name) +'</div>'+
			 			'<div class="col-lg-3 padding-five">'+ 
			 				'<div class="row" style="text-align: right;"><button type="button" class="btn btn-primary btn-sm" onclick="showEditUser(this, '+ index +');">编辑</button></div>'+
			 			'</div>'+
		 			'</div>'+
		 			'<div class="row">'+
			 			'<div class="col-lg-3">出生日期：'+ changeNotNullString(user.birth_date) +'</div>'+
			 			'<div class="col-lg-3">性别：'+ changeNotNullString(user.sex) +'</div>'+
			 			'<div class="col-lg-3">籍贯：'+ changeNotNullString(user.nation) +'</div>'+
			 			'<div class="col-lg-3 padding-five">'+ 
			 				'<div class="row" style="text-align: right;"><button type="button" class="btn btn-primary btn-sm" onclick="deleteUser(this, '+ index +');">删除</button></div>'+
			 			'</div>'+
		 			'</div>'+
		 			'<div class="row">'+
			 			'<div class="col-lg-3">QQ号码：'+ changeNotNullString(user.qq) +'</div>'+
			 			'<div class="col-lg-3">手机号码：'+ changeNotNullString(user.mobile_phone) +'</div>'+
			 			'<div class="col-lg-3">注册时间：'+ changeNotNullString(user.register_time) +'</div>'+
			 			'<div class="col-lg-3 padding-five">'+ 
			 				'<div class="row" style="text-align: right;"><button type="button" class="btn btn-primary btn-sm" onclick="showSendMessage(this, '+ index +');">发信息</button></div>'+
			 			'</div>'+
		 			'</div>'+
		 			'<div class="row">'+
			 			'<div class="col-lg-3">邮件：'+ changeNotNullString(user.email) +'</div>'+
			 			'<div class="col-lg-3">教育背景：'+ changeNotNullString(user.education_background) +'</div>'+
			 			'<div class="col-lg-3">出生地：'+ changeNotNullString(user.native_place) +'</div>'+
			 			'<div class="col-lg-3 padding-five">'+ 
			 				'<div class="row" style="text-align: right;"><button type="button" class="btn btn-primary btn-sm" onclick="resetPassword(this, '+ index +');">重置密码</button></div>'+
			 			'</div>'+
		 			'</div>'+
		 			'<div class="row">'+
		 				'<div class="col-lg-9">'+
			 				'<div class="form-group">个人简介：'+ changeNotNullString(user.personal_introduction) +'</div>'+
						'</div>'+
						'<div class="col-lg-3 padding-five">'+ 
			 				'<div class="row" style="text-align: right;"><button type="button" class="btn btn-primary btn-sm" onclick="showUploadHeadLink(this, '+ index +');">上传头像</button></div>'+
			 			'</div>'+
		 			'</div>'+
		 		'</div>'+
			 '</div>'+
		'</div>'+
	'</div>';
	}
	
	$(".mainContainer").append(html);
	
}

/**
* 展示显示编辑用户的模态框
*/
function showEditUser(obj, index){
	var user = users[index];
	$("#edit-user-info").attr("data-id", user.id);
	$("#edit-user-info").find('[name="account"]').val(changeNotNullString(user.account));
	$("#edit-user-info").find('[name="personal_introduction"]').val(changeNotNullString(user.personal_introduction));
	var status = user.status;
	var labels = $(".status-btn");
	for(var i = 0 ; i < labels.length; i++){
		if(parseInt($(labels[i]).find("input").attr("type-value")) == status){
			$(labels[i]).addClass("active");
			break;
		}
	}
	$("#edit-user-info").modal("show");
}
/**
* 执行编辑操作
*/
function editUser(obj){
	
	var userInfoObj = $(obj).closest("#edit-user-info");
	var account = userInfoObj.find('[name="account"]').val();
	
	if(isEmpty(account)){
		layer.msg("账号名称不能为空");
		userInfoObj.find('[name="account"]').focus();
		return;
	}
	
	
	var status = 0;
	var statusBtnObjs = $(".status-btn");
	for(var i = 0 ; i < statusBtnObjs.length; i++){
		if($(statusBtnObjs[i]).hasClass("active")){
			status = $(statusBtnObjs[i]).find("input").attr("type-value");
			break;
		}
	}
	
	var personal_introduction = userInfoObj.find('[name="personal_introduction"]').val();
	var id = $("#edit-user-info").attr("data-id");
	var params = {to_user_id: id, account: account, personal_introduction: personal_introduction, status: status, t: Math.random()};
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "put",
		data : params,
		url : "/us/ad/user",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			
			if(data.isSuccess){
				layer.msg(data.message +",1秒钟后自动刷新");
				setTimeout("window.location.reload();", 1000);
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
* 执行删除操作
*/
function deleteUser(obj, index){
	if(typeof(users) != 'undefined' && users.length > 0 && users.length > index){
		
		layer.confirm('您要删除该用户吗？注意：这是不可逆行为。', {
			  btn: ['确定','点错了'] //按钮
		}, function(){
			var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
			var id = $(obj).closest(".each-row").attr("data-id");
			$.ajax({
				type : "delete",
				url : "/us/user?to_user_id=" +id,
				dataType: 'json', 
				beforeSend:function(){
				},
				success : function(data) {
					layer.close(loadi);
					if(data.isSuccess){
						layer.msg(data.message +",1秒钟后自动刷新");
						setTimeout("window.location.reload();", 1000);
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
	}else{
		layer.msg("页面参数有误，请刷新！");
	}
}

/**
* 执行重置密码操作
*/
function resetPassword(obj, index){
	if(typeof(users) != 'undefined' && users.length > 0 && users.length > index){
		
		layer.confirm('您要重置该用户的登录密码吗？注意：这是不可逆行为。', {
			  btn: ['确定','点错了'] //按钮
		}, function(){
			var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
			var id = $(obj).closest(".each-row").attr("data-id");
			$.ajax({
				type : "put",
				data : {to_user_id: id, t: Math.random()},
				url : "/us/ad/resetPwd",
				dataType: 'json', 
				beforeSend:function(){
				},
				success : function(data) {
					layer.close(loadi);
					if(data.isSuccess){
						layer.msg(data.message +",1秒钟后自动刷新");
						reloadPage(1000);
					}else{
						ajaxError(data);
					}
				},
				error : function(data) {
					layer.msg("网络请求失败");
					ajaxError(data);
				}
			});
		}, function(){
		});
	}else{
		layer.msg("页面参数有误，请刷新！");
	}
}
/**
* 显示发送信息的模态框
*/
function showSendMessage(obj, index){
	var user = users[index];
	//用户没有邮件，不能发送邮件。
	if(isEmpty(user.email)){
		$("#send-message").find("#email").closest("label").addClass("disabled");
	}else{
		$("#send-message").find("#email").closest("label").removeClass("disabled");
	}
	$("#send-message").find(".send-message-btn").removeClass("active");
	$("#send-message").find(".send-message-btn:first-child").addClass("active");
	$("#send-message").attr("data-id", user.id);
	$("#send-message").modal("show");
}

/**
* 执行发送信息操作
*/
function sendMessage(obj){
	var userInfoObj = $(obj).closest("#send-message");
	var content = userInfoObj.find('[name="content"]').val();
	
	if(isEmpty(content)){
		layer.msg("发送信息内容不能为空");
		userInfoObj.find('[name="content"]').focus();
		return;
	}
	var type = $("#send-message").find(".btn-group label.active").children("input").attr("type-value");
	var id = $("#send-message").attr("data-id");
	var params = {to_user_id: id, type: type, content: content, t: Math.random()};
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "post",
		data : params,
		url : "/tl/sendMsg",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg(data.message);
				$("#send-message").modal("hide");
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
*显示上传用户头像模态框
*/
function showUploadHeadLink(obj, index){
	var user = users[index];
	$("#upload-user-head").attr("data-id", user.id);
	$("#upload-user-head").modal("show");
}

/**
*执行上传头像
*/
function uploadHeadLink(obj){
	var uploadUserHead = $(obj).closest("#upload-user-head");
	var link = uploadUserHead.find('input[name="link"]').val();
	
	if(isEmpty(link)){
		layer.msg("请先输入头像图片链接！");
		uploadUserHead.find('input[name="link"]').focus();
		return;
	}
	var id = $("#upload-user-head").attr("data-id");
	var params = {to_user_id: id, link: link, t: Math.random()};
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "post",
		data : params,
		url : "/fp/uploadHeadImgLink",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
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