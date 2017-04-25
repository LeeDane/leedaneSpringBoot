var colorsList = ["#FFC0CB", "#D8BFD8", "#DDA0DD", "#9932CC", "#E6E6FA", "#4169E1", "#E0FFFF"];
//回车执行登录
document.onkeydown=function(event){
    var e = event || window.event || arguments.callee.caller.arguments[0];
    if(e && e.keyCode==13){ // enter 键
    	//sendMsg();
   }
}

var websocket = null;

var atScrollIndex = 0;

//将消息显示在网页上
function setMessageInnerHTML(innerHTML){
  //document.getElementById('message').innerHTML += innerHTML + '<br/>';
	console.log("data---"+innerHTML)
}

//关闭连接
function closeWebSocket(){
  websocket.close();
}

$(function(){
	//验证浏览器是否支持WebSocket协议
    if(!window.WebSocket){
    	layer.alert('WebSockeet not supported by this browser!', {
    		  skin: 'layui-layer-molv' //样式类名
    		  ,closeBtn: 0
   		}, function(){
   			window.open("about:blank","_self").close();
   		});
    }else{
    	if(!isLogin){
    		//询问框
        	layer.confirm('是否以访客身份围观？', {
        	  btn: ['确定','去登录'] //按钮
        	}, function(){
        		userId = 12344555 + "UNION" + Math.random(); //用户ID
        		init();
        		layer.msg('您已选择以访客身份围观！', {
        		    time: 1000 //1s后自动关闭
        		    });
        	}, function(){
        		//登录
        		goToLogin();
        	});
    	}else{
    		init();
    	}
    }
  //去掉默认的contextmenu事件，否则会和右键事件同时出现。
    document.oncontextmenu = function(e){
        e.preventDefault();
    };
    document.getElementById("content").onmouseup=function(oEvent) {
        if (!oEvent) oEvent=window.event;
        if (oEvent.button==2) {
            //-- do something for user right click
            alert("Mouse up");
        }
    }
});
var ws;
var pageW = $(document.body).width(); //页面总高度 
var topArray = [100, 130, 170]; //距离顶部的可能数组
var topIndex = 0;//弹屏距离顶部的距离样式控制

function initWebSocket(){
	 websocket = new WebSocket("ws://leedanetf.tunnel.qydev.com/websocket?id=123");

	//连接发生错误的回调方法
	websocket.onerror = function(){
	  setMessageInnerHTML("error");
	};

	//连接成功建立的回调方法
	websocket.onopen = function(event){
		if(isLogin)
			sendMessageToServer(getSendJson(loginUserId, "hello，我是"+account, true));
	}

	//接收到消息的回调方法
	websocket.onmessage = function(event){
		//setMessageInnerHTML(event.data);
		log(event.data);
	}

	//连接关闭的回调方法
	websocket.onclose = function(){
		if(isLogin)
			sendMessageToServer(getSendJson(loginUserId, account+ "已离开聊天室", true));
	}

	//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
	window.onbeforeunload = function(){
	  websocket.close();
	}
}

/**
* 初始化函数
*/
function init(){
	//$("#content").empty();
	initWebSocket(true);
	
	$("#has-at-style").click(function(){
		$(this).hide();
		if(atScrollIndex > 0){
			$("#content").stop(true);
			$("#content").animate({scrollTop: $(".chat-list-row").eq(atScrollIndex).offset().top}, 1000);
			atScrollIndex = 0;
		}
	});
	
	var height = $("#content").height(); //页面总高度
    
    $("#content").scroll(function (e) {
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
	     
	    var divContentHeight = $("#content")[0].scrollHeight; //页面总高度 
	    var scrollT = $("#content").scrollTop(); //滚动条top 
	    console.log("差---->" +(divContentHeight-scrollT) + ", scrollT=" + scrollT +", pageH="+ divContentHeight);
	    if(divContentHeight - scrollT < 100 + height){
	    	
	    }
	}); 
    
    getActiveUsers();
    //定时10分钟去查询活跃用户
    setInterval("getActiveUsers()","60000");
    //获取登录用户积分
    if(isLogin)
    	getScore();
}

