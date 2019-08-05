  $(function () {
      $("#show-login-qr-code-btn").on("click", function(){
    	  //验证浏览器是否支持WebSocket协议
    	  if(!window.WebSocket){
    		  layer.msg("您当前的浏览器不支持扫描登录功能。");
    		  return;
    	  }
    	  init();
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
	  
	  var crypt = new JSEncrypt();
	  crypt.setPublicKey($("#publicKey").val());
	  //password = crypt.encrypt(password);
	    
	  var params = {account: account, pwd: crypt.encrypt($.md5(password)), remember: $("#remember").prop("checked"), t: Math.random()};
	  //params[parameterName] = token;
	  var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	  $.ajax({
			type : "post",
			data : params,
			url : "/us/login",
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
			error : function(data) {
				layer.close(loadi);
				ajaxError(data);
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
	  
	  var crypt = new JSEncrypt();
	  crypt.setPublicKey($("#publicKey").val());
	    	  
	  var params = {mobilePhone: crypt.encrypt(mobilePhone), account: account, confirmPassword: crypt.encrypt($.md5(confirmPassword)), password: crypt.encrypt($.md5(password)), t: Math.random()};
	  var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	  $.ajax({
			type : "post",
			data : params,
			url : "us/phone/register/noValidate",
			dataType: 'json',
			withCredentials:true,
			beforeSend:function(request){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					layer.msg(data.message);
					reloadPage(100);
				}else{
					var errorHtml ='<div class="alert alert-warning alert-dismissible" role="alert">'+
									  '<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>'+
									  '<strong>警告!</strong>'+ data.message +
									'</div>';
					$("#registerErrorMessage").html(errorHtml);
				}
			},
			error : function(data) {
				layer.close(loadi);
				ajaxError(data);
			}
		});
  }
  
  var websocket;
  
function init(){
	//验证浏览器是否支持WebSocket协议
    if(!window.WebSocket){
    	layer.alert('WebSockeet not supported by this browser!', {
    		  skin: 'layui-layer-molv' //样式类名
    		  ,closeBtn: 0
   		}, function(){
   			//
   			closeModal();
   			$("#show-login-qr-code-btn").hide();
   		});
    }else{
    	initWebsocket();
   }
}
  
//回车执行登录
document.onkeydown=function(event){
    var e = event || window.event || arguments.callee.caller.arguments[0];
    if(e && e.keyCode==13){ // enter 键
    	doLogin();
   }
}
 
function initWebsocket(){
	websocket = new WebSocket("ws://129.28.172.37:8089/scanLogin");

	//连接发生错误的回调方法
	websocket.onerror = function(){
	  setMessageInnerHTML("error");
	};

	//连接成功建立的回调方法
	websocket.onopen = function(event){
		
	}

	//接收到消息的回调方法
	websocket.onmessage = function(event){
		//setMessageInnerHTML(event.data);
		//event.data
		var data = eval('(' + event.data + ')');
		var message = data['message'];
		if(message == 'login'){
			checkToken(data.token, data.userid);
		}else{
			loadQRCode(data.message);
		}
	}

	//连接关闭的回调方法
	websocket.onclose = function(){
		
	}

	//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
	window.onbeforeunload = function(){
	  websocket.close();
	}
}

//将消息显示在网页上
function setMessageInnerHTML(innerHTML){
  //document.getElementById('message').innerHTML += innerHTML + '<br/>';
	alert("data---"+innerHTML)
}
/**
 * 校验token
 * @param connId
 */
function checkToken(token, userid){
	  var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	  layer.msg("正在检查校验");
	  $.ajax({
		  	type : "post",
			data : {token: token, userid: userid, t: Math.random()},
			url : "/us/scan/check",
			dataType: 'json', 
			beforeSend:function(request){
				request.setRequestHeader("token", token);
				request.setRequestHeader("userid", userid);
			},
			success : function(data) {
				if(data.isSuccess)
					reloadPage(1);
				else
					ajaxError(data);
			},
			error : function(data) {
				layer.close(loadi);
				ajaxError(data);
			}
		});
}

//关闭连接
function closeWebSocket(){
  websocket.close();
}

  
  function loadQRCode(connId){
	  var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	  $.ajax({
			data : "cnid="+connId,
			url : "/tl/loginQrCode",
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
  
  function closeModal(){
	  $("#load-qr-code").modal("hide");
  }