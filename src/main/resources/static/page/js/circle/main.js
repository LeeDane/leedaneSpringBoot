$(function(){
	
	$("[data-toggle='tooltip']").tooltip();
	
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-circle").addClass("active");
	
	$(".tooltip").css("display", "block");
	
	//加入圈子
	$(".into-circle").click(function(){
		$.ajax({
			url : "/cc/join/check?cid="+circleId,
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				if(data.isSuccess){
					if(data.message){
						var question = data.question;
						layer.prompt({title: question, formType: 0}, function(pass, promptIndex){
						  var index = layer.load(1, {
						    shade: [0.1,'#fff'] //0.1透明度的白色背景
						  });
						  joinCircle(pass);
						});
					}else{
						//询问框
						layer.confirm('您确定加入该圈子吗？', {
						  btn: ['确定','放弃'] //按钮
						}, function(){
							joinCircle();
						}, function(){
						  
						});
					}
				}else{
					ajaxError(data);
				}
			},
			error : function(data) {
				ajaxError(data);
			}
		});
	});
	
	//离开圈子
	$(".circle-leave").click(function(){
		//询问框
		layer.confirm('您确定退出该圈子吗？退出将清空您关于该圈子的全部记录，不可恢复，请谨慎！', {
		  btn: ['确定','放弃'] //按钮
		}, function(){
			leaveCircle();
		}, function(){
		  
		});
	});
	//add-clock-in打卡
	$(".add-clock-in").click(function(){
		doClockIn();
	});
});

/**
 * 申请加入圈子
 * @param answer
 */
function joinCircle(answer){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type: 'POST',
		data: {cid: circleId, answer: answer},
		url : "/cc/join",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg("加入圈子成功，1秒后自动刷新");
				reloadPage(1000);
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 离开该圈子
 */
function leaveCircle(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type: 'DELETE',
		url : "/cc/leave/"+ circleId,
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg("离开该圈子成功，1秒后自动刷新");
				reloadPage(1000);
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 执行打卡操作
 */
function doClockIn(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type: 'POST',
		data: {forms: 'web网页端'},
		url : "/cc/clockIn/"+circleId,
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg("您已经打卡成功，明天记得早点过来，1秒后自动刷新");
				reloadPage(1000);
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}