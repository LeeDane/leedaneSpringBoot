/*
 * api文档的地址
 * http://www.cnblogs.com/mfc-itblog/p/5233453.html
 * 新增动态添加节点的例子代码：http://blog.csdn.net/qq_25628235/article/details/51719917
 * 新增动态删除节点的例子代码：http://blog.csdn.net/u012718733/article/details/53288584
*/
var $tree;
var isFirst = false;
$(function(){
	$tree = $('#tree');
	//获取子节点
	getChildNodes(0, rootId);
});

/**
 * 获取直接一级的节点
 * @param nodeId
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
				if(!isFirst){
					/**
					 * 展示数据并同时添加展开事件的监听(事件的监听必须在绑定数据之后)
					 */
					$tree.treeview({data: treeData, enableLinks: false, onNodeSelected: function(event, data) {
						
	
					}, onNodeExpanded: function(event, data) {
						getChildNodes(data.nodeId, data.id);
	
					}});
					isFirst = true;
				}else{
					$tree.treeview("addNodes", [nodeId, { node: treeData}]); 
				}
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
 * 父窗口调用获取选择的分类
 */
function getSelectData(){
	var nodeSelect = $tree.treeview('getSelected', 0);
	if(nodeSelect && nodeSelect.length > 0 ){
		return nodeSelect[0];
	}else{
		return null;
	}
}