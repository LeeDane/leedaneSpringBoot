layui.use(['layer'], function(){
	layer = layui.layer;
	//$tree.treeview({data: getTree(), enableLinks: false});
	$tree = $('#tree');
	//获取子节点
	getChildNodes(0, 0);  
});
/*
 * api文档的地址
 * http://www.cnblogs.com/mfc-itblog/p/5233453.html
 * 新增动态添加节点的例子代码：http://blog.csdn.net/qq_25628235/article/details/51719917
 * 新增动态删除节点的例子代码：http://blog.csdn.net/u012718733/article/details/53288584
*/
var $tree;

/**
 * 获取直接一级的节点
 * @param nodeId 当前节点的id，为0表示根节点
 * @param pid
 */
function getChildNodes(nodeId, pid){
	 var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/cg/category/"+ pid +"/children",
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				var treeData = data.message;
				if(pid < 1){
					
					/**
					 * 展示数据并同时添加展开事件的监听(事件的监听必须在绑定数据之后)
					 */
					$tree.treeview({data: treeData, enableLinks: false, onNodeExpanded: function(event, data) {
						console.log("data=");
						getChildNodes(data.nodeId, data.id);

					}});
				}else{
					$tree.treeview("addNodes", [nodeId, { node: treeData}]); 
				}
				//$tree.treeview({data: getTree(nodeId, data.message), enableLinks: false});
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
 * 添加根节点(只有管理员才有此操作)
 * @param obj
 */
function addRoot(obj){
	layer.prompt({title: '请输入根节点的文本(不能为空，不能超过8个字符)', formType: 0}, function(pass, promptIndex){
	  //layer.close(promptIndex);
	  //loading层
	  var index = layer.load(1, {
	    shade: [0.1,'#fff'] //0.1透明度的白色背景
	  });
	  var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	  $.ajax({
		  	type: 'POST',
		  	data: {text: pass},
			url : "/cg/category",
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				if(data.isSuccess){
					//关闭弹出loading
					layer.closeAll();
					layer.msg("根节点添加成功，1秒后自动刷新");
					reloadPage(1000);
				}else{
					layer.close(loadi);
					ajaxError(data);
				}
			},
			error : function(data) {
				layer.close(loadi);
				ajaxError(data);
			}
		});
	});
}

/**
 * 添加子节点
 * @param obj
 */
function addNode(obj){
	var nodeSelect = $tree.treeview('getSelected', 0);
	if(nodeSelect && nodeSelect.length > 0 ){
		layer.prompt({title: '请输入子节点的文本(不能为空，不能超过8个字符)', formType: 0}, function(pass, promptIndex){
			  //layer.close(promptIndex);
			  //loading层
			  var index = layer.load(1, {
			    shade: [0.1,'#fff'] //0.1透明度的白色背景
			  });
			  var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
			  $.ajax({
				  	type: 'POST',
				  	data: {text: pass, pid: nodeSelect[0].id},
					url : "/cg/category",
					dataType: 'json', 
					beforeSend:function(){
					},
					success : function(data) {
						if(data.isSuccess){
							//关闭弹出loading
							layer.closeAll();
							var addNode = data.message;
							addNode.state = {};
							addNode.state.expanded = true;
							$tree.treeview("addNode", [nodeSelect[0].nodeId, { node: addNode}]); 
						}else{
							layer.close(loadi);
							ajaxError(data);
						}
					},
					error : function(data) {
						layer.close(loadi);
						ajaxError(data);
					}
				});
		});
	}else{
		layer.msg("请先选择一个节点！");
	}
}

/**
 * 更新子节点
 * @param obj
 */
function updateNode(obj){
	var nodeSelect = $tree.treeview('getSelected', 0);
	if(nodeSelect && nodeSelect.length > 0 ){
		var isSystem = nodeSelect[0].is_system;
		if(!isAdmin && isSystem){
			layer.msg("您没有修改根节点的权限");
			return;
		}
		layer.prompt({title: '请输入修改子节点的文本(不能为空，不能超过8个字符)', formType: 0}, function(pass, promptIndex){
			  //layer.close(promptIndex);
			  //loading层
			  var index = layer.load(1, {
			    shade: [0.1,'#fff'] //0.1透明度的白色背景
			  });
			  var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
			  $.ajax({
				  	type: 'PUT',
				  	data: {text: pass},
					url : "/cg/category/" + nodeSelect[0].id,
					dataType: 'json', 
					beforeSend:function(){
					},
					success : function(data) {
						if(data.isSuccess){
							//关闭弹出loading
							layer.closeAll();
							layer.msg(data.message);
							$tree.treeview("updateNode", [nodeSelect[0].nodeId, { node: { text: pass, state: {expanded: true}} }]); 
						}else{
							layer.close(loadi);
							ajaxError(data);
						}
					},
					error : function(data) {
						layer.close(loadi);
						ajaxError(data);
					}
				});
		});
	}else{
		layer.msg("请先选择一个您要修改的节点！");
	}
}

/**
 * 删除节点以及其下面全部的子节点
 * @param obj
 */
function deleteNode(obj){
	var nodeSelect = $tree.treeview('getSelected', 0);
	if(nodeSelect && nodeSelect.length > 0 ){
		var isSystem = nodeSelect[0].is_system;
		if(!isAdmin && isSystem){
			layer.msg("您没有删除根节点的权限");
			return;
		}
		
		layer.confirm('您要删除该节点以及其下面的全部节点吗？\r\n删除成功后将无法恢复，请慎重！', {
			  btn: ['确定','点错了'] //按钮
		}, function(){
			var id = nodeSelect[0].id;
			var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
			$.ajax({
			  	type: 'DELETE',
				url : "/cg/category/"+ id,
				dataType: 'json', 
				beforeSend:function(){
				},
				success : function(data) {
					layer.close(loadi);
					if(data.isSuccess){
						layer.msg(data.message);
						$tree.treeview('deleteNode', [ nodeSelect[0].nodeId, { silent: true} ]); 
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
		
	}else{
		layer.msg("请先选择一个要删除的节点！");
	}
}