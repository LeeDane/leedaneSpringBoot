$(function(){
	
});

/**
 * 按图标的点击事件
 * @param obj
 */
function btnSelectMaterial(obj){
	 createSelectMaterialModal(this, 1, 1, 'afterSelect');
}

/**
 * 选择素材后的回调函数
 */
function afterSelect(links){
	 $('#shop-img').attr("src", links.split(";")[0]);
	 $('body').removeClass("modal-open");
}
