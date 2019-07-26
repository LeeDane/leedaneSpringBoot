/**
 * 创建选择素材的Modal
 * @param obj 当前点击的对象
 * @param select 最多选择的数量
 * @param selectType 选择类型1是图片
 * @param callBackFun 回调的函数
 */
function createSelectMaterialModal(obj, select, selectType, callBackFun){
	if(isEmpty(callBackFun)){
		layer.msg("根据实际需要，你还未指定成功后的回调函数");
		return;
	}
	var modalId = "modal-select-material";
	$("#"+modalId).remove();
	var html = '<div class="modal fade" id="'+ modalId +'" tabindex="-1" role="dialog" aria-labelledby="uploadImageModalLabel" aria-hidden="true">'+
						'<div class="modal-dialog">'+
					'<div class="modal-content">'+
						'<div class="modal-header">'+
							'<button type="button" class="close" data-dismiss="modal" aria-hidden="true">'+
								'&times;'+
							'</button>'+
							'<h4 class="modal-title" id="uploadImageModalLabel">'+
								'选择素材 <a href="/mt" target="_blank" class="btn btn-primary btn-sm" role="button">素材管理中心</a>'+
							'</h4>'+
						'</div>'+
						'<div class="modal-body modal-body-update-image"  style="height: 600px;">'+
							'<iframe id="select-material-iframe" old-src="/mt/select" width="100%" height="100%" frameborder="0"></iframe>'+
						'</div>'+
						'<div class="modal-footer">'+
							'<button type="button" class="btn btn-default" data-dismiss="modal">关闭'+
							'</button>'+
							'<button type="button" class="btn btn-primary update-image-btn" onclick="getSelectMaterialData(\''+ callBackFun +'\');">'+
								'确定选择'+
							'</button>'+
						'</div>'+
					'</div>'+
				'</div>'+
			'</div>';
	$("body").append(html);
	/*$("body").attr("SCROLLBAR-FACE-COLOR", "rgb(10,236,209)");//滚动条凸出部分的颜色
	$("body").attr("SCROLLBAR-HIGHLIGHT-COLOR", "rgb(23,255,155)");//滚动条空白部分的颜色
	$("body").attr("SCROLLBAR-SHADOW-COLOR", "rgb(255,116,23)");//立体滚动条阴影的颜色
	$("body").attr("SCROLLBAR-3DLIGHT-COLOR", "rgb(66,93,127)");//滚动条亮边的颜色
	$("body").attr("SCROLLBAR-ARROW-COLOR", "rgb(93,232,255)");//上下按钮上三角箭头的颜色
	$("body").attr("SCROLLBAR-TRACK-COLOR", "rgb(255,70,130)");//滚动条的背景颜色
	$("body").attr("SCROLLBAR-DARKSHADOW-COLOR", "rgb(10,0,209)");//滚动条强阴影的颜
	$("body").attr("SCROLLBAR-BASE-COLOR", "rgb(66,93,128)");//滚动条的基本颜色*/
	var $container = $("#"+modalId);
	$container.modal({backdrop: 'static', keyboard: false});
	var width = $container.find(".modal-body").height();
	$("#select-material-iframe").attr("src", "/mt/select?width="+ width +"&select="+ select +"&selectType="+selectType);
	$container.modal('show');
}

/**
 * 获取子窗口传递的数据
 * @param callBackFun 获取数据成功后的回调函数
 */
function getSelectMaterialData(callBackFun){
	var data;
	var links = "";
	try{
		data = document.getElementById("select-material-iframe").contentWindow.getSelectMaterialData();
		for(var d in data){
			links += data[d] +';';
		}
	}catch(err){
		//在这里处理错误
		layer.msg("选择文件出现错误，信息是："+err.message);
		return;
	 }
	
	if(isEmpty(links)){
		layer.msg("请先选中文件！");
		return;
	}
	
	doCallback(eval(callBackFun), [deleteLastStr(links)]);
	
	var modalId = "modal-select-material";
	$("#"+modalId).modal('hide');
	$("#"+modalId).remove();
	//只删除最后添加的模糊框
	$(".modal-backdrop").eq($(".modal-backdrop").length -1).remove();
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