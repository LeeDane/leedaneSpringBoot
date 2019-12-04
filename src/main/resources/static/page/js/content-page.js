$(function(){
	});
	
function clickImg(obj, index){
	var screenW = !(typeof winW === 'undefined');
	//大屏幕下 
	if(screenW && winW > 800){
		showImg(index);
	}else{
		
		var width = parseInt($(obj).width());
		var height = parseInt($(obj).height());
		webview.clickImg(imgs, index, width, height);
	}
}

//展示图片的链接
function showImg(index){	
	//切割获取图像数组
	var imgArr = imgs.split(";");
	var json = {
			  "title": "相册标题", //相册标题
			  "id": 0, //相册id
			  "start": index //初始显示的图片序号，默认0
			};
	var datas = new Array();
	for(var i = 0; i < imgArr.length; i++){
		var each = {};
		var path = imgArr[i];
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