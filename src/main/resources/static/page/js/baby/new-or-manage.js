var born = true;
layui.use(['layer', 'laydate', 'element'], function(){
	layer = layui.layer;
	laydate = layui.laydate;
	element = layui.element;
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-baby").addClass("active");
	
	//触发事件
	//监听Tab切换，以改变地址hash值
	  element.on('tab(resultTabBrief)', function(data){
		  console.log(this); //当前Tab标题所在的原始DOM元素
		  console.log(data.index); //得到当前Tab的所在下标
		  switch(data.index){
		  case 0://出生
			    born = true;
		  		break;
		  	case 1:  //备孕
		  		born = false;
			  break;
		  }
		});
});

/**
 * 提交表单
 */
function submitThis(){
	var formControl;
	//检查空判断
	if(born)
		formControl = $('#bornForm').find(".form-control");
	else
		formControl = $('#noBornForm').find(".form-control");
	
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
		params["id"] = dataId;
		params['born'] = born;
		var loadi = layer.load('努力加载中…');
		$.ajax({
			type : babyId > 0 ? "PUT": "post",
			data: params,
			url : babyId > 0 ? "/baby/"+ babyId: "/baby/add",
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
					
					layer.close(loadi);
					if(data.success){
						layer.msg("添加成功");
						if(babyId > 0)
							window.location.reload();
						else
							window.open("/baby", "_self");
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
 * 删除单个宝宝
 */
function deleteThis(obj){
	layer.confirm('您要删除这个宝宝吗？ 这是不可逆行为，请慎重操作！', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "delete",
			url : "/baby/" + babyId,
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.success){
					layer.msg(data.message + ",1秒后自动刷新");
					window.open("/baby", "_self");
				}else{
					ajaxError(data);
				}
			},
			error : function(data) {
				layer.close(loadi);
				ajaxError(data);
			}
		});
	}, function(){
	});
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