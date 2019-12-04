var $usersContainer;
layui.use(['layer', 'util'], function(){
	layer = layui.layer;
	util = layui.util;
	//获取所有的在线用户列表
	$usersContainer = $("#users-container");
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
		    $usersContainer.empty();
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

/**
** 获取每一条数据
*/
function buildEachRow(json, index){
    var title = json.location + '---' + setTimeAgo(json.time);
    var html = '<button type="button" class="btn btn-danger btn-sm user-item" data-session="'+ json.session+'" style="margin-top: 10px;" title="'+ title +'" onclick="logout(this);">'+
                      '<span class="glyphicon glyphicon-off" aria-hidden="true"></span> <font style="font-size: 16px;">'+ json.name + '</font>(<small>' + json.ip  + '</small>)' +
                    '</button>';

    $usersContainer.append(html);
}

/**
** 剔除登录用户
*/
function logout(obj){
    var session = $(obj).data("session");
    layer.confirm('您要剔除该用户吗？', {
    		  btn: ['确定','点错了'] //按钮
    	}, function(){
    		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
    		$.ajax({
    			type: "delete",
    			url : "/us/logoutOther?session=" + session,
    			dataType: 'json',
    			beforeSend:function(){
    			},
    			success : function(data) {
    				layer.close(loadi);
    				if(data.isSuccess){
    					layer.msg(data.message + ",1秒后自动刷新");
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
    	}, function(){
    	});
}