<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/page/common/basePath.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>微信账号绑定</title>
</head>

<script type="text/javascript" src="<%=basePath %>js/others/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="<%=basePath %>js/others/jquery.md5.js"></script>
<script type="text/javascript" src="<%=basePath %>other/layer/layer.js"></script>

<script type="text/javascript">
	function bind(){
		var basePath = $("#basePath").val();
		var url = basePath + "user_bingWechat.action";
		var userName = $("#userName").val();
		var userPassword = $("#userPassword").val();	
		var urlWindow = window.location.href; //取得当前的地址栏地址信息
		var FromUserName = getURLParam(urlWindow, "FromUserName");
		var currentType = getURLParam(urlWindow, "currentType");
		if(FromUserName == null || FromUserName == undefined || FromUserName == ""){
			alert("当前链接无效，请重新打开");
			return;
		}
		
		var data = '{"account" : "'+userName+'", "password" : "'+$.md5(userPassword)+'","FromUserName":"'+FromUserName+'","currentType":"'+currentType+'"}';
		$.ajax({
			type:'post',
			data:{params : data},
			url: url,
			dataType:'json',
			success:function(data){
				if(data.isSuccess){
					alert(data.message);
				}else{
					alert(data.message);
				}
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
</script>

<body>
	<table border="1" style="text-align: center;">
		<tr>
			<td>
				leedane用户名：<input type="text" id="userName" name="userName" value="Lee"/>
			</td>		
		</tr>
		<tr>
			<td>
				leedane密码：<input type="text" id="userPassword" name="userPassword" value="123"/>
			</td>
		</tr>
		<tr>
			<td>
				<input type="button" value="绑定账号" onclick="bind();"/>
			</td>
		</tr>
	</table>
</body>
</html>