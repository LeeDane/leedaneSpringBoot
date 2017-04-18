$(function(){
	$('[name="account"]').focus();
	$('[name="register_time"]').val(formatDateTime());
});

/**
*	执行添加操作
*/
function add(obj){
	var account = $('[name="account"]').val();
	if(isEmpty(account)){
		$('[name="account"]').focus();
		layer.msg("请先输入账号！");
		return;
	}
	
	var password = $('[name="password"]').val();
	if(isEmpty(password)){
		$('[name="password"]').focus();
		layer.msg("请先输入登录密码！");
		return;
	}
	
	var confirmPassword = $('[name="confirm_password"]').val();
	if(isEmpty(confirmPassword)){
		$('[name="confirm_password"]').focus();
		layer.msg("请再次输入登录密码！");
		return;
	}
	
	if(password != confirmPassword){
		$('[name="confirm_password"]').focus();
		layer.msg("两次输入的登录密码不一致！");
		return;
	}
	
	var mobilePhone = $('[name="mobile_phone"]').val();
	if(isEmpty(mobilePhone) || mobilePhone.length != 11){
		$('[name="mobile_phone"]').focus();
		layer.msg("请输入11位的手机号码！");
		return;
	}
	
	
	var formControl = $(".form-control");
	var params = {};
	formControl.each(function(index){
		var name = $(this).attr("name");
		params[name] = $(this).val();
	});
	console.log(params);
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "post",
		data : params,
		url : "/us/user",
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