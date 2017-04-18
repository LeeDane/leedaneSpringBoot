var permissions;
var currentIndex = 0;
var pageSize = 2;
var totalPage = 0;
//浏览器可视区域页面的高度
$(function(){
	
	$("input[type='checkbox']").toggle(function(){
		//$("tr.each-row").find("input[type='checkbox']").attr("checked", "checked");
	}, function(){
		//$("tr.each-row").find("input[type='checkbox']").removeAttr("checked");
	});
	//默认查询操作
	getPermissions();
});

/**
* 查询
*/
function getPermissions(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/pm/permissions?"+ jsonToGetRequestParams(getQueryPagingParams()),
		dataType: 'json', 
		beforeSend:function(){
		
		},
		success : function(data) {
			layer.close(loadi);
			//清空原来的数据
			$(".table").find(".each-row").remove();
			if(data.isSuccess){
				if(data.message.length == 0){
					$(".table").append('<tr class="each-row"><td colspan="7">空空的，还没有数据</td></tr>');
					return;
				}
				
				permissions = data.message;
				for(var i = 0; i < permissions.length; i++){
					$(".table").append(buildRow(permissions[i], i));
				}
				
				pageDivUtil(data.total);
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			isLoad = false;
			ajaxError(data);
		}
	});
}

/**
 * 添加权限
 */
function addPermission(){
	$("#add-or-edit-permission").modal("show");
}

/**
 * 修改权限
 */
function editPermission(){
	var checkboxs = $("tr.each-row").find('input[type="checkbox"]:checked');
	
	if(!checkboxs || checkboxs.length == 0){
		layer.msg("请选择您要编辑的权限");
		return;
	}
	if(checkboxs && checkboxs.length > 1 ){
		layer.msg("一次只能编辑一个权限");
		return;
	}
	
	var permission = permissions[$(checkboxs[0]).closest("tr").attr("index")];
	$("#add-or-edit-permission").attr("data-id", permission.id);
	$("#add-or-edit-permission").find('input[name="code"]').val(permission.permission_code);
	$("#add-or-edit-permission").find('input[name="name"]').val(permission.permission_name);
	$("#add-or-edit-permission").find('input[name="order"]').val(permission.permission_order);
	$("#add-or-edit-permission").find('textarea[name="desc"]').val(permission.permission_desc);
	if(permission.status == 1){
		$("#add-or-edit-permission").find('input[name1="status_normal"]').attr("checked", "checked");
		$("#add-or-edit-permission").find('input[name1="status_disabled"]').removeAttr("checked");
	}else{
		$("#add-or-edit-permission").find('input[name1="status_disabled"]').attr("checked", "checked");
		$("#add-or-edit-permission").find('input[name1="status_normal"]').removeAttr("checked");
	}
	$("#add-or-edit-permission").modal("show");
}

/**
 * 删除权限
 */
function deletePermission(){
	var checkboxs = $("tr.each-row").find('input[type="checkbox"]:checked');
	
	if(!checkboxs || checkboxs.length == 0){
		layer.msg("请选择您要删除的权限");
		return;
	}
	
	layer.confirm('您要删除这'+ checkboxs.length +'条权限记录吗？', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var pmids = "";
		for(var i = 0; i < checkboxs.length; i++){
			pmids += $(checkboxs[i]).closest("tr.each-row").attr("data-id") +",";
		}
		
		pmids = deleteLastStr(pmids);
		
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "delete",
			url : "/pm/permissions?pmids=" + pmids,
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

/**
 * 添加获取编辑的提交操作
 * @param obj
 */
function addOrEditCommit(obj){
	var modal = $(obj).closest(".modal");
	var dataId = modal.attr("data-id");
	
	var formControl = modal.find(".form-control");
	var params = {};
	var flag = true;
	formControl.each(function(index){
		var name = $(this).attr("name");
		var empty = $(this).attr("empty");
		if(empty && empty == "false" && isEmpty($(this).val())){
			$(this).focus();
			layer.msg($(this).attr("placeholder"));
			flag = false;
			return;
		}
			
		params[name] = $(this).val();
	});
	
	if(flag){
		params.id = dataId;
		params.status = modal.find('input[name="status"]:checked').val();
		
		console.log(params);
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type : isEmpty(dataId) ? "post" : "put",
			data : params,
			url : "/pm/permission",
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					layer.msg(data.message +",1秒钟后自动刷新");
					setTimeout("window.location.reload();", 1000);
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
	
}

/**
 * 获取请求列表
 */
function getQueryPagingParams(){
	return {page_size: pageSize, current: currentIndex, t: Math.random()};
}

function buildRow(permission, index){
	var html = '<tr class="each-row" data-id="'+ permission.id+'" index="'+ index +'">'+
					'<td><input type="checkbox" /></td>'+
					'<td>'+ permission.permission_name+'</td>'+
					'<td>'+ permission.permission_code+'</td>'+
					'<td>'+ permission.permission_order+'</td>'+
					'<td>'+ permission.permission_desc+'</td>'+
					'<td>'+ (permission.status == 1? '正常': '禁用')+'</td>'+
					'<td>'+ permission.create_time+'</td>'+
					'<td><a href="">编辑</a><a href="" style="margin-left: 10px;">删除</a></td>'+
				'</tr>';
	return html;
}

/**
 * 生成分页div
 * @param total
 */
function pageDivUtil(total){
	var html = '<li>'+
					'<a href="javascript:void(0);" onclick="pre();" aria-label="Previous">'+
						'<span aria-hidden="true">&laquo;</span>'+
					'</a>'+
				'</li>';
	totalPage = parseInt(Math.ceil(total / pageSize));
	var start = 0;
	var end = totalPage > start + 10 ? start + 10: totalPage;
	
	var selectHtml = '<li><select class="form-control" onchange="optionChange()">';
	for(var i = 0; i < totalPage; i++){
		if(currentIndex == i)
			selectHtml += '<option name="pageIndex" selected="selected" value="'+ i +'">'+ (i + 1) +'</option>';
		else
			selectHtml += '<option name="pageIndex" value="'+ i +'">'+ (i + 1) +'</option>';
	}
	
	for(var i = start; i < end; i++){
		if(currentIndex == i)
			html += '<li class="active"><a href="javascript:void(0);" onclick="goIndex('+ i +');">'+ (i+1) +'</a></li>';
		else
			html += '<li><a href="javascript:void(0);" onclick="goIndex('+ i +');">'+ (i+1) +'</a></li>';
	}
	html += '<li>'+
				'<a href="javascript:void(0);" onclick="next();" aria-label="Next">'+
					'<span aria-hidden="true">&raquo;</span>'+
				'</a>'+
			'</li>';
	
	selectHtml += '</select></li>';
	
	html += selectHtml;
	$(".pagination").html(html);
}

/**
 * 选择改变的监听
 */
function optionChange(){
	var objS = document.getElementsByTagName("select")[0];
    var index = objS.options[objS.selectedIndex].value;
    currentIndex = index;
    getPermissions();
}

/**
 * 点击向左的按钮
 */
function goIndex(index){
	currentIndex = index;
	getPermissions();
}

/**
 * 点击向左的按钮
 */
function pre(){
	currentIndex --;
	if(currentIndex < 0)
		currentIndex = 0;
	getPermissions();
}


/**
 * 点击向右的按钮
 */
function next(){
	currentIndex ++;
	if(currentIndex > totalPage)
		currentIndex = totalPage;
	getPermissions();
}