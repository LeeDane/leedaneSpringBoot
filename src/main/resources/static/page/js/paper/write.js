var $contentContainer;
layui.use(['layer'], function(){
	layer = layui.layer;
	$contentContainer = $("textarea[name='content']");
	$contentContainer.markdown();
	
	$("[data-toggle='tooltip']").tooltip();
});
