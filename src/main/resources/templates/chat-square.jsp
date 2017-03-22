<%@page import="com.cn.leedane.chat_room.Util"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="java.util.UUID"%>
<%@page import="com.cn.leedane.controller.UserController"%>
<%
	Object obj = session.getAttribute(UserController.USER_INFO_KEY);
	UserBean userBean = null;
	String account = "";
	int userId = 0;
	boolean isLogin = obj != null;
	
	String bp = request.getScheme()+"://"+request.getServerName()
			+":"+request.getServerPort()+request.getContextPath()+"/";
	
	if(isLogin){
		userBean = (UserBean)obj;
		account = userBean.getAccount();
		userId = userBean.getId();
	}else{
		//response.sendRedirect(bp +"page/login.jsp?ref="+CommonUtil.getFullPath(request)+"&t="+UUID.randomUUID().toString());
	}
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>聊天广场</title>
	<link rel="stylesheet" href="other/layui/css/layui.css">
	<script src="js/base.js"></script>
	<style type="text/css">
		#main-container{
			margin-top: 60px;
		}
		
		#content{
			height: 300px;
			/* background-color: #F5F5F5; */
			margin-bottom: 10px;
			overflow-y: scroll; 
		}
		#send{
			height: auto; 
			bottom: 0;
		}
		.chat-user-name{
			font-size: 14px;
			font-family: "Pingfang SC",STHeiti,"Lantinghei SC","Open Sans",Arial,"Hiragino Sans GB","Microsoft YaHei","WenQuanYi Micro Hei",SimSun,sans-serif;
		}
		.chat-create-time{
			font-size: 14px;
			font-family: "Pingfang SC",STHeiti,"Lantinghei SC","Open Sans",Arial,"Hiragino Sans GB","Microsoft YaHei","WenQuanYi Micro Hei",SimSun,sans-serif;
		}
		.chat-list-row{
			border: 1px solid #F5F5F5;
		}
		
		.one{
			background-color: #BF0449 !important;
		}
		
		.two{
			background-color: #F2B705 !important;
		}
		.third{
			background-color: #D98E04 !important;
		}
		.play-screen{
			position: fixed;
			top: 100px;
			right: 0px;
			z-index: 999;
		}
	</style>
