<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%

	String basePath = request.getScheme()+"://"+request.getServerName()
				+":"+request.getServerPort()+request.getContextPath()+"/"; 
	String content = String.valueOf(request.getAttribute("content"));
	String imgs = String.valueOf(request.getAttribute("imgs"));
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1">
<title>全文阅读</title>

<!-- 百度JQUERYCDN -->
<script type="text/javascript" src="http://libs.baidu.com/jquery/1.9.1/jquery.min.js"></script>
<style type="text/css">
	*{
		padding: 0;
		margin: 0;
	}
	#content{
		margin-top: 1px;
		margin-left: 0px !important;
		margin-right: 0px !important;
	}
</style>
</head>
<body style="overflow-x:hidden;overflow-y:scroll">
	<div id="content" style="width:100%;" onclick="showOnclickMessage()"><%=content %></div>
</body>

<script type="text/javascript">
	$(function(){
	});
	
	function clickImg(obj, index){
		if(winW < 600){
			var width = parseInt($(obj).width());
			var height = parseInt($(obj).height());
			webview.clickImg("<%=imgs %>", index, width, height);
		}else{
			layer.msg("fff");
		}
	}
	
</script>
</html>