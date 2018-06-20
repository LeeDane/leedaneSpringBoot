//对Date的扩展，将 Date 转化为指定格式的String
//月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
//年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
//例子： 
//(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
//(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.Format = function (fmt) { //author: meizz 
 var o = {
     "M+": this.getMonth() + 1, //月份 
     "d+": this.getDate(), //日 
     "H+": this.getHours(), //小时 
     "m+": this.getMinutes(), //分 
     "s+": this.getSeconds(), //秒 
     "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
     "S": this.getMilliseconds() //毫秒 
 };
 if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
 for (var k in o)
 if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
 return fmt;
}

/**
 * 判断json是否为空
 * @param e
 * @returns {Number}
 */
function isEmptyObject(e) {  
    var t;  
    for (t in e)  
        return false;  
    return true  
} 

/**
 * 判断字符串是否为空
 * @param str
 */
function isEmpty(str){
	return typeof(str) == 'undefined' || str == null || str == undefined || str == '' || str.trim == '' || str == 'null';
}

/**
 * 判断字符串不为空
 * @param str
 */
function isNotEmpty(str){
	return !isEmpty(str);
}

/**
 * 校验连接是否合法
 * @param link
 */
function isLink(link){
	var urlRegExp=/^((https|http|ftp|rtsp|mms)?:\/\/)+[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\':+!]*([^<>\"\"])*$/;
    return urlRegExp.test(url);
}


/**
 * 对空的字符串，以""输出
 * @param str
 * @returns
 */
function changeNotNullString(str){
	if(isEmpty(str))
		return "";
	return str;
}

/**
 * 对空的字符串，以""输出
 * @param str
 * @param key
 * @returns
 */
function changeObjNotNullString(obj, key){
	if(typeof(obj) == 'undefined')
		return '';
	if(isEmpty(obj[key]))
		return "";
	return obj[key];
}

/**
 * 删除最后一个字符
 * @param str
 */
function deleteLastStr(str){
	if(isEmpty(str))
		return str;
	return str.substring(0, str.length - 1);  
}

/**
 * 判断key是否在json里面
 * @param json
 * @param key
 */
function inJson(json, key){
	if(!json)
		return false;
	
	return isNotEmpty(json[key]);
}

/**
 * 展示图片的链接
 * @param index  当前心情的索引
 * @param imgIndex 当前心情图片的索引
 */
function showSingleImg(obj){
	var path = $(obj).attr("src");
	if(isNotEmpty(path)){
		var json = {
				  "title": "相册标题", //相册标题
				  "id": 0, //相册id
				  "start": 0 //初始显示的图片序号，默认0
				};
		var datas = new Array();
		var each = {};
		each.src = path;//原图地址
		each.alt = path;//缩略图地址
		datas.push(each);
		
		
		json.data = datas;
		
		layer.photos({
		    photos: json
		    ,shift: 1 //0-6的选择，指定弹出图片动画类型，默认随机
		  });
	}else{
		layer.msg("无法获取当前图片的路径");
	}
	
}

/**
 * 展示图片的链接
 * @param index  当前心情的索引
 * @param imgIndex 当前心情图片的索引
 */
function showImg(index, imgIndex){
	var mood = moods[index];
	var json = {
			  "title": "相册标题", //相册标题
			  "id": 0, //相册id
			  "start": imgIndex //初始显示的图片序号，默认0
			};
	var datas = new Array();
	var photos = mood.imgs.split(";");
	for(var i = 0; i < photos.length; i++){
		var each = {};
		var path = photos[i];
		each.src = path;//原图地址
		each.alt = path;//缩略图地址
		datas.push(each);
	}
	
	json.data = datas;
	
	layer.photos({
	    photos: json
	    ,shift: 1 //0-6的选择，指定弹出图片动画类型，默认随机
	  });
}

/**
 * 将form的serializeArray数组转化成json对象
 * @param array
 */
function serializeArrayToJsonObject(array){
	var json = {};
	if(array.length > 0){
		for(var i = 0; i < array.length; i++){
			var o = array[i];
			json[o.name] = o.value;
		}
	}
	return json;
}

/**
 * 自动刷新页面
 * @param time 多少秒后
 */
function reloadPage(time){
	setTimeout("window.location.reload();", time);
}

/**
 * 跳转到登录页面
 */
function linkToLogin(){
	var href = window.location.href;
	if(isEmpty(href))
		href = "";
	else
		href = "?ref=" + href;
	window.open("/lg"+ href, "_self");
}

/**
 * 跳转到指定的网址
 * @param link
 */
function openLink(link){
	if(isEmpty(link)){
		layer.msg("要跳转的链接不能为空");
		return;
	}
	window.open(link, '_blank');
}

/**
 * 跳转到404页面
 */
function linkTo404(errorMessage){
	if(isEmpty(errorMessage))
		errorMessage = "";
	else
		errorMessage = "?errorMessage=" + errorMessage;
	window.open("/404"+ errorMessage, "_self");
}

/**
 * 跳转到403页面
 */
function linkTo403(){
	window.open("/403", "_self");
}

/**
 * 跳转到我的个人中心
 * @param id
 */
function linkToMy(id){
	if(isEmpty(id)){
		layer.msg("该用户不存在，请联系管理员核实");
		return;
	}
	window.open("/my/"+id, "_self");
}

/**
 * 跳转到圈子页面
 * @param id
 */
function linkToCircle(id){
	if(isEmpty(id)){
		layer.msg("该圈子不存在，请联系管理员核实");
		return;
	}
	window.open("/cc/"+id, "_self");
}

/**
 * 跳转到帖子详细页面
 * @param circleId
 * @param postId
 */
function linkToPostDetail(circleId, postId){
	if(isEmpty(circleId) || isEmpty(postId)){
		layer.msg("该帖子不存在，请联系管理员核实");
		return;
	}
	window.open("/cc/"+circleId +"/post/"+ postId, "_self");
}

/**
 * 跳转到我的表名和表ID对应的详情
 * @param tableName
 * @param tableId
 * @param createUserId
 */
function linkToTable(tableName, tableId, createUserId){
	if(tableName == "t_blog"){
		goToReadFull(tableId);
	}else if(tableName == "t_mood"){
		goToReadMoodFull(tableId, createUserId);
	}else if(tableName == "t_circle_post"){
		goToReadPostFull(tableId);
	}
	return;
}

/**
 * 跳转到搜索
 */
function linkToSearch(searchKey){
	window.open('/s?q='+searchKey+'&t='+Math.random(), '_blank');
}

/**
 * 跳转到全文阅读
 * @param id
 */
function goToReadFull(id){
	//layer.msg("文章ID为："+id);
	if(isEmpty(id)){
		layer.msg("该博客不存在，请联系管理员核实");
		return;
	}
	window.open("/dt/"+id, "_blank");
}

/**
 * 跳转到帖子详情阅读
 * @param id
 */
function goToReadPostFull(id){
	//layer.msg("文章ID为："+id);
	if(isEmpty(id)){
		layer.msg("该帖子不存在，请联系管理员核实");
		return;
	}
	window.open("/cc/post/dt/"+id, "_blank");
}

/**
 * 跳转到心情详细阅读
 * @param mid
 * @param createUserId
 */
function goToReadMoodFull(mid, createUserId){
	if(isEmpty(mid)){
		layer.msg("该心情不存在，请联系管理员核实");
		return;
	}
	window.open("/user/"+createUserId+"/mood/"+mid+"/dt", "_blank");
}


/**
 * 跳转到商品详情页
 * @param productId
 */
function linkToProduct(productId){
	window.open('/mall/product/'+ productId +'/detail', '_blank');
}

/**
 * 添加cookie
 * @param key
 * @param value
 */
function addCookie(key, value){
	window.cookie  = key +'=' +value;
}

/**
 * 获取cookie
 * @param value
 */
function getCookie(value){
	if (document.cookie.length > 0){//先查询cookie是否为空，为空就return ""
		c_start = document.cookie.indexOf(value + "=")//通过String对象的indexOf()来检查这个cookie是否存在，不存在就为 -1　　
		if (c_start!=-1){
			c_start = c_start + value.length + 1;//最后这个+1其实就是表示"="号啦，这样就获取到了cookie值的开始位置
			c_end = document.cookie.indexOf(";",c_start);//其实我刚看见indexOf()第二个参数的时候猛然有点晕，后来想起来表示指定的开始索引的位置...这句是为了得到值的结束位置。因为需要考虑是否是最后一项，所以通过";"号是否存在来判断
			if (c_end == -1) 
					c_end = document.cookie.length;
			return unescape(document.cookie.substring(c_start, c_end));//通过substring()得到了值。想了解unescape()得先知道escape()是做什么的，都是很重要的基础，想了解的可以搜索下，在文章结尾处也会进行讲解cookie编码细节
		}
	}
	return ""
}


/**
 * 对数组对象进行排序，格式[{name: 'name', total: 0}, {name: 'name1', total: 100}]
 * @param key  根据上面格式，total可以传进去排序
 * @param desc  是否倒序排序(从大到小)，默认是
 * @returns {Function}
 */
function sortByObjectKey(key, desc) {
	return function(a,b){
		var value1 = a[key];
		var value2 = b[key];
		if(value1 < value2){
	        return desc ? 1 : -1;
	    }else if(value1 > value2){
	        return desc ? -1 : 1;
	    }else{
	        return 0;
	    }
	}
}

/**
 * 将日期字符串转成日期时间格式
 * @param time
 */
function formatStringToDateFormattime(str){
	return str.replace("T", " ") + ":00";
}
/**
 * 格式化日期格式
 * @param time
 */
function formatDateTime(time){
	var now;
	if(isNotEmpty(time))
		now = new Date(time);
	else
		now = new Date();
	
	return now.getFullYear() + "-" + fix((now.getMonth() + 1),2) + "-" + fix(now.getDate(),2) + "T" + fix(now.getHours(),2) + ":" + fix(now.getMinutes(),2);
}
function fix(num, length) {
	 return ('' + num).length < length ? ((new Array(length + 1)).join('0') + num).slice(-length) : '' + num;
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

/**
 * 获取随机整数(不包括0，包括number)
 * @param number
 */
function getRandomNumber(number){
	return Math.ceil(Math.random() * number);
}

/**
 * 获取随机整数(包括0，不包括number)
 * @param number
 */
function getRandomNumber1(number){
	return Math.floor(Math.random() * number);
}

/**
 * ajax请求error统一处理
 * @param data
 * @returns
 */
function ajaxError(data){
	var json = isJson(data)? data : eval('(' + data.responseText + ')');
	
	if(!json.message && json.responseText)
		json.message = json.responseText;
	//如果是需要用户登录
	if(json['responseCode'] && json.responseCode == 1001){
		linkToLogin();
	}else if(json['responseCode'] && json.responseCode == 404){
		linkTo404(json.message);
	}else if(json['responseCode'] && json.responseCode == 403){
		linkTo403(json.message);
	}else{
		if(json['message'])
			layer.msg(json.message);
		else{
			if(json['responseCode'])
				layer.msg("服务器处理异常，异常编码是："+json.responseCode);
			else{
				if(json.responseCode){
					layer.msg("服务器处理异常"+json.responseCode);
				}else{
					layer.msg("网络连接失败！");
				}
			}
				
		}
			
	}
}

/**
 * 判断obj是否为json对象
 * @param obj
 * @returns {Boolean}
 */
function isJson(obj){  
    return typeof(obj) == "object" && Object.prototype.toString.call(obj).toLowerCase() == "[object object]" && !obj.length;   
}  

/**
 * 将json对象转换成get请求参数字符串
 * @param json
 */
function jsonToGetRequestParams(json){
	var params = "";
	var i = 0;
	var length = 0;
	for(var key in json){
		length++;
	}
	for(var key in json){
		if(i == length -1){
			params += key + "=" + json[key];
		}else{
			params += key + "=" + json[key] +"&";
		}
	}
	
	return params;
}

/**
 * 这个方法做了一些操作、然后调用回调函数   
 * @param fn function对象
 * @param args 请求的参数,注意，这里必须是数组
 * @returns
 */
function doCallback(fn,args){    
    return fn.apply(this, args);  
} 

/**
 * 自定义模态框的点击关闭事件
 * @param obj
 */
function leeCloseModal(obj){
	$(".lee-modal-bg").css("display", "none");
}

/**
 * 自定义模态框的点击关闭事件
 * @param obj
 */
function leeShowModal(obj){
	$(".lee-modal-bg").css("display", "block");
}

/**
 * 校验链接是否是图片链接
 * @param link
 */
function isImg(link){
	 if(link){
		 var index = link.lastIndexOf(".");
		 if(index > 0){
			 link = link.substring(index + 1, link.length);
			 if(link && (link == "png" || link == "jpg" || link == "jpeg"))
				 return true;
		 }
	 }
	return false;
}

/**
 * 校验链接是否是视频链接
 * @param link
 */
function isVideo(link){
	 if(link){
		 var index = link.lastIndexOf(".");
		 if(index > 0){
			 link = link.substring(index + 1, link.length);
			 if(link && link == "mp4")
				 return true;
		 }
	 }
	return false;
}

/**
 * 校验链接是否是视频链接
 * @param link
 */
function getVideoHtml(link){
	 if(isVideo){
		 return '<video style="width: 100%; max-height: 500px; margin-top: 10px; margin-bottom: 10px;" src="'+ changeNotNullString(link) +'" controls="controls">Your browser does not support the video tag.</video>';
	 }
	return "";
}

/**
 * 校验链接是否是音频链接
 * @param link
 */
function isAudio(link){
	 if(link){
		 var index = link.lastIndexOf(".");
		 if(index > 0){
			 link = link.substring(index + 1, link.length);
			 if(link && link == "mp3")
				 return true;
		 }
	 }
	return false;
}

/**
 * 校验链接是否是音频链接
 * @param link
 */
function getAudioHtml(link){
	 if(isAudio){
		 return '<audio style="width: 100%; max-height: 500px; margin-top: 10px; margin-bottom: 10px;" src="'+ changeNotNullString(link) +'" controls="controls">Your browser does not support the audio tag.</audio>';
	 }
	return "";
}