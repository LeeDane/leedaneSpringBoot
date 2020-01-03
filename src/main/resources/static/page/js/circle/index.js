layui.use(['layer'], function(){
	layer = layui.layer;
	$("[data-toggle='tooltip']").tooltip();
	
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-circle").addClass("active");
	
	$(".tooltip").css("display", "block");
	
	//创建圈子
	$(".create-circle").click(function(){
		$.ajax({
			url : "/cc/check",
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				if(data.success){
					showCreateModal(data.message);
				}else{
					ajaxError(data);
				}
			},
			error : function(data) {
				ajaxError(data);
			}
		});
	});
	
	//圈子图片的绑定
	$(document).on("click", "img.img-circle", function(event){
		event.stopPropagation();//阻止冒泡 
		var $img = $(this);
		var id = $img.attr("data");
		linkToCircle(id);
	});  
	
	resetParticlesHeight();
});

/**
 * 展示创建圈子的modal
 * @param number
 */
function showCreateModal(number){
	//prompt层
	layer.prompt({title: '输入圈子名称并确认(还能创建'+ number +'个)', formType: 0}, function(pass, promptIndex){
	  //layer.close(promptIndex);
	  //loading层
	  var index = layer.load(1, {
	    shade: [0.1,'#fff'] //0.1透明度的白色背景
	  });
	  
	  $.ajax({
		  	type: 'POST',
			url : "/cc",
			data: {name: pass},
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				//关闭弹出loading
				layer.close(index);
				if(data.success){
					layer.msg(data.message+ "，1秒后自动刷新");
					//添加圈子成功，关闭窗口
					layer.close(promptIndex);
					reloadPage(1000);
				}else{
					ajaxError(data);
				}
			},
			error : function(data) {
				layer.close(index);
				ajaxError(data);
			}
		});
	});
}