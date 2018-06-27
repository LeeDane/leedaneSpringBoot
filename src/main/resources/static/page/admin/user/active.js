layui.use(['layer'], function(){
	layer = layui.layer;
	//获取所有的在线用户列表
	getActive();
});

/**
 * 获取所有的在线用户列表
 */
function getActive(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/us/actives?t="+ Math.random(),
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				//清空原来的数据
				$(".each-row").remove();
				
				users = data.message;
				if(users.length == 0){
					layer.msg("无更多数据");
					return;
				}
				
				for(var i = 0; i < data.message.length; i++)
					buildEachRow(data.message[i], i);
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