</head>
<body>
<%@ include file="common.jsp" %>
<script src="<%=basePath %>page/js/chart-square.js"></script>
<script type="text/javascript" src="<%=basePath %>page/other/jquery.md5.js"></script>
<script type="text/javascript" charset="utf-8" src="<%=basePath %>page/other/ueditor1_4_3_3-utf8-jsp/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8" src="<%=basePath %>page/other/ueditor1_4_3_3-utf8-jsp/ueditor.all.min.js"> </script>
<!--建议手动加在语言，避免在ie下有时因为加载语言失败导致编辑器加载失败-->
<!--这里加载的语言文件会覆盖你在配置项目里添加的语言类型，比如你在配置项目里配置的是英文，这里加载的中文，那最后就是中文-->
<script type="text/javascript" charset="utf-8" src="<%=basePath %>page/other/ueditor1_4_3_3-utf8-jsp/lang/zh-cn/zh-cn.js"></script>
<div class="main clearFloat">
	<div class="container" id="main-container">
	   <div class="row">
			<div class="col-lg-3">
				<div class="row">
					<div class="panel panel-info">
					    <div class="panel-heading">
					    	<% if(isLogin){ %>
					    		<%=account %>
					    		&nbsp;&nbsp;当前积分：<span id="score"></span>
					    	<%}else{ %>
					    		访客
					    		&nbsp;&nbsp;当前积分：<span id="score">0</span>
					        	<button class="btn btn-default panel-title" type="button" onclick="goToLogin();">去登录</button>
					        <%} %>
					    </div>
					    <!-- <div class="panel-body">
					        <div class="row">
					        	<div class="col-lg-12">
					        		欢迎签名：我是leedane，我来了！
					        		<button type="button" class="btn btn-default btn-xs">
									  <span class="glyphicon glyphicon-edit" aria-hidden="true"></span> 编辑
									</button>
					        	</div>
					        </div>
					    </div> -->
					</div>
				</div>
				<div class="row" id="active-users">
					<a href="javascript:void(0);" class="list-group-item active">今日活跃用户排名</a>
				</div>
			</div>
			<div class="col-lg-9">
				<div>系统当前时间为：2017-12-12 11：10：10</div>
				<div class="panel panel-default">
					<div class="panel-body" id="content">
						<!-- <div class="row chat-list-row">
					   		<div class="col-lg-2" style="text-align: center;margin-top: 10px;">
								<img width="60" height="60" class="img-circle hand" alt="" src=""  onclick="showSingleImg(this);"/>
							</div>
							<div class="col-lg-8">
								<div class="row" style="font-family: '微软雅黑'; font-size: 15px; margin-top: 10px;">
									<div class="col-lg-12">
										<span class="chat-user-name"><a href="JavaScript:void(0);" onclick="linkToMy(1)">leedane</a></span>   
										<span class="chat-create-time">&nbsp;&nbsp;2017-01-01 00:00</span>
									</div>
								</div>
								<div class="row" style="font-family: '微软雅黑'; font-size: 17px;margin-top: 5px;">
									<div class="col-lg-12">文/清澈的蓝挽一缕月光照亮夜晚的轩窗微凉的风摇曳丝丝岸柳似少女般漫妙轻舒弄影( 文章阅读网：www.sanwen.net)飘舞在迷人的曉风琬月下凝眸远望是江南的一帘秋水长天*轻移莲步採一朵清新的茉莉花</div>
								</div>
							</div>
							<div class="col-lg-2" style="text-align: center;margin-top: 10px;">
								<img width="60" height="60" class="img-circle hand" alt="" src=""  onclick="showSingleImg(this);"/>
							</div>
						</div>
						<div class="row chat-list-row">
					   		<div class="col-lg-2" style="text-align: center;margin-top: 10px;">
								<img width="60" height="60" class="img-circle hand" alt="" src=""  onclick="showSingleImg(this);"/>
							</div>
							<div class="col-lg-10">
								<div class="row" style="font-family: '微软雅黑'; font-size: 15px; margin-top: 10px;">
									<div class="col-lg-12">
										<span class="chat-user-name"><a href="JavaScript:void(0);" onclick="linkToMy(1)">leedane</a></span>   
										<span class="chat-create-time">&nbsp;&nbsp;2017-01-01 00:00</span>
									</div>
								</div>
								<div class="row" style="font-family: '微软雅黑'; font-size: 17px;margin-top: 5px;">
									<div class="col-lg-12">文/清澈的蓝挽一缕月光照亮夜晚的轩窗微凉的风摇曳丝丝岸柳似少女般漫妙轻舒弄影( 文章阅读网：www.sanwen.net)飘舞在迷人的曉风琬月下凝眸远望是江南的一帘秋水长天*轻移莲步採一朵清新的茉莉花</div>
								</div>
							</div>
						</div>
						<div class="row chat-list-row">
					   		<div class="col-lg-2" style="text-align: center;margin-top: 10px;">
								<img width="60" height="60" class="img-circle hand" alt="" src=""  onclick="showSingleImg(this);"/>
							</div>
							<div class="col-lg-10">
								<div class="row" style="font-family: '微软雅黑'; font-size: 15px; margin-top: 10px;">
									<div class="col-lg-12">
										<span class="chat-user-name"><a href="JavaScript:void(0);" onclick="linkToMy(1)">leedane</a></span>   
										<span class="chat-create-time">&nbsp;&nbsp;2017-01-01 00:00</span>
									</div>
								</div>
								<div class="row" style="font-family: '微软雅黑'; font-size: 17px;margin-top: 5px;">
									<div class="col-lg-12">文/清澈的蓝挽一缕月光照亮夜晚的轩窗微凉的风摇曳丝丝岸柳似少女般漫妙轻舒弄影( 文章阅读网：www.sanwen.net)飘舞在迷人的曉风琬月下凝眸远望是江南的一帘秋水长天*轻移莲步採一朵清新的茉莉花</div>
								</div>
							</div>
						</div> -->
						
					</div>
				<% if(isLogin){ %>
					<div class="panel-footer">
						<div class="row" id="send">
							<div class="col-lg-12">
								<div class="row">
									<div class="col-lg-12">
										<script id="editor" type="text/plain" style="width:100%;" onkeypress="if (event.keyCode == 13) sendMsg(this);"></script>
									</div>
								</div>
								<div class="row" style="margin-top: 8px;">
									<div class="col-lg-12">
										<button class="btn btn-default" type="button" onclick="sendMsg();">发送</button>
										<button class="btn btn-default" id="send-play-screen" type="button" onclick="sendPlayScreen();">发送弹屏(扣一分积分)</button>
										<button class="btn btn-default" type="button" onclick="clearMsg();">清屏</button>
									</div>
								</div>
							</div>
						</div>
					</div>
				<%} %>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="play-screen"></div>
