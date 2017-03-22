var connId = ""; //当前页面的连接ID
  $(function () {
      $("#show-login-qr-code-btn").on("click", function(){
    	  loadQRCode();
    	  $("#load-qr-code").modal("show");
      });
      
      $(".form-signin-heading-small").on("click", function(){
    	  var tx = $(this).text();
    	  if(tx == "登录"){
    		  $("#login-container").show();
    		  $("#register-container").hide();
    	  }else{
    		  $("#register-container").show();
    		  $("#login-container").hide();
    	  }
      });
      
      //init();
      
  });
  
  function doLogin(){
	  var account = $("#account").val();
	  if(isEmpty(account)){
		  layer.msg("请输入账号");
		  return;
	  }
	  
	  var password = $("#password").val();
	  if(isEmpty(password)){
		  layer.msg("请输入密码");
		  return;
	  }
	  var params = {account: account, pwd: $.md5(password), t: Math.random()};
	  var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	  $.ajax({
			type : "post",
			data : params,
			url : "us/login",
			dataType: 'json',
			withCredentials:true,
			beforeSend:function(request){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					layer.msg(data.message);
					var ref = getURLParam(window.location.href, "ref");
					if(isEmpty(ref))
						ref = "/index";
					window.location.href = ref;
				}else{
					var errorHtml ='<div class="alert alert-warning alert-dismissible" role="alert">'+
									  '<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>'+
									  '<strong>警告!</strong>'+ data.message +
									'</div>';
					$("#errorMessage").html(errorHtml);
				}
			},
			error : function() {
				layer.close(loadi);
				layer.msg("网络请求失败");
			}
		});
  }
  
  function doRegister(){
	  var account = $("#rg-account").val();
	  if(isEmpty(account)){
		  layer.msg("请输入账号");
		  $("#rg-account").focus();
		  return;
	  }
	  
	  var password = $("#rg-password").val();
	  if(isEmpty(password)){
		  layer.msg("请输入密码");
		  $("#rg-password").focus();
		  return;
	  }
	  
	  var confirmPassword = $("#rg-confirmPassword").val();
	  if(isEmpty(confirmPassword)){
		  layer.msg("请输入确认密码");
		  $("#rg-confirmPassword").focus();
		  return;
	  }
	  
	  if(password != confirmPassword){
		  layer.msg("两次输入的密码不匹配");
		  $("#rg-confirmPassword").focus();
		  return;
	  }
	  
	  var mobilePhone = $("#rg-phone").val();
	  if(isEmpty(mobilePhone) || mobilePhone.length != 11){
		  layer.msg("请输入正确的手机号码");
		  $("#rg-phone").focus();
		  return;
	  }
	  
	  var params = {mobilePhone: mobilePhone, account: account, confirmPassword: $.md5(confirmPassword), password: $.md5(password), t: Math.random()};
	  var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	  $.ajax({
			type : "post",
			data : params,
			url : "us/registerByPhoneNoValidate.action",
			dataType: 'json',
			withCredentials:true,
			beforeSend:function(request){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					layer.msg(data.message);
					window.location.href="122";
				}else{
					var errorHtml ='<div class="alert alert-warning alert-dismissible" role="alert">'+
									  '<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>'+
									  '<strong>警告!</strong>'+ data.message +
									'</div>';
					$("#registerErrorMessage").html(errorHtml);
				}
			},
			error : function() {
				layer.close(loadi);
				layer.msg("网络请求失败");
			}
		});
  }
  
  function init(){
      JS.Engine.on({  
    	  scan_login : function(data){
    		 	data = eval('(' + data + ')');
    			if(data.isSuccess){
    				if("cancel" == data.message){
    					//window.close();
    					window.open("about:blank","_self").close();
    					//window.open("","_self").close()
    				}else
    					window.location.reload();
    			}
      			//alert("返回的数据是："+data);
          }  
      });  
      JS.Engine.start('/leedaneMVC/conn?channel=scan_login'); 
      JS.Engine.on('start',function(cid){
    	connId = cid;
      	console.log("长链接连接"+cid);
      });
      JS.Engine.on('stop',function(cause, cid, url, engine){//页面刷新执行
      	console.log("长链接已经断连接"+cid);
      	connId = cid;
      	//移除id
      	$.ajax({
				type : "post",
				data : "cid=" + cid+"&&channel=scan_login",
				url : "destroyedComet4jServlet",
				async: false,
				//dataType : "json",
				timeout:1000,
				cache : false,
				beforeSend : function() {
				},
				success : function(data) {
					console.log("移除id"+cid);
				},
				error : function() {
				}
		});
      });
	}
  
//回车执行登录
document.onkeydown=function(event){
    var e = event || window.event || arguments.callee.caller.arguments[0];
    if(e && e.keyCode==13){ // enter 键
    	doLogin();
   }
}
 
//页面关闭和刷新执行方法
  window.onbeforeunload = onbeforeunload_handler;
  window.onunload = onunload_handler;
  function onbeforeunload_handler() {//页面关闭执行
  	if(connId != ""){
  		$.ajax({
  			type : "post",
  			data : "cid=" + connId +"&channel=scan_login",
  			url : "destroyedComet4jServlet",
  			async: false,
  			//dataType : "json",
  			timeout:1000,
  			cache : false,
  			beforeSend : function() {
  			},
  			success : function(data) {
  				console.log("移除id"+cId);
  			},
  			error : function() {
  			}
  		});
  	}
  	//return connId;
  }
  function onunload_handler() {//页面关闭执行
		
	}
  
  function loadQRCode(){
	  var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	  $.ajax({
			type : "post",
			data : "cid="+connId,
			url : "loginQrCode",
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					$("#modal-qr-code-img").attr("src", data.message);
				}else{
					layer.msg(data.message);
				}
				console.log(data);
			},
			error : function() {
				layer.close(loadi);
				layer.msg("网络请求失败");
			}
		});
  }
  
  /**
   * 获取url地址中的参数的方法
   * @param url url地址
   * @param sProp  参数的名称
   * @returns
   */
  function getURLParam(url, sProp) {
  	if(!url)
  		url = window.location.href; //取得当前的饿地址栏地址信息
  	
  	// 正则字符串
  	var re = new RegExp("[&,?]" + sProp + "=([^\\&]*)", "i");
  	// 执行正则匹配
  	var a = re.exec(url);
  	if (a == null) {
  		return "";
  	}
  	return a[1];
  }