layui.use(['layer', 'laydate'], function(){
	layer = layui.layer;
	laydate = layui.laydate;
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-baby").addClass("active");
});

/**
 * 提交表单
 */
function submitWash(){
	
	//检查空判断
	var formControl = $('#myform').find(".form-control");
	var params = {};
	var flag = true;
	formControl.each(function(index){
		var name = $(this).attr("name");
		var empty = $(this).attr("empty");
		if(empty && empty == "false" && isEmpty($(this).val())){
			$(this).focus();
			layer.msg($(this).attr("placeholder"));
			flag = false;
			return flag;
		}
			
		params[name] = $(this).val();
	});
	
	if(flag){
		//var data=$('#myform').serializeArray();
		//data = formArrayToObject(data);
		//params["id"] = dataId;
		params['occur_time'] = params.occur_date + " " +params.occur_time +":00";
		params.life_type = 3
		params['wash_end_time'] = params.wash_end_date + " " +params.wash_end_time +":00";;
		var loadi = layer.load('努力加载中…');
		$.ajax({
			type : "post",
			data: params,
			url : "/baby/"+ babyId +"/life",
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
					
					layer.close(loadi);
					if(data.isSuccess){
						layer.msg("添加成功");
						window.location.reload();
					}else
						layer.msg(data.message);
					
			},
			error : function(dd) {
				layer.close(loadi);
				layer.msg("网络请求失败");
			}
		});
	}
	
	
}

/**
 * 将form的serializeArray();处理后的数据转换成json对象的方法
 * @param data
 */
function formArrayToObject(data){
	var jsonObject = {};
	if(!data)
		return jsonObject;
	
	for(var i = 0; i < data.length; i++){
		var object = data[i];
		var key = object['name'];
		var value = object['value'];
		jsonObject[key] = value;
	}
	console.log(jsonObject);
	return jsonObject;
}