</body>
<script type="text/javascript">
	var isLogin = <%=isLogin %>; //是否已经登录
	var userName = '<%=account %>'; //登录用户的名称
	var userId = <%=userId %> + "UNION" + Math.random(); //用户ID
	//实例化编辑器
    //建议使用工厂方法getEditor创建和引用编辑器实例，如果在某个闭包下引用该编辑器，直接调用UE.getEditor('editor')就能拿到相关的实例
    //var ue = UE.getEditor('editor');
	<% if(isLogin){ %>
    var ue = UE.getEditor('editor', {
        "initialFrameHeight": "100",
        autoHeightEnabled: false,
        elementPathEnabled : false,
        maximumWords: 10,
        wordCount: true,
        toolbars: [[
	              'fullscreen', 'source', '|', 'undo', 'redo', '|',
	              'bold', 'italic', 'underline', 'fontborder', 'strikethrough', 'blockquote', 'pasteplain', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', '|',
	              'rowspacingtop', 'rowspacingbottom', 'lineheight', '|',
	              'customstyle', 'paragraph', 'fontfamily', 'fontsize', '|',
	              'indent', '|',
	              'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|', '|',
	              'link', 'unlink', '|', 'imagenone', 'imageleft', 'imageright', 'imagecenter', '|',
	              'simpleupload', 'insertimage', 'emotion', 'scrawl', 'attachment', 'insertcode', '|',
	              'horizontal', '|',
	              'drafts'/* , 'help' 帮助 */
	          ]],
        enterTag: "&nbsp;"
    });
    <%} %>
    
  //回车执行登录
    document.onkeydown=function(event){
        var e = event || window.event || arguments.callee.caller.arguments[0];
        if(e && e.keyCode==13){ // enter 键
        	//sendMsg();
       }
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
            		userId = <%=Util.VISITORS_ID %> + "UNION" + Math.random(); //用户ID
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
    /**
    * 初始化函数
    */
    function init(){
    	//$("#content").empty();
    	initWebSocket(true);
    	
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
 			type : "post",
 			dataType: 'json',  
 			url : getBasePath() +"leedane/score/getTotalScore.action",
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
 					layer.msg(data.message);
 				}
 			},
 			error : function() {
 				layer.msg("网络请求失败");
 			}
 		});
     }
    
    /**
    *	查询活跃用户
    */
    function getActiveUsers(){
    	$.ajax({
			type : "post",
			dataType: 'json',  
			url : getBasePath() +"leedane/chat/square/getActiveUser.action",
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
				}
			},
			error : function() {
				layer.msg("网络请求失败");
			}
		});
    }
    
    /**
    *	显示
    */
    function initWebSocket(first){
    	ws = new WebSocket("ws://127.0.0.1:8080/leedaneMVC/websocket?account="+encodeURI(encodeURI(userId)));
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
    			ws.send(getSendJson(userId, "大家好，我是"+userName, true));
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
    		
    		if(isLogin && <%=userId %> == json.id){
    			container.append(buildEachRightRow(json));
    		}else{
    			container.append(buildEachLeftRow(json));
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
     	    	if(isNotEmpty(json.screenText)){
     	    		var ht = '<div id="'+ palyScreenContainerId +'" class="play-screen">'+ json.screenText +'</div>';
     	    	}else{
     	    		var ht = '<div id="'+ palyScreenContainerId +'" class="play-screen">'+ json.content +'</div>';
     	    	}
     	    	
     	    	$("body").append(ht);
     	    	//topArray
     	    	topIndex = topIndex + 1;
     	    	if(topIndex > topArray.length){
     	    		topIndex = 0;
     	    	}
     	    	
     	    	$("#"+palyScreenContainerId).css("top", topArray[topIndex] + "px");
     	        //setInterval('playScreen()',"1000");
     	        playScreen(palyScreenContainerId);
     	        
     	        //当前信息是自己发的，就重新去获取新的积分
     	        if(isLogin && <%=userId %> == json.id)
     	       		getScore();
     	    }
     	   
     	//}
     	   
    }
   	
    /**
    *	执行弹屏的动画效果
    */
    function playScreen(palyScreenContainerId){
    	//随机5秒到8秒
    	var time = 5000 + getRandomNumber1(4) * 1000;
    	var width = $("#"+palyScreenContainerId).width();
    	$("#"+palyScreenContainerId).animate({right: (pageW - width) +"px"}, time, function(){
    		$("#"+palyScreenContainerId).remove();
    	});
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
    	ws.send(getSendJson(userId, content));
    	//清空编辑器的内容
    	ue.setContent("");
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
    	ws.send(JSON.stringify({id: userId, content: content, type: "PlayScreen"}));
    	//清空编辑器的内容
    	ue.setContent("");
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
    	  layer.msg('已清空 ', {icon: 1, time: 800});
    	}, function(){
    	  
    	});
    }
    
    /**
     *	构建左侧的
     */
     function buildEachLeftRow(chat){
     	var html = '<div class="row chat-list-row">'+
 				   		'<div class="col-lg-1" style="text-align: center;margin-top: 10px;">';
 				   		if(isNotEmpty(chat.user_pic_path)){
 				   			html += '<img width="100%" style="max-width: 45px;" height="45" class="img-rounded hand" alt="" src="'+ chat.user_pic_path + '"  onclick="showSingleImg(this);"/>';
 				   		}else{
 				   			html += '<img width="100%" style="max-width: 45px;" height="45" class="img-rounded" alt="" src=""/>';
 				   		}
 							
 				html += '</div>'+
 						'<div class="col-lg-10">'+
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
	 					'<div class="col-lg-1" style="text-align: center;margin-top: 10px;">'+
	 					'</div>'+
 					'</div>';
     	
     	return html;
     }
    
     /**
      *	构建右侧的
      */
      function buildEachRightRow(chat){
      	var html = '<div class="row chat-list-row">'+
  				   		'<div class="col-lg-1" style="text-align: center;margin-top: 10px;">'+
  				   		'</div>'+
  						'<div class="col-lg-10"  style="text-align: right;">'+
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
	  					'<div class="col-lg-1" style="text-align: center;margin-top: 10px;">';
	  				if(isNotEmpty(chat.user_pic_path)){
				   			html += '<img width="100%" style="max-width: 45px;" height="45" class="img-rounded hand" alt="" src="'+ chat.user_pic_path + '"  onclick="showSingleImg(this);"/>';
				   		}else{
				   			html += '<img width="100%" style="max-width: 45px;" height="45" class="img-rounded" alt="" src=""/>';
				   		}
	 			html += '</div>'+
		 			'</div>';
      	
      	return html;
      }
	</script>
</html>