/**
 *	获取积分
 */
 function getScore(){
 	$.ajax({
		dataType: 'json',  
		url : "sc/score/total",
		beforeSend:function(){
		},
		success : function(data) {
			if(data.isSuccess){
				var sc = data.message;
				if(sc < 1){
					//设置无法发弹屏
					$("#send-play-screen").attr("disabled", "disabled");
				}
				$("#score").html(sc);
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
*	查询活跃用户
*/
function getActiveUsers(){
	$.ajax({
		dataType: 'json',  
		url : "/cs/user/active",
		beforeSend:function(){
		},
		success : function(data) {
			//layer.msg(data.message);
			$("#active-users").find(".active-users-item").remove();
			if(data.isSuccess){
				var message = data.message;
				if(message.length > 0){
					var colorClass = "";
					for(var i = 0; i < message.length; i++){
						if(i == 0){
							colorClass = "one";
						}else if(i == 1){
							colorClass = "two";
						}else if(i == 2){
							colorClass = "third";
						}else{
							colorClass = "";
						}
						var userPicPath = "";
						if(isNotEmpty(message[i].user_pic_path)){
							userPicPath = message[i].user_pic_path;
						}
						$("#active-users").append('<a href="javascript:void(0);" class="list-group-item active-users-item" onclick="linkToMy('+ message[i].create_user_id+');"><img width="30" height="30" class="img-circle hand" alt="" src="'+ userPicPath +'"  onclick="showSingleImg(this);"/>&nbsp;&nbsp; '+ message[i].account+'<span class="badge '+ colorClass +'">'+ message[i].count +'</span></a>');
					}
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
*	显示
*/
function initWebSocket1(first){
	ws = new WebSocket("");
	//监听消息
	ws.onmessage = function(event){
		log(event.data);
	}
 
	//关闭WebSocket
	ws.onclose = function(event){
		initWebSocket(false);
	}
	//打开WebSocket
	ws.onopen = function(event){
		if(first){
			ws.send(getSendJson(loginUserId, "大家好，我是"+userName, true));
		}
	}
	ws.onerror = function(event){
		//alert(1);
	}
}

/**
*	构建发送的json数据
*/
function getSendJson(userId, content, welcome){
	if(welcome){
		var json = {id: userId, content: content, type: "welcome"};
	}else{
		var json = {id: userId, content: content};
	}
	
	return JSON.stringify(json);
}
var log = function(s){
	/* if(document.readyState !== "complete"){
		log.buffer.pust(s);
	}else{ */
		var json = eval('(' + s + ')'); 
		var container = $("#content");
		
		if(isLogin && loginUserId == json.id && json['type'] && json.type == "welcome")
			return;
		
		//处理错误提示信息
		if(json['type'] && json.type == "error"){
			container.append(buildEachErrorRow(json));
			return;
		}
		
		
		if(isLogin && loginUserId == json.id){
			container.append(buildEachRightRow(json));
		}else{
			container.append(buildEachLeftRow(json));
		}
		
		//处理at某人
		if(json['at_other'] && isNotEmpty(json.at_other)){
			console.log("at--"+ json.at_other);
			var others = json.at_other.split(",");
			for(var j = 0; j < others.length; j++){
				if(loginUserId == others[j]){
					$("#has-at-style").show();
					atScrollIndex = $(".chat-list-row").length - 1;
					//展示at用户
					break;
				}
			}
		}
		
		var height = container.height(); //页面总高度
		var divContentHeight = container[0].scrollHeight; //页面总高度 
 	    var scrollT = container.scrollTop(); //滚动条top
 	    
 	    //对于滑动不远的数据，直接滚动回最新的地方
 	    var offset = divContentHeight - scrollT - height;
 	    if(offset < 400){
 	    	$("#content").scrollTop(divContentHeight - height);
 	    }
 	    if(json.type == 'PlayScreen'){
 	    	var palyScreenContainerId = "play-screen-" + getRandomNumber(10000);
 	    	
 	    	var userPath = "";
 	    	if(isNotEmpty(json.user_pic_path)){
 	    		userPath = json.user_pic_path;
 	    	}
 	    	if(isNotEmpty(json.screenText)){
 	    		var ht = '<div id="'+ palyScreenContainerId +'" class="play-screen"><span class="screen-play-img"><img class="img-circle hand" width="25" height="25" src="'+ userPath +'"></span><span class="screen-play-content">'+ json.screenText +'</span></div>';
 	    	}else{
 	    		var ht = '<div id="'+ palyScreenContainerId +'" class="play-screen"><span class="screen-play-img"><img class="img-circle hand" width="25" height="25" src="'+ userPath +'"></span><span class="screen-play-content">'+ json.content +'</span></div>';
 	    	}
 	    	
 	    	$("body").append(ht);
 	    	//topArray
 	    	topIndex = topIndex + 1;
 	    	if(topIndex > topArray.length){
 	    		topIndex = 0;
 	    	}
 	    	
 	    	var color = colorsList[getRandomNumber1(colorsList.length)];
 	    	var palyScreenContainer = $("#"+palyScreenContainerId);
 	    	palyScreenContainer.css("top", topArray[topIndex] + "px");
 	    	palyScreenContainer.css("background-color", color);
 	    	palyScreenContainer.css("border", "1px solid "+ color);
 	    	palyScreenContainer.find(".screen-play-content").css("line-height", palyScreenContainer.outerHeight() + "px");
 	        //setInterval('playScreen()',"1000");
 	        playScreen(palyScreenContainer);

 	        //当前信息是自己发的，就重新去获取新的积分
 	    	if(isLogin && loginUserId == json.id)
 	       		getScore();
 	    }
 	   
 	//}
 	   
}

/**
*	执行弹屏的动画效果
*/
function playScreen(palyScreenContainer){
	//随机8秒到14秒
	var time = 8000 + getRandomNumber1(6) * 1000;
	var width = palyScreenContainer.outerWidth();
	palyScreenContainer.animate({right: (pageW - width) +"px"}, time, function(){
		palyScreenContainer.remove();
	});
	
	palyScreenContainer.hover(function(){  
		palyScreenContainer.stop();  
     },function(){  
		 palyScreenContainer.animate({right: (pageW - width) +"px"}, time, function(){
			 palyScreenContainer.remove();
		 });
     })  
	//right = right + 150;
}

/**
*	发送信息
*/
function sendMsg(){
	//校验内容
  	var content = ue.getContent();
  	if(isEmpty(content)){
  		layer.msg("内容不能为空");
  		ue.focus();
  		return;
  	}
  	
  	var buttons = $("#at-other-container").children("button");
  	if(buttons && buttons.length > 0){
  		var atOther = "";
  		for(var i = 0; i < buttons.length; i++){
  			atOther += $(buttons[i]).attr("uid") + ",";
  		}
  		atOther = atOther.substring(0,atOther.length-1)
  		sendMessageToServer(JSON.stringify({id: loginUserId, at_other: atOther, content: content}));
  	}else{
  		sendMessageToServer(getSendJson(loginUserId, content));
  	}
  	
  	
	//清空编辑器的内容
	ue.setContent("");
	$("#at-other-container").empty();
}

/**
 * 把消息发送到服务器
 * @param msg
 */
function sendMessageToServer(msg){
	websocket.send(msg);
}

/**
*	发送弹屏信息
*/
function sendPlayScreen(){
	//校验内容
  	var content = ue.getContent();
  	if(isEmpty(content)){
  		layer.msg("内容不能为空");
  		ue.focus();
  		return;
  	}
  	
  	var buttons = $("#at-other-container").children("button");
  	if(buttons && buttons.length > 0){
  		var atOther = "";
  		for(var i = 0; i < buttons.length; i++){
  			atOther += $(buttons[i]).attr("uid") + ",";
  		}
  		atOther = atOther.substring(0,atOther.length-1)
  		sendMessageToServer(JSON.stringify({id: loginUserId, at_other: atOther, content: content, type: "PlayScreen"}));
  	}else{
  		sendMessageToServer(JSON.stringify({id: loginUserId, content: content, type: "PlayScreen"}));
  	}
  	
	//清空编辑器的内容
	ue.setContent("");
	$("#at-other-container").empty();
}

/**
*	清屏
*/
function clearMsg(){
	//询问框
	layer.confirm('确定清空聊天列表？', {
	  btn: ['确定','点错了'] //按钮
	}, function(){
	  $("#content").empty();
	  $("#at-other-container").empty();
	  layer.msg('已清空 ', {icon: 1, time: 800});
	}, function(){
	  
	});
}

/**
 * 删除At某人
 * @param obj
 */
function deleteAt(obj){
	var o = $(obj);
	o.remove();
}

/**
 * At某人
 * @param account
 * @param id
 */
function atOther(account, id){
	console.log("account="+ account);
	//获取所有At列表，判断是否已经存在
	var buttons = $("#at-other-container").children("button");
	if(buttons){
		for(var i = 0; i < buttons.length; i++){
			if($(buttons[i]).attr("uid") == id)
				return;
		}
	}
	
	$("#at-other-container").append('<button type="button" class="btn btn-default btn-xs" uid="'+ id +'" onclick="deleteAt(this)">' +
			  							'@'+ account +' <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>' +
									'</button>');
}

/**
 *	构建左侧的
 */
 function buildEachLeftRow(chat){
	//var canClick = isLogin && chat.id != loginUserId && chat.account;
	 var canClick = (isLogin && chat.id > 0 && chat.id != loginUserId && chat.account) || (!isLogin && chat.id > 0 && chat.account) ;
 	var html = '<div class="row chat-list-row'+ (canClick ? ' hand' : '') +'" '+ (canClick ? 'onclick="atOther(\''+ chat.account +'\','+ chat.id +');"': '') +'>'+
			   		'<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2" style="text-align: center;margin-top: 10px;">';
			   		if(isNotEmpty(chat.user_pic_path)){
			   			html += '<img style="width: 40px; Height: 40px;"" class="img-rounded hand" alt="" src="'+ chat.user_pic_path + '"  onclick="showSingleImg(this);"/>';
			   		}else{
			   			html += '<img style="width: 40px; Height: 40px;" class="img-rounded" alt="" src=""/>';
			   		}
						
			html += '</div>'+
					'<div class="col-lg-10 col-md-10 col-sm-8 col-xs-8">'+
						'<div class="row" style="font-family: \'微软雅黑\'; font-size: 15px; margin-top: 10px;">'+
							'<div class="col-lg-12">'+
								'<span class="chat-user-name"><a href="JavaScript:void(0);" onclick="linkToMy('+ chat.id +')">'+ chat.account +'</a></span>'+
								'<span class="chat-create-time">&nbsp;&nbsp;'+ chat.time +'</span>'+
							'</div>'+
						'</div>'+
						'<div class="row" style="font-family: \'微软雅黑\'; font-size: 17px;margin-top: 5px;">'+
							'<div class="col-lg-12">'+ chat.content +'</div>'+
						'</div>'+
					'</div>'+
 					'<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2" style="text-align: center;margin-top: 10px;">'+
 					'</div>'+
				'</div>';
 	
 	return html;
 }

 /**
  *	构建右侧的
  */
  function buildEachRightRow(chat){
  	var html = '<div class="row chat-list-row">'+
			   		'<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2" style="text-align: center;margin-top: 10px;">'+
			   		'</div>'+
					'<div class="col-lg-10 col-md-10 col-sm-8 col-xs-8"  style="text-align: right;">'+
						'<div class="row" style="font-family: \'微软雅黑\'; font-size: 15px; margin-top: 10px;">'+
							'<div class="col-lg-12">'+
								'<span class="chat-create-time">'+ chat.time +'</span>'+
								'<span class="chat-user-name"><a href="JavaScript:void(0);" onclick="linkToMy('+ chat.id +')">&nbsp;&nbsp;'+ chat.account +'</a></span>'+
							'</div>'+
						'</div>'+
						'<div class="row" style="font-family: \'微软雅黑\'; font-size: 17px;margin-top: 5px;">'+
							'<div class="col-lg-12">'+ chat.content +'</div>'+
						'</div>'+
					'</div>'+
  					'<div class="col-lg-1 col-md-1 col-sm-2 col-xs-2" style="text-align: center;margin-top: 10px;">';
  				if(isNotEmpty(chat.user_pic_path)){
			   			html += '<img style="width: 40px; Height: 40px;" class="img-rounded carousel-inner hand" alt="" src="'+ chat.user_pic_path + '"  onclick="showSingleImg(this);"/>';
			   		}else{
			   			html += '<img style="width: 40px; Height: 40px;" class="img-rounded carousel-inner " alt="" src=""/>';
			   		}
 			html += '</div>'+
	 			'</div>';
  	
  	return html;
  }
  
  /**
   * 构建错误提示行
   * @param chat
   * @returns {String}
   */
   function buildEachErrorRow(chat){
   	var html = '<div class="row chat-list-row">'+
   					'<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12" style="text-align: center;margin-top: 10px; color: #999">'+
   						chat.content
   					'</div>'+
  				'</div>';
   	
   	return html;
   }