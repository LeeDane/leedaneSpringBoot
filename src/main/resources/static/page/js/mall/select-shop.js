//注意：parent 是 JS 自带的全局对象，可用于操作父页面
var global_CallBackFun; //获取数据成功后的回调函数
/**
 * 创建选择商店的Modal
 * @param obj 当前点击的对象
 * @param select 最多选择的数量
 * @param callBackFun 回调的函数
 */
function createSelectShopModal(obj, select, callBackFun){
	if(isEmpty(callBackFun)){
		layer.msg("根据实际需要，你还未指定成功后的回调函数");
		return;
	}
	global_CallBackFun = callBackFun;
	var noPhone = $(window).width() > 600; //通过简单判断当前的设备是否是移动端使用
	var width = noPhone ? "600px": $(window).width()+"px";
	
	//iframe层-父子操作
	layer.open({
			  title: '选择商店',
			  type: 2,
			  area: [width, '400px'],
			  fixed: false, //不固定
			  maxmin: true,
			  content: '/mall/shop/select'
			});
}

/**
 * 获取子窗口传递的数据
 * @param shop 参数
 */
function getSelectShopData(layerIndex, shop){	
	if(isEmptyObject(shop)){
		layer.msg("请先选中商店！");
		return;
	}
	
	doCallback(eval(global_CallBackFun), [shop]);
	parent.layer.close(layerIndex);
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