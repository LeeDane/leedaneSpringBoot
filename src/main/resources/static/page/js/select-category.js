/**
 * 创建选择素材的Modal
 * @param obj 当前点击的对象
 * @param callBackFun 回调的函数
 */
function createSelectCategoryModal(obj, rootId, callBackFun){
	if(isEmpty(callBackFun)){
		layer.msg("根据实际需要，你还未指定成功后的回调函数");
		return;
	}
	var modalId = "modal-select-category";
	$("#"+modalId).remove();
	var html = '<div class="modal fade" id="'+ modalId +'" tabindex="-1" role="dialog" aria-labelledby="uploadImageModalLabel" aria-hidden="true">'+
						'<div class="modal-dialog">'+
					'<div class="modal-content">'+
						'<div class="modal-header">'+
							'<button type="button" class="close" data-dismiss="modal" aria-hidden="true">'+
								'&times;'+
							'</button>'+
							'<h4 class="modal-title" id="uploadImageModalLabel">'+
								'选择分类 <a href="/cg" class="btn btn-primary btn-sm" role="button">分类管理中心</a>'+
							'</h4>'+
						'</div>'+
						'<div class="modal-body modal-body-update-image"  style="height: 600px;">'+
							'<iframe id="select-category-iframe" old-src="/cg/select" width="100%" height="100%" frameborder="0"></iframe>'+
						'</div>'+
						'<div class="modal-footer">'+
							'<button type="button" class="btn btn-default" data-dismiss="modal">关闭'+
							'</button>'+
							'<button type="button" class="btn btn-primary update-image-btn" onclick="getSelectData(\''+ callBackFun +'\');">'+
								'确定选择'+
							'</button>'+
						'</div>'+
					'</div>'+
				'</div>'+
			'</div>';
	$("body").append(html);
	var $container = $("#"+modalId);
	$container.modal({backdrop: 'static', keyboard: false});
	var width = $container.find(".modal-body").height();
	$("#select-category-iframe").attr("src", "/cg/select?rootId="+ rootId);
	$container.modal('show');
}

/**
 * 获取子窗口传递的数据
 * @param callBackFun 获取数据成功后的回调函数
 */
function getSelectData(callBackFun){
	var data;
	try{
		data = document.getElementById("select-category-iframe").contentWindow.getSelectData();
	}catch(err){
		//在这里处理错误
		layer.msg("选择分类出现错误，信息是："+err.message);
		return;
	 }
	
	if(data == null){
		layer.msg("请先选择一个分类节点！");
		return;
	}
	
	doCallback(eval(callBackFun), [data.id, data.text]);
	
	var modalId = "modal-select-category